package com.opencps.intergrate.analayze;

import java.util.List;

import com.google.gson.JsonObject;

public class MessageBodyBean {
	
	/**
	 * @return the documentTypeCode
	 */
	public String getDocumentTypeCode() {
		return DocumentTypeCode;
	}
	/**
	 * @param documentTypeCode the documentTypeCode to set
	 */
	public void setDocumentTypeCode(String documentTypeCode) {
		DocumentTypeCode = documentTypeCode;
	}
	/**
	 * @return the tauBayCode
	 */
	public String getTauBayCode() {
		return TauBayCode;
	}
	/**
	 * @param tauBayCode the tauBayCode to set
	 */
	public void setTauBayCode(String tauBayCode) {
		TauBayCode = tauBayCode;
	}
	/**
	 * @return the nguoiDuaDonID
	 */
	public long getNguoiDuaDonID() {
		return NguoiDuaDonID;
	}
	/**
	 * @param nguoiDuaDonID the nguoiDuaDonID to set
	 */
	public void setNguoiDuaDonID(long nguoiDuaDonID) {
		NguoiDuaDonID = nguoiDuaDonID;
	}
	
	/**
	 * @return the documentContenJson
	 */
	public JsonObject getDocumentContenJson() {
		return DocumentContenJson;
	}
	/**
	 * @param documentContenJson the documentContenJson to set
	 */
	public void setDocumentContenJson(JsonObject documentContenJson) {
		DocumentContenJson = documentContenJson;
	}
	/**
	 * @return the attachFiles
	 */
	public List<AttachFile> getAttachFiles() {
		return attachFiles;
	}
	/**
	 * @param attachFiles the attachFiles to set
	 */
	public void setAttachFiles(List<AttachFile> attachFiles) {
		this.attachFiles = attachFiles;
	}
	
	/**
	 * @return the dossierStatus
	 */
	public String getDossierStatus() {
		return dossierStatus;
	}
	/**
	 * @param dossierStatus the dossierStatus to set
	 */
	public void setDossierStatus(String dossierStatus) {
		this.dossierStatus = dossierStatus;
	}
	
	
	/**
	 * @return the documentCode
	 */
	public String getDocumentCode() {
		return DocumentCode;
	}
	/**
	 * @param documentCode the documentCode to set
	 */
	public void setDocumentCode(String documentCode) {
		DocumentCode = documentCode;
	}
	/**
	 * @return the dossierId
	 */
	public long getDossierId() {
		return dossierId;
	}
	/**
	 * @param dossierId the dossierId to set
	 */
	public void setDossierId(long dossierId) {
		this.dossierId = dossierId;
	}
	
	/**
	 * @return the nguoiDuaDon_Name
	 */
	public String getNguoiDuaDon_Name() {
		return NguoiDuaDon_Name;
	}
	/**
	 * @param nguoiDuaDon_Name the nguoiDuaDon_Name to set
	 */
	public void setNguoiDuaDon_Name(String nguoiDuaDon_Name) {
		NguoiDuaDon_Name = nguoiDuaDon_Name;
	}
	/**
	 * @return the nguoiDuaDon_Address
	 */
	public String getNguoiDuaDon_Address() {
		return NguoiDuaDon_Address;
	}
	/**
	 * @param nguoiDuaDon_Address the nguoiDuaDon_Address to set
	 */
	public void setNguoiDuaDon_Address(String nguoiDuaDon_Address) {
		NguoiDuaDon_Address = nguoiDuaDon_Address;
	}
	/**
	 * @return the nguoiDuaDon_Email
	 */
	public String getNguoiDuaDon_Email() {
		return NguoiDuaDon_Email;
	}
	/**
	 * @param nguoiDuaDon_Email the nguoiDuaDon_Email to set
	 */
	public void setNguoiDuaDon_Email(String nguoiDuaDon_Email) {
		NguoiDuaDon_Email = nguoiDuaDon_Email;
	}
	/**
	 * @return the nguoiDuaDon_ID_Passport
	 */
	public String getNguoiDuaDon_ID_Passport() {
		return NguoiDuaDon_ID_Passport;
	}
	/**
	 * @param nguoiDuaDon_ID_Passport the nguoiDuaDon_ID_Passport to set
	 */
	public void setNguoiDuaDon_ID_Passport(String nguoiDuaDon_ID_Passport) {
		NguoiDuaDon_ID_Passport = nguoiDuaDon_ID_Passport;
	}
	/**
	 * @return the nguoiDuaDon_Tel
	 */
	public String getNguoiDuaDon_Tel() {
		return NguoiDuaDon_Tel;
	}
	/**
	 * @param nguoiDuaDon_Tel the nguoiDuaDon_Tel to set
	 */
	public void setNguoiDuaDon_Tel(String nguoiDuaDon_Tel) {
		NguoiDuaDon_Tel = nguoiDuaDon_Tel;
	}

	private String DocumentCode; // oid
	private long dossierId;
	private long NguoiDuaDonID;
	private String NguoiDuaDon_Name;
	private String NguoiDuaDon_Address;
	private String NguoiDuaDon_Email;
	private String NguoiDuaDon_ID_Passport;
	private String NguoiDuaDon_Tel;
	private String dossierStatus;
	private String DocumentTypeCode;
	private JsonObject DocumentContenJson;
    private String TauBayCode;
	private List<AttachFile> attachFiles;
	
}
