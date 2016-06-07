package cn.wanther.toolkit.component;

import android.content.Context;

import cn.wanther.toolkit.component.impl.PushJPushImpl;

public abstract class Push {

    public static final String ACTION_PUSH_NOTIFICATION_OPENED = "cn.wanther.toolkit.NOTIFICATION_OPENED";
    public static final String KEY_THIRD_PLATFORM_ID = "_third_platform_id";
    public static final String KEY_TITLE = "_title";
    public static final String KEY_CONTENT = "_title";
    public static final String KEY_EXTRA = "_extra";

    private static Push sInstance;

    public static void create(Context context) {
        sInstance = new PushJPushImpl(context);
        sInstance.init();
    }

    public static Push Instance() {
        return sInstance;
    }

    private Context mContext;

    public Push(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract void init();

    public abstract void setAlias(String alias);

    public abstract void setTag(String tags);

    public abstract void setAliasAndTags(String alias, String tags);

    public abstract void reportNotificationOpened(String messageId);
}
