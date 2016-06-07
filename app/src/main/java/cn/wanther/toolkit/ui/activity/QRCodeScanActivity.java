package cn.wanther.toolkit.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.WindowManager;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.R;
import cn.wanther.toolkit.component.async.AsyncTazk;
import cn.wanther.toolkit.ui.view.CameraView;
import cn.wanther.toolkit.ui.view.QRScanOverlay;
import cn.wanther.toolkit.utils.Utils;

import java.util.concurrent.Future;

public class QRCodeScanActivity extends BaseActivity implements Camera.PreviewCallback {

	private static final String TAG = "QRCodeScanActivity";

	public static int REQ_CODE_SCAN_QRCODE = 1;
	public static String KEY_SCAN_RESULT = "result";

	private Reader mReader;
	private Future<?> mDecodeTask;
	private boolean mIsResumed;

	private CameraView mCameraView;
	private QRScanOverlay mScanOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.qrcode_scan);

		mCameraView = (CameraView)findViewById(R.id.camera_view);
		mScanOverlay = (QRScanOverlay)findViewById(R.id.scan_overlay);

		mReader = new QRCodeReader();

	}

	@Override
	protected void onResume() {
		super.onResume();

		mIsResumed = true;

		mCameraView.openCamera();
		mCameraView.requestPreview();
		mCameraView.requestPreviewFrame(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mIsResumed = false;

		Utils.cancelTask(mDecodeTask, true);

		mCameraView.stopPreview();
		mCameraView.releaseCamera();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Utils.cancelTask(mDecodeTask, true);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (!mIsResumed || (mDecodeTask != null && !mDecodeTask.isDone())) {
			return;
		}

		Rect winRect = mScanOverlay.getWinRect();
		int winLen = winRect.width();

		int vWidth = mScanOverlay.getWidth();
		int vHeight = mScanOverlay.getHeight();

		if (winLen <= 0 || vWidth <= 0 || vHeight <= 0) {
			return;
		}

		Camera.Parameters params = camera.getParameters();
		Camera.Size size = params.getPreviewSize();

		mDecodeTask = new DecodeTask(data, size.width, size.height, vWidth, vHeight, winRect, mReader).execute(getApp().getShortTimeExecutor());
	}

	@Override
	protected String getPageName() {
		return "QRCodeScan";
	}

	private class DecodeTask extends AsyncTazk<Result> {

		private byte[] mData;
		private int mDataWidth;
		private int mDataHeight;
		private int mViewWidth;
		private int mViewHeight;
		private Rect mWinRect;
		private Reader mReader;

		//private int mPFormat;

		public DecodeTask(byte[] data, int dataWidth, int dataHeight, int vWidth, int vHeight, Rect winRect, Reader reader) {
			mData = data;
			mDataWidth = dataWidth;
			mDataHeight = dataHeight;
			mViewWidth = vWidth;
			mViewHeight = vHeight;
			mWinRect = winRect;
			mReader = reader;
		}

//		public DecodeTask(byte[] data, int dataWidth, int dataHeight, int vWidth, int vHeight, Rect winRect, Reader reader, int pFormat) {
//			this(data, dataWidth, dataHeight, vWidth, vHeight, winRect, reader);
//			mPFormat = pFormat;
//		}

		@Override
		protected Result doInBackground() throws Exception {

			float scaleX = 1f * mDataHeight / mViewWidth;
			float scaleY = 1f * mDataWidth / mViewHeight;

			int realLeft = (int)(mWinRect.left * scaleX);
			int realTop = (int)(mWinRect.top * scaleY);
			int realWidth = (int)(mWinRect.width() * scaleX);
			int realHeight = (int)(mWinRect.height() * scaleY);

			// 因为旋转了90度，传参需要注意
			PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(mData, mDataWidth, mDataHeight, realTop, mDataHeight - realLeft - realWidth, realHeight, realWidth, false);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

//			YuvImage image = new YuvImage(mData, mPFormat, mDataWidth, mDataHeight, null);
//			ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
//			image.compressToJpeg(new Rect(realTop, mDataHeight - realLeft - realWidth, realTop + realHeight, mDataHeight - realLeft), 100, os);
//
//			byte[] data = os.toByteArray();
//			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//			mScanOverlay.drawBitmap(bmp);

			return mReader.decode(bitmap);
		}

		@Override
		protected void onSuccess(Result result) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, result.getText());
			}

			Intent resultIntent = new Intent();
			resultIntent.putExtra(KEY_SCAN_RESULT, result.getText());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}

		@Override
		protected void onError(Exception e) {
			mCameraView.requestPreviewFrame(QRCodeScanActivity.this);
		}

		@Override
		protected void onFinished() {
			mData = null;
			mWinRect = null;
			mReader = null;
		}
	}
}
