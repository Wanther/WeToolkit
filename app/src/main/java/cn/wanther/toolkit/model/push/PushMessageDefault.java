package cn.wanther.toolkit.model.push;

import android.content.Context;
import android.content.Intent;

import cn.wanther.toolkit.ui.activity.MainActivity;

public class PushMessageDefault extends PushMessageNotification {

    public PushMessageDefault(int id, int type, String title, String body) {
        super(id, type, title, body, null);
    }

    protected Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }
}
