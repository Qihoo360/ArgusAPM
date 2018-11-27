package com.argusapm.sample.test;

import android.app.Activity;
import android.os.Bundle;

import com.argusapm.sample.R;

/**
 * Block测试页面
 *
 * @author ArgusAPM Team
 */
public class TestBlockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_test_block);
    }
}
