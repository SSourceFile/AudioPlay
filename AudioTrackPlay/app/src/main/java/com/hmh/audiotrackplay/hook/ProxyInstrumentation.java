package com.hmh.audiotrackplay.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

public class ProxyInstrumentation extends Instrumentation {

    private Instrumentation ins;
    public ProxyInstrumentation(Instrumentation base) {
        this.ins = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        Log.d("+++++++", "我们自己的逻辑");

        //这里还要执行系统的原本逻辑，但是突然发现，这个execStartActivity居然是hide的，只能反射咯
        try {
//            Class<?> InstrumentationClz = Class.forName("android.app.Instrumentation");
//            Method execStartActivity = InstrumentationClz.getDeclaredMethod("execStartActivity",
//                    Context.class, IBinder.class, IBinder.class, Activity.class,
//                    Intent.class, int.class, Bundle.class);
//            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class,
//                                        IBinder.class, IBinder.class, Activity.class,
//                                        Intent.class, int.class, Bundle.class);
//
//            return (ActivityResult) execStartActivity.invoke(ins,
//                    who, contextThread, token, target, intent, requestCode, options);
            Class[] classes = {Context.class,IBinder.class,IBinder.class,Activity.class,Intent.class,int.class, Bundle.class};
            Object[] objects = {who,contextThread,token,target,intent,requestCode,options};
            Log.d("MyInstrumentation","Instrumentation Hook22222");
            return (ActivityResult) RefInvoke.invokeInstanceMethod(ins,"execStartActivity",classes,objects);

        } catch (Exception e) {
            Log.e("+++++++", "error==="+e.getLocalizedMessage());
            e.printStackTrace();
        }

        return null;
    }
}
