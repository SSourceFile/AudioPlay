package com.hmh.audiotrackplay.hook;


import android.app.Activity;
import android.content.Intent;

import com.taobao.android.dexposed.XC_MethodHook;

public class AcitvityHook extends XC_MethodHook {

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Activity thisObject = (Activity) param.thisObject;

        thisObject.startActivity(new Intent(thisObject, HookOneActivity.class));

    }
}
