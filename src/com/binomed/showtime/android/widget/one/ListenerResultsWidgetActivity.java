package com.binomed.showtime.android.widget.one;

import java.io.UnsupportedEncodingException;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerResultsWidgetActivity implements OnItemClickListener //
		, OnClickListener//
{

	private AndShowTimeResultsWidgetActivity resultActivity;
	private ControlerResultsWidgetActivity controler;
	private ModelResultsWidgetActivity model;

	private static final String TAG = "ListenerResultsActivity"; //$NON-NLS-1$

	public ListenerResultsWidgetActivity(AndShowTimeResultsWidgetActivity nearActivity, ControlerResultsWidgetActivity controlerNearActivity, ModelResultsWidgetActivity model) {
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
			int theaterListSize = BeanManagerFactory.getNearRespFromWidget().getTheaterList().size();
			if (theaterListSize == groupPosition) {
				model.setStart(model.getStart() + 10);
				try {
					resultActivity.launchNearService();
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "error launching service", e);
				}
			} else {
				AndShowTimeWidgetHelper.finalizeWidget(resultActivity, BeanManagerFactory.getNearRespFromWidget().getTheaterList().get(groupPosition) //
						, BeanManagerFactory.getNearRespFromWidget().getCityName());
			}
			break;
		}

		default:
			break;
		}

	}

}
