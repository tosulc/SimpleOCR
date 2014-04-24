package com.example.simpleocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "SimpleOCRSettings";
	Context context;
	Button btn_ocr, btn_pick_threshold;
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	EditText et_minutes, et_seconds;
	TextView tv_threshold_value;
	int minutes, seconds, threshold_value;
	long interval;
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/SimpleOCR/";

	public static final String lang = "eng";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.main);

		// Needed for Tessaract library to know how to translate numbers/words from picture.
		// lang.traineddata file with the app (in assets folder). You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return;
				} 
			}

		}

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

			} catch (IOException e) {
			}
		}

		et_minutes = (EditText) findViewById(R.id.et_minutes);
		et_seconds = (EditText) findViewById(R.id.et_seconds);
		tv_threshold_value = (TextView) findViewById(R.id.tv_threshold_value);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		et_minutes.setText(String.valueOf(settings.getInt("minutes", 0)));
		et_seconds.setText(String.valueOf(settings.getInt("seconds", 20)));
		tv_threshold_value.setText(String.valueOf(settings.getInt("threshold",
				128)));

		btn_ocr = (Button) findViewById(R.id.btn_ocr);
		btn_ocr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (et_minutes.getText().toString().equals("")
						| et_seconds.getText().toString().equals("")) {
					Toast.makeText(context, "No number entry!",
							Toast.LENGTH_SHORT).show();
				} else {
					minutes = Integer.parseInt(et_minutes.getText().toString());
					seconds = Integer.parseInt(et_seconds.getText().toString());
					threshold_value = Integer.parseInt(tv_threshold_value
							.getText().toString());

					if (seconds < 10 && minutes <= 0) {
						Toast.makeText(context,
								"You need to specify at least 10sec.",
								Toast.LENGTH_SHORT).show();
					} else {

						SharedPreferences settings = getSharedPreferences(
								PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putInt("minutes", minutes);
						editor.putInt("seconds", seconds);
						editor.putInt("threshold", threshold_value);
						editor.commit();

						interval = (minutes * 60 * 1000) + (seconds * 1000);

						Intent i = new Intent(context, SimpleOCRActivity.class);
						i.putExtra("interval", interval);
						startActivity(i);
					}
				}
			}
		});

		btn_pick_threshold = (Button) findViewById(R.id.btn_pick_threshold);
		btn_pick_threshold.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, PickThresholdActivity.class);
				startActivity(i);
			}
		});

	}// onCreate

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		et_minutes.setText(String.valueOf(settings.getInt("minutes", 0)));
		et_seconds.setText(String.valueOf(settings.getInt("seconds", 20)));
		tv_threshold_value.setText(String.valueOf(settings.getInt("threshold",
				128)));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
