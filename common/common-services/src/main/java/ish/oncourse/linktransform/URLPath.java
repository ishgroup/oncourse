package ish.oncourse.linktransform;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class URLPath {

	private static final Logger logger = LogManager.getLogger();

	private Path decodedPath;

	private Path encodedPath;

	public String getDecodedPath() {
		return decodedPath.path;
	}

	public String getEncodedPath() {
		return encodedPath.path;
	}

	private String convert(String value, boolean encode) throws UnsupportedEncodingException {
		return encode ? URLEncoder.encode(value, "UTF-8") : URLDecoder.decode(value, "UTF-8");
	}

	private Path convertPath(Path input, boolean encode) {
		try {
			Path output = new Path();
			output.pathItems = new String[input.pathItems.length];
			for (int i = 0; i < input.pathItems.length; i++) {
				output.pathItems[i] = convert(input.pathItems[i], encode);
			}
			if (input.queryItems != null) {
				output.queryItems = new Query[input.queryItems.length];
				for (int i = 0; i < input.queryItems.length; i++) {
					output.queryItems[i] = Query.valueOf(convert(input.queryItems[i].getKey(), encode),
							convert(input.queryItems[i].getValue(), encode));
				}
			}
			output.path = formatPath(output.pathItems, output.queryItems);
			return output;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Path parsePath(String stringPath) {
		try {
			URL url = new URL("http://127.0.0.1" + stringPath);
			Path path = new Path();
			path.path = stringPath;
			path.pathItems = StringUtils.split(url.getPath(), "/");

			String[] queries = StringUtils.split(url.getQuery(), "&");
			if (queries != null) {
				path.queryItems = new Query[queries.length];
				for (int i = 0; i < queries.length; i++) {
					String query = queries[i];
					path.queryItems[i] = Query.valueOf(query);
				}
			}
			return path;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String formatPath(String[] paths, Query[] queries) {
		ArrayList<String> queryList = null;
		if (queries != null) {
			queryList = new ArrayList<>();
			for (Query query : queries) {
				queryList.add(query.getKey() + "=" + query.getValue());
			}
		}
		return "/" + StringUtils.join(paths, "/") + (queryList != null ? ("?" + StringUtils.join(queryList, "&")) : "");
	}

	public static URLPath valueOf(String dbPath) {
		URLPath path = new URLPath();
		path.decodedPath = path.parsePath(dbPath);
		Path decoded = path.convertPath(path.decodedPath, false);
		if (!decoded.path.equals(dbPath)) {
			path.decodedPath = decoded;
		}
		path.encodedPath = path.convertPath(path.decodedPath, true);
		return path;
	}


	private static class Path {
		private String path;
		private String[] pathItems;
		private Query[] queryItems;
	}

	private static class Query {

		private String key;
		private String value;

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public static Query valueOf(String queryString) {
			Query query = new Query();
			String[] keyValue = StringUtils.split(queryString, '=');

			switch (keyValue.length) {
				case 1:
					query.key = keyValue[0];
					query.value = StringUtils.EMPTY;
					break;
				case 2:
					query.key = keyValue[0];
					query.value = keyValue[1];
					break;
				default:
					query.key = queryString;
					query.value = StringUtils.EMPTY;
					logger.error("Wrong query {}", queryString);
			}
			return query;
		}

		public static Query valueOf(String key, String value) {
			Query query = new Query();
			query.key = key;
			query.value = value;
			return query;
		}
	}
}
