package cn.wanther.toolkit.component.impl;

import android.content.Context;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.component.Statistics;

public class StatisticsUmeng implements Statistics {

    public StatisticsUmeng(Context context) {
        AnalyticsConfig.setAppkey(context, App.Instance().getProperty("appkey.umeng.analytics"));
        AnalyticsConfig.setChannel("official");
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
        }
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void onPageStart(Context context, String pageName) {
        MobclickAgent.onPageStart(pageName);
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPageEnd(Context context, String pageName) {
        MobclickAgent.onPause(context);
        MobclickAgent.onPageEnd(pageName);
    }

    @Override
    public void onEditOwnerInfo(Context context) {
        MobclickAgent.onEvent(context, "edit_owner_info");
    }

    @Override
    public void onDoEditOwnerInfo(Context context) {
        MobclickAgent.onEvent(context, "do_edit_owner_info");
    }

    @Override
    public void onQRCodeScan(Context context) {
        MobclickAgent.onEvent(context, "qrcode_scan");
    }

    @Override
    public void onQRCodeScanResult(Context context) {
        MobclickAgent.onEvent(context, "qrcode_result");
    }
}
