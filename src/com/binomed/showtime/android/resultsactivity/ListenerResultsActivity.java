package com.binomed.showtime.android.resultsactivity;

import java.io.UnsupportedEncodingException;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;

import com.binomed.showtime.R;
import com.binomed.showtime.android.cst.AndShowtimeCst;
import com.binomed.showtime.android.layout.dialogs.sort.ListSelectionListener;
import com.binomed.showtime.android.layout.view.ObjectMasterView;
import com.binomed.showtime.android.layout.view.ObjectSubView;
import com.binomed.showtime.android.util.AndShowtimeFactory;
import com.binomed.showtime.android.util.BeanManagerFactory;
import com.binomed.showtime.android.util.comparator.AndShowtimeComparator;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.TheaterBean;

public class ListenerResultsActivity implements OnChildClickListener //
		, OnGroupClickListener //
		, OnGroupExpandListener //
		, OnGroupCollapseListener //
		, ListSelectionListener //
		, OnClickListener//
{

	private AndShowTimeResultsActivity resultActivity;
	private ControlerResultsActivity controler;
	private ModelResultsActivity model;

	private static final String TAG = "ListenerResultsActivity"; //$NON-NLS-1$

	public ListenerResultsActivity(AndShowTimeResultsActivity nearActivity, ControlerResultsActivity controlerNearActivity, ModelResultsActivity model) {
		super();
		this.resultActivity = nearActivity;
		this.controler = controlerNearActivity;
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListView.OnChildClickListener#onChildClick(android .widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		ObjectSubView subView = (ObjectSubView) v;
		TheaterBean theater = subView.getTheaterBean();
		MovieBean movie = subView.getMovieBean();
		controler.openMovieActivity(movie, theater);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListView.OnGroupClickListener#onGroupClick(android .widget.ExpandableListView, android.view.View, int, long)
	 */
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		switch (parent.getId()) {
		case R.id.resultListResult: {
			int theaterListSize = BeanManagerFactory.getNearResp().getTheaterList().size();
			if (theaterListSize == groupPosition) {
				model.setStart(model.getStart() + 10);
				try {
					resultActivity.launchNearService();
				} catch (UnsupportedEncodingException e) {
					// TODO
				}
			}
			break;
		}

		default:
			break;
		}
		return false;
	}

	@Override
	public void sortSelected(int sourceID, int sortKey) {
		sourceLabel: switch (sourceID) {
		case AndShowTimeResultsActivity.ID_SORT: {
			AndShowtimeComparator<?> comparator = null;
			sortLabel: switch (sortKey) {
			case AndShowtimeCst.SORT_THEATER_NAME:
				comparator = AndShowtimeFactory.getTheaterNameComparator();
				break sortLabel;
			case AndShowtimeCst.SORT_THEATER_DISTANCE:
				comparator = AndShowtimeFactory.getTheaterDistanceComparator();
				break sortLabel;
			case AndShowtimeCst.SORT_SHOWTIME:
				comparator = AndShowtimeFactory.getTheaterShowtimeComparator();
				break sortLabel;
			case AndShowtimeCst.SORT_MOVIE_NAME:
				comparator = AndShowtimeFactory.getMovieNameComparator();
				break sortLabel;

			default:
				comparator = AndShowtimeFactory.getTheaterNameComparator();
				break sortLabel;
			}
			resultActivity.changeComparator(comparator);
			break sourceLabel;
		}
		default:
			break sourceLabel;
		}

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
	public void onGroupExpand(int groupPosition) {
		model.getGroupExpanded().add(groupPosition);
	}

	@Override
	public void onGroupCollapse(int groupPosition) {
		model.getGroupExpanded().remove(groupPosition);
	}

}
