package cn.wanther.toolkit.utils;

import java.lang.reflect.Method;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;

public class Compat {
	
	public static void setCameraFocusMode(Camera.Parameters params){
		List<String> focusModes = params.getSupportedFocusModes();
		
		if(focusModes == null){	// <--- device no camera
			return;
		}
		
		if(hasICE_CREAM_SANDWICH()){
			if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
		}else if(hasGINGERBREAD()){
			if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
		} else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
			if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static Point getScreenSize(Context context){
		Point size = new Point();
		
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		if(hasJELLY_BEAN_MR1()){
			display.getRealSize(size);
		}else if(hasICE_CREAM_SANDWICH()){
			try {
				Method getRawWidth = Display.class.getMethod("getRawWidth");
				Method getRawHeight = Display.class.getMethod("getRawHeight");
				size.x = (Integer)getRawWidth.invoke(display);
				size.y = (Integer)getRawHeight.invoke(display);
			} catch (Exception e) {
				size.x = display.getWidth();
				size.y = display.getHeight();
			}
		}else{
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
		
		return size;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getSharedPreferences(Context context,
			String fileName) {
		if (hasHONEYCOMB()) {
			return context.getSharedPreferences(fileName, Context.MODE_PRIVATE
					| Context.MODE_MULTI_PROCESS);
		} else {
			return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public static void removeOnGlobalLayoutListener(View v,
			OnGlobalLayoutListener listener) {
		if (hasJELLY_BEAN()) {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public static void setBackground(View view, Drawable drawable) {
		if (hasJELLY_BEAN()) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
	}
	
	public static int getOSSDKVersion(){
		return android.os.Build.VERSION.SDK_INT;
	}
	
	/**
	 * Android 1.6, SDK_INT=4
	 * @return
	 */
	public static boolean hasDONUT(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.DONUT;
	}
	
	/**
	 * Android 2.0 SDK_INT=5
	 * @return
	 */
	public static boolean hasECLAIR(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.ECLAIR;
	}
	
	/**
	 * Android 2.0.1 SDK_INT=6
	 * @return
	 */
	public static boolean hasECLAIR_0_1(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.ECLAIR_0_1;
	}
	
	/**
	 * Android 2.1 SDK_INT=7
	 * @return
	 */
	public static boolean hasECLAIR_MR1(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.ECLAIR_MR1;
	}
	
	/**
	 static  2.2 SDK_INT=8
	 * @return
	 */
	public static boolean hasFROYO(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.FROYO;
	}
	
	/**
	 * Android 2.3 SDK_INT=9
	 * @return
	 */
	public static boolean hasGINGERBREAD(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.GINGERBREAD;
	}
	
	/**
	 * Android 2.3.3 SDK_INT=10
	 * @return
	 */
	public static boolean hasGINGERBREAD_MR1(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
	}
	
	/**
	 * Android 3.0 SDK_INT=11
	 * @return
	 */
	public static boolean hasHONEYCOMB(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.HONEYCOMB;
	}
	
	/**
	 * Android 3.1 SDK_INT=12
	 * @return
	 */
	public static boolean hasHONEYCOMB_MR1(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
	}
	
	/**
	 * Android 3.2 SDK_INT=13
	 * @return
	 */
	public static boolean hasHONEYCOMB_MR2(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2;
	}
	
	/**
	 * Android 4.0,4.0.1,4.0.2 SDK_INT=14
	 * @return
	 */
	public static boolean hasICE_CREAM_SANDWICH(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
	
	/**
	 * Android 4.0.3,4.0.4 SDK_INT=15
	 * @return
	 */
	public static boolean hasICE_CREAM_SANDWICH_MR1(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
	}
	
	/**
	 * Android 4.1,4.1.1 SDK_INT=16
	 * @return
	 */
	public static boolean hasJELLY_BEAN(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.JELLY_BEAN;
	}
	
	/**
	 * Android 4.2 SDK_INT=17
	 * @return
	 */
	public static boolean hasJELLY_BEAN_MR1(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
	}
	
	/**
	 * Android 4.3 SDK_INT=18
	 * @return
	 */
	public static boolean hasJELLY_BEAN_MR2(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
	}
	
	/**
	 * Android 4.4 SDK_INT=19
	 * @return
	 */
	public static boolean hasKITKAT(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.KITKAT;
	}
	
	/**
	 * Android 5.0 SDK_INT=21
	 * @return
	 */
	public static boolean hasLOLIPOP(){
		return getOSSDKVersion() >= android.os.Build.VERSION_CODES.LOLLIPOP;
	}

}


