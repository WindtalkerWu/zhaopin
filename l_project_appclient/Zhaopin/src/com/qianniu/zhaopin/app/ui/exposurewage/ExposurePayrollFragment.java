package com.qianniu.zhaopin.app.ui.exposurewage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.ui.BaseFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ExposurePayrollFragment extends BaseFragment {
	
	private RelativeLayout ImageBg;
	private ImageView image;
	private Button uploadImage;
	private TextView cityTv;
	private EditText companyNameEt;
	private EditText postNameEt;
	private RelativeLayout salaryBg;
	private EditText salaryEt;
	private Button doSubmit;
	
	private PopupWindow imagePopupWindow;

	/** 通过图库获取图片 */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/** 照相机拍照获取图片 */
	private static final int CAMERA_WITH_DATA = 3022;
	/**
	 * 使用照相机拍摄照片作为头像时会使用到这个路径
	 */
	private static final int ICON_SIZE = 100;
	public static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/matrix/Camera");
	private File mCurrentPhotoFile;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MyLogger.i(getTag(), "onCreateView");
		View view = inflater.inflate(R.layout.fragment_exposure_payroll, null);
		
		initView(view);
		
		return view;
	}
	private void initView(View view) {
		ImageBg = (RelativeLayout) view.findViewById(R.id.exposure_payroll_image_item);
		image = (ImageView) view.findViewById(R.id.exposure_payroll_image);
		uploadImage = (Button) view.findViewById(R.id.exposure_payroll_image_upload);
		cityTv = (TextView) view.findViewById(R.id.exposure_wage_city);
		companyNameEt = (EditText) view.findViewById(R.id.exposure_wage_company);
		postNameEt = (EditText) view.findViewById(R.id.exposure_wage_post);
		salaryBg = (RelativeLayout) view.findViewById(R.id.exposure_wage_salary_item);
		salaryBg.setVisibility(View.GONE);
		doSubmit = (Button) view.findViewById(R.id.exposure_wage_dosubmit);
		
		setListener();
	}
	private void setListener() {
		uploadImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPhotoSelectPopwindow();
			}
		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != getActivity().RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {

			final Bitmap photo = data.getParcelableExtra("data");
			image.setBackgroundDrawable(new BitmapDrawable(photo));
			image.setPadding(0, 0, 0, 0);
//			image.setImageBitmap(photo);
			break;
		}
		case CAMERA_WITH_DATA: {
			doCropPhoto(mCurrentPhotoFile);
			break;
		}

		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void showPhotoSelectPopwindow() {
		if (imagePopupWindow == null) {
			View contentView = LayoutInflater.from(getActivity().getApplicationContext())
					.inflate(R.layout.popup_window_image_select, null);
			imagePopupWindow= new PopupWindow(contentView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			imagePopupWindow.setFocusable(true);
			imagePopupWindow.setAnimationStyle(R.anim.dialog_rotate);
			imagePopupWindow.setOutsideTouchable(false);

			ColorDrawable cw = new ColorDrawable(Color.parseColor("#00000000"));
			imagePopupWindow.setBackgroundDrawable(cw);

			Button localbtn = (Button) contentView.findViewById(R.id.local_picture);
			localbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					doPickPhotoFromGallery();
					imagePopupWindow.dismiss();
				}
			});
			Button takepohtobtn = (Button) contentView
					.findViewById(R.id.take_picture);
			takepohtobtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					doTakePhoto();
					imagePopupWindow.dismiss();
				}
			});

			Button cancelBtn = (Button) contentView.findViewById(R.id.cancel);
			cancelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imagePopupWindow.dismiss();
				}
			});
		}
		imagePopupWindow.showAtLocation(uploadImage, Gravity.BOTTOM, 0, 0);

	}
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {

		}
	}
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
		}
	}
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	protected void doCropPhoto(File f) {
		try {
			// Add the image to the media store
			MediaScannerConnection.scanFile(getActivity(),
					new String[] { f.getAbsolutePath() },
					new String[] { null }, null);

			// Launch gallery to crop the photo
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {

		}
	}

	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}
}
