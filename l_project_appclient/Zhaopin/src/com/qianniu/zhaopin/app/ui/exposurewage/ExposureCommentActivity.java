package com.qianniu.zhaopin.app.ui.exposurewage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.ui.BaseActivity;

public class ExposureCommentActivity extends BaseActivity {

	private ImageView back;
	private EditText content;
	private ImageView titleSubmit;
	private Button doSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exposure_comment);

		initView();

		setListener();
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.exposure_comment_goback);
		content = (EditText) findViewById(R.id.exposure_comment_content);
		titleSubmit = (ImageView) findViewById(R.id.exposure_comment_title_submit);
		doSubmit = (Button) findViewById(R.id.exposure_comment_dosubmit);
	}

	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		doSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

	}

}
