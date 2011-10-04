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
package com.binomed.showtime.android.model;

import com.binomed.showtime.R;

public enum OptionEnum {

	SMS(R.string.menuSms, R.drawable.sms_32x32) //
	, AGENDA(R.string.menuAddEvent, R.drawable.calendar_32x32) //
	, MAIL(R.string.menuMail, R.drawable.envelope_closed_32x32) //
	, RESERVATION(R.string.menuReservation, R.drawable.ticket_32x32) //
	;

	private int ressourceText;
	private int ressourceDrawable;

	private OptionEnum(int ressourceText, int ressourceDrawable) {
		this.ressourceText = ressourceText;
		this.ressourceDrawable = ressourceDrawable;
	}

	public int getRessourceText() {
		return ressourceText;
	}

	public int getRessourceDrawable() {
		return ressourceDrawable;
	}

}