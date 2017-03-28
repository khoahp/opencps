package com.opencps.intergrate.analayze;

public class AttachFile {
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return Code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		Code = code;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return FileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return FileUrl;
	}
	/**
	 * @param fileUrl the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		FileUrl = fileUrl;
	}
	
	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return FileExtension;
	}
	/**
	 * @param fileExtension the fileExtension to set
	 */
	public void setFileExtension(String fileExtension) {
		FileExtension = fileExtension;
	}

	private String FileExtension;
	private String Code;
	private String FileName;
	private String FileUrl;
}
