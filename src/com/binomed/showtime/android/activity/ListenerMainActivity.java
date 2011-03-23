package com.binomed.showtime.android.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.cst.ParamIntent;
import com.binomed.showtime.android.layout.view.TheaterFavView;
import com.binomed.showtime.android.service.AndShowDBGlobalService;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerMainActivity implements OnClickListener //
		, OnItemClickListener // 
{

	private ControlerMainActivity controler;
	private AndShowTimeMainActivity mainActivity;

	public ListenerMainActivity(ControlerMainActivity controler, AndShowTimeMainActivity mainActivity) {
		super();
		this.controler = controler;
		this.mainActivity = mainActivity;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainBtnSearchNear: {
			controler.openSearchActivity(null);
			break;
		}
		case R.id.favItemDelete: {
			TheaterFavView thFavView = (TheaterFavView) v.getParent().getParent();
			TheaterBean thTmp = thFavView.getTheaterBean();
			Intent intentRemoveTh = new Intent(mainActivity, AndShowDBGlobalService.class);
			intentRemoveTh.putExtra(ParamIntent.SERVICE_DB_TYPE, AndShowtimeCst.DB_TYPE_FAV_DELETE);
			BeanManagerFactory.setTheaterTemp(thTmp);
			mainActivity.startService(intentRemoveTh);
			mainActivity.model.getFavList().remove(thTmp);
			if (mainActivity.model.getFavList().size() == 0) {
				TheaterBean thEmtpy = new TheaterBean();
				thEmtpy.setId("0");
				thEmtpy.setTheaterName(mainActivity.getResources().getString(R.string.msgNoDFav));
				mainActivity.model.getFavList().add(thEmtpy);
			}
			mainActivity.adapter.notifyDataSetChanged();
			break;
		}
			// case R.id.mainBtnSearchMovie: {
			// controler.openSearchMovieActivity();
			// break;
			// }
			// case R.id.mainBtnTheaterFav: {
			// List<TheaterBean> theaterList = controler.getFavTheater();
			// if (!theaterList.isEmpty()) {
			// FavDialog dialog = new FavDialog(//
			// mainActivity //
			// , ListenerMainActivity.this //
			// , theaterList//
			// );
			// dialog.setTitle(mainActivity.getResources().getString(R.string.dialogBookmarkTitle));
			// dialog.show();
			// } else {
			// Toast.makeText(mainActivity, R.string.msgNoDFav, Toast.LENGTH_SHORT).show();
			//
			// }
			// break;
			// }
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
		// Sinon on ouvre la page résultats
		TheaterBean theater = mainActivity.model.getFavList().get(groupPosition);
		controler.openResultsActivity(theater);
	}

}
