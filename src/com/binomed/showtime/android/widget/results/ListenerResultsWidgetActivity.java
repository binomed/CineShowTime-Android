package com.binomed.showtime.android.widget.results;

import java.io.UnsupportedEncodingException;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.model.TheaterBean;
import com.binomed.showtime.android.widget.CineShowTimeWidgetHelper;

public class ListenerResultsWidgetActivity implements OnItemClickListener //
		, OnClickListener//
{

	private CineShowTimeResultsWidgetActivity resultActivity;
	private ControlerResultsWidgetActivity controler;
	private ModelResultsWidgetActivity model;

	private static final String TAG = "ListenerResultsActivity"; //$NON-NLS-1$

	public ListenerResultsWidgetActivity(CineShowTimeResultsWidgetActivity nearActivity, ControlerResultsWidgetActivity controlerNearActivity, ModelResultsWidgetActivity model) {
		super();
		this.resultActivity = nearActivity;
		this.controler = controlerNearActivity;
		this.model = model;
	}

	@Override
	public void onClick(View v) {
		ImageView imageViewFav = (ImageView) v;

		ObjectMasterView objectMasterView = (ObjectMasterView) imageViewFav.getParent().getParent();

		boolean isFav = objectMasterView.isFav();
		TheaterBean theaterBean = objectMasterView.getTheaterBean();
		if (isFav) {
			controler.removeFavorite(theaterBean);
		} else {
			controler.addFavorite(theaterBean);
		}
		objectMasterView.toggleFav();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int groupPosition, long arg3) {
		switch (parent.getId()) {
		case R.id.resultWidgetListResult: {
			int theaterListSize = model.getNearResp().getTheaterList().size();
			if (theaterListSize == groupPosition) {
				model.setStart(model.getStart() + 10);
				try {
					resultActivity.launchNearService();
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error launching service", e);
				}
			} else {
				CineShowTimeWidgetHelper.finalizeWidget(resultActivity, model.getNearResp().getTheaterList().get(groupPosition) //
						, model.getNearResp().getCityName());
			}
			break;
		}

		default:
			break;
		}

	}

}
