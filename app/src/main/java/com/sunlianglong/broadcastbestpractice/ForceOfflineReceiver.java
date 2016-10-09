package com.sunlianglong.broadcastbestpractice;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by sun liang long on 2016/8/12.
 */
public class ForceOfflineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);//构建一个对话框
        dialogBuilder.setTitle("Worning");
        dialogBuilder.setMessage("You are to be offline.Please try to login again");
        dialogBuilder.setCancelable(false);//将对话框设置为不可取消
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override   //给对话框注册按钮
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //解释：http://www.cnblogs.com/lwbqqyumidi/p/3775479.html
                context.startActivity(intent);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        //因为Alert的显示需要依附于一个确定的Activity类。而以上做法就是声明我们要弹出的这个
        // 提示框是一个系统的提示框，即全局性质的提示框，所以只要手机处于开机状态，无论它
        // 现在处于何种界面之下，只要调用alertDialog.show()，就会弹出提示框来。
    }
}
