package cn.wanther.toolkit.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.R;
import cn.wanther.toolkit.component.async.AsyncTazk;
import cn.wanther.toolkit.manager.SimpleCallback;
import cn.wanther.toolkit.manager.SimpleLocalDataCallback;
import cn.wanther.toolkit.model.DeviceInfo;
import cn.wanther.toolkit.model.OwnerInfo;
import cn.wanther.toolkit.model.UpdateInfo;
import cn.wanther.toolkit.utils.Utils;

import java.util.concurrent.Future;

public class MainActivity extends BaseActivity
		implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
	private static final String TAG = "MainActivity";

	private long mGoBackLastPressed;
	private Toast mExitTipToast;
	private DrawerLayout mDrawerLayout;

	private DeviceInfo mDeviceinfo;
	private RecyclerView.Adapter mInfoAdapter;
	private TextView mOwnerInfoTv;
	private TextView mUserTv;
	private Future<?> mInfoLoadTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
		collapsingToolbarLayout.setTitle(getString(R.string.device_info));

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		TextView tv = (TextView)navigationView.findViewById(R.id.app_version);
		tv.setText(getString(R.string.version_info_label, Utils.getVersionName(this)));

		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
		fab.setOnClickListener(this);

		// owner info
		mUserTv = (TextView)findViewById(R.id.user);
		mOwnerInfoTv = (TextView)findViewById(R.id.device_owner);

		tv = (TextView)findViewById(R.id.label_user);
		tv.setText(getString(R.string.colon, getString(R.string.user)));

		tv = (TextView)findViewById(R.id.label_device_owner);
		tv.setText(getString(R.string.colon, getString(R.string.device_owner)));

		// device info
		RecyclerView rv = (RecyclerView)findViewById(android.R.id.list);
		rv.setLayoutManager(new LinearLayoutManager(this));
		mInfoAdapter = new DeviceInfoListAdapter();
		rv.setAdapter(mInfoAdapter);

		mInfoLoadTask = new DeviceInfoGetTask(this).execute(getApp().getShortTimeExecutor());

		getApp().getSysManager().getUpdateInfo(this, new SimpleCallback<UpdateInfo>(){
			@Override
			public void onOperateSuccess(UpdateInfo data) {
				if (data == null) {
					return;
				}

				Intent intent = new Intent(MainActivity.this, UpdateInfoActivity.class);
				intent.putExtra(UpdateInfo.KEY, data.toString());
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == QRCodeScanActivity.REQ_CODE_SCAN_QRCODE) {
			handleQRCodeScanResult(resultCode, data);
		} else if (requestCode == UpdateOwnerInfoActivity.REQ_CODE_UPDATE_OWNER) {
			handleUpdateOwnerInfo(resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mExitTipToast != null) {
			mExitTipToast.cancel();
		}

		Utils.cancelTask(mInfoLoadTask, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_qrcode_scan:
				startActivityForResult(new Intent(this, QRCodeScanActivity.class), QRCodeScanActivity.REQ_CODE_SCAN_QRCODE);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			/*long now = System.currentTimeMillis();
			if (now - mGoBackLastPressed <= 3500) {
				super.onBackPressed();
				return;
			}

			mGoBackLastPressed = now;
			mExitTipToast = Toast.makeText(this, R.string.exit_tip, Toast.LENGTH_LONG);
			mExitTipToast.show();*/
			super.onBackPressed();
		}

	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
			case R.id.action_qrcode_scan:
				gotoQRCodeScan();
				break;
			case R.id.action_web_home:
				gotoWebHome();
				break;
		}

		mDrawerLayout.closeDrawer(GravityCompat.START);

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fab:
				gotoUpdateOwnerInfo();
				break;
		}
	}

	protected void bindOwnerInfo(OwnerInfo ownerInfo) {
		mDeviceinfo.setOwnerInfo(ownerInfo);

		String ownerName = null;
		String userName = null;

		if (ownerInfo != null) {
			ownerName = ownerInfo.getOwnerName();
			userName = ownerInfo.getUserName();
		}

		if (TextUtils.isEmpty(ownerName)) {
			ownerName = getString(R.string.unknown);
		}

		if (TextUtils.isEmpty(userName)) {
			userName = getString(R.string.unknown);
		}

		mOwnerInfoTv.setText(ownerName);
		mUserTv.setText(userName);
	}

	protected void gotoUpdateOwnerInfo() {
		Intent intent = new Intent(this, UpdateOwnerInfoActivity.class);
		OwnerInfo ownerInfo = null;
		if (mDeviceinfo != null) {
			ownerInfo = mDeviceinfo.getOwnerInfo();
		}

		if (ownerInfo != null) {
			intent.putExtra(OwnerInfo.KEY, ownerInfo.toString());
		}

		startActivityForResult(intent, UpdateOwnerInfoActivity.REQ_CODE_UPDATE_OWNER);
	}

	protected void gotoQRCodeScan() {
		startActivityForResult(new Intent(this, QRCodeScanActivity.class), QRCodeScanActivity.REQ_CODE_SCAN_QRCODE);
	}

	protected void gotoWebHome() {
		try {
			Uri uri = Uri.parse(App.Instance().getProperty("url.webhome"));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
			startActivity(intent);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, e.getMessage(), e);
			}
			Toast.makeText(this, "请安装浏览器", Toast.LENGTH_SHORT).show();
		}
	}

	protected void handleQRCodeScanResult(int resultCode, Intent data) {

		getApp().getStatistics().onQRCodeScanResult(this);

		if (resultCode != RESULT_OK) {
			return;
		}

		String result = data.getStringExtra(QRCodeScanActivity.KEY_SCAN_RESULT);

		if (TextUtils.isEmpty(result)) {
			return;
		}

		try {
			Uri uri = Uri.parse(result);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
			startActivity(intent);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, e.getMessage(), e);
			}
			new AlertDialog.Builder(this).setTitle(R.string.scan_result)
					.setMessage(result)
					.create()
					.show();
		}

	}

	protected void handleUpdateOwnerInfo(int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		Utils.cancelTask(mInfoLoadTask, true);
		mInfoLoadTask = new DeviceInfoGetTask(this).execute(getApp().getShortTimeExecutor());
	}

	@Override
	protected String getPageName() {
		return "Main";
	}

	private class DeviceInfoGetTask extends AsyncTazk<DeviceInfo> {

		private Context mContext;

		public DeviceInfoGetTask(Context context){
			mContext = context;
		}

		@Override
		public DeviceInfo doInBackground() throws Exception {
			return DeviceInfo.create(mContext);
		}

		@Override
		public void onSuccess(DeviceInfo result) {
			mDeviceinfo = result;
			mInfoAdapter.notifyDataSetChanged();
			mInfoLoadTask = getApp().getSysManager().getDeviceOwnerInfo(mContext, new SimpleLocalDataCallback<OwnerInfo>(){
				@Override
				public void onLocalDataLoaded(OwnerInfo data) {
					bindOwnerInfo(data);
				}

				@Override
				public void onOperateSuccess(OwnerInfo data) {
					bindOwnerInfo(data);
				}

				@Override
				public void onOperateError(Exception e) {
					dealWithException(e);
				}
			});
		}

		@Override
		public void onError(Exception e) {
			dealWithException(e);
		}

	}

	private class DeviceInfoListAdapter extends RecyclerView.Adapter<DeviceViewHolder>{

		@Override
		public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item_text2, parent, false);
			return new DeviceViewHolder(v);
		}

		@Override
		public void onBindViewHolder(DeviceViewHolder holder, int position) {
			if (mDeviceinfo == null) {
				return;
			}

			String label = null;
			String value = null;
			switch(position){
				case 0:
					label = getString(R.string.phone_type);
					value = mDeviceinfo.getPhoneType();
					break;
				case 1:
					label = getString(R.string.os_version);
					value = mDeviceinfo.getOSVersion();
					break;
				case 2:
					label = getString(R.string.resolution_ratio);
					value = mDeviceinfo.getScreenWidth() + "*" + mDeviceinfo.getScreeHeight();
					break;
				case 3:
					label = getString(R.string.density);
					value = mDeviceinfo.getDensity() + "";
					break;
				case 4:
					label = getString(R.string.mac_address);
					value = mDeviceinfo.getMacAddress();
					break;
				case 5:
					label = getString(R.string.sdk_version);
					value = mDeviceinfo.getSDKVersion() + "";
					break;
				case 6:
					label = getString(R.string.android_id);
					value = mDeviceinfo.getAndroidId();
					break;
				case 7:
					label = getString(R.string.serial_number);
					value = mDeviceinfo.getSerialNumber();
					break;
				case 8:
					label = getString(R.string.device_id);
					value = mDeviceinfo.getDeviceId();
					break;
			}

			holder.labelTv.setText(label);
			holder.valueTv.setText(value);
		}

		@Override
		public int getItemCount() {
			return mDeviceinfo == null ? 0 : 9;
		}

	}

	private class DeviceViewHolder extends RecyclerView.ViewHolder{

		TextView labelTv;
		TextView valueTv;

		public DeviceViewHolder(View itemView) {
			super(itemView);

			labelTv = (TextView)itemView.findViewById(android.R.id.text1);
			valueTv = (TextView)itemView.findViewById(android.R.id.text2);
		}
	}
}
