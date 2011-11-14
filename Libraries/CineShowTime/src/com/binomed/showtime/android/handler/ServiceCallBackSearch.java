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

import android.os.Handler;
import android.os.Message;

public abstract class ServiceCallBackSearch extends Handler {

	/**
	 * Message handler for a input recived
	 */
	private static final int INPUT_RECIVED = 0;
	private static final int UNREGISTER = 1;

	/**
	 * Sends a message when an input was recived by the service
	 */
	public void sendInputRecieved(String theaterId) {
		Message msg = obtainMessage();
		msg.obj = theaterId;
		msg.what = INPUT_RECIVED;
		sendMessage(msg);
	}

	public void couldUnRegister() {
		sendEmptyMessage(UNREGISTER);
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
			handleInputRecived((String) msg.obj);
			break;
		case UNREGISTER:
			unRegister();
			break;
		default:
			super.handleMessage(msg);
		}

	}

	/**
	 * handles when a new input is received
	 * 
	 */
	public abstract void handleInputRecived(String theaterId);

	public abstract void unRegister();

}
