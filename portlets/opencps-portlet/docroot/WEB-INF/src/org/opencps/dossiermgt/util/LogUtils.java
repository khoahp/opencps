package org.opencps.dossiermgt.util;

import com.liferay.portal.kernel.util.StringPool;

public class LogUtils {
	public static String getCSSClassLog(String dossierStatus) {
		
		String logIcon = StringPool.BLANK;
		
		switch (dossierStatus) {
		case "new":
			logIcon = "fa-plus ";
			break;
		case "submitting":
			logIcon = "fa-send-o";
			break;
		case "receiving":
			logIcon = "fa-share-square-o";
			break;
		case "outstanding":
			logIcon = "fa-money";
			break;
		case "paying":
			logIcon = "fa-money";
			break;
		case "waiting":
			logIcon = "fa-refresh";
			break;
		case "processing":
			logIcon = "fa-cogs";
			break;
		case "handover":
			logIcon = "fa-briefcase";
			break;
		case "crosshandover":
			logIcon = "fa-bank";
			break;
		case "releasing":
			logIcon = "fa-exchange";
			break;
		case "done":
			logIcon = "fa-handshake-o";
			break;
		case "cancelled":
			logIcon = " fa-mail-reply-all";
			break;
		case "paid":
			logIcon = " fa-money";
			break;

		default:
			break;
		}
		
		return logIcon;
	}
}
