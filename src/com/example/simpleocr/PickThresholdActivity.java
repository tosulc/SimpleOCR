package com.example.simpleocr;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PickThresholdActivity extends Activity {
	public static final String PACKAGE_NAME = "com.example.simpleocr";
	public static final String PREFS_NAME = "SimpleOCRSettings";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/SimpleOCR/";

	public static final String lang = "eng";

	public int threshold_value;

	protected Button button_ocr, button_reset, button_set;
	protected EditText et_threshold_value;

	protected EditText et_ocr_text;
	protected String jpg_path = DATA_PATH + "/ThresholdPicture.jpg",
			cropped_path;
	protected boolean taken_picture;

	protected static final String PHOTO_TAKEN = "photo_taken";
	private Uri outputFileUri;
	final int PIC_TAKEN = 1;
	public ImageView iv_show_taken_picture;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.pick_threshold);

		iv_show_taken_picture = (ImageView) findViewById(R.id.iv_picture_taken_show);
		et_threshold_value = (EditText) findViewById(R.id.et_threshold_value);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		et_threshold_value.setText(String.valueOf(settings.getInt("threshold",
				128)));

		File file = new File(jpg_path);
		if (file.exists()) {
			setPreviewImage();
		}

		button_ocr = (Button) findViewById(R.id.button_take_picture);
		button_ocr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startCameraActivity();
			}
		});

		button_reset = (Button) findViewById(R.id.button_reset);
		button_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				File file = new File(jpg_path);
				if (file.exists()) {
					setPreviewImage();
				}
			}
		});

		button_set = (Button) findViewById(R.id.button_done);
		button_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				threshold_value = Integer.parseInt(et_threshold_value.getText()
						.toString());
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("threshold", threshold_value);
				editor.commit();
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
				finish(); 
			}
		});

	}

	protected void startCameraActivity() {
		File file = new File(jpg_path);
		outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, PIC_TAKEN);
	}

	public void setPreviewImage() {
		threshold_value = Integer.parseInt(et_threshold_value.getText()
				.toString());
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 18;
		Bitmap bitmap = BitmapFactory.decodeFile(jpg_path, options);
		bitmap = createBlackAndWhite(bitmap, threshold_value);
		iv_show_taken_picture.setImageBitmap(bitmap);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PIC_TAKEN) {
			onPhotoTaken();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(PickThresholdActivity.PHOTO_TAKEN, taken_picture);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.getBoolean(PickThresholdActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		taken_picture = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 18;

		Bitmap bitmap = BitmapFactory.decodeFile(jpg_path, options);

		bitmap = createBlackAndWhite(bitmap, 128); // TODO: set treshold from settings!
		iv_show_taken_picture = (ImageView) findViewById(R.id.iv_picture_taken_show);
		iv_show_taken_picture.setImageBitmap(bitmap);

	}

	public static Bitmap createBlackAndWhite(Bitmap src, int threshold) {
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;
		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				int gray = (int) (0.299 * R + 0.587 * G + 0.114 * B);

				if (gray > threshold)
					gray = 255; // white
				else
					gray = 0; // black
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
			}
		}
		return bmOut;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		iv_show_taken_picture = (ImageView) findViewById(R.id.iv_picture_taken_show);
		et_threshold_value = (EditText) findViewById(R.id.et_threshold_value);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		et_threshold_value.setText(String.valueOf(settings.getInt("threshold",
				128)));
	}

}
