package cn.wanther.toolkit.utils;

import java.util.List;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.exception.HardwareException;

public class DeviceUtil {
	public static boolean hasFlashLight() {
		return App.Instance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	public static void openFlashLight(Camera camera) {
		if(camera == null || !hasFlashLight()){
			return;
		}
		
		Camera.Parameters params = camera.getParameters();
		if(Camera.Parameters.FLASH_MODE_ON.equals(params.getFlashMode())
				|| Camera.Parameters.FLASH_MODE_TORCH.equals(params.getFlashMode())){
			return;
		}
		
		List<String> flashModes = params.getSupportedFlashModes();
		if(flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)){
			params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		}else{
			params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
		}
		
		camera.setParameters(params);
		
	}

	public static void closeFlashLight(Camera camera) {
		if(camera == null || !hasFlashLight()){
			return;
		}
		
		Camera.Parameters params = camera.getParameters();
		if(!Camera.Parameters.FLASH_MODE_OFF.equals(params.getFlashMode())){
			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		}
		
		camera.setParameters(params);
	}
	
	public static boolean hasCamera() {
		return App.Instance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
	
	public static Camera openCamera() throws HardwareException {
		try{
			Camera camera = Camera.open();
			
			return camera;
			
		}catch(Exception e){
			throw new HardwareException("open camera failed " + e.getMessage(), e);
		}
		
	}

	public static boolean hasExternalStorage() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
}
