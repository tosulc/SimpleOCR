package com.example.simpleocr;

import java.util.Timer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SimpleOCRActivity extends Activity {

	private static Camera mCamera;
	private CameraPreview mPreview;
	public Context context;
	public TextView tv_tekst_sa_slike;
	public SharedPreferences settings;
	public FrameLayout preview;
	public static final String PREFS_NAME = "SimpleOCRSettings";
	public static final String PACKAGE_NAME = "com.example.simpleocr";
	protected Button button_ocr;
	protected EditText et_ocr_text;
	protected String jpg_path, cropped_path;
	protected boolean taken_picture;

	protected static final String PHOTO_TAKEN = "photo_taken";

	public static Timer timer;

	public int threshold_value;

	TextView tv_postotak_first_color, tv_postotak_second_color;
	TextView tv_vrijeme_sad;
	Button btn_povecaj;
	public long interval;

	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {

		interval = getIntent().getExtras().getLong("interval");
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		threshold_value = settings.getInt("threshold", 128);

		super.onCreate(savedInstanceState);
		context = this;

		// code for hiding status bar
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {

			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
			ActionBar actionBar = getActionBar();
			actionBar.hide();
		}
		setContentView(R.layout.ocr_layout);
		mCamera = getCameraInstance();

		mPreview = new CameraPreview(this, mCamera, null, interval,
				threshold_value);
		preview = (FrameLayout) findViewById(R.id.surfaceView);
		preview.addView(mPreview);

	}

	/* for date and time formating and showing
	String strDate;
	Calendar c = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat(
			"dd:MMMM:yyyy HH:mm:ss a");
	strDate = sdf.format(c.getTime());*/

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	@Override
	public void onResume() {
		super.onResume();
		mCamera = getCameraInstance();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}