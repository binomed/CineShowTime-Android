/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.util;

import com.binomed.showtime.cst.EncodingUtil;

public final class CineShowTimeEncodingUtil {

	private static CineShowTimeEncodingUtil instance;

	private String encoding;

	private CineShowTimeEncodingUtil() {
		super();
		encoding = EncodingUtil.UTF8;
	}

	private static CineShowTimeEncodingUtil getInstance() {
		if (instance == null) {
			instance = new CineShowTimeEncodingUtil();
		}
		return instance;
	}

	public static void setEncoding(String encoding) {
		CineShowTimeEncodingUtil.getInstance().encoding = encoding;
	}

	public static String getEncoding() {
		return CineShowTimeEncodingUtil.getInstance().encoding;
	}

	// TODO a remettre quand j'aurais une api de traduction
	// public static Language convertLocaleToLanguage() {
	// Locale locale = Resources.getSystem().getConfiguration().locale;
	// Language language = null;
	// if (locale.getLanguage().equals("fr")) {
	// language = Language.FRENCH;
	// } else if (locale.getLanguage().equals("zh")) {
	// language = Language.CHINESE;
	// } else if (locale.getLanguage().equals("de")) {
	// language = Language.GERMAN;
	// } else if (locale.getLanguage().equals("es")) {
	// language = Language.SPANISH;
	// } else if (locale.getLanguage().equals("it")) {
	// language = Language.ITALIAN;
	// } else if (locale.getLanguage().equals("ja")) {
	// language = Language.JAPANESE;
	// } else if (locale.getLanguage().equals("ko")) {
	// language = Language.KOREAN;
	// } else if (locale.getLanguage().equals("nl")) {
	// language = Language.DUTCH;
	// } else if (locale.getLanguage().equals("pl")) {
	// language = Language.POLISH;
	// } else if (locale.getLanguage().equals("ru")) {
	// language = Language.RUSSIAN;
	// } else if (locale.getLanguage().equals("th")) {
	// language = Language.THAI;
	// } else if (locale.getLanguage().equals("pt")) {
	// language = Language.PORTUGUESE;
	// } else if (locale.getLanguage().equals("cs")) {
	// language = Language.CZECH;
	// } else {
	// language = Language.ENGLISH;
	// }
	// return language;
	// }

	public static String convertLocaleToEncoding() {
		return getEncoding();
	}

}
