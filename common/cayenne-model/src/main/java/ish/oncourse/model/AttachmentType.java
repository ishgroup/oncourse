package ish.oncourse.model;


import org.apache.commons.lang.StringUtils;

public enum AttachmentType {
	
	JPEG("image/jpeg", "jpeg"),
	GIF("image/gif", "gif"),
	PNG("image/png", "png"),
	BMP("image/bmp", "bmp"),
	TIFF("image/tiff", "tiff"),
	PDF("application/pdf","pdf"),
	DOC("application/msword","doc"),
	XLS("application/vnd.ms-excel","xls"),
	ZIP("application/zip","zip"),
	GZIP("application/x-gzip","gz"),
	TXT("application/txt","txt");
	
	private String mimeType;
	private String extention;
	
	private AttachmentType(String mimeType, String extention){
		this.mimeType = mimeType;
		this.extention = extention;
	}
	
	public static String getExtentionByMimeType(String mimeType) {
		for(AttachmentType type:values()){
			if(type.mimeType.equals(mimeType)){
				return type.extention;
			}
		}

		return StringUtils.EMPTY;
	}

	
}
