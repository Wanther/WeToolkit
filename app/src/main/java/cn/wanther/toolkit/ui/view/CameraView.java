package cn.wanther.toolkit.ui.view;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.exception.HardwareException;
import cn.wanther.toolkit.utils.Compat;
import cn.wanther.toolkit.utils.DeviceUtil;


public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CameraView";
	
	// -- constructors
	public CameraView(Context context) {
		super(context);
		initialize();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	// -- private fields
	private Camera mCamera;
	private boolean mSurfaceCreated;
	private boolean mPreviewRequested;

	@SuppressWarnings("deprecation")
	protected void initialize() {
		if (!Compat.hasHONEYCOMB()) {
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		getHolder().addCallback(this);
	}

	/**
	 * 判断设备是否具备闪光灯
	 * 
	 * @author Nano
	 * @return
	 */
	public boolean hasFlashLight() {
		return DeviceUtil.hasFlashLight();
	}

	/**
	 * 打开闪光灯
	 * 
	 * @author Nano
	 * @return 是否成功打开。如果没有找到相机设备，返回{@code false}。 若之前已经打开闪光，返回{@code false}。
	 */
	public boolean openFlashLight() {
		if (mCamera == null)
			return false;
		Camera.Parameters params = mCamera.getParameters();
		if (Camera.Parameters.FLASH_MODE_OFF.equals(params.getFlashMode())) {
			DeviceUtil.openFlashLight(mCamera);
			return true;
		}
		// flash light already opened
		return false;
	}

	/**
	 * 关闭闪光灯
	 * 
	 * @author Nano
	 * @return 是否成功关闭。如果没有找到相机设备，返回{@code false}。 若之前闪光已经关闭，返回{@code false}。
	 */
	public boolean closeFlashLight() {
		if (mCamera == null)
			return false;
		Camera.Parameters params = mCamera.getParameters();
		String flashMode = params.getFlashMode();
		// close flash light
		if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)
				|| Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
			DeviceUtil.closeFlashLight(mCamera);
			return true;
		}
		// flash light already closed
		return false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceCreated = true;
		startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceCreated = false;
	}

	public void openCamera() {
		if (mCamera == null) {
			// TODO:
			try {
				mCamera = DeviceUtil.openCamera();
				mCamera.setDisplayOrientation(90);
			} catch (HardwareException e) {
				if(BuildConfig.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
				Toast.makeText(App.Instance(), "打开相机失败", Toast.LENGTH_SHORT)
					.show();
			}
			
		}
	}
	
	public void requestPreview(){
		mPreviewRequested = true;
		startPreview();
	}
	
	public void startPreview() {
		
		if (mCamera == null || !mSurfaceCreated || !mPreviewRequested) {
			return;
		}
		
		// Camera ready & Surface ready & preview requested

		try {
			Camera.Parameters params = mCamera.getParameters();

			int vWidth = getWidth();
			int vHeight = getHeight();

			// picture size
			// vertical
			if (vHeight < 1024) {
				vHeight = 1024;
			}
			Size picSize = getCameraPictureSize(mCamera, vHeight, vWidth);
			params.setPictureSize(picSize.width, picSize.height);

			// format
			params.setPictureFormat(ImageFormat.JPEG);
			
			// auto focus
			Compat.setCameraFocusMode(params);
			
			mCamera.setParameters(params);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "vWidth=" + vWidth + ",vHeight=" + vHeight
						+ ",picWidth=" + params.getPictureSize().width
						+ ",picHeight=" + params.getPictureSize().height);
			}

			mCamera.setPreviewDisplay(getHolder());
			mCamera.startPreview();
			
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

	}
	
	public void manualFocus(final AutoFocusCallback ac){
		if(mCamera == null){
			return;
		}
		
		Camera.Parameters params = mCamera.getParameters();
		List<String> focusModes = params.getSupportedFocusModes();
		String focusMode = params.getFocusMode();

		if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)
				&& !Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)){
			
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(params);
		}
		
		focusMode = params.getFocusMode();
		
		if(Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)){
			mCamera.cancelAutoFocus();
			mCamera.autoFocus(ac);
		}
		
	}
	
	public void stopPreview() {
		if (mCamera != null) {
			try{
				getHolder().removeCallback(this);
				mCamera.stopPreview();
				mPreviewRequested = false;
			} catch (Exception e) {
				if(BuildConfig.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
	}

	public void releaseCamera() {
		if (mCamera != null) {
			try{
				mCamera.release();
			} catch (Exception e) {
				if(BuildConfig.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			
			mCamera = null;
		}
	}
	
	public void takePicture(Camera.ShutterCallback shutterCallback,
			Camera.PictureCallback jpeg) {
		if (mCamera != null) {
			mCamera.takePicture(shutterCallback, null, jpeg);
		}
		// TODO: no camera
	}
	
	public void requestPreviewFrame(PreviewCallback previewCallback){
		if(mCamera != null){
			mCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	protected Size getCameraPictureSize(Camera camera, int dWidth, int dHeight) {
		Camera.Parameters params = camera.getParameters();

		List<Size> supportedSizeList = params.getSupportedPictureSizes();
		Size nearestBiggerSize = null;
		int minBiggerDist = Integer.MAX_VALUE;
		Size nearestSmallSize = null;
		int minSmallerDist = Integer.MIN_VALUE;
		for (Size size : supportedSizeList) {
			int dist = dWidth - size.width;
			if (dist >= 0) {
				if (minBiggerDist < dist) {
					minBiggerDist = dist;
					nearestBiggerSize = size;
				}
			} else {
				if (minSmallerDist < dist) {
					minSmallerDist = dist;
					nearestSmallSize = size;
				}
			}
		}
		if (nearestBiggerSize != null) {
			return nearestBiggerSize;
		}
		if (nearestSmallSize != null) {
			return nearestSmallSize;
		}

		return params.getPictureSize();
	}

}
