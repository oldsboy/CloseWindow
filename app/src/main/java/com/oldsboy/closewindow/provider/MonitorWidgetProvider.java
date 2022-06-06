package com.oldsboy.closewindow.provider;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.oldsboy.closewindow.R;

import static android.content.Context.DEVICE_POLICY_SERVICE;

public class MonitorWidgetProvider extends AppWidgetProvider {
    public static final String TAG = "widget";
    public static final String BTN_CLICK_ACTION = "Widget.Button.Click";

    public MonitorWidgetProvider() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(TAG, "onReceive: 收到点击：" + intent.getAction());
        if (intent != null && intent.getAction() != null) {
            Log.d(TAG, "onReceive: 收到广播通知：" + intent.getAction());
            switch (intent.getAction()) {
                case BTN_CLICK_ACTION:
                    closeWindow(context);
            }
        }
    }

    private void closeWindow(final Context context) {
        ComponentName adminReceiver = new ComponentName(context, AdminReceiver.class);
        DevicePolicyManager systemService = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
        if (!systemService.isAdminActive(adminReceiver)){
            Toast.makeText(context, "需要先进入app进行授权", Toast.LENGTH_SHORT).show();
        }else {
            Log.d(TAG, "closeWindow: ");
            new Handler().postDelayed(systemService::lockNow, 300);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lock_layout);

        remoteViews.setImageViewResource(R.id.img_lock, R.drawable.press_icon);

        Intent intent = new Intent(BTN_CLICK_ACTION);
        intent.setClass(context, MonitorWidgetProvider.class);
        intent.setFlags(intent.getFlags()| 0x01000000);

        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.container, pendingIntent2);
        remoteViews.setOnClickPendingIntent(R.id.img_lock, pendingIntent2);
        remoteViews.setOnClickPendingIntent(R.id.tv, pendingIntent2);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        Log.d(TAG, "onUpdate: 配置点击事件完成");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}