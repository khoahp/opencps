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

import org.opencps.util.WebKeys;

public class PostalKeys extends WebKeys {
	
	public static final String ERROR = "error";
	public static final String DOSSIER_SENDED = "dossier-sended";
	
	public static final String DOSSIER_COLLECT ="dossier-need-collect";
	public static final String DOSSIER_COLLECTED = "dossier-collected";
	
	public static final String DOSSIER_DELIVERY = "dossier-need-delivery";
	public static final String DOSSIER_DELIVERED = "dossier-delivered";
	
	public static final String API_DIEUTIN = "/postDieuTin";
	public static final String API_DELIVERY_STATUS = "/getDelivery";
	public static final String API_COLLECT_STATUS = "/getAcceptance";
	
	public static final boolean DISABLE = false;
	public static final boolean ACTIVE = true;

}