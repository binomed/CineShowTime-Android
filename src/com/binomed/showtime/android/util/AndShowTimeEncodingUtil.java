package com.binomed.showtime.android.util;

import java.util.Locale;

import android.content.res.Resources;

import com.binomed.showtime.cst.EncodingUtil;
import com.google.api.translate.Language;

public final class AndShowTimeEncodingUtil {

	private static AndShowTimeEncodingUtil instance;

	private String encoding;

	private AndShowTimeEncodingUtil() {
		super();
		encoding = EncodingUtil.UTF8;
	}

	private static AndShowTimeEncodingUtil getInstance() {
		if (instance == null) {
			instance = new AndShowTimeEncodingUtil();
		}
		return instance;
	}

	public static void setEncoding(String encoding) {
		AndShowTimeEncodingUtil.getInstance().encoding = encoding;
	}

	public static String getEncoding() {
		return AndShowTimeEncodingUtil.getInstance().encoding;
	}

	public static Language convertLocaleToLanguage() {
		Locale locale = Resources.getSystem().getConfiguration().locale;
		Language language = null;
		if (locale.getLanguage().equals("fr")) {
			language = Language.FRENCH;
		} else if (locale.getLanguage().equals("zh")) {
			language = Language.CHINESE;
		} else if (locale.getLanguage().equals("de")) {
			language = Language.GERMAN;
		} else if (locale.getLanguage().equals("es")) {
			language = Language.SPANISH;
		} else if (locale.getLanguage().equals("it")) {
			language = Language.ITALIAN;
		} else if (locale.getLanguage().equals("ja")) {
			language = Language.JAPANESE;
		} else if (locale.getLanguage().equals("ko")) {
			language = Language.KOREAN;
		} else if (locale.getLanguage().equals("nl")) {
			language = Language.DUTCH;
		} else if (locale.getLanguage().equals("pl")) {
			language = Language.POLISH;
		} else if (locale.getLanguage().equals("ru")) {
			language = Language.RUSSIAN;
		} else if (locale.getLanguage().equals("th")) {
			language = Language.THAI;
		} else if (locale.getLanguage().equals("pt")) {
			language = Language.PORTUGUESE;
		} else if (locale.getLanguage().equals("cs")) {
			language = Language.CZECH;
		} else {
			language = Language.ENGLISH;
		}
		return language;
	}

	public static String convertLocaleToEncoding() {
		return getEncoding();
	}

}
