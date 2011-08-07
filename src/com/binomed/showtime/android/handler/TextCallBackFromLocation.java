package com.binomed.showtime.android.handler;

import android.os.Handler;
import android.os.Message;

import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech;

public class TextCallBackFromLocation extends Handler {

	private AutoCompleteTextWithSpeech autoCompleteText;

	private String cityName;

	public TextCallBackFromLocation(AutoCompleteTextWithSpeech autoCompleteText) {
		super();
		this.autoCompleteText = autoCompleteText;
	}

	/**
	 * Message handler for a input recived
	 */
	private static final int INPUT_RECIVED = 0;

	/**
	 * Sends a message when an input was recived by the service
	 */
	public void sendInputRecieved(String cityName) {
		this.cityName = cityName;
		sendEmptyMessage(INPUT_RECIVED);
	}

	/**
	 * Removes all custom messages from the message queue
	 */
	public void removeCustomMessages() {
		removeMessages(INPUT_RECIVED);
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case INPUT_RECIVED:
			handleInputRecived();
		default:
			super.handleMessage(msg);
		}

	}

	/**
	 * handles when a new input is received
	 * 
	 */
	public void handleInputRecived() {
		if (autoCompleteText != null) {
			autoCompleteText.setText(cityName);
		}
	}

}
