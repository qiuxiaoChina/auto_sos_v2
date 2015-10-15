package com.autosos.yd.util;

import android.os.Message;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;

public class MyUtils {

	public static void istartAnimOut(ImageView view) {
		
		istartAnimOut(view, 0);
		
	}

	public static void istartAnimIn(ImageView view) {

		RotateAnimation animation = new RotateAnimation(345, 375, view.getWidth()/2, view.getHeight());
		animation.setDuration(1000);
		animation.setFillAfter(true);
//		animation.setRepeatCount(30);
		view.startAnimation(animation);
		
	}

	public static void istartAnimOut(ImageView view, long offset) {
		RotateAnimation animation = new RotateAnimation(360, 0, view.getWidth()/2, view.getHeight()/2);
		animation.setDuration(1000);
		animation.setFillAfter(true);
		animation.setRepeatCount(50);
		animation.setStartOffset(offset);
		view.startAnimation(animation);
	}

	public static void getNetImageBytes(String path, android.os.Handler handler){
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLconnection = (HttpURLConnection) url.openConnection();
			httpURLconnection.setRequestMethod("GET");
			httpURLconnection.setReadTimeout(6 * 1000);
			InputStream in = null;
			byte[] b = new byte[1024];
			int len = -1;
			if (httpURLconnection.getResponseCode() == 200) {
				in = httpURLconnection.getInputStream();
				byte[] result = readStream(in);
				in.close();
				Message message = new Message();
				message.what = 1;
				message.obj = result;
				handler.sendMessage(message);

			}
		}catch (Exception e ){
			e.printStackTrace();
		}
	}
	public static byte[] readStream(InputStream in) throws Exception{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}
}
