package com.example.takephotoapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class PhotoSaveServis extends Service {

	private final IBinder DownloadServisBinder = new LocalBinder();
	private String temporalFileName = "temporalPhoto.png";
	private String temporalPhotoPath = null;

	private File image;
	private File temporalImage;

	private File saveFileDirectory;
	private File cacheDirectory;

	private OutputStream output;
	private InputStream input;

	public class LocalBinder extends Binder {
		PhotoSaveServis getService() {
			return PhotoSaveServis.this;
		}
	}

	public void savePhotoFile(String name) throws IOException {
		if (temporalPhotoPath != null) {
			saveFileDirectory = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			saveFileDirectory.mkdir();
			image = new File(saveFileDirectory + File.separator + name + ".png");
			try {
				input = new FileInputStream(temporalImage);
				output = new FileOutputStream(image);

				byte[] bufphoto = new byte[1024];
				int count;
				while ((count = input.read(bufphoto)) > 0)
					output.write(bufphoto, 0, count);

			} catch (Exception e) {

			} finally {
				input.close();
				output.close();
			}

		} else {
			Toast.makeText(getApplicationContext(), "First make a photo",
					Toast.LENGTH_SHORT).show();
		}
	}

	public File createTemporalFile() throws IOException {
		cacheDirectory = getExternalCacheDir();
		cacheDirectory.mkdir();
		temporalImage = new File(cacheDirectory + File.separator
				+ temporalFileName);
		temporalPhotoPath = temporalImage.getAbsolutePath();

		return temporalImage;
	}

	public String getPhotoPath() {
		return temporalPhotoPath;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return DownloadServisBinder;
	}

}
