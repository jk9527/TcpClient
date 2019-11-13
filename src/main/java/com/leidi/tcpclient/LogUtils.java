package com.leidi.tcpclient;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by 40208 on 2019/11/11.
 */

public class LogUtils {

    private static String losgStr = "td-lte";

    /**
     * 记录日志
     *
     * @param msg
     */
    public static void Log(String msg) {
            Log.i(losgStr, msg);
    }

    public static void Log(String tag, String msg) {
            Log.i(tag, msg);
    }

    public static void Loge(String msg) {
            Log.e(losgStr, msg);

    }

    public static void Loge(String tag, String msg) {
            Log.e(tag, msg);

    }

    /**
     * 显示提示信息
     *
     * @param context
     * @param msg
     */
    public static void showMsg(Context context, String msg) {
        // Writelog(losgStr,msg,"s");
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

        // showTip(context,msg);
    }

    private static Toast mToast;



    /**
     * 显示提示信息
     *
     * @param context
     * @param msg
     */
    public static void showMsgShort(Context context, String msg) {
        // Writelog(losgStr,msg,"s");
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示提示信息在中间
     *
     * @param context
     * @param msg
     */
    public static void showMsgCenter(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

}
