package com.argusapm.android.core.job.anr;

import android.content.Context;
import android.text.TextUtils;

import com.argusapm.android.core.Manager;
import com.argusapm.android.core.TaskConfig;
import com.argusapm.android.debug.storage.IOUtil;
import com.argusapm.android.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * anr文件解析类
 *
 * @author ArgusAPM Team
 */
public class AnrFileParser {
    public static final String SUB_TAG = "AnrFileParser";
    public static final String JSON_KEY_TIME = "time";
    public static final String JSON_KEY_PID = "pid";
    private Pattern startPattern = Pattern.compile("-{5}\\spid\\s\\d+\\sat\\s\\d+-\\d+-\\d+\\s\\d{2}:\\d{2}:\\d{2}\\s-{5}"); //第一行
    private Pattern endPattern = Pattern.compile("-{5}\\send\\s\\d+\\s-{5}"); //最后一行
    private Pattern cmdLinePattern = Pattern.compile("Cmd\\sline:\\s(\\S+)"); //第二行 cmd进程名称
    private Pattern threadPattern = Pattern.compile("\".+\"\\s(daemon\\s){0,1}prio=\\d+\\stid=\\d+\\s.*"); //线程
    private SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private List<String> allowedList;
    private Context mContext;

    public AnrFileParser(Context c, List<String> list) {
        allowedList = list;
        mContext = c;
    }

    public AnrInfo parseFile(String path) {
        AnrInfo anrInfo = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String buffer = null;
            StringBuffer stringBuffer = null;
            while ((buffer = bufferedReader.readLine()) != null) {
                if (TextUtils.isEmpty(buffer)) {
                    continue;
                }
                if (startPattern.matcher(buffer).matches()) {
                    // 需要判断当前的anr信息是否曾经读过，若读过，则跳过
                    String[] sections = buffer.split("\\s");
                    if (sections != null && sections.length > 0) {
                        String pidStr = sections[2].trim();
                        String timeStr = sections[4].trim() + " " + sections[5].trim();
                        long pid = Long.valueOf(pidStr);
                        long ts = localSimpleDateFormat.parse(timeStr).getTime();
                        if (!isUploaded(pid, ts)) {
                            anrInfo = new AnrInfo();
                            stringBuffer = new StringBuffer();
                            anrInfo.setTime(ts);
                            anrInfo.setProId(pid);
                            appendContent(stringBuffer, buffer);
                        } else {
                            return null;
                        }
                    }
                } else if (endPattern.matcher(buffer).matches()) {
                    String[] sections = buffer.split("\\s");
                    String pidStr = sections[2].trim();
                    long pid = Long.valueOf(pidStr);
                    appendContent(stringBuffer, buffer);
                    if (pid == anrInfo.getProId()) {
                        anrInfo.setAnrContent(stringBuffer.toString());
                        return anrInfo; //解析成功
                    }
                    return null;
                } else if (cmdLinePattern.matcher(buffer).matches()) {
                    String proName = getProName(buffer);
                    if (anrInfo != null) {
                        anrInfo.setProName(proName);
                    }
                    boolean isValidPro = false; //进程是否有效
                    if (proName.contains(Manager.getContext().getPackageName())) {
                        isValidPro = true;
                    } else {
                        if (allowedList != null && !allowedList.isEmpty()) {
                            for (String str : allowedList) {
                                if (proName.contains(str)) {
                                    isValidPro = true;
                                }
                            }
                        }
                    }
                    if (!isValidPro) { //无效anr文件
                        return null;
                    }
                    appendContent(stringBuffer, buffer);
                } else {
                    appendContent(stringBuffer, buffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.safeClose(bufferedReader);
        }
        return null;
    }

    private String getProName(String buffer) {
        String proName = "";
        int index = buffer.indexOf(":");
        index++;
        if (index >= 0 && index < buffer.length()) {
            proName = buffer.substring(index).trim();
        }
        return proName;
    }

    /**
     * 把上传记录保存下来
     *
     * @param pid
     * @param ts
     */
    public static void addUploadedPref(long pid, long ts) {
        try {
            JSONArray recentArray = getRecentsJSONArray();
            // 先更新anr列表，再写入
            if (recentArray == null) {
                recentArray = new JSONArray();
            } else {
                JSONArray tmp = new JSONArray();
                for (int i = 0; i < recentArray.length(); i++) {
                    JSONObject record = (JSONObject) recentArray.get(i);
                    long recordTime = record.getLong(JSON_KEY_TIME);
                    long curTime = System.currentTimeMillis();
                    long interval = curTime - recordTime;
                    if (interval < TaskConfig.ANR_VALID_TIME) {//过期不保存记录
                        tmp.put(record);
                    }

                }
                recentArray = tmp;
            }
            JSONObject record = new JSONObject();
            record.put(JSON_KEY_TIME, ts);
            record.put(JSON_KEY_PID, pid);
            recentArray.put(record);
            PreferenceUtils.setString(Manager.getContext(), PreferenceUtils.SP_KEY_RECENT_PIDS, recentArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendContent(StringBuffer stringBuffer, String content) {
        if (stringBuffer != null) {
            stringBuffer.append(content).append("\n");
        }
    }

    /**
     * 判断是否已经上传过
     */
    private boolean isUploaded(long pid, long ts) {
        try {
            String recentPidsStr = PreferenceUtils.getString(mContext, PreferenceUtils.SP_KEY_RECENT_PIDS, "");
            if (TextUtils.isEmpty(recentPidsStr)) {
                return false;
            }
            JSONArray jsonArray = new JSONArray(recentPidsStr);
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject recordJson = (JSONObject) jsonArray.get(i);
                    long recordPid = recordJson.getLong(JSON_KEY_PID);
                    long recordTime = recordJson.getLong(JSON_KEY_TIME);
                    if (pid == recordPid && recordTime == ts) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取已经上传过的列表
     *
     * @return
     */
    private static JSONArray getRecentsJSONArray() {
        String str = PreferenceUtils.getString(Manager.getContext(), PreferenceUtils.SP_KEY_RECENT_PIDS, "");
        JSONArray jsonArray = null;
        if (!TextUtils.isEmpty(str)) {
            try {
                jsonArray = new JSONArray(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

}
