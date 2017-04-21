/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.opencps.dossiermgt.service.impl;

import java.util.Date;
import java.util.List;

import org.opencps.dossiermgt.NoSuchDossierPartException;
import org.opencps.dossiermgt.comparator.DossierPartSiblingComparator;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.service.base.DossierPartLocalServiceBaseImpl;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the dossier part local service. <p> All custom service
 * methods should be put in this class. Whenever methods are added, rerun
 * ServiceBuilder to copy their definitions into the
 * {@link org.opencps.dossiermgt.service.DossierPartLocalService} interface. <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM. </p>
 *
 * @author trungnt
 * @see org.opencps.dossiermgt.service.base.DossierPartLocalServiceBaseImpl
 * @see org.opencps.dossiermgt.service.DossierPartLocalServiceUtil
 */
public class DossierPartLocalServiceImpl
	extends DossierPartLocalServiceBaseImpl {

	public DossierPart addDossierPart(
		long dossierTemplateId, String partNo, String partName, String partTip,
		int partType, long parentId, double sibling, String formScript,
		String formReport, String sampleData, boolean required,
		String templateFileNo, long userId, boolean hasSign, ServiceContext serviceContext)
		throws SystemException, NoSuchDossierPartException {

		long dossierPartId =
			CounterLocalServiceUtil.increment(DossierPart.class.getName());
		DossierPart dossierPart = dossierPartPersistence.create(dossierPartId);

		String treeIndex = getTreeIndex(sibling,parentId);

		Date currentDate = new Date();

		dossierPart.setUserId(userId);
		dossierPart.setCompanyId(serviceContext.getCompanyId());
		dossierPart.setGroupId(serviceContext.getScopeGroupId());
		dossierPart.setCreateDate(currentDate);
		dossierPart.setModifiedDate(currentDate);

		dossierPart.setDossierTemplateId(dossierTemplateId);
		dossierPart.setPartNo(partNo);
		dossierPart.setPartName(partName);
		dossierPart.setPartTip(partTip);
		dossierPart.setPartType(partType);
		dossierPart.setParentId(parentId);
		dossierPart.setSibling(sibling);
		dossierPart.setFormScript(formScript);
		dossierPart.setFormReport(formReport);
		dossierPart.setSampleData(sampleData);
		dossierPart.setRequired(required);
		dossierPart.setTemplateFileNo(templateFileNo);
		dossierPart.setTreeIndex(treeIndex);
		dossierPart.setHasSign(hasSign);
		
		return dossierPartPersistence.update(dossierPart);
	}

	public DossierPart updateDossierPart(
		long dossierPartId, long dossierTemplateId, String partNo,
		String partName, String partTip, int partType, long parentId,
		double sibling, String formScript, String formReport,
		String sampleData, boolean required, String templateFileNo,

		long userId,boolean hasSign, ServiceContext serviceContext)
		throws SystemException, NoSuchDossierPartException {

		DossierPart dossierPartBeforeUpdateSibling =
			dossierPartPersistence.fetchByPrimaryKey(dossierPartId);
		
		double siblingBefore = dossierPartBeforeUpdateSibling.getSibling();
		if(siblingBefore != sibling) {
			DossierPart dossierPartSwapPosition = dossierPartPersistence
					.findByT_S(dossierTemplateId, sibling);
			String treeIndex = getTreeIndex(siblingBefore,
					dossierPartSwapPosition.getParentId());
			
			dossierPartSwapPosition.setSibling(siblingBefore);
			dossierPartSwapPosition.setTreeIndex(treeIndex);
			dossierPartPersistence.update(dossierPartSwapPosition);
			updateTreeIndexChirls(dossierPartSwapPosition.getDossierpartId());
			
			//update this dossierpart sibling
			String thisTreeIndex = getTreeIndex(sibling,
					dossierPartBeforeUpdateSibling.getParentId());
			
			dossierPartBeforeUpdateSibling.setSibling(sibling);
			dossierPartBeforeUpdateSibling.setTreeIndex(thisTreeIndex);
			dossierPartPersistence.update(dossierPartBeforeUpdateSibling);
			updateTreeIndexChirls(dossierPartBeforeUpdateSibling.getDossierpartId());
			
		}
		DossierPart dossierPart =
				dossierPartPersistence.fetchByPrimaryKey(dossierPartId);
		Date currentDate = new Date();

		dossierPart.setUserId(userId);
		dossierPart.setCompanyId(serviceContext.getCompanyId());
		dossierPart.setGroupId(serviceContext.getScopeGroupId());
		dossierPart.setCreateDate(currentDate);
		dossierPart.setModifiedDate(currentDate);

		dossierPart.setDossierTemplateId(dossierTemplateId);
		dossierPart.setPartName(partName);
		dossierPart.setPartNo(partNo);
		dossierPart.setPartTip(partTip);
		dossierPart.setPartType(partType);
		dossierPart.setParentId(parentId);
		dossierPart.setFormScript(formScript);
		dossierPart.setFormReport(formReport);
		dossierPart.setSampleData(sampleData);
		dossierPart.setRequired(required);
		dossierPart.setTemplateFileNo(templateFileNo);
		dossierPart.setHasSign(hasSign);
		
		return dossierPartPersistence.update(dossierPart);
	}

	public void deleteDossierPartById(long dossierPartId)
		throws NoSuchDossierPartException, SystemException {

		int dossierPartParentCount =
			dossierPartPersistence.countByParentId(dossierPartId);
		// update sibling before delete
		updateSiblings(dossierPartId);
		if(dossierPartParentCount != 0) {
			// delete all chirld
			deleteAllChilds(dossierPartId);
		}
		
		//delete this
		dossierPartPersistence.remove(dossierPartId);
	}
	@Deprecated
	public String getTreeIndex(long parentId, long dossierPartId)
		throws SystemException, NoSuchDossierPartException {

		if (parentId == 0) {
			return String.valueOf(dossierPartId);
		}
		else if (parentId > 0) {
			DossierPart dossierPart =
				dossierPartPersistence.fetchByPrimaryKey(parentId);
			return dossierPart.getTreeIndex() + StringPool.PERIOD +
				String.valueOf(dossierPartId);
		}
		else {
			throw new NoSuchDossierPartException();
		}
	}
	
	public String getTreeIndex(double sibling, long parentId)
			throws SystemException {
		String treeIndex = StringPool.BLANK;
		if (parentId == 0) {
			treeIndex = String.valueOf((int)sibling);
		} else {
			DossierPart dossierPart =
					dossierPartPersistence.fetchByPrimaryKey(parentId);
			treeIndex = dossierPart.getTreeIndex() +
					StringPool.PERIOD +
					String.valueOf((int) sibling);
		}
		
		return treeIndex;
	}

	public List<DossierPart> getDossierParts(
		int start, int end, OrderByComparator orderByComparator)
		throws SystemException {

		return dossierPartPersistence.findAll(start, end, orderByComparator);
	}

	public List<DossierPart> getDossierParts(String partName)
		throws SystemException {

		return dossierPartPersistence.findByPartName(partName);
	}

	public int countAll()
		throws SystemException {

		return dossierPartPersistence.countAll();
	}

	public List<DossierPart> getDossierParts(long dossierTemplateId)
		throws SystemException {

		return dossierPartPersistence.findByDossierTemplateId(dossierTemplateId);
	}
	
	public DossierPart getDossierPartByT_S_P(long dossierTemplateId, double sibling,
			long parentId) throws NoSuchDossierPartException, SystemException {
		return dossierPartPersistence
				.findByT_S_P(dossierTemplateId, sibling, parentId);
	}

	public List<DossierPart> getDossierParts(
		long dossierTemplateId, int start, int end)
		throws SystemException {

		boolean orderByAsc = true;
		
		DossierPartSiblingComparator orderComparator =
			new DossierPartSiblingComparator(orderByAsc);

		return dossierPartPersistence.findByDossierTemplateId(
			dossierTemplateId, start, end, orderComparator);
	}

	public List<DossierPart> getDossierPartsByParentId(long parentId)
		throws SystemException {

		return dossierPartPersistence.findByParentId(parentId);
	}

	public int countByTempalteId(long dossierTemplateId)
		throws SystemException {

		return dossierPartPersistence.countByDossierTemplateId(dossierTemplateId);
	}

	public int CountByParentId(long dossierPartParentId)
		throws SystemException {

		return dossierPartPersistence.countByParentId(dossierPartParentId);
	}

	public DossierPart getDossierPartByPartNo(String partNo)
		throws NoSuchDossierPartException, SystemException {

		return dossierPartPersistence.findByPartNo(partNo);
	}

	public List<DossierPart> getDossierPartsByT_P(
		long dossierTemplateId, long parentId)
		throws SystemException {

		return dossierPartPersistence.findByT_P(dossierTemplateId, parentId);
	}
	
	public List<DossierPart> getDossierPartsByT_P_Order(
			long dossierTemplateId, long parentId)
			throws SystemException {
			boolean orderByAsc = true;
			DossierPartSiblingComparator comparator = new DossierPartSiblingComparator(orderByAsc);
			return dossierPartPersistence.findByT_P(dossierTemplateId, 
					parentId,QueryUtil.ALL_POS, QueryUtil.ALL_POS, comparator);
		}

	public List<DossierPart> getDossierPartsByT_P_PT(
		long dossierTemplateId, long parentId, int partType)
		throws SystemException {

		return dossierPartPersistence.findByT_P_PT(
			dossierTemplateId, parentId, partType);
	}

	public DossierPart getDossierPartByT_S(
		long dossierTemplateId, Double sibling)
		throws NoSuchDossierPartException, SystemException {

		return dossierPartPersistence.findByT_S(dossierTemplateId, sibling);
	}

	public List<DossierPart> getDossierPartsByT_T(
		long dossierTemplateId, int partType)
		throws SystemException {

		return dossierPartPersistence.findByT_T(dossierTemplateId, partType);
	}

	public DossierPart getDossierPartByT_PN(long templateId, String partNo)
		throws NoSuchDossierPartException, SystemException {

		return dossierPartPersistence.findByT_PN(templateId, partNo);
	}
	public DossierPart getByF_FORM_ONLINE(long dossierTemplateId, long parentId, long groupId, int partType) 
			throws NoSuchDossierPartException, SystemException {
		System.out.println("DossierPartLocalServiceImpl.getByF_FORM_ONLINE()"+dossierPartPersistence.countByF_FORM_ONLINE(dossierTemplateId, parentId, groupId, partType));
		return dossierPartPersistence.fetchByF_FORM_ONLINE_First(dossierTemplateId, parentId, groupId,partType,null);
		}
	public DossierPart getDossierPartByTFN_PN(String templateFileNo, String partNo) 
			throws NoSuchDossierPartException, SystemException {
		return dossierPartPersistence.findByTFN_PN(templateFileNo, partNo);
	}	
	
	private void deleteAllChilds(long dossierPartId) throws SystemException, NoSuchDossierPartException {
		DossierPart dossierPart = dossierPartPersistence
				.fetchByPrimaryKey(dossierPartId);
		if(Validator.isNotNull(dossierPart)) {
			List<DossierPart> dossierPartSameLevels = dossierPartPersistence
					.findByParentId(dossierPart.getDossierpartId());
			
			if(dossierPartSameLevels.size() == 0) {
				dossierPartPersistence.remove(dossierPartId);
			}
			
			for(DossierPart dossierPartIndex : dossierPartSameLevels) {
				deleteAllChilds(dossierPartIndex.getDossierpartId());
			}
		}
	}
	
	private void updateSiblings(long dossierPartId)
			throws SystemException {
		DossierPart dossierPart = dossierPartPersistence.fetchByPrimaryKey(dossierPartId);
		if(Validator.isNotNull(dossierPart)) {
			List<DossierPart> dossierPartSameLevels = dossierPartPersistence
					.findByParentId(dossierPart.getParentId());
			
			for(DossierPart dossierPartIndex : dossierPartSameLevels) {
				if(dossierPart.getSibling() < dossierPartIndex.getSibling()) {
					double siblingUpdate = dossierPartIndex.getSibling() - 1;
					String treeIndex = getTreeIndex(siblingUpdate, dossierPartIndex.getParentId());
					dossierPartIndex.setSibling(siblingUpdate);
					
					dossierPartIndex.setTreeIndex(treeIndex);
					dossierPartPersistence.update(dossierPartIndex);
					updateTreeIndexChirls(dossierPartIndex.getDossierpartId());
				}
			}
		}
	}
	
	private void updateTreeIndexChirls(long dossierPartParentId)
			throws SystemException {
		List<DossierPart> dossierPartChilds = dossierPartPersistence
				.findByParentId(dossierPartParentId);
		
		for(DossierPart dossierPartChild : dossierPartChilds) {
			String treeIndexChild = getTreeIndex(dossierPartChild.getSibling(), dossierPartParentId);
			dossierPartChild.setTreeIndex(treeIndexChild);
			dossierPartPersistence.update(dossierPartChild);
			updateTreeIndexChirls(dossierPartChild.getDossierpartId());
		}
	}
}