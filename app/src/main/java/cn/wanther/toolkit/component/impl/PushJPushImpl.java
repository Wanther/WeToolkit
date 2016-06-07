package cn.wanther.toolkit.component.impl;

import android.content.Context;
import android.text.TextUtils;

import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.component.Push;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class PushJPushImpl extends Push {

    public PushJPushImpl(Context context) {
        super(context);
    }

    @Override
    public void init() {
        if (BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }

        JPushInterface.setStatisticsEnable(false);
        JPushInterface.init(getContext());
    }

    @Override
    public void setAlias(String alias) {
        JPushInterface.setAlias(getContext(), alias, null);
    }

    @Override
    public void setTag(String tags) {
        Set<String> tagSet = null;

        if (!TextUtils.isEmpty(tags)) {
            tagSet = new HashSet<String>();

            String[] tagArray = tags.split("[,]");

            for (String tag : tagArray) {
                tagSet.add(tag.trim());
            }
        }

        JPushInterface.setTags(getContext(), tagSet, null);
    }

    @Override
    public void setAliasAndTags(String alias, String tags) {
        Set<String> tagSet = null;

        if (!TextUtils.isEmpty(tags)) {
            tagSet = new HashSet<String>();

            String[] tagArray = tags.split("[,]");

            for (String tag : tagArray) {
                tagSet.add(tag.trim());
            }
        }

        JPushInterface.setAliasAndTags(getContext(), alias, tagSet);
    }

    @Override
    public void reportNotificationOpened(final String messageId) {
        JPushInterface.reportNotificationOpened(getContext(), messageId);
    }

}
