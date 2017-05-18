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

package org.opencps.datamgt.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.opencps.datamgt.DuplicateCollectionException;
import org.opencps.datamgt.DuplicateItemException;
import org.opencps.datamgt.EmptyCollectionCodeException;
import org.opencps.datamgt.EmptyDictCollectionNameException;
import org.opencps.datamgt.EmptyDictItemNameException;
import org.opencps.datamgt.EmptyItemCodeException;
import org.opencps.datamgt.NoSuchDictCollectionException;
import org.opencps.datamgt.NoSuchDictItemException;
import org.opencps.datamgt.OutOfLengthCollectionCodeException;
import org.opencps.datamgt.OutOfLengthCollectionNameException;
import org.opencps.datamgt.OutOfLengthItemCodeException;
import org.opencps.datamgt.OutOfLengthItemNameException;
import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictCollectionType;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.DictItemType;
import org.opencps.datamgt.model.DictPermissions;
import org.opencps.datamgt.model.DictVersion;
import org.opencps.datamgt.model.impl.DictPermissionsImpl;
import org.opencps.datamgt.search.DictCollectionDisplayTerms;
import org.opencps.datamgt.search.DictItemDisplayTerms;
import org.opencps.datamgt.search.DictItemSearch;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.service.DictCollectionTypeLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.datamgt.service.DictItemTypeLocalServiceUtil;
import org.opencps.datamgt.service.DictPermissionsLocalServiceUtil;
import org.opencps.datamgt.service.DictVersionLocalServiceUtil;
import org.opencps.datamgt.util.DataMgtUtil;
import org.opencps.util.MessageKeys;
import org.opencps.util.PortletPropsValues;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * @author trungnt
 */
public class DataMamagementPortlet extends MVCPortlet {

