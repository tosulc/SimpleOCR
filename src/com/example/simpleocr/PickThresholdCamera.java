package com.example.simpleocr;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class PickThresholdCamera extends Activity implements
		SurfaceHolder.Callback {
	public ImageView iv_image;
	private SurfaceView sv;
	public Bitmap bmp;
	private SurfaceHolder sHolder;
	private Camera mCamera;
	private Parameters parameters;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		iv_image = (ImageView) findViewById(R.id.imageView1);
		sv = (SurfaceView) findViewById(R.id.surfaceView);
		sHolder = sv.getHolder();
		sHolder.addCallback(this);
		// tells Android that this surface will have its data constantly replaced
		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// get camera parameters
		parameters = mCamera.getParameters();

		// set camera parameters
		mCamera.setParameters(parameters);
		mCamera.startPreview();

		// sets what code should be executed after the picture is taken
		Camera.PictureCallback mCall = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				releaseCamera();
				goBack();
			}
		};

		mCamera.takePicture(null, null, mCall);
	}

	public void goBack() {
		Intent i = new Intent(getApplicationContext(),
				PickThresholdActivity.class);
		startActivity(i);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}
}