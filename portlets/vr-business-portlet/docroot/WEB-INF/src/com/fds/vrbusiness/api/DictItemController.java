package com.fds.vrbusiness.api;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;

import com.fds.vrbusiness.utils.DictItemsUtils;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

@Path("/vr-app")
public class DictItemController {

	@GET
	@Path("/collections/{code: .*}/{type: .*}/items")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response alo(@PathParam("code") String code,
			@PathParam("type") String type) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		long dictCollectionId = DictItemsUtils.getDictCollectionId(code);

		try {

			List<DictItem> items = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionId(dictCollectionId);
			
			JSONArray itemsJson = getDictItem(items, code);
			
			resp.put("Items", itemsJson);
			resp.put("Total", itemsJson.length());
			
			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();

		}

	}

	private JSONArray getDictItem(List<DictItem> items, String code) {
		JSONArray array = JSONFactoryUtil.createJSONArray();
		
		
		Locale locale = new Locale("vi", "VN");

		for(DictItem dictItem : items) {
			
			if (dictItem.getItemCode().toLowerCase().contains(code.toLowerCase())) {
				JSONObject input = JSONFactoryUtil.createJSONObject();

				input.put("key", dictItem.getItemCode());
	
				input.put("value", dictItem.getItemName(locale));
				
				array.put(input);
			}
			
		}
		
		return array;
	}
	

}
