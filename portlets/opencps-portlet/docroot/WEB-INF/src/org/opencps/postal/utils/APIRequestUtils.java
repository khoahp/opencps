/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

/**
 * @author nhanhlt
 * */
package org.opencps.postal.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;

@SuppressWarnings("deprecation")
public class APIRequestUtils {

	private static Log _log = LogFactoryUtil.getLog(APIRequestUtils.class);


	public static final String USERNAME = "User10";
	public static final String PASSWORD = "cd@2016";

	/**
	 * @param domainName
	 * @param APIPath
	 * @param token
	 * @param jsonData
	 * @return
	 */
	public JSONObject callAPI(String domainName, String APIPath, String token,
			String jsonData) {

		DefaultHttpClient httpClient = new DefaultHttpClient();

		try {

			StringEntity json = new StringEntity(jsonData);

			StringBuffer urlString = new StringBuffer();
			urlString.append(domainName);
			urlString.append(APIPath);
			urlString.append(StringPool.QUESTION);
			urlString.append("token=").append(token);

			HttpPost url = new HttpPost(urlString.toString());
			json.setContentType("application/json");
			url.setEntity(json);

			HttpResponse httpResponse = httpClient.execute(url);

			int responseCode = httpResponse.getStatusLine().getStatusCode();
			
			_log.info("=====responseCode:"+responseCode);

			// Reading response from input Stream
			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));

			String output;

			StringBuffer response = new StringBuffer();

			while ((output = in.readLine()) != null) {
				response.append(output);
			}

			in.close();

			JSONObject fileJSON = JSONFactoryUtil.createJSONObject(response
					.toString());
			
			_log.info("=====return Data:"+fileJSON.toString());

			return fileJSON;

		} catch (Exception e) {
			_log.error(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return JSONFactoryUtil.createJSONObject();

	}
}