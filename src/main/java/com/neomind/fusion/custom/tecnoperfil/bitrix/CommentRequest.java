package com.neomind.fusion.custom.tecnoperfil.bitrix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

public class CommentRequest {
	private String comment;
	private File file;
	private String enityId;
	private String entityType;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getEnityId() {
		return enityId;
	}

	public void setEnityId(String enityId) {
		this.enityId = enityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}
