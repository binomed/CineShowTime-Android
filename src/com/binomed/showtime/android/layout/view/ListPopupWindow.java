package com.binomed.showtime.android.layout.view;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.binomed.showtime.R;
import com.binomed.showtime.android.objects.OptionEnum;
import com.binomed.showtime.android.util.AndShowtimeDateNumberUtil;
import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.ProjectionBean;
import com.binomed.showtime.beans.TheaterBean;

public class ListPopupWindow extends AbstractCustomPopupWindow {

	private Context ctx;
	private ProjectionBean projectionBean;
	private TheaterBean theater;
	private MovieBean movie;
	private ListOptionProjectionView listView;

	private static final String TAG = "ListPopupWindow";

	public ListPopupWindow(View anchor, Context ctx, TheaterBean theater, MovieBean movie, ProjectionBean projectionBean) {
		super(anchor);
		this.ctx = ctx;
		this.projectionBean = projectionBean;
		this.theater = theater;
		this.movie = movie;
	}

	@Override
	protected void onCreate() {

		OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpater, View view, int groupPosition, long id) {
				if (view instanceof OptionProjectionView) {
					boolean format24 = AndShowtimeDateNumberUtil.isFormat24(ctx);

					switch (((OptionProjectionView) view).getOption()) {
					case SMS: {
						try {

							Object[] testArgs = { new Long(3), "MyDisk" };

							MessageFormat form = new MessageFormat("The disk \"{1}\" contains {0} file(s).");

							String rest = form.format(testArgs);

							Intent sendIntent = new Intent(Intent.ACTION_VIEW);
							String msg = MessageFormat.format(ctx.getResources().getString(R.string.smsContent) // //$NON-NLS-1$
									, movie.getMovieName() //
									, AndShowtimeDateNumberUtil.getDayString(ctx, projectionBean.getShowtime()) //
									, AndShowtimeDateNumberUtil.showMovieTime(ctx, projectionBean.getShowtime(), format24) //
									, theater.getTheaterName());
							sendIntent.putExtra("sms_body", msg);
							sendIntent.setType("vnd.android-dir/mms-sms"); //$NON-NLS-1$
							ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getString(R.string.chooseIntentSms)));

						} catch (Exception e) {
							Log.e(TAG, "error while translating", e); //$NON-NLS-1$
						}
						break;
					}
					case MAIL: {
						try {

							// Create a new Intent to send messages
							Intent sendIntent = new Intent(Intent.ACTION_SEND);
							sendIntent.setType("text/html"); //$NON-NLS-1$
							// sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
							String subject = MessageFormat.format(ctx.getResources().getString(R.string.mailSubject), movie.getMovieName());
							String msg = MessageFormat.format(ctx.getResources().getString(R.string.mailContent) //
									, movie.getMovieName() //
									, AndShowtimeDateNumberUtil.getDayString(ctx, projectionBean.getShowtime()) //
									, AndShowtimeDateNumberUtil.showMovieTime(ctx, projectionBean.getShowtime(), format24) //
									, theater.getTheaterName());
							sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
							sendIntent.putExtra(Intent.EXTRA_TEXT,//
									msg);
							ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getString(R.string.chooseIntentMail)));
						} catch (Exception e) {
							Log.e(TAG, "error while translating", e); //$NON-NLS-1$
						}
						break;
					}
					case AGENDA: {
						try {

							// Before or equel Donuts
							Uri uri = null;
							if (Integer.valueOf(Build.VERSION.SDK) <= 7) {
								uri = Uri.parse("content://calendar/events");
							} else if (Integer.valueOf(Build.VERSION.SDK) <= 8) {
								uri = Uri.parse("content://com.android.calendar/events");

							}

							if (uri != null) {
								ContentResolver cr = ctx.getContentResolver();

								Calendar timeAfter = Calendar.getInstance();
								timeAfter.setTimeInMillis(projectionBean.getShowtime());
								Calendar timeMovie = Calendar.getInstance();
								timeMovie.setTimeInMillis(movie.getMovieTime());
								timeAfter.add(Calendar.HOUR_OF_DAY, timeMovie.get(Calendar.HOUR_OF_DAY));
								timeAfter.add(Calendar.MINUTE, timeMovie.get(Calendar.MINUTE));
								ContentValues values = new ContentValues();
								values.put("eventTimezone", TimeZone.getDefault().getID());
								values.put("calendar_id", 1); // query content://calendar/calendars for more
								values.put("title", movie.getMovieName());
								values.put("allDay", 0);
								values.put("dtstart", projectionBean.getShowtime()); // long (start date in ms)
								values.put("dtend", timeAfter.getTimeInMillis()); // long (end date in ms)
								values.put("description", movie.getMovieName() + " at " + theater.getTheaterName());
								values.put("eventLocation", (theater.getPlace() != null) ? theater.getPlace().getSearchQuery() : null);
								values.put("transparency", 0);
								values.put("visibility", 0);
								values.put("hasAlarm", 0);

								cr.insert(uri, values);

								Toast.makeText(ctx, R.string.msgEventAdd, Toast.LENGTH_LONG).show();
							}

						} catch (Exception e) {
							Log.e(TAG, "error while translating", e); //$NON-NLS-1$
						}
						break;
					}
					case RESERVATION: {
						Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(projectionBean.getReservationLink()));
						ctx.startActivity(myIntent);
						break;
					}
					default:
						break;
					}
					ListPopupWindow.this.dismiss();
				}

			}
		};

		listView = new ListOptionProjectionView(this.anchor.getContext(), itemClickListener);
	}

	public void loadView() {
		listView.setProjectionBean(projectionBean);
		this.setContentView(listView);

	}

	public Rect getSize() {
		int[] location = new int[2];
		this.listView.getLocationOnScreen(location);

		Rect listViewRect = new Rect(location[0], location[1], location[0] + this.listView.getWidth(), location[1] + this.listView.getHeight());
		return listViewRect;
	}

	public List<OptionEnum> getOptions() {
		return this.listView.getOptions();
	}

	@Override
	protected void onShow() {
		// TODO gérer les actions
		super.onShow();
	}

}
