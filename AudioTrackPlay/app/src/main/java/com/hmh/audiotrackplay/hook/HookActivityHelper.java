package com.hmh.audiotrackplay.hook;

import android.app.Activity;
import android.app.Instrumentation;

//import com.bytedance.android.bytehook.ByteHook;

import java.lang.reflect.Field;

/**
 *hookhelp类
 *
 **/
public class HookActivityHelper {

    public static void init(Activity activity){
        try {
//            Field mInstumentation = Activity.class.getDeclaredField("mInstrumentation");
//            mInstumentation.setAccessible(true);
////            mInstumentation.get
////            ByteHook.
//            Instrumentation instrumentation = (Instrumentation) mInstumentation.get(activity);
//
//            ProxyInstrumentation proxyInstrumentation = new ProxyInstrumentation(instrumentation);
//            mInstumentation.set(activity, proxyInstrumentation);
            Instrumentation instrumentation = (Instrumentation) RefInvoke.getFieldObject(Activity.class,activity,"mInstrumentation");
            ProxyInstrumentation instrumentation1 = new ProxyInstrumentation(instrumentation);
            RefInvoke.setFieldObject(Activity.class,activity,"mInstrumentation",instrumentation1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
