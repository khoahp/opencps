package org.opencps.notificationmgt.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;

public class APISMSRequestUtils {

	private static final String USER_AGENT = "Mozilla/5.0";
	
	private static Log _log = LogFactoryUtil.getLog(APISMSRequestUtils.class);

	public static String sendSMS(String domainName, String APIPath,
			JSONObject jsonBody) {

		try {

			String urlString = domainName + APIPath;

			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// add request header
			con.setRequestMethod("POST");

			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					con.getOutputStream());
			wr.write(jsonBody.toString());
			wr.flush();
			wr.close();

//			int responseCode = con.getResponseCode();
//			_log.info("Sending get request : " + url);
//			_log.info("jsonBody: " + jsonBody);
//			_log.info("Response code : " + responseCode);

			// Reading response from input Stream
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String output;

			StringBuffer response = new StringBuffer();

			while ((output = in.readLine()) != null) {
				response.append(output);
			}

			in.close();

			JSONObject fileJson = JSONFactoryUtil.createJSONObject(response
					.toString());

			return fileJson.toString();

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return StringPool.BLANK;
	}
}
