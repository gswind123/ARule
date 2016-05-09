package org.windning.demo;

/**
 * Created by yw_sun on 2016/2/17.
 */
public class SunLogUtil {
    static public void saveCrashTrace(Throwable ex) {
        StackTraceElement[] elements = ex.getStackTrace();
        StringBuilder builder = new StringBuilder(ex.getMessage());
        for(StackTraceElement ele : elements) {
            builder.append('\n').append(ele.toString());
        }
        android.util.Log.e("crash", builder.toString());
    }
}
