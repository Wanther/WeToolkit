package cn.wanther.toolkit.component.impl;

import android.content.Context;
import android.util.Log;

import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.component.Statistics;

public class StatisticsNothing implements Statistics {
    @Override
    public void onPageStart(Context context, String pageName) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, pageName + " page start");
        }
    }

    @Override
    public void onPageEnd(Context context, String pageName) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, pageName + " page end");
        }
    }

    @Override
    public void onEditOwnerInfo(Context context) {

    }

    @Override
    public void onDoEditOwnerInfo(Context context) {

    }

    @Override
    public void onQRCodeScan(Context context) {

    }

    @Override
    public void onQRCodeScanResult(Context context) {

    }
}
