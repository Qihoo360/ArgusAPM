package com.argusapm.sample.test;

import android.app.Activity;
import android.os.Bundle;

import com.argusapm.sample.R;

/**
 * Activity测试类
 *
 * @author ArgusAPM Team
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
