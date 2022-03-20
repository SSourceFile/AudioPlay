package com.hmh.audiotrackplay.hook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hmh.audiotrackplay.R;

public class HookOneActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("++++", "启动activity");
        setContentView(R.layout.activity_hook_one);
    }
}
