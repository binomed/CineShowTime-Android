/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.AutoCompleteTextWithSpeech;

public class TextCallBackFromLocation extends Handler {

	private AutoCompleteTextWithSpeech autoCompleteText;
	private Context context;

	private String cityName;

	public TextCallBackFromLocation(AutoCompleteTextWithSpeech autoCompleteText) {
		super();
		this.autoCompleteText = autoCompleteText;
	}

	/**
	 * Message handler for a input recived
	 */
	private static final int INPUT_RECIVED = 0;
	private static final int ERROR = 1;

	/**
	 * Sends a message when an input was recived by the service
	 */
	public void sendInputRecieved(String cityName) {
		this.cityName = cityName;
		sendEmptyMessage(INPUT_RECIVED);
	}

	/**
	 * Sends a message when an input was recived by the service
	 */
	public void sendError(Context context) {
		this.context = context;
		sendEmptyMessage(ERROR);
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
			break;
		case ERROR:
			AlertDialog.Builder errorDialog = new AlertDialog.Builder(context);
			errorDialog.setTitle(R.string.errorMsg);
			errorDialog.setMessage(R.string.msgErrorOnServer);
			errorDialog.setCancelable(false);
			errorDialog.setIcon(R.drawable.icon);
			errorDialog.setNeutralButton(R.string.btnClose, null);
			errorDialog.show();
			break;
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
