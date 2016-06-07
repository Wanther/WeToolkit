package cn.wanther.toolkit.component;

import android.content.Context;

public interface Statistics {
    String TAG = "Statistics";

    void onPageStart(Context context, String pageName);
    void onPageEnd(Context context, String pageName);
    void onEditOwnerInfo(Context context);
    void onDoEditOwnerInfo(Context context);
    void onQRCodeScan(Context context);
    void onQRCodeScanResult(Context context);
}
