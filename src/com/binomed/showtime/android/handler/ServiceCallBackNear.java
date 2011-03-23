package com.binomed.showtime.android.handler;

import android.os.Handler;
import android.os.Message;

public abstract class ServiceCallBackNear extends Handler {

	/**
	 * Message handler for a input recived
	 */
	private static final int INPUT_RECIVED = 0;

	/**
	 * Sends a message when an input was recived by the service
	 */
	public void sendInputRecieved() {
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
	public abstract void handleInputRecived();

}
