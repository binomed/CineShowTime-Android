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