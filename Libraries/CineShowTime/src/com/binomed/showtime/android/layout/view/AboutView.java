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
package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;

public class AboutView extends LinearLayout {

	private Context mainContext;
	private TextView developpedText, copyRightText;
	private ImageButton btnDonate;

	public AboutView(Context context) {
		super(context);
		mainContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dialog_about, this);

		copyRightText = (TextView) findViewById(R.id.txtAboutDialogCopyright);
		developpedText = (TextView) findViewById(R.id.txtAboutDialogDevelop);
		btnDonate = (ImageButton) findViewById(R.id.btnAboutDialogDonate);

		developpedText.setText(Html.fromHtml(new StringBuilder() //
				.append(mainContext.getResources().getString(R.string.msgDevelopped)).append(" ").append("<A HREF='http://blog.binomed.fr'>Binomed</A><br>")//
				.append(mainContext.getResources().getString(R.string.msgTraductorName))//
				.toString()));
		developpedText.setMovementMethod(LinkMovementMethod.getInstance());

		copyRightText.setText(Html.fromHtml(new StringBuilder() //
				.append("© 2011 - Binomed ").append("<br>")//
				.append("Eclipse Public License - v 1.0").append("<br>")//
				.append("Results from Google Movies").append("<br>")//
				.append("Use of Google Analytics Service").append("")//
				.toString()));

		btnDonate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String donationLink = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=C2RWWTDVTBMBS&lc=US&item_name=Binomed&item_number=binomed&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted";
				Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(donationLink));
				mainContext.startActivity(myIntent);
			}
		});

	}
}
