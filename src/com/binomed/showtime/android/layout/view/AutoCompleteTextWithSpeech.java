package com.binomed.showtime.android.layout.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.dialogs.sort.ListDialog;
import com.binomed.showtime.android.layout.dialogs.sort.ListSelectionListener;

public class AutoCompleteTextWithSpeech extends RelativeLayout implements OnClickListener, ListSelectionListener {

	private AutoCompleteTextView autoCompleteText;
	private AutoCompleteInteraction callBack;
	private ArrayList<String> matches;

	public AutoCompleteTextWithSpeech(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AutoCompleteTextWithSpeech(Context context) {
		super(context);
		init(context);
	}

	public void setCallBack(AutoCompleteInteraction callBack) {
		this.callBack = callBack;
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_auto_complete_with_speech, this);

		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		RelativeLayout layoutWithSpeech = (RelativeLayout) findViewById(R.id.layoutWithSpeech);
		AutoCompleteTextView autoCompleteTextWithSpeech = (AutoCompleteTextView) findViewById(R.id.searchWithSpeech);
		ImageButton btnSpeech = (ImageButton) findViewById(R.id.btnSpeech);
		AutoCompleteTextView autoCompleteTextWithoutSpeech = (AutoCompleteTextView) findViewById(R.id.searchWithoutSpeech);

		if (activities.size() == 0) {
			layoutWithSpeech.setVisibility(View.GONE);
			autoCompleteText = autoCompleteTextWithoutSpeech;
		} else {
			autoCompleteTextWithoutSpeech.setVisibility(View.GONE);
			autoCompleteText = autoCompleteTextWithSpeech;
			btnSpeech.setOnClickListener(this);

		}
	}

	public boolean onVoiceRecognitionResult(Intent data, int resultCode, int requestCode) {
		if ((requestCode == callBack.getRequestCode(getId())) && (resultCode == Activity.RESULT_OK)) {
			matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if ((matches != null) && (matches.size() > 0)) {
				ListDialog dialog = new ListDialog(//
						getContext() //
						, this //
						, matches //
						, -1//
				);
				dialog.setTitle(getContext().getResources().getString(R.string.msgSpeecRecognition));
				dialog.show();
				return true;
			}
		}
		return false;
	}

	public void setText(String text) {
		autoCompleteText.setText(text);
	}

	public Editable getText() {
		return autoCompleteText.getText();
	}

	public void setAdapter(ArrayAdapter<String> arrayValues) {
		autoCompleteText.setAdapter(arrayValues);
	}

	public AutoCompleteTextView getEditText() {
		return autoCompleteText;
	}

	/*
	 * 
	 * Event part
	 */

	@Override
	public void sortSelected(int viewId, int selectKey) {
		autoCompleteText.setText(matches.get(selectKey));

	}

	@Override
	public void onClick(View arg0) {
		if (callBack != null) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getContext().getResources().getString(R.string.msgSpeecCity));
			callBack.startActivityForResult(intent, callBack.getRequestCode(getId()));
		}

	}

	public interface AutoCompleteInteraction {
		void startActivityForResult(Intent intent, int requestCode);

		int getRequestCode(int itemId);

	}
}
