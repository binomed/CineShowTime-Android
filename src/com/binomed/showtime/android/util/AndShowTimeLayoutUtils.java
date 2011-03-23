package com.binomed.showtime.android.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public final class AndShowTimeLayoutUtils {

	public static void manageVisibiltyFieldSpeech(Context context, ImageButton button, AutoCompleteTextView text, int idRightof, int idLeftOf, int idBelow) {
		// Manage speech button just if package present on device
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			button.setVisibility(View.GONE);

			if (text != null && Integer.valueOf(Build.VERSION.SDK) <= 3) {
				// Manage specificity for version before 4
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				if (idRightof != -1) {
					params.addRule(RelativeLayout.RIGHT_OF, idRightof);
				}
				if (idLeftOf != -1) {
					params.addRule(RelativeLayout.LEFT_OF, idLeftOf);
				}
				if (idBelow != -1) {
					params.addRule(RelativeLayout.BELOW, idBelow);
				}
				text.setSingleLine(true);
				text.setLayoutParams(params);
			}
		}

	}

}
