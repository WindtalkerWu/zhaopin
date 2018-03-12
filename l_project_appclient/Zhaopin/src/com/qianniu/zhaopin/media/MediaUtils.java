package com.qianniu.zhaopin.media;

import java.io.IOException;

import com.qianniu.zhaopin.app.common.DsLog;

import android.content.Context;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MediaUtils {

	public static final String TAG = "media.MediaUtils";

	private Context mContext;
	private MediaPlayer player = null;
	private MediaRecorder recorder = null;
	private AudioManager am = null;
	private Handler timeoutHandler = null;

	public MediaUtils(Context context, Handler handler) {
		this.mContext = context;
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		this.timeoutHandler = handler;
	}

	public void startPlaying(String fileName, boolean isSpeaker) {
		DsLog.v(TAG, "startPlaying, player:" + player + ", isSpeaker:"
				+ isSpeaker + ", fileName:" + fileName);
		if (player != null && player.isPlaying()) {
			stopPlaying();
		}
		if (player == null) {
			player = new MediaPlayer();
			player.setOnCompletionListener(completionListenernew);
		}
		try {
			DsLog.d(TAG, "startPlaying, player:" + player + " try");
			// player.setVolume(1, 1);
			player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
			if (!isSpeaker) {
				closeSpeaker(mContext);
			} else {
				openSpeaker(mContext);
			}
			player.setDataSource(fileName);
			player.prepare();
			player.start();
		} catch (IOException e) {
			DsLog.e2(TAG, "prepare() failed");
		}
	}

	private static int currVolume = -1;

	/**
	 * ��������
	 */
	private static void openSpeaker(Context mContext) {
		try {
			AudioManager audioManager = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_NORMAL);
			currVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

			DsLog.d(TAG, "before openSpeaker audioManager.isSpeakerphoneOn():"
					+ audioManager.isSpeakerphoneOn());
			if (!audioManager.isSpeakerphoneOn()) {
				// setSpeakerphoneOn() only work when audio mode set to
				// MODE_IN_CALL.
				audioManager.setMode(AudioManager.MODE_IN_CALL);
				audioManager.setSpeakerphoneOn(true);
				audioManager
						.setStreamVolume(
								AudioManager.STREAM_VOICE_CALL,
								audioManager
										.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
								AudioManager.STREAM_VOICE_CALL);
			}
			DsLog.d(TAG, "after openSpeaker audioManager.isSpeakerphoneOn():"
					+ audioManager.isSpeakerphoneOn());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ر�������
	 */
	public static void closeSpeaker(Context mContext) {
		try {
			AudioManager audioManager = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
			DsLog.d(TAG, "before closeSpeaker audioManager.isSpeakerphoneOn():"
					+ audioManager.isSpeakerphoneOn());
			if (audioManager != null && -1 != currVolume) {
				if (audioManager.isSpeakerphoneOn()) {
					audioManager.setSpeakerphoneOn(false);
					audioManager.setStreamVolume(
							AudioManager.STREAM_VOICE_CALL, currVolume,
							AudioManager.STREAM_VOICE_CALL);
				}
			}
			DsLog.d(TAG, "after closeSpeaker audioManager.isSpeakerphoneOn():"
					+ audioManager.isSpeakerphoneOn());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopPlaying() {
		player.release();
		player = null;
	}

	public void startRecording(String fileName) {
		if (recorder == null) {
			recorder = new MediaRecorder();
		}
		// ������ԴΪMicphone
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// ���÷�װ��ʽ
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		// ���ñ����ʽ
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(8000);

		recorder.setAudioEncodingBitRate(6400);
		DsLog.i("startRecording, fileName:" + fileName);
		recorder.setOutputFile(fileName);
		try {
			recorder.prepare();
		} catch (IOException e) {
			DsLog.e2(TAG, "prepare() failed");
		}
		DsLog.d(TAG, "Recorder prepared!");
		recorder.start();
		handler.sendEmptyMessageDelayed(10, 50 * 1000);
		DsLog.d(TAG, "Recorder start!");
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, "¼��ʣ��ʱ�� " + msg.what + "�룡", 800).show();
			if (msg.what-- != 0) {
				handler.sendEmptyMessageDelayed(msg.what, 1000);
			} else {
				timeoutHandler.sendEmptyMessage(0);
			}

		}

	};

	public void stopRecording() {
		recorder.stop();
		recorder.release();
		handler.removeMessages(10);
		recorder = null;
	}

	public void release() {
		if (player != null) {
			player.release();
			player = null;
		}
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}
	}

	private OnCompletionListener completionListenernew = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			player.release();
			player = null;
			DsLog.i(TAG, "mediaPlayer is onCompletion!");
		}

	};

}
