package cn.wanther.toolkit.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.component.Push;
import cn.wanther.toolkit.component.async.AsyncTazk;
import cn.wanther.toolkit.model.push.PushMessage;
import cn.wanther.toolkit.model.push.PushMessageNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class PushPayloadReceiver extends BroadcastReceiver {

    private static final String TAG = "PushPayloadReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BuildConfig.DEBUG) {
            String bundleLogString = "";
            Bundle bundle = intent.getExtras();
            Set<String> keySet = bundle.keySet();
            for (String key : keySet) {
                bundleLogString += (key + "=" + bundle.get(key));
            }
            Log.d(TAG, bundleLogString);
        }

        if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)){	// <-- 自定义消息
            onPushMessageReceived(context, intent.getExtras());
        }else if(JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)){

        }else if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)){

        }else if(Push.ACTION_PUSH_NOTIFICATION_OPENED.equals(action)) {
            onNotificationClicked(context, intent.getExtras());

            final String thirdpartMsgId = intent.getStringExtra(Push.KEY_THIRD_PLATFORM_ID);
            new AsyncTazk<Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Push.Instance().reportNotificationOpened(thirdpartMsgId);
                    return null;
                }
            }.execute(App.Instance().getConcurrentExecutor());
        }
    }

    protected void onNotificationClicked(Context context, Bundle bundle){
        String extra = bundle.getString(Push.KEY_EXTRA);

        if(TextUtils.isEmpty(extra)){
            extra = "{}";
        }

        try {
            String title = bundle.getString(Push.KEY_TITLE);
            String content = bundle.getString(Push.KEY_CONTENT);
            PushMessageNotification msg = (PushMessageNotification)PushMessage.create(title, content, new JSONObject(extra));
            if(msg != null){
                msg.onClick(context);
            }
        } catch (JSONException e) {
            if(BuildConfig.DEBUG){
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    protected void onPushMessageReceived(Context context, Bundle bundle) {
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

        if(TextUtils.isEmpty(extra)){
            extra = "{}";
        }

        try {
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            PushMessage msg = PushMessage.create(title, content, new JSONObject(extra));
            if (msg instanceof PushMessageNotification) {
                PushMessageNotification msgNotification = (PushMessageNotification) msg;
                Notification notification = msgNotification.buildNotification(context);
                if (notification != null) {
                    Intent intent = new Intent(Push.ACTION_PUSH_NOTIFICATION_OPENED);
                    intent.addCategory(context.getPackageName());
                    intent.putExtra(Push.KEY_THIRD_PLATFORM_ID, bundle.getString(JPushInterface.EXTRA_MSG_ID));
                    intent.putExtra(Push.KEY_TITLE, msg.getTitle());
                    intent.putExtra(Push.KEY_CONTENT, msg.getBody());
                    intent.putExtra(Push.KEY_EXTRA, msg.getData() == null ? "{}" : msg.getData().toString());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.contentIntent = pendingIntent;

                    int msgId = msg.getId();
                    if (msgId <= 0) {
                        msgId = (int)Math.round(Math.random() * 1000);
                    }
                    NotificationManagerCompat.from(context).notify(msgId, notification);
                }
            }
        } catch (JSONException e) {
            if(BuildConfig.DEBUG){
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
