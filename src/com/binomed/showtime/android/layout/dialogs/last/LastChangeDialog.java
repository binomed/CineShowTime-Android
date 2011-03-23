package com.binomed.showtime.android.layout.dialogs.last;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.binomed.showtime.R;

public class LastChangeDialog extends Dialog {

	private Context mainContext;

	public LastChangeDialog(Context context) {
		super(context);
		mainContext = context;
	}

	/**
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_last);

		setTitle(mainContext.getResources().getString(R.string.dialogLastChangeTitle));
		TextView contentLastChange = (TextView) findViewById(R.id.lastChangetText);
		Spanned spanned = Html.fromHtml( //
				"<b>v2.1.0</b><br><br>" + //
						" * Add of SplashScreen<br>" + //
						" * Add of Google Analytics<br>" + //
						" * Add of menu in movie screen<br>" + //
						" * App2Sd support<br>" + //
						" * Add of GPS animation<br>" + //
						" * Fix some crash problems<br>" + //
						"<b>v2.0.3</b><br><br>" + //
						" * Fix some crash problems<br>" + //
						"<b>v2.0.2</b><br><br>" + //
						" * Fix some crash problems<br>" + //
						"<br><b>v2.0.1</b><br><br>" + //
						" * Fix landscape problem for search screen<br>" + //
						" * Fix some crash problems<br>" + //
						"<br><b>v2.0.0</b><br><br>" + //
						" * New UI<br>" + //
						" * Add Themes management<br>" + //
						" * Add tab review in movie screen<br>" + //
						" * Add trailers directly in movie screen<br>" + //
						" * Possibility to reserve if infomation is available<br>" + //
						"<br><b>v1.10.1</b><br><br>" + //
						" * Minor fix corresponding to crash reports<br>" + //
						" * Suppression of gps message when launching app<br>" + //
						"<br><b>v1.10.0</b><br><br>" + //
						" * Fix with favorites<br>" + //
						" * Fix with distance<br>" + //
						"<br><b>v1.9.2</b><br><br>" + //
						" * Fix for archos tablet<br>" + //
						"<br><b>v1.9.1</b><br><br>" + //
						" * Minor bug correction with favorites<br>" + //
						" * Change of icon<br>" + //
						" * <b>Please update your favorites cinemas (delete, recreate) if there is an error<b><br>" + //
						"<br><b>v1.9.0</b><br><br>" + //
						" * Support of japanese langage and italian<br>" + //
						" * Change of icon<br>" + //
						" * Change of widget : you have to reset your widget<br>" + //
						"<br><b>v1.8.1</b><br><br>" + //
						" * Minor fix for bookmark bug" + //
						"<br><br><b>v1.8.0</b><br><br>" + //
						" * Location bugs correction<br>" + //
						" * crash bug correction<br>" + //
						" * correction on server for movie descriptions<br>" + //
						" * integration of 'es' langage, complete rewrite of 'en' traductions.<br>" + //
						" * managment of widget in cupcake in multi-screen and multi resolutions<br>" + //
						"<br><br><b>v1.7.2 and 1.7.3</b><br>" + //
						"<br>" + //
						" * Location bugs correction<br>" + //
						" * crash bug correction<br>" + //
						" * correction on server for movie times<br>" + //
						" * integration of pt, pt_BR, and cz langages<br>" + //
						"<br><br><b>v1.7.1</b><br>" + //
						"<br>" + //
						" * Minor update for integrating message when no results come from server<br>" + //
						"<br><br><b>v1.7.0</b><br>" + //
						"<br>" + //
						" * Management of Turkish langage<br>" + //
						" * Management of widget for multiScreen (Add of widget for cupcake)<br>" + //
						"<br><br><b>v1.6.2 and v1.6.3</b><br>" + //
						"<br>" + //
						" * Minor update correcting widget bug<br>" + //
						"<br><br><b>v1.6.1</b><br>" + //
						"<br>" + //
						" * Enhance UI<br>" + //
						" * Management of deutch langage<br>" + //
						"<br><br><b>v1.6.0</b><br>" + //
						"<br>" + //
						" * Integration of SkyHook? framework => you can search near your wifi position, ip position<br>" + //
						" * Correction of bugs with bookmarks theater.<br>" + //
						" * Integration of projection langage.<br>" + //
						" * Warning ! this version will reset your bookmark theater and the widget.<br>" + //
						"<br><br><b>v1.5.0</b><br>" + //
						"<br>" + //
						" * Management of time of adds before movies showtime in preferences<br>" + //
						" * Correction of bug of gps localisation invisible buttons, for android 1.1 and 1.5<br>" + //
						" * Support of ru langage (Thanks Stan)<br>" + //
						"<br><br><b>v1.4.0</b><br>" + //
						"<br>" + //
						" * Add of button Bookmarks on main screen<br>" + //
						" * Click on widget now open directly on theater showtimes<br>" + //
						" * Add of possibility to have drive direction to theaters<br>" + //
						" * Possibility of multi-page in widget search<br>" + //
						" * Add search speech for name of city and name of movie (if speech available). Works for english word only (wait for google :))<br>" + //
						" * Increase number of day in spinner<br>" + //
						" * Improve of way to get movie summary<br>" + //
						"<br><br><b>v1.3.1</b><br>" + //
						"<br>" + //
						" * Correction for motorola blur (but many other phone could be concerned) for text of buttons invisible<br>" + //
						"<br><br><b>v1.3.0</b><br>" + //
						"<br>" + //
						" * Management of drive time to go to theater. You could have only showtime reachables.<br>" + //
						" * You can add a showtime into calendar application.<br>" + //
						" * You can localise you with network position.<br>" + //
						" * Correction of issues 1,2,3 and 4.<br>" + //
						" * Add of option in menu for management of localisation.<br>" + //
						"<br><br><b>v1.2.0</b><br>" + //
						"<br>" + //
						" * Add of about menu (version informations)<br>" + //
						" * Add of help menu<br>" + //
						" * Correction of bug with showtimes accross days and theaters<br>" + //
						" * Improve UI fast<br>" + //
						"<br><br><b>v1.1.0</b><br>" + //
						"<br>" + //
						" * Widget management<br>" + //
						" * Improve UI<br>" + //
						" * Correction of some bugs<br>" + //
						"<br><br><b>v1.0.0</b><br>" + //
						"<br>" + //
						" * Search theaters according to position and show movie list and showtimes corresponding<br>" + //
						" * You can call theaters<br>" + //
						" * You can launch maps to localize the theaters<br>" + //
						" * You can search trailers of the movies<br>" + //
						" * You can manage theaters bookmarks<br>" + //
						" * For each movie, there is a description screen with several information on the movie<br>" + //
						" * You can use your GPS to localise you and search show time near your position<br>" + //
						" * You can sort your results by :<br>" + //
						" * Distance of the theater<br>" + //
						" * Name of the theater<br>" + //
						" * The closest showtime<br>" + //
						" * You can see what's on screens during the 3 next day<br>" + //
						" * Search showtimes by movie name<br>" + //
						" * You can invite your friends by sms or mail to a film event<br>" + //
						" * Management of langage :<br>" + //
						" ->en<br>" + //
						" ->fr<br>" + //
						" ->de<br>" + //
						" ->pt<br>"//
				);

		contentLastChange.setText(spanned);

	}
}
