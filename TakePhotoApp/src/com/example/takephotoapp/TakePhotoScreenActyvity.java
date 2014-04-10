package com.example.takephotoapp;


import java.io.File;
import java.io.IOException;



import com.example.takephotoapp.PhotoSaveServis.LocalBinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class TakePhotoScreenActyvity extends Activity {
	private Button takePhotoButton;
	private ImageView photoView;
	private PhotoSaveServis photoSaveService;
	private Boolean bound = false;
	private static final int REQUEST_TAKE_PHOTO = 1;
	private final int DIALOG_SAVE = 1;
	
	private String photoPath;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_take_photo_screen);
		takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
		photoView = (ImageView) findViewById(R.id.photoView);

		Intent intentPhotoServis = new Intent(this, PhotoSaveServis.class);
		startService(intentPhotoServis);
		bindService(intentPhotoServis, mConnection, Context.BIND_AUTO_CREATE);

		if (photoPath != null) {
			setPhotoImageView();
		}

		takePhotoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					takePictureIntent();
				} catch (Exception e) {
				}
			}
		});
	}

	private void takePictureIntent() throws IOException {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

			File photoFile = photoSaveService.createTemporalFile();
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setPhotoImageView();
	}

	private void setPhotoImageView() {
		photoPath = photoSaveService.getPhotoPath();
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		photoView.setImageBitmap(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.take_photo_screen_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_save_photo) {
			showDialog(DIALOG_SAVE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_SAVE) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);

			adb.setTitle(R.string.get_name_photo);
			final EditText input = new EditText(this);
			adb.setView(input);
			adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					fileName = input.getText().toString();
				     try {
						photoSaveService.savePhotoFile(fileName);							
					} catch ( IOException e) {
						e.printStackTrace();
					}
				}
			});

			return adb.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (bound) {
			unbindService(mConnection);
			bound = false;
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;

			photoSaveService = binder.getService();
			bound = true;

			photoPath = photoSaveService.getPhotoPath();
			if (photoPath != null) {
				setPhotoImageView();
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
	};
}
