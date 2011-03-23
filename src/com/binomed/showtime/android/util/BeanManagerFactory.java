package com.binomed.showtime.android.util;

import java.util.HashMap;

import android.content.pm.PackageManager;

import com.binomed.showtime.beans.MovieBean;
import com.binomed.showtime.beans.MovieResp;
import com.binomed.showtime.beans.NearResp;
import com.binomed.showtime.beans.TheaterBean;

public final class BeanManagerFactory {

	private static BeanManagerFactory instance;

	private TheaterBean theaterTemp;
	private MovieBean movieDesc;
	private NearResp nearResp;
	private NearResp nearRespFromWidget;
	private MovieResp movieResp;
	private HashMap<String, MovieBean> centralMovieMap;
	private HashMap<String, TheaterBean> centralTheaterMap;
	private Boolean mapInstalled;
	private Boolean dialerInstalled;

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

	private Boolean isPrivateMapsInstalled() {
		return mapInstalled;
	}

	private void setPrivateMapsInstalled(boolean mapsInstalled) {
		this.mapInstalled = mapsInstalled;
	}

	private Boolean isPrivateDialerInstalled() {
		return dialerInstalled;
	}

	private void setPrivateDialerInstalled(boolean dialerInstalled) {
		this.dialerInstalled = dialerInstalled;
	}

	private TheaterBean getPrivateTheaterTemp() {
		return theaterTemp;
	}

	private void setPrivateTheaterTemp(TheaterBean theater) {
		this.theaterTemp = theater;
	}

	private MovieBean getPrivateMovieDesc() {
		return movieDesc;
	}

	private void setPrivateMovieDesc(MovieBean movie) {
		this.movieDesc = movie;
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

	public synchronized static void setTheaterTemp(TheaterBean theater) {
		BeanManagerFactory.getInstance().setPrivateTheaterTemp(theater);
	}

	public synchronized static TheaterBean getTheaterTemp() {
		return BeanManagerFactory.getInstance().getPrivateTheaterTemp();
	}

	public synchronized static void setMovieDesc(MovieBean movie) {
		BeanManagerFactory.getInstance().setPrivateMovieDesc(movie);
	}

	public synchronized static MovieBean getMovieDesc() {
		return BeanManagerFactory.getInstance().getPrivateMovieDesc();
	}

	public synchronized static boolean isMapsInstalled(PackageManager packageManager) {
		Boolean result = BeanManagerFactory.getInstance().isPrivateMapsInstalled();
		if (result == null) {
			result = AndShowTimeMenuUtil.isMapsInstalled(packageManager);
			BeanManagerFactory.getInstance().setPrivateMapsInstalled(result);
		}
		return result;
	}

	public synchronized static boolean isDialerInstalled(PackageManager packageManager) {
		Boolean result = BeanManagerFactory.getInstance().isPrivateDialerInstalled();
		if (result == null) {
			result = AndShowTimeMenuUtil.isDialerInstalled(packageManager);
			BeanManagerFactory.getInstance().setPrivateDialerInstalled(result);
		}
		return result;
	}

}
