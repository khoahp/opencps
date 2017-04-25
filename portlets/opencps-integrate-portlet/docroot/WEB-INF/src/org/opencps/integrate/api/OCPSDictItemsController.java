package org.opencps.integrate.api;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

@Path("/api")
public class OCPSDictItemsController {
	
	public final String COLLECTION_CODE_REGION = "ADMINISTRATIVE_REGION";

	/**
	 * @param request
	 * @param collectioncode
	 * @return
	 */
	@GET
	@Path("/dictitems/collectioncode/{collectioncode: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDictItems(
			@PathParam("collectioncode") String collectioncode) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();
		

		if (Validator.isNotNull(collectioncode)) {
			try {
				// TODO: Hard code groupId = 20182, will be update
				long groupId = 20182; 
				
				DictCollection dc = DictCollectionLocalServiceUtil
						.getDictCollection(groupId,
								collectioncode);
				
				List<DictItem> dictItems = DictItemLocalServiceUtil
						.getDictItemsByDictCollectionId(dc
								.getDictCollectionId());

				int total = dictItems.size();

				JSONArray items = getRespDictItems(dictItems);

				resp.put("Total", total);
				resp.put("Items", items);

				return Response.status(200).entity(resp.toString()).build();

			} catch (Exception e) {
				return Response.status(404).entity(resp.toString()).build();
			}

		} else {
			
			return Response.status(404).entity(resp.toString()).build();
		}

	}
	
	/**
	 * @return
	 */
	@GET
	@Path("/cities")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getCities() {
		JSONObject resp = JSONFactoryUtil.createJSONObject();

		long dictCollectionId = getCollectionId(COLLECTION_CODE_REGION);

		try {
			List<DictItem> items = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollectionId, 0);
			
			int total = items.size();
			
			resp.put("Total", total);
			resp.put("Cities", getCities(items));
			
			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}
	}

	/**
	 * @param citycode
	 * @return
	 */
	@GET
	@Path("/districts/citycode/{citycode}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDistricts(@PathParam("citycode") long citycode) {
		JSONObject resp = JSONFactoryUtil.createJSONObject();
		
		long dictCollectionId = getCollectionId(COLLECTION_CODE_REGION);

		try {
			List<DictItem> items = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollectionId, citycode);
			
			int total = items.size();
			
			resp.put("Total", total);
			resp.put("Districts", getDistricts(items));
			
			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}

	}
	/**
	 * @return
	 */
	@GET
	@Path("/wards/districtcode/{districtcode}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getWards(@PathParam("districtcode") long districtcode) {
		JSONObject resp = JSONFactoryUtil.createJSONObject();

		long dictCollectionId = getCollectionId(COLLECTION_CODE_REGION);

		try {
			List<DictItem> items = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollectionId, districtcode);
			
			int total = items.size();
			
			resp.put("Total", total);
			resp.put("Wards", getWards(items));
			
			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}
	}


	/**
	 * 
	 * @param items
	 * @return
	 */
	private JSONArray getRespDictItems(List<DictItem> items) {
		
		JSONArray ls = JSONFactoryUtil.createJSONArray();
		
		for (DictItem di : items) {
			
			JSONObject input = JSONFactoryUtil.createJSONObject();
			Locale locale = new Locale("vi", "VN");

			input.put("ItemName", di.getItemName(locale));
			input.put("ItemCode", di.getItemCode());
			input.put("ItemId", di.getDictItemId());
			input.put("ItemDescription", di.getItemDescription());
			
			ls.put(input);
		}
		
		return ls;
	}
	
	private JSONArray getCities(List<DictItem> items) {
		
		JSONArray ls = JSONFactoryUtil.createJSONArray();
		
		for (DictItem di : items) {
			
			JSONObject input = JSONFactoryUtil.createJSONObject();
			Locale locale = new Locale("vi", "VN");

			input.put("CityName", di.getItemName(locale));
			input.put("CityCode", di.getItemCode());
			input.put("CityId", di.getDictItemId());
			
			ls.put(input);
		}
		
		return ls;
	}

	private JSONArray getDistricts(List<DictItem> items) {
		
		JSONArray ls = JSONFactoryUtil.createJSONArray();
		
		for (DictItem di : items) {
			
			JSONObject input = JSONFactoryUtil.createJSONObject();
			Locale locale = new Locale("vi", "VN");

			input.put("DistrictName", di.getItemName(locale));
			input.put("DistrictCode", di.getItemCode());
			input.put("DistrictId", di.getDictItemId());
			
			ls.put(input);
		}
		
		return ls;
	}

	private JSONArray getWards(List<DictItem> items) {
		
		JSONArray ls = JSONFactoryUtil.createJSONArray();
		
		for (DictItem di : items) {
			
			JSONObject input = JSONFactoryUtil.createJSONObject();
			Locale locale = new Locale("vi", "VN");

			input.put("WardsName", di.getItemName(locale));
			input.put("WardsCode", di.getItemCode());
			input.put("WardsId", di.getDictItemId());
			
			ls.put(input);
		}
		
		return ls;
	}

	
	/**
	 * @param collectionCode
	 * @return
	 */
	private long getCollectionId(String collectionCode) {
		
		long collectionId = 0;
		
		// TODO: Hard code groupId = 20182, will be update
		long groupId = 20182;

		try {
			DictCollection dc = DictCollectionLocalServiceUtil.getDictCollection(
					groupId, collectionCode);
			
			collectionId = dc.getDictCollectionId();
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		return collectionId;
	}
	
	private Log _log = LogFactoryUtil.getLog(OCPSDictItemsController.class);

}
