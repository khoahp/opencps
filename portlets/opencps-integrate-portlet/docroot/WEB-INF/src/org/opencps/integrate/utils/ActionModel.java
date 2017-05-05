package org.opencps.integrate.utils;

import java.util.Date;

public class ActionModel {
	public String getActionCode() {
		return actionCode;
	}
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getActionNote() {
		return actionNote;
	}
	public void setActionNote(String actionNote) {
		this.actionNote = actionNote;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Date getReceviceDate() {
		return receviceDate;
	}
	public void setReceviceDate(Date receviceDate) {
		this.receviceDate = receviceDate;
	}
	public String getDossierNo() {
		return dossierNo;
	}
	public void setDossierNo(String dossierNo) {
		this.dossierNo = dossierNo;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getFinishedDate() {
		return finishedDate;
	}
	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}
	String actionCode;
	String actionName;
	String actionNote;
	Date modifiedDate;
	Date receviceDate;
	String dossierNo;
	Date dueDate;
	Date finishedDate;
}
