package com.binomed.showtime.android.util;

import java.util.HashMap;

import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public final class BeanManagerFactory {

	private static BeanManagerFactory instance;

	private NearResp nearResp;
	private NearResp nearRespFromWidget;
	private MovieResp movieResp;
	private HashMap<String, MovieBean> centralMovieMap;
	private HashMap<String, TheaterBean> centralTheaterMap;

	private boolean firstOpenAsk;

	private BeanManagerFactory() {
		centralMovieMap = new HashMap<String, MovieBean>();
		centralTheaterMap = new HashMap<String, TheaterBean>();
	}

	private static BeanManagerFactory getInstance() {
		if (instance == null) {
			instance = new BeanManagerFactory();
		}
		return instance;
	}

	private HashMap<String, MovieBean> getCentralMovieMap() {
		return centralMovieMap;
	}

	private HashMap<String, TheaterBean> getCentralTheaterMap() {
		return centralTheaterMap;
	}

	private NearResp getPrivateNearResp() {
		return nearResp;
	}

	private void setPrivateNearResp(NearResp nearResp) {
		this.nearResp = nearResp;
	}

	private NearResp getPrivateNearRespFromWidget() {
		return nearRespFromWidget;
	}

	private void setPrivateNearRespFromWidget(NearResp nearRespFromWidget) {
		this.nearRespFromWidget = nearRespFromWidget;
	}

	private MovieResp getPrivateMovieResp() {
		return movieResp;
	}

	private void setPrivateMovieResp(MovieResp MovieResp) {
		this.movieResp = MovieResp;
	}

	private boolean isPrivateFirstOpen() {
		return firstOpenAsk;
	}

	private void setPrivateFirstOpen(boolean firstOpen) {
		this.firstOpenAsk = firstOpen;
	}

	public synchronized static void cleanCentralMovieMap() {
		BeanManagerFactory.getInstance().getCentralMovieMap().clear();
	}

	public synchronized static void putMovie(MovieBean movie) {
		BeanManagerFactory.getInstance().getCentralMovieMap().put(movie.getId(), movie);
	}

	public synchronized static MovieBean getMovieForId(String movieId) {
		return BeanManagerFactory.getInstance().getCentralMovieMap().get(movieId);
	}

	public synchronized static void cleanCentralTheaterMap() {
		BeanManagerFactory.getInstance().getCentralTheaterMap().clear();
	}

	public synchronized static void putTheater(TheaterBean Theater) {
		BeanManagerFactory.getInstance().getCentralTheaterMap().put(Theater.getId(), Theater);
	}

	public synchronized static TheaterBean getTheaterForId(String TheaterId) {
		return BeanManagerFactory.getInstance().getCentralTheaterMap().get(TheaterId);
	}

	public synchronized static void setNearResp(NearResp nearResp) {
		BeanManagerFactory.getInstance().setPrivateNearResp(nearResp);
	}

	public synchronized static NearResp getNearResp() {
		return BeanManagerFactory.getInstance().getPrivateNearResp();
	}

	public synchronized static void setNearRespFromWidget(NearResp nearRespFromWidget) {
		BeanManagerFactory.getInstance().setPrivateNearRespFromWidget(nearRespFromWidget);
	}

	public synchronized static NearResp getNearRespFromWidget() {
		return BeanManagerFactory.getInstance().getPrivateNearRespFromWidget();
	}

	public synchronized static void setMovieResp(MovieResp MovieResp) {
		BeanManagerFactory.getInstance().setPrivateMovieResp(MovieResp);
	}

	public synchronized static MovieResp getMovieResp() {
		return BeanManagerFactory.getInstance().getPrivateMovieResp();
	}

	public synchronized static void setFirstOpen() {
		BeanManagerFactory.getInstance().setPrivateFirstOpen(true);
	}

	public synchronized static boolean isFirstOpen() {
		return BeanManagerFactory.getInstance().isPrivateFirstOpen();
	}

}
