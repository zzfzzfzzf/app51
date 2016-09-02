package com.zzf.app51.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
	private static final String baseUrl = "http://image.baidu.com/channel/listjson?";
	private static final String tag1 = "tag1="+Constant.tag1+"&tag2=";
	private static final String BASE_URL = "http://image.baidu.com/channel/"
			+ "listjson?pn=0&rn=" + 103
			+ "&tag1="+Constant.tag1+"&ftags="
			+ "&sorttype=0&ie=utf8&oe=utf-8&image_id=&tag2=";
	private String strs = "%E6%B0%94%E8%B4%A8&width=580&height=200";
	public static String appendPnandRn(int num) {
		String str = "pn=0&rn=6&";
		if (num > 6) {
			// int pn = num - 6 + 1;
			int pn = 0;
			int rn = num + 1;
			str = "pn=" + pn + "&rn=" + rn + "&";
		}
		return str;
	}
	public static String urlEncode(String tag2) {
		String str = "";
		try {
			str = URLEncoder.encode(tag2, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;

	}
	public static String appendUrl(String name, String imgWH, int num) {
		return baseUrl
				+ appendPnandRn(num)
				+ tag1
				+ urlEncode(name)
				+ "&tag3=&"
				+ imgWH
				+ "&ic=0&ie=utf8&oe=utf-8&image_id=&fr=channel&app=img.browse.channel.wallpaper&t="
				+ Math.random();
	}
	private static String httpRequest(String requestUrl) {
		StringBuffer buffer = null;

		try {
			
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");

			
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			
			buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

		
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			httpUrlConn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (buffer != null) {
			return buffer.toString();
		}
		return null;
	}
	public static String getImgUrl(String name, String imgWH) {
		String url = appendUrl(name, imgWH, 6);
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		while (i < 1) {
			String html = httpRequest(url + Math.random());

			Pattern p = Pattern
					.compile("(.*)(image_url\":\")(.*?)(\"image_width)(.*)");
			Matcher m = p.matcher(html);
			if (m.matches()) {
				
				for (String info : m.group(3).split("\",")) {
					
					if (!"".equals(info)) {
						buffer.append(info).append("+");
					}
				}
			}
			i += 1;
		}
		return (null == buffer) ? null : buffer.substring(0,
				buffer.lastIndexOf("+"));
	}
	public static String parseJson(String name, String imgWH, int num) {
		String url = appendUrl(name, imgWH, num);
		StringBuffer buffer = new StringBuffer();
		String html = httpRequest(url + Math.random());
		String res = "";
		try {
			if (html != null) {
				JSONArray jsonObjs = new JSONObject(html).getJSONArray("data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject jo = (JSONObject) jsonObjs.opt(i);
					res += jo.getString("image_url") + ",";
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("".equals(res)) {
			return "http://f.hiphotos.baidu.com/image/pic/item/9f2f070828381f30fc66bbacab014c086e06f0b3.jpg,";
		}
		return res;

	}
	public static String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		// 设置网络超时参数
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		HttpConnectionParams.setSoTimeout(httpParams, 6000);
		HttpResponse response = client.execute(new HttpGet(BASE_URL + url));
		
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"), 8192);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}
		return sb.toString();
	}
}
