package com.binomed.showtime.android.layout.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binomed.showtime.R;
import com.binomed.showtime.beans.ReviewBean;

public class ReviewView extends LinearLayout {

	private Context context;
	private ImageView imgRate1, imgRate2, imgRate3, imgRate4, imgRate5;
	private TextView reviewAuthor;
	private TextView reviewUrl;
	private TextView reviewContent;
	private ReviewBean reviewBean;

	private Bitmap bitmapRateOff;
	private Bitmap bitmapRateHalf;
	private Bitmap bitmapRateOn;

	public ReviewView(Context context) {
		super(context);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_review_group_item, this);

		// expandImg = (ImageView) this.findViewById(R.id.expand_img);
		imgRate1 = (ImageView) this.findViewById(R.id.reviewImgRate1);
		imgRate2 = (ImageView) this.findViewById(R.id.reviewImgRate2);
		imgRate3 = (ImageView) this.findViewById(R.id.reviewImgRate3);
		imgRate4 = (ImageView) this.findViewById(R.id.reviewImgRate4);
		imgRate5 = (ImageView) this.findViewById(R.id.reviewImgRate5);

		// Init star img
		bitmapRateOff = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_off);
		bitmapRateHalf = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_half);
		bitmapRateOn = BitmapFactory.decodeResource(getResources(), R.drawable.rate_star_small_on);

		reviewAuthor = (TextView) this.findViewById(R.id.reviewAutor);
		reviewUrl = (TextView) this.findViewById(R.id.reviewUrl);
		reviewContent = (TextView) this.findViewById(R.id.reviewReview);

		reviewUrl.setClickable(true);
		reviewUrl.setAutoLinkMask(Linkify.ALL);
	}

	public ReviewBean getReviewBean() {
		return reviewBean;
	}

	public void setReviewBean(ReviewBean reviewBean) {
		this.reviewBean = reviewBean;

		StringBuilder strContent = new StringBuilder();
		reviewAuthor.setText(reviewBean.getAuthor());

		int rate1 = R.drawable.rate_star_small_off;
		int rate2 = R.drawable.rate_star_small_off;
		int rate3 = R.drawable.rate_star_small_off;
		int rate4 = R.drawable.rate_star_small_off;
		int rate5 = R.drawable.rate_star_small_off;
		Float rate = reviewBean.getRate();
		if (rate != null) {
			switch (rate.intValue()) {
			case 5:
				rate5 = R.drawable.rate_star_small_on;
			case 4:
				rate4 = R.drawable.rate_star_small_on;
				if (rate > 4.5 && rate < 5) {
					rate5 = R.drawable.rate_star_small_half;
				}
			case 3:
				rate3 = R.drawable.rate_star_small_on;
				if (rate > 3.5 && rate < 4) {
					rate4 = R.drawable.rate_star_small_half;
				}
			case 2:
				rate2 = R.drawable.rate_star_small_on;
				if (rate > 2.5 && rate < 3) {
					rate3 = R.drawable.rate_star_small_half;
				}
			case 1:
				rate1 = R.drawable.rate_star_small_on;
				if (rate > 1.5 && rate < 2) {
					rate2 = R.drawable.rate_star_small_half;
				}
			case 0:
				if (rate > 0.5 && rate < 1) {
					rate1 = R.drawable.rate_star_small_half;
				}
			default:
				break;
			}
		}

		imgRate1.setImageBitmap(getImg(rate1));
		imgRate2.setImageBitmap(getImg(rate2));
		imgRate3.setImageBitmap(getImg(rate3));
		imgRate4.setImageBitmap(getImg(rate4));
		imgRate5.setImageBitmap(getImg(rate5));

		strContent.append("<A HREF='").append(reviewBean.getSource()).append("'>").append(reviewBean.getSource()).append("</A>");
		reviewUrl.setText(Html.fromHtml(strContent.toString()));

		strContent.delete(0, strContent.length());
		strContent.append(reviewBean.getReview());
		strContent.append("<A HREF='").append(reviewBean.getUrlReview()).append("'>").append(" Lire a suite ...").append("</A>");
		reviewContent.setText(Html.fromHtml(strContent.toString()));
		reviewContent.setMovementMethod(LinkMovementMethod.getInstance());

	}

	/**
	 * @param rate
	 * @return
	 */
	private Bitmap getImg(int rate) {
		if (rate == R.drawable.rate_star_small_off) {
			return bitmapRateOff;
		} else if (rate == R.drawable.rate_star_small_half) {
			return bitmapRateHalf;
		} else {
			return bitmapRateOn;
		}
	}
}
