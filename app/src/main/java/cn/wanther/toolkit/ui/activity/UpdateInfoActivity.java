package cn.wanther.toolkit.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.R;
import cn.wanther.toolkit.model.UpdateInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateInfoActivity extends BaseActivity implements DialogInterface.OnClickListener {

    private static final String TAG = "UpdateInfoActivity";

    private UpdateInfo mUpdateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String updateInfoJsonString;
        if (savedInstanceState == null) {
            updateInfoJsonString = getIntent().getStringExtra(UpdateInfo.KEY);
        } else {
            updateInfoJsonString = savedInstanceState.getString(UpdateInfo.KEY);
        }

        try {
            mUpdateInfo = UpdateInfo.create(new JSONObject(updateInfoJsonString));
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        if (mUpdateInfo == null) {
            finish();
        } else {
            showUpdateUI();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mUpdateInfo != null) {
            outState.putString(UpdateInfo.KEY, mUpdateInfo.toString());
        }
    }

    protected void showUpdateUI() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.find_new_version);

        String content = "新版本：" + mUpdateInfo.getVersionName() + "\n" + mUpdateInfo.getDescText();
        builder.setMessage(content);

        if (mUpdateInfo.isForceUpdate()) {
            builder.setCancelable(false);
        } else {
            builder.setNegativeButton(android.R.string.cancel, null);
        }
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        builder.create().show();
    }

    protected void doUpdate(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
        startActivity(intent);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                doUpdate(mUpdateInfo.getDownloadUrl());
                break;
        }
    }

    @Override
    protected String getPageName() {
        return "UpdateInfo";
    }
}
