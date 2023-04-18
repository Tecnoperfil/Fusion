package com.neomind.fusion.custom.tecnoperfil.bitrix;

import java.io.File;

public class DealRequest {

	private String id;
	private String requestNumber;
	private String title;
	private String stageId;
	private String value;
	private String opportunity;
	private String companyId;
	private File proposta;
	private String responsavel;
	private String categoria;
	
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getRequestNumber() {
		return requestNumber;
	}

	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}

	public File getProposta() {
		return proposta;
	}

	public void setProposta(File proposta) {
		this.proposta = proposta;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	private String comments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOpportunity() {
		return opportunity;
	}

	public void setOpportunity(String opportunity) {
		this.opportunity = opportunity;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
}
