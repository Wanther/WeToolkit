package cn.wanther.toolkit.model.push;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import cn.wanther.toolkit.R;

import org.json.JSONObject;

/**
 * 通知
 */
public abstract class PushMessageNotification extends PushMessage {

    public PushMessageNotification(int id, int type, String title, String body, JSONObject extra){
        super(id, type, title, body, extra);
    }

    public Notification buildNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_push_small)
                .setColor(0xff00bdef)
                .setOngoing(false)
                .setContentTitle(getTitle())
                .setAutoCancel(true)
                .setContentText(getBody())
                .setTicker(getBody())
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return builder.build();
    }

    public void onClick(Context context){
        Intent intent = getIntent(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    protected abstract Intent getIntent(Context context);
}
