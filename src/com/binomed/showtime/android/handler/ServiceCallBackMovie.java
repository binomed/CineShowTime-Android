package com.binomed.showtime.android.handler;

import android.os.Handler;
import android.os.Message;

public abstract class ServiceCallBackMovie extends Handler {

	/**
	 * Message handler for a input recived
	 */
	private static final int INPUT_RECIVED = 0;

	/**
	 * Sends a message when an input was recived by the service
	 */
	public void sendInputRecieved(String movieId) {
		sendMessage(obtainMessage(INPUT_RECIVED, movieId));
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
			handleInputRecived(msg.obj.toString());
		default:
			super.handleMessage(msg);
		}

	}

	/**
	 * handles when a new input is received
	 * 
	 * @param idMovie
	 *            the id of movie that change
	 */
	public abstract void handleInputRecived(String idMovie);

}
