package cn.wanther.toolkit.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.R;
import cn.wanther.toolkit.manager.SimpleLocalDataCallback;
import cn.wanther.toolkit.model.OwnerInfo;
import cn.wanther.toolkit.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateOwnerInfoActivity extends BaseActivity implements TextWatcher {

    private static final String TAG = "UpdateOwnerInfoActivity";

    public static final int REQ_CODE_UPDATE_OWNER = 2;

    private Spinner mInputDeviceOwner;
    private TextInputLayout mInputUser;
    private OwnerInfo mOwnerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String ownerInfoJSONString;
        if (savedInstanceState == null) {
            ownerInfoJSONString = getIntent().getStringExtra(OwnerInfo.KEY);
        } else {
            ownerInfoJSONString = savedInstanceState.getString(OwnerInfo.KEY);
        }

        if (TextUtils.isEmpty(ownerInfoJSONString)) {
            mOwnerInfo = new OwnerInfo();
            mOwnerInfo.setOwner(OwnerInfo.OWNER_TYPE_COMPANY);
        } else {
            try {
                mOwnerInfo = OwnerInfo.create(new JSONObject(ownerInfoJSONString));
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }

        setContentView(R.layout.update_owner_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mInputDeviceOwner = (AppCompatSpinner)findViewById(R.id.device_owner);
        mInputDeviceOwner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.owner_type)));
        mInputDeviceOwner.setSelection(mOwnerInfo.getOwner() - 1);

        mInputUser = (TextInputLayout)findViewById(R.id.input_user);
        mInputUser.getEditText().addTextChangedListener(this);
        if (!TextUtils.isEmpty(mOwnerInfo.getUserName())) {
            mInputUser.getEditText().setText(mOwnerInfo.getUserName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.update_owner_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.ok:
                updateOwnerInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void updateOwnerInfo() {
        getApp().getStatistics().onDoEditOwnerInfo(this);

        int ownerType = (int)mInputDeviceOwner.getSelectedItemId();
        String user = mInputUser.getEditText().getText().toString();
        if (TextUtils.isEmpty(user) || Utils.getByteCount(user) > 20) {
            mInputUser.setError(getString(R.string.user_input_hint));
            return;
        }

        mOwnerInfo.setOwner(ownerType + 1);
        mOwnerInfo.setUserName(user);

        getApp().getSysManager().updateOwnerInfo(this, mOwnerInfo, new SimpleLocalDataCallback<OwnerInfo>(){
            @Override
            public void onStart() {
                showProgressDialog(R.string.loading);
            }

            @Override
            public void onOperateSuccess(OwnerInfo data) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onOperateError(Exception e) {
                dealWithException(e);
            }

            @Override
            public void onOperateFinish() {
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s) || Utils.getByteCount(s.toString()) > 20) {
            mInputUser.setError(getString(R.string.user_input_hint));
        } else {
            mInputUser.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected String getPageName() {
        return "UpdateOwnerInfo";
    }
}
