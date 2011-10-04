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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

public class URLBuilder {

	private static final String TAG = "URLBuilder"; //$NON-NLS-1$

	private static final String PROTO = "://"; //$NON-NLS-1$
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final String AND = "&"; //$NON-NLS-1$
	private static final String INTEROGATION = "?"; //$NON-NLS-1$
	private static final String EQUAL = "="; //$NON-NLS-1$

	private StringBuilder url;
	private String encoding;
	private String protocol;
	private String adress;
	private List<String> pathElements;
	private HashMap<String, Set<String>> queryParameters;

	public URLBuilder(String encoding) {
		super();
		if ((encoding == null) || (encoding.length() == 0)) {
			this.encoding = CineShowTimeEncodingUtil.convertLocaleToEncoding();
		} else {
			this.encoding = encoding;
		}
		url = new StringBuilder();
		pathElements = new ArrayList<String>();
		queryParameters = new HashMap<String, Set<String>>();
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public void completePath(String pathElement) {
		pathElements.add(pathElement);
	}

	public void addQueryParameter(String key, String parameter) {
		Set<String> params = queryParameters.get(key);
		if (params == null) {
			params = new HashSet<String>();
			queryParameters.put(key, params);
		}
		try {
			params.add(URLEncoder.encode(parameter, encoding));
		} catch (Exception e) {
			Log.e(TAG, "Error while encoding string : " + parameter, e); //$NON-NLS-1$
		}
	}

	public String toUri() {
		url = new StringBuilder(protocol);
		url.append(PROTO);
		url.append(adress);
		for (String pathElement : pathElements) {
			url.append(SLASH).append(pathElement);
		}
		if (!queryParameters.isEmpty()) {
			url.append(INTEROGATION);
			boolean firstParam = true;
			Set<String> paramValues;
			for (String key : queryParameters.keySet()) {
				paramValues = queryParameters.get(key);
				for (String parameter : paramValues) {
					if (firstParam) {
						firstParam = false;
					} else {
						url.append(AND);
					}
					url.append(key).append(EQUAL).append(parameter);
				}
			}
		}
		return url.toString();
	}
}
