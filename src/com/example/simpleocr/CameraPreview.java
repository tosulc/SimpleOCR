package com.example.simpleocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private PreviewCallback pcb;

	public static Timer timer;
	private String path;
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/SimpleOCR/";

	public static final String lang = "eng";
	public EditText et_tekst_sa_slike;
	Bitmap resizedBitmap, rotated, snowBitmap;
	public long interval;
	static int color_for_tolerance, tolerance_black_white = 0;
	public int threshold_value;

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera, PreviewCallback pcb,
			long interval, int threshold_value) {
		super(context);
		mCamera = camera;
		this.pcb = pcb;
		this.interval = interval;
		this.threshold_value = threshold_value;

		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	final Camera.PictureCallback mCall = new Camera.PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {

			/*Saving picture to sd card*/
			path = Environment.getExternalStorageDirectory().toString()
					+ "/SimpleOCR/Originalna_slika.jpg";
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(path);
				outStream.write(data);
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 18; // Resize more? Maybe to fixed size for cameras with >12MP.
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			String path = Environment.getExternalStorageDirectory().toString()
					+ "/SimpleOCR/";
			OutputStream fOut = null;
			File file = new File(path, "Bitmap.png");
			try {
				fOut = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			// bitmap = toGrayscale(bitmap);
			bitmap = createBlackAndWhite(bitmap, threshold_value);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true); // Tessaract algorithm needs ARGB_8888

			TessBaseApiCall(bitmap);

			// this has to be in the end
			mCamera.stopPreview();
			try {
				mCamera.setPreviewDisplay(mHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.setPreviewCallback(pcb);
			mCamera.startPreview();

		}
	};

	/**
	 * Every pixel from a given bitmap turns in to black/white representation.
	 * Gets pixel gray value, if it's > threshold_value -> white, else black.
	 * @param src
	 * @param threshold_value
	 * @return black&white bitmap
	 */
	public static Bitmap createBlackAndWhite(Bitmap src, int threshold_value) {
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

				if (gray > threshold_value)
					gray = 255; // white
				else
					gray = 0; // black
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
			}
		}
		return bmOut;
	}

	public Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);

		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);

		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			timer = new Timer();

			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					mCamera.takePicture(null, null, mCall);
				}
			}, interval, interval);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCameraAndSurface();
		timer.cancel();
	}

	public void releaseCameraAndSurface() {
		mCamera.stopPreview();
		mCamera.setPreviewCallback(null);
		mCamera.release(); // release the camera for other applications
		mCamera = null;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mHolder.getSurface() == null) {
			return;
		}

		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setRotation(0);
			// parameters.setFlashMode("FLASH_MODE_AUTO");

			// parameters.setColorEffect("mono");
			mCamera.setParameters(parameters);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setPreviewCallback(pcb);
			// mCamera.setDisplayOrientation(90);
			mCamera.startPreview();

		} catch (Exception e) {
			mCamera.stopPreview();
			e.printStackTrace();
		}
	}

	public void TessBaseApiCall(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text();
		//Log.v(TAG, "Raw OCR text: " + recognizedText);
		baseApi.end();

		if (lang.equalsIgnoreCase("eng")) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
			// recognizedText = recognizedText.replaceAll("[^0-9\\:]+", " "); for numbers only
		}

		recognizedText = recognizedText.trim();
		final Toast toast = Toast.makeText(getContext(), recognizedText,
				Toast.LENGTH_SHORT);

		if (recognizedText.length() != 0) {
			toast.show();

			/* Here comes the code to transfer recognizedText to anything */

		}
	}

}