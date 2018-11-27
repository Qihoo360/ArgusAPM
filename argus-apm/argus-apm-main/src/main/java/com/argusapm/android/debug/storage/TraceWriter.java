package com.argusapm.android.debug.storage;

import android.os.Build;

import com.argusapm.android.Env;
import com.argusapm.android.core.Manager;
import com.argusapm.android.debug.config.DebugConfig;
import com.argusapm.android.utils.LogX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 用于将样本分析之后的有效数据写入文件，方便查找性能问题
 * 防止文件无限膨胀，设置最大文件大小，超过则删除老数据
 *
 * @author ArgusAPM Team
 */
public class TraceWriter {
    private static final String SUB_TAG = "trace";

    private static final long MAX_IDLE_TIME = 1 * 60 * 1000L;
    private static final long MAX_LOG_SIZE = 3 * 1024 * 1024; // 1M
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    private static final Object LOCKER_WRITE_THREAD = new Object();
    private static WriteFileRun sWriteThread = null;
    private static ConcurrentLinkedQueue<Object[]> sQueuePool = new ConcurrentLinkedQueue<Object[]>();


    public static void log(String tagName, String content) {
        log(tagName, content, true);
    }

    private synchronized static void log(String tagName, String content, boolean forceFlush) {
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "tagName = " + tagName + " content = " + content);
        }
        if (sWriteThread == null) {
            sWriteThread = new WriteFileRun();
            Thread t = new Thread(sWriteThread);
            t.setName("ApmTrace.Thread");
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();

            String initContent = "---- Phone=" + Build.BRAND + "/" + Build.MODEL + "/verName:" + " ----";
            sQueuePool.offer(new Object[]{tagName, initContent, Boolean.valueOf(forceFlush)});
            if (Env.DEBUG) {
                LogX.d(Env.TAG, SUB_TAG, "init offer content = " + content);
            }
        }
        if (Env.DEBUG) {
            LogX.d(Env.TAG, SUB_TAG, "offer content = " + content);
        }
        sQueuePool.offer(new Object[]{tagName, content, Boolean.valueOf(forceFlush)});

        synchronized (LOCKER_WRITE_THREAD) {
            LOCKER_WRITE_THREAD.notify();
        }
    }

    public synchronized static void stop() {
        if (sWriteThread != null) {
            sWriteThread.stopRun();
            sWriteThread = null;
        }
    }

    static class WriteFileRun implements Runnable {

        private volatile boolean mRunning = true;
        private File fLog;

        public void stopRun() {
            mRunning = false;
            synchronized (LOCKER_WRITE_THREAD) {
                LOCKER_WRITE_THREAD.notify();
            }
        }

        @Override
        public void run() {
            while (mRunning) {
                try {
                    Object[] oneLog = sQueuePool.poll();

                    if (oneLog == null) {
                        if (Env.DEBUG) {
                            LogX.d(Env.TAG, SUB_TAG, "oneLog == null");
                        }
                        synchronized (LOCKER_WRITE_THREAD) {
                            LOCKER_WRITE_THREAD.wait(MAX_IDLE_TIME);
                        }
                    } else {
                        String tagName = (String) oneLog[0];
                        String content = (String) oneLog[1];
                        Boolean forceFlush = (Boolean) oneLog[2];
                        if (Env.DEBUG) {
                            LogX.d(Env.TAG, SUB_TAG, "oneLog = [ " + tagName + ", " + content + ", " + forceFlush + " ]");
                        }

                        if (fLog == null) {
                            fLog = ensureFile();
                        }

                        if (fLog != null) {
                            FileWriter fos = null;
                            Writer mWriter = null;
                            try {
                                fos = new FileWriter(fLog, true);
                                mWriter = new BufferedWriter(fos, 1024);

                                handleWrite(mWriter, tagName, content, forceFlush);
                            } catch (Exception e) {
                                if (Env.DEBUG) {
                                    LogX.d(Env.TAG, SUB_TAG, "ex : " + e);
                                }
                            } finally {
                                IOUtil.safeClose(mWriter);
                                IOUtil.safeClose(fos);
                            }

                            if (fLog.length() > MAX_LOG_SIZE) {
                                long time = System.currentTimeMillis();
                                File fTmp = copyLogTail(fLog);
                                if (Env.DEBUG) {
                                    LogX.d(Env.TAG, SUB_TAG, "WriteFileRun timeuse : " + (System.currentTimeMillis() - time));
                                }

                                if (fTmp != null) {
                                    fLog.delete();
                                    fTmp.renameTo(fLog);
                                }
                            }
                        } else {
                            if (Env.DEBUG) {
                                LogX.d(Env.TAG, SUB_TAG, "WriteFileRun file null");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * log文件大于3M的时候就做一次清理，保留最后N条log，剩下清理掉
         *
         * @param f
         * @return
         */
        private File copyLogTail(File f) {
            File fTmp = new File(f.getParent(), f.getName() + "_tmp");
            BufferedReader br = null;
            BufferedWriter bw = null;
            try {
                br = new BufferedReader(new FileReader(f));
                LinkedList<String> list = new LinkedList<String>();
                String sTmp = null;
                while ((sTmp = br.readLine()) != null) {
                    list.add(sTmp);
                    if (list.size() > 3000) {
                        list.poll();
                    }
                }

                bw = new BufferedWriter(new FileWriter(fTmp, true));
                Iterator<String> itr = list.iterator();
                while (itr.hasNext()) {
                    bw.write(itr.next());
                    bw.write('\n');
                }
                bw.flush();

                return fTmp;
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                IOUtil.safeClose(br);
                IOUtil.safeClose(bw);
            }

            return null;
        }
    }

    private static File getTraceDir() {
        return new File(Manager.getInstance().getBasePath() + Manager.getContext().getPackageName() + File.separator);
    }

    private static File ensureFile() {
        File logDir = getTraceDir();
        if (!logDir.exists() && !logDir.mkdirs()) {
            return null;
        }

        File f = new File(logDir, DebugConfig.OUTPUT_FILE);

        try {
            if (!f.exists() && !f.createNewFile()) {
                return null;
            }

            return f;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getTime(long timeMillis) {
        return DATE_FORMAT.format(timeMillis);
    }

    private static void handleWrite(Writer os, String tagName, String content, Boolean forceFlush) {
        if (os != null) {
            try {
                os.append(getTime(System.currentTimeMillis())).append(' ').append(tagName).append("  ");
                if (content != null) {
                    os.append(content);
                }
                os.append('\n');

                if (forceFlush) {
                    os.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