	public void deleteDictCollection(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long dictCollectionId = ParamUtil.getLong(actionRequest,
				DictCollectionDisplayTerms.DICTCOLLECTION_ID);
		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");

		try {
			DictCollectionLocalServiceUtil.deleteCollection(dictCollectionId);
		} catch (NoSuchDictCollectionException e) {
			SessionErrors.add(actionRequest,
					NoSuchDictCollectionException.class);
			_log.error(e);
		} catch (SystemException e) {
			SessionErrors.add(actionRequest,
					MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED);
			_log.error(e);
		} finally {
			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}
		}
	}

	public void deleteDictItem(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long dictItemId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.DICTITEM_ID, 0L);
		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");
		try {
			DictItemLocalServiceUtil.deleteDictItem(dictItemId);
		} catch (Exception e) {
			SessionErrors.add(actionRequest,
					MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED);
			_log.error(e);
		} finally {
			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}
		}
	}
	
	public void changeStatusItemToNoUse(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long dictItemId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.DICTITEM_ID, 0L);
		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");
		try {
			DictItem item = DictItemLocalServiceUtil.getDictItem(dictItemId);
			item.setIssueStatus(2);
			DictItemLocalServiceUtil.updateDictItem(item);
		} catch (Exception e) {
			SessionErrors.add(actionRequest,
					MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED);
			_log.error(e);
		} finally {
			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}
		}
	}

	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {

		long dictCollectionId = ParamUtil.getLong(renderRequest,
				DictCollectionDisplayTerms.DICTCOLLECTION_ID);
		long dictVersionId = ParamUtil.getLong(renderRequest, "dictVersionId");
		long dictItemId = ParamUtil.getLong(renderRequest,
				DictItemDisplayTerms.DICTITEM_ID);

		try {
			if (dictCollectionId > 0) {
				DictCollection dictCollection = DictCollectionLocalServiceUtil
						.getDictCollection(dictCollectionId);
				renderRequest.setAttribute(WebKeys.DICT_COLLECTION_ENTRY,
						dictCollection);
			}

			if (dictVersionId > 0) {
				DictVersion dictVersion = DictVersionLocalServiceUtil
						.getDictVersion(dictVersionId);
				renderRequest.setAttribute(WebKeys.DICT_VERSION_ENTRY,
						dictVersion);
			}

			if (dictItemId > 0) {
				DictItem dictItem = DictItemLocalServiceUtil
						.getDictItem(dictItemId);
				renderRequest.setAttribute(WebKeys.DICT_ITEM_ENTRY, dictItem);
			}
		} catch (Exception e) {
			_log.error(e);
		}

		String actionKey = ParamUtil.getString(renderRequest, "actionKey");
		if (actionKey.equals("ajax-load-dict-items")) {

			int cur = ParamUtil.getInteger(renderRequest, "cur", 1);
			int delta = ParamUtil.getInteger(renderRequest, "delta", 20);

			String itemNames = ParamUtil.getString(renderRequest, "keyword");
			itemNames = StringPool.PERCENT + itemNames + StringPool.PERCENT;

			PortletURL iteratorURL = renderResponse.createRenderURL();

			iteratorURL.setWindowState(LiferayWindowState.NORMAL);

			SearchContainer<DictItem> itemsListSearchContainer = new SearchContainer<DictItem>(
					renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM, cur, delta, iteratorURL,
					null, DictItemSearch.EMPTY_RESULTS_MESSAGE);

			renderRequest.setAttribute("itemsListSearchContainer",
					itemsListSearchContainer);
		}

		super.render(renderRequest, renderResponse);
	}

	public void updateDictCollection(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long collectionId = ParamUtil.getLong(actionRequest,
				DictCollectionDisplayTerms.DICTCOLLECTION_ID, 0L);

		Map<Locale, String> collectionNameMap = LocalizationUtil
				.getLocalizationMap(actionRequest,
						DictCollectionDisplayTerms.COLLECTION_NAME);
		String collectionCode = ParamUtil.getString(actionRequest,
				DictCollectionDisplayTerms.COLLECTION_CODE);
		String description = ParamUtil.getString(actionRequest,
				DictCollectionDisplayTerms.DESCRIPTION);

		String collectionName = collectionNameMap
				.get(actionRequest.getLocale());
		
		long[] dictCollectionsType = ParamUtil.getLongValues(actionRequest,
				"dictCollectionsLinked");

		for (Map.Entry<Locale, String> entry : collectionNameMap.entrySet()) {
			if (entry.getValue().length() > collectionName.length()) {
				collectionName = entry.getValue();
			}
		}
		
		if (Validator.isNull(collectionName)) {
			collectionName = ParamUtil.getString(actionRequest,
					DictCollectionDisplayTerms.COLLECTION_NAME);
			Locale vnLocale = new Locale("vi", "VN");
			collectionNameMap.put(vnLocale, collectionName);
		}
		
		String collectionLinked = StringPool.BLANK;
		if (dictCollectionsType.length == 0) {
			collectionLinked = ParamUtil.getString(actionRequest,
					"collectionLinked");
			String[] strArr = StringUtil.split(collectionLinked);
			dictCollectionsType = new long[strArr.length];
			for (int i = 0; i < strArr.length; i++) {
				try {
					dictCollectionsType[i] = Long.parseLong(strArr[i]);
				} catch (Exception e) {}
			}
		}
		
		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");
		String returnURL = ParamUtil.getString(actionRequest, "returnURL");
		
		DictCollection dictCollection = null;
		try {
			ServiceContext serviceContext = ServiceContextFactory
					.getInstance(actionRequest);
			validatetDictCollection(collectionId, collectionName,
					collectionCode, serviceContext);

			if (collectionId == 0) {
				dictCollection = DictCollectionLocalServiceUtil.addDictCollection(
						serviceContext.getUserId(), collectionCode,
						collectionNameMap, description, serviceContext);
				SessionMessages.add(actionRequest,
						MessageKeys.DATAMGT_ADD_SUCESS);
				
				updateDictCollectionsType(
						dictCollection.getDictCollectionId(),
						dictCollectionsType, 0, false, serviceContext);
			} else {
				dictCollection = DictCollectionLocalServiceUtil.updateDictCollection(
						collectionId, serviceContext.getUserId(),
						collectionCode, collectionNameMap, description,
						serviceContext);
				SessionMessages.add(actionRequest,
						MessageKeys.DATAMGT_UPDATE_SUCESS);
				
				updateDictCollectionsType(
						dictCollection.getDictCollectionId(),
						dictCollectionsType, 0, true, serviceContext);
			}
			
		} catch (Exception e) {
			if (e instanceof OutOfLengthCollectionCodeException) {
				SessionErrors.add(actionRequest,
						OutOfLengthCollectionCodeException.class);
			} else if (e instanceof OutOfLengthCollectionNameException) {
				SessionErrors.add(actionRequest,
						OutOfLengthCollectionNameException.class);
			} else if (e instanceof DuplicateCollectionException) {
				SessionErrors.add(actionRequest,
						DuplicateCollectionException.class);
			} else if (e instanceof NoSuchDictCollectionException) {
				SessionErrors.add(actionRequest,
						NoSuchDictCollectionException.class);
			} else if (e instanceof EmptyDictCollectionNameException) {
				SessionErrors.add(actionRequest,
						EmptyDictCollectionNameException.class);
			} else if (e instanceof EmptyCollectionCodeException) {
				SessionErrors.add(actionRequest,
						EmptyCollectionCodeException.class);
			} else {
				SessionErrors.add(actionRequest,
						MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED);
			}

			redirectURL = returnURL;
			
			_log.error(e);

		} finally {
			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}
		}
	}

	private void updateDictCollectionsType(long dictCollectionId,
			long[] dictCollectionsIdType, long sequenceNo, boolean update,
			ServiceContext serviceContext) throws SystemException {

		// delete collection linked
		if (update) {
			List<DictCollectionType> collectionTypes = DictCollectionTypeLocalServiceUtil
					.getByDictCollectionId(dictCollectionId);
			for (DictCollectionType dictCollectionType : collectionTypes) {
				DictCollectionTypeLocalServiceUtil
						.deleteDictCollectionType(dictCollectionType);
			}
		}

		// add collections linked
		for (long l : dictCollectionsIdType) {
			if (l > 0) {
				DictCollectionTypeLocalServiceUtil.addDictCollectionType(
						dictCollectionId, l, sequenceNo, serviceContext);
			}
		}
	}

	public void updateDictItem(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long dictItemId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.DICTITEM_ID, 0L);

		long dictCollectionId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.DICTCOLLECTION_ID, 0L);

		long dictVersionId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.DICTVERSION_ID, 0L);

		long parentItemId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.PARENTITEM_ID, 0L);

		Map<Locale, String> itemNameMap = LocalizationUtil.getLocalizationMap(
				actionRequest, DictItemDisplayTerms.ITEM_NAME);

		String itemCode = ParamUtil.getString(actionRequest,
				DictItemDisplayTerms.ITEM_CODE);

		long[] dictItemsLinked = ParamUtil.getLongValues(actionRequest,
				"dictItemLinked");
		
		long sibling = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.SIBLING);

		int status = ParamUtil.getInteger(actionRequest, "status");

		String itemName = itemNameMap.get(actionRequest.getLocale());

		for (Map.Entry<Locale, String> entry : itemNameMap.entrySet()) {
			if (entry.getValue().length() > itemName.length()) {
				itemName = entry.getValue();
			}
		}
		
		if (Validator.isNull(itemName)) {
			itemName = ParamUtil.getString(actionRequest,
					DictItemDisplayTerms.ITEM_NAME);
			Locale vnLocale = new Locale("vi", "VN");
			itemNameMap.put(vnLocale, itemName);
		}
		
		String itemsLinked = StringPool.BLANK;
		if (dictItemsLinked.length == 0) {
			itemsLinked = ParamUtil.getString(actionRequest,
					"collectionLinked");
			String[] strArr = StringUtil.split(itemsLinked);
			dictItemsLinked = new long[strArr.length];
			for (int i = 0; i < strArr.length; i++) {
				try {
					dictItemsLinked[i] = Long.parseLong(strArr[i]);
				} catch (Exception e) {}
			}
		}

		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");
		String returnURL = ParamUtil.getString(actionRequest, "returnURL");
		try {

			ServiceContext serviceContext = ServiceContextFactory
					.getInstance(actionRequest);
			validatetDictItem(dictItemId, itemName, itemCode, serviceContext);

			DictItem dictItem = null;
			if (dictItemId == 0) {
				if (dictVersionId == 0) {
					dictItem = DictItemLocalServiceUtil.addDictItem(
							serviceContext.getUserId(), dictCollectionId,
							itemCode, itemNameMap, itemNameMap, parentItemId,
							sibling, status, serviceContext);
				} else {
					dictItem = DictItemLocalServiceUtil.addDictItem(
							serviceContext.getUserId(), dictCollectionId,
							dictVersionId, itemCode, itemNameMap, parentItemId,
							serviceContext);
				}

				updateSiblingTree(dictItem, null, sibling, true);

				updateDictItemLinked(dictItem.getDictItemId(), dictItemsLinked,
						0, false, serviceContext);

				SessionMessages.add(actionRequest,
						MessageKeys.DATAMGT_ADD_SUCESS);
			} else {
				DictItem dictItemBefor = DictItemLocalServiceUtil
						.getDictItem(dictItemId);

				dictItem = DictItemLocalServiceUtil.updateDictItem(dictItemId,
						dictCollectionId, dictVersionId, itemCode, itemNameMap,
						itemNameMap, parentItemId, sibling, status, serviceContext);

				boolean moveDown = true;
				if (dictItemBefor.getSibling() > sibling) {
					moveDown = false;
				}

				updateSiblingTree(dictItem, dictItemBefor, sibling, moveDown);

				updateDictItemLinked(dictItem.getDictItemId(), dictItemsLinked,
						0, true, serviceContext);

				SessionMessages.add(actionRequest,
						MessageKeys.DATAMGT_UPDATE_SUCESS);
			}
		} catch (Exception e) {
			if (e instanceof EmptyItemCodeException) {
				SessionErrors.add(actionRequest, EmptyItemCodeException.class);
			} else if (e instanceof OutOfLengthItemCodeException) {
				SessionErrors.add(actionRequest,
						OutOfLengthItemCodeException.class);
			} else if (e instanceof EmptyDictItemNameException) {
				SessionErrors.add(actionRequest,
						EmptyDictItemNameException.class);
			} else if (e instanceof OutOfLengthItemNameException) {
				SessionErrors.add(actionRequest,
						OutOfLengthItemNameException.class);
			} else if (e instanceof DuplicateItemException) {
				SessionErrors.add(actionRequest, DuplicateItemException.class);
			} else if (e instanceof NoSuchDictItemException) {
				SessionErrors.add(actionRequest, NoSuchDictItemException.class);
			} else {
				SessionErrors.add(actionRequest,
						MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED);
			}

			redirectURL = returnURL;
			
			_log.error(e);

		} finally {
			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}
		}
	}
	
	private void updateSiblingTree(DictItem dictItem, DictItem dictItemBefor,
			long sibling, boolean moveDown) throws NoSuchDictItemException,
			PortalException, SystemException {
		List<DictItem> items = DictItemLocalServiceUtil.getBy_D_P(dictItem
				.getDictCollectionId(), dictItem.getParentItemId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, DataMgtUtil
						.getDictItemOrderByComparator(
								DictItemDisplayTerms.SIBLING,
								WebKeys.ORDER_BY_ASC));
		if (dictItemBefor == null) {
			// new dict item
			for (DictItem d : items) {
				if (d.getDictItemId() != dictItem.getDictItemId()
						&& d.getSibling() >= sibling) {
					d.setSibling(d.getSibling() + 1);
					DictItemLocalServiceUtil.updateDictItem(d);
				}
			}
		} else {
			// update exist dict item
			if (dictItem.getParentItemId() == dictItemBefor.getParentItemId()) {
				// item no change parent
				if (dictItem.getSibling() > dictItemBefor.getSibling()) {
					// item move down
					for (DictItem d : items) {
						if (d.getDictItemId() != dictItem.getDictItemId()
								&& d.getSibling() > dictItemBefor.getSibling()
								&& d.getSibling() <= sibling) {
							d.setSibling(d.getSibling() - 1);
							DictItemLocalServiceUtil.updateDictItem(d);
						}
					}
				} else {
					// item move up
					for (DictItem d : items) {
						if (d.getDictItemId() != dictItem.getDictItemId()
								&& d.getSibling() >= sibling
								&& d.getSibling() < dictItemBefor.getSibling()) {
							d.setSibling(d.getSibling() + 1);
							DictItemLocalServiceUtil.updateDictItem(d);
						}
					}
				}
			} else {
				// item change parent
				// update current tree
				for (DictItem d : items) {
					if (d.getDictItemId() != dictItem.getDictItemId()
							&& d.getSibling() >= sibling) {
						d.setSibling(d.getSibling() + 1);
						DictItemLocalServiceUtil.updateDictItem(d);
					}
				}
				// update previous tree
				items = DictItemLocalServiceUtil.getBy_D_P(dictItemBefor
						.getDictCollectionId(),
						dictItemBefor.getParentItemId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, DataMgtUtil
								.getDictItemOrderByComparator(
										DictItemDisplayTerms.SIBLING,
										WebKeys.ORDER_BY_ASC));
				for (DictItem d : items) {
					if (d.getSibling() > dictItemBefor.getSibling()) {
						d.setSibling(d.getSibling() - 1);
						DictItemLocalServiceUtil.updateDictItem(d);
					}
				}

				// update tree index for children tree
				items = DictItemLocalServiceUtil
						.getDictItemsByParentItemId(dictItem.getDictItemId());
				updateTreeIndexChildrenTree(dictItem, items);
			}
		}
	}

	private void updateTreeIndexChildrenTree(DictItem parent,
			List<DictItem> children) throws SystemException {
		List<DictItem> subItem = new ArrayList<DictItem>();
		String regex = "^.+" + String.valueOf(parent.getDictItemId());
		String newTreIndex = StringPool.BLANK;
		for (DictItem dictItem : children) {
			newTreIndex = parent.getTreeIndex()
					+ (StringPool.SPACE + dictItem.getTreeIndex()).replaceAll(
							regex, StringPool.BLANK);
			dictItem.setTreeIndex(newTreIndex);
			DictItemLocalServiceUtil.updateDictItem(dictItem);
			subItem = DictItemLocalServiceUtil
					.getDictItemsByParentItemId(dictItem.getDictItemId());
			updateTreeIndexChildrenTree(dictItem, subItem);
		}
	}

	private void updateDictItemLinked(long dictItemId,
			long[] dictItemsTypeId, long sequenceNo, boolean update,
			ServiceContext serviceContext) throws SystemException {

		// delete dictItemLinked
		if (update) {
			List<DictItemType> dictItemsType = DictItemTypeLocalServiceUtil
					.getByDictItemId(dictItemId);
			for (DictItemType dictItemType : dictItemsType) {
				DictItemTypeLocalServiceUtil.deleteDictItemType(dictItemType);
			}
		}

		// add dictItemLinked
		for (long dictItemTypeId : dictItemsTypeId) {
			DictItemTypeLocalServiceUtil.addDictItemType(dictItemId,
					dictItemTypeId, sequenceNo, serviceContext);
		}
	}

	protected void validatetDictCollection(long collectionId,
			String collectionName, String collectionCode,
			ServiceContext serviceContext)
			throws OutOfLengthCollectionCodeException,
			OutOfLengthCollectionNameException, DuplicateCollectionException,
			EmptyCollectionCodeException, EmptyDictCollectionNameException {

		if (Validator.isNull(collectionCode)) {
			throw new EmptyCollectionCodeException();
		}

		if (collectionCode.trim()
				.length() > PortletPropsValues.DATAMGT_DICTCOLLECTION_CODE_LENGHT) {
			throw new OutOfLengthCollectionCodeException();
		}

		if (Validator.isNull(collectionName)) {
			throw new EmptyDictCollectionNameException();
		}

		if (collectionName
				.length() > PortletPropsValues.DATAMGT_DICTCOLLECTION_NAME_LENGHT) {
			throw new OutOfLengthCollectionNameException();
		}

		if (collectionId > 0) {
			DictCollection dictCollection = null;
			try {
				dictCollection = DictCollectionLocalServiceUtil
						.getDictCollection(serviceContext.getScopeGroupId(),
								collectionCode);

			} catch (Exception e) {
				// Nothing to do
			}

			if (dictCollection != null
					&& dictCollection.getDictCollectionId() != collectionId) {
				throw new DuplicateCollectionException();
			}
		}
	}

	public static void validatetDictItem(long dictItemId, String itemName,
			String itemCode, ServiceContext serviceContext)
			throws EmptyItemCodeException, OutOfLengthItemCodeException,
			EmptyDictItemNameException, OutOfLengthItemNameException,
			DuplicateItemException {

		if (Validator.isNull(itemCode)) {
			throw new EmptyItemCodeException();
		}

		if (itemCode.trim()
				.length() > PortletPropsValues.DATAMGT_DICTITEM_CODE_LENGHT) {
			throw new OutOfLengthItemCodeException();
		}

		if (Validator.isNull(itemName)) {
			throw new EmptyDictItemNameException();
		}

		if (itemName
				.length() > PortletPropsValues.DATAMGT_DICTITEM_NAME_LENGHT) {
			throw new OutOfLengthItemNameException();
		}

		if (dictItemId > 0) {
			DictItem dictItem = null;
			try {
				dictItem = DictItemLocalServiceUtil.getDictItem(dictItemId);
			} catch (Exception e) {
				// Nothing to do
			}

			if (dictItem != null && dictItem.getDictItemId() != dictItemId) {
				throw new DuplicateItemException();
			}
		}
	}
	
	public void updateDatabaseDictitems(ActionRequest actionRequest,
			ActionResponse actionResponse) throws SystemException,
			NoSuchDictCollectionException, PortalException {

		String actionKey = ParamUtil.getString(actionRequest, "actionKey");
		
		int actionMode = ParamUtil.getInteger(actionRequest,
				"actionMode");
		long dictCollectionId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.DICTCOLLECTION_ID);

		if (actionKey.equals("numbered-sibling")) {
			switch (actionMode) {
			case 1:
				_numberedSiblingDictItems(0);
				break;
			case 2:
				_numberedSiblingDictItems(dictCollectionId);
				break;
			default:
				break;
			}
		} 
		else if (actionKey.equals("update-tree-index")) {
			switch (actionMode) {
			case 1:
				_updateTreeIndexDictItems(0);
				break;
			case 2:
				_updateTreeIndexDictItems(dictCollectionId);
				break;
			default:
				break;
			}
		}
		
		actionResponse
				.setRenderParameter("mvcPath",
						"/html/portlets/data_management/admin/display/update_items.jsp");
	}
	
	private void _updateTreeIndexDictItems(long dictCollectionId)
			throws SystemException, NoSuchDictCollectionException,
			PortalException {

		List<DictCollection> dictCollections = new ArrayList<DictCollection>();

		if (dictCollectionId > 0) {
			dictCollections.add(DictCollectionLocalServiceUtil
					.getDictCollection(dictCollectionId));
		} else {
			dictCollections = DictCollectionLocalServiceUtil
					.getDictCollections();
		}
		_log.info("~~~~~~~~~~~~~~~>>> update treeIndex for items in dictCollectionId: "
				+ dictCollectionId);
		for (DictCollection dictCollection : dictCollections) {
			List<DictItem> rootItems = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollection.getDictCollectionId(), 0);
			_updateTreeIndexDictItem(dictCollection.getDictCollectionId(),
					rootItems, StringPool.BLANK);
		}

	}

	private void _updateTreeIndexDictItem(long dictCollectionId,
			List<DictItem> dictItems, String preTreeIndex)
			throws SystemException {

		for (DictItem dictItem : dictItems) {

			dictItem.setTreeIndex(preTreeIndex + dictItem.getDictItemId());
			DictItemLocalServiceUtil.updateDictItem(dictItem);
			_log.info("~~~~~~~~~~~~~~~>>> dictItem id: "
					+ dictItem.getDictItemId() + "  treeIndex: "
					+ dictItem.getTreeIndex());

			List<DictItem> subDictItems = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollectionId, dictItem.getDictItemId());
			_updateTreeIndexDictItem(dictCollectionId, subDictItems,
					dictItem.getTreeIndex() + StringPool.PERIOD);
		}
	}

	private void _numberedSiblingDictItems(long dictCollectionId)
			throws SystemException, NoSuchDictCollectionException,
			PortalException {
		
		List<DictCollection> dictCollections = new ArrayList<DictCollection>();
		
		if (dictCollectionId > 0) {
			dictCollections.add(DictCollectionLocalServiceUtil
					.getDictCollection(dictCollectionId));
		} else {
			dictCollections = DictCollectionLocalServiceUtil
					.getDictCollections();
		}

		for (DictCollection dictCollection : dictCollections) {
			List<DictItem> rootItems = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollection.getDictCollectionId(), 0);
			_numberedSiblingItemsTree(dictCollection.getDictCollectionId(),
					rootItems);
		}

	}

	private void _numberedSiblingItemsTree(long dictCollectionId,
			List<DictItem> dictItems) throws SystemException {
		long sibling = 0;
		for (DictItem dictItem : dictItems) {
			sibling++;
			dictItem.setSibling(sibling);
			DictItemLocalServiceUtil.updateDictItem(dictItem);

			List<DictItem> subDictItems = DictItemLocalServiceUtil
					.getDictItemsInUseByDictCollectionIdAndParentItemId(
							dictCollectionId, dictItem.getDictItemId());
			_numberedSiblingItemsTree(dictCollectionId, subDictItems);
		}
	}

	public void updateDictPermissions(ActionRequest actionRequest,
			ActionResponse actionResponse) throws PortalException,
			SystemException {

		long userId = ParamUtil.getLong(actionRequest,
				DictItemDisplayTerms.USER_ID);

		boolean addCollectionPermission = ParamUtil.getBoolean(actionRequest,
				"addCollectionPermission");
		boolean viewPermissionAll = ParamUtil.getBoolean(actionRequest,
				"viewPermissionAll");
		boolean addPermissionAll = ParamUtil.getBoolean(actionRequest,
				"addPermissionAll");
		boolean editPermissionAll = ParamUtil.getBoolean(actionRequest,
				"editPermissionAll");
		boolean deletePermissionAll = ParamUtil.getBoolean(actionRequest,
				"deletePermissionAll");

		ServiceContext serviceContext = ServiceContextFactory
				.getInstance(actionRequest);

		if (addCollectionPermission) {
			DictPermissionsLocalServiceUtil.addDictPermission(userId, -1, true,
					true, true, true, serviceContext);
		}

		List<DictCollection> collections = DictCollectionLocalServiceUtil
				.getDictCollections(serviceContext.getScopeGroupId());
		Map<Long, DictPermissions> dictPermissMap = new HashMap<Long, DictPermissions>();

		for (DictCollection col : collections) {

			DictPermissions permiss = new DictPermissionsImpl();

			permiss.setUserIdMap(userId);
			permiss.setDictCollectionId(col.getDictCollectionId());

			permiss.setCompanyId(serviceContext.getCompanyId());
			permiss.setGroupId(serviceContext.getScopeGroupId());
			permiss.setUserId(serviceContext.getUserId());
			permiss.setCreateDate(new Date());

			permiss.setView(viewPermissionAll);
			permiss.setAdd(addPermissionAll);
			permiss.setEdit(editPermissionAll);
			permiss.setDelete(deletePermissionAll);

			dictPermissMap.put(col.getDictCollectionId(), permiss);
		}

		String viewPermissions = ParamUtil.getString(actionRequest,
				"viewPermissions");
		String addPermissions = ParamUtil.getString(actionRequest,
				"addPermissions");
		String editPermissions = ParamUtil.getString(actionRequest,
				"editPermissions");
		String deletePermissions = ParamUtil.getString(actionRequest,
				"deletePermissions");

		List<String> view = ListUtil.toList(StringUtil.split(viewPermissions));
		List<String> add = ListUtil.toList(StringUtil.split(addPermissions));
		List<String> edit = ListUtil.toList(StringUtil.split(editPermissions));
		List<String> delete = ListUtil.toList(StringUtil
				.split(deletePermissions));

		DictPermissions permiss = null;
		if (!viewPermissionAll) {
			for (String str : view) {
				try {
					permiss = dictPermissMap.get(Long.parseLong(str));
					permiss.setView(true);
				} catch (Exception e) {
				}
			}
		}
		if (!addPermissionAll) {
			for (String str : add) {
				try {
					permiss = dictPermissMap.get(Long.parseLong(str));
					permiss.setAdd(true);
				} catch (Exception e) {
				}
			}
		}
		if (!editPermissionAll) {
			for (String str : edit) {
				try {
					permiss = dictPermissMap.get(Long.parseLong(str));
					permiss.setEdit(true);
				} catch (Exception e) {
				}
			}
		}
		if (!deletePermissionAll) {
			for (String str : delete) {
				try {
					permiss = dictPermissMap.get(Long.parseLong(str));
					permiss.setDelete(true);
				} catch (Exception e) {
				}
			}
		}

		Set<Long> keySet = dictPermissMap.keySet();
		DictPermissions permission = null;
		for (Long l : keySet) {
			permission = dictPermissMap.get(l);
			DictPermissionsLocalServiceUtil.updateDictPermissions(permission);
		}
	}

	private Log _log = LogFactoryUtil
			.getLog(DataMamagementPortlet.class.getName());
}
