package com.binomed.showtime.android.activity;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.binomed.showtime.R;
import com.binomed.showtime.android.layout.dialogs.fav.FavDialog;
import com.binomed.showtime.android.layout.dialogs.fav.TheaterFavSelectionListener;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerMainActivity implements OnClickListener, TheaterFavSelectionListener {

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
			controler.openSearchNearActivity(null);
			break;
		}
		case R.id.mainBtnSearchMovie: {
			controler.openSearchMovieActivity();
			break;
		}
		case R.id.mainBtnTheaterFav: {
			List<TheaterBean> theaterList = controler.getFavTheater();
			if (!theaterList.isEmpty()) {
				FavDialog dialog = new FavDialog(//
						mainActivity //
						, ListenerMainActivity.this //
						, theaterList//
				);
				dialog.setTitle(mainActivity.getResources().getString(R.string.dialogBookmarkTitle));
				dialog.show();
			} else {
				Toast.makeText(mainActivity, R.string.msgNoDFav, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.fav.TheaterFavSelectionListener#removeTheater(com.binomed.showtime.beans.TheaterBean)
	 */
	@Override
	public void removeTheater(TheaterBean theater) {
		controler.removeFavorite(theater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.binomed.showtime.android.fav.TheaterFavSelectionListener#theaterSelected(com.binomed.showtime.beans.TheaterBean)
	 */
	@Override
	public void theaterSelected(TheaterBean theater) {
		controler.openSearchNearActivity(theater);

	}

}
