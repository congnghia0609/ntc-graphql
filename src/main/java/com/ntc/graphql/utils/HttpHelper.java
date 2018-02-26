/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.graphql.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since May 10, 2015
 */
public class HttpHelper {
    private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);

	public static String sendPostRequest(String url, Map<String, String> data) {
		StringBuilder result = new StringBuilder();
		try {
			URL ulrConn = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) ulrConn.openConnection();
			/*
			 * Post parameter
			 */
			String strData = buildParameter(data);

			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(strData.length()));
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(strData);
			writer.flush();

			/*
			 * read data result
			 */
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
		}

		return result.toString();
	}

	public static String sendGetRequest(String url, Map<String, String> data) {
		StringBuilder result = new StringBuilder();
		try {
			String strData = buildParameter(data);
			if (!strData.isEmpty()) {
				url += "?" + strData;
			}
			URL ulrConn = new URL(url);
			URLConnection conn = ulrConn.openConnection();
			/*
			 * read data result
			 */
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
		} catch (Exception ex) {
			log.error(ex.getMessage() + ex);
		}

		return result.toString();
	}

	private static String buildParameter(Map<String, String> data) {
		StringBuilder result = new StringBuilder();

		try {
			if (data != null) {
				Set<Map.Entry<String, String>> setEntry = data.entrySet();
				Iterator it = setEntry.iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					result.append("&");
					result.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
					result.append("=");
					result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				}
			}
		} catch (UnsupportedEncodingException ex) {
			log.error(ex.getMessage() + ex);
		}
		if (result.indexOf("&") > -1) {
			result.deleteCharAt(0);
		}
		return result.toString();
	}

	public static String getSession(HttpServletRequest req, String name) {
		String result = "";
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					result = cookie.getValue();
					break;
				}
			}
		}
		return result;
	}

	public static void setSession(HttpServletResponse resp, String domain, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		resp.addCookie(cookie);
	}
	
	public static void removeSession(HttpServletResponse resp, String domain, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		resp.addCookie(cookie);
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			return true;
		}
		return false;
	}
    
    public static boolean isMultipartRequest(HttpServletRequest request) {
		if (request.getContentType() != null && request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1) {
			return true;
		}
		return false;
	}
    
    
}
