package com.neomind.fusion.custom.tecnoperfil.bitrix;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


import javax.swing.text.MaskFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import com.neomind.fusion.workflow.exception.WorkflowException;

import bsh.ParseException;

public class BitrixConnect {
	private String urlBase;
	private String token;

	public void configure(String urlBase, String token) {
		this.urlBase = urlBase;
		this.token = token;
		
	}

	private StringBuilder getUrl() {
		StringBuilder url = new StringBuilder();

		url.append(urlBase);
		url.append(token);
		url.append("/");

		return url;
	}

	public JSONObject getData(String method) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();

		StringBuilder url = getUrl();
		url.append(method);

		HttpPost httppost = new HttpPost(url.toString());
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		String result = Conversor.convertStreamToString(entity.getContent());

		JSONObject json = Conversor.stringToJson(result);

		return json;
	}

	public JSONObject postData(String method, List<NameValuePair> params) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();

		StringBuilder url = getUrl();
		url.append(method);

		HttpPost httppost = new HttpPost(url.toString());

		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		System.out.println("Enviando");
		System.out.println(method);
		System.out.println(params);

		HttpResponse response = httpclient.execute(httppost);
		System.out.println("Entity enviada:");
		System.out.println(httppost.getEntity().getContent());
		HttpEntity entity = response.getEntity();

		System.out.println("RETORNO");
		String result = Conversor.convertStreamToString(entity.getContent());
		System.out.println(result);

		JSONObject json = Conversor.stringToJson(result);

		return json;
	}

	public JSONObject findUserByEmail(String email) throws Exception {
		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("filter[EMAIL]", email));

		JSONObject response = postData("user.get", params);

		return response;

	}

	public JSONObject findCompany(String razaoSocial, String codigoKugel) throws Exception {
		List<NameValuePair> params = new ArrayList<>();

		// params.add(new BasicNameValuePair("filter[TITLE]", razaoSocial));
		params.add(new BasicNameValuePair("filter[UF_CRM_5DA47CA729CB2]", codigoKugel.trim()));
		params.add(new BasicNameValuePair("select[]", "UF_CRM_5DA47CA729CB2"));
		params.add(new BasicNameValuePair("select[]", "ID"));
		params.add(new BasicNameValuePair("select[]", "TITLE"));
		params.add(new BasicNameValuePair("select[]", "ASSIGNED_BY_ID"));

		JSONObject response = postData("crm.company.list", params);

		return response;
	}

	public JSONObject getDeal(String dealId) throws Exception {
		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("ID", dealId));

		JSONObject response = postData("crm.deal.get", params);

		return response;
	}

	public boolean createCompany(CompanyRequest companyRequest) {
		try {
			List<NameValuePair> params = new ArrayList<>();

			params.add(new BasicNameValuePair("fields[TITLE]", companyRequest.getTitle()));

			if (companyRequest.getCodigoKugel() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA729CB2]", companyRequest.getCodigoKugel()));
			}

			if (companyRequest.getNomeFantasia() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA482DFD420A]", companyRequest.getNomeFantasia()));// UF_CRM_5DA482DFD420A
			}

			if (companyRequest.getCidade() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA75E560]", companyRequest.getCidade()));
			}

			if (companyRequest.getBairro() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA7642F4]", companyRequest.getBairro()));
			}

			if (companyRequest.getComplemento() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA77DBA0]", companyRequest.getComplemento()));
			}

			if (companyRequest.getEndereco() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_1636390371]", companyRequest.getEndereco()));
			}
			
			if (companyRequest.getNumero() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_1636483102]", companyRequest.getNumero()));
			}

			if (companyRequest.getEmails() != null && !companyRequest.getEmails().isEmpty()) {
				List<String> emails = companyRequest.getEmails();
				int position = 0;

				for (String email : emails) {
					params.add(new BasicNameValuePair("fields[EMAIL][" + position + "][VALUE]", email));
					params.add(new BasicNameValuePair("fields[EMAIL][" + position + "][VALUE_TYPE]", "WORK"));

					position++;
				}
			}

			if (companyRequest.getEstado() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA74E55C]", companyRequest.getEstado()));
			}

			if (companyRequest.getCep() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA7733DA]", companyRequest.getCep()));
			}

			if (companyRequest.getCnpj() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA7349C0]",
						addMask(companyRequest.getCnpj(), "##.###.###/####-##")));
			}

			if (companyRequest.getInscricaoEstadual() != null) {
				params.add(
						new BasicNameValuePair("fields[UF_CRM_5DA47CA7423B5]", companyRequest.getInscricaoEstadual()));
			}

			if (companyRequest.getCategoria() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_1586362535]", companyRequest.getCategoria()));
			}

			if (companyRequest.getClassificacao() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_1586355060734]", companyRequest.getClassificacao()));
			}

			if (companyRequest.getCpf() != null) {
				params.add(new BasicNameValuePair("fields[UF_CRM_5DA47CA73B846]",
						addMask(companyRequest.getCpf(), "###.###.###-##")));
			}

			if (companyRequest.getResponsavel() != null) {
				params.add(new BasicNameValuePair("fields[ASSIGNED_BY_ID]", companyRequest.getResponsavel()));
				params.add(new BasicNameValuePair("fields[CREATED_BY_ID]", companyRequest.getResponsavel()));
			}

			if (companyRequest.getTelefones() != null && !companyRequest.getTelefones().isEmpty()) {
				int phoneIndex = 0;
				List<String> telefones = companyRequest.getTelefones();

				for (String telefone : telefones) {
					params.add(new BasicNameValuePair("fields[PHONE][" + phoneIndex + "][VALUE]", telefone));
					params.add(new BasicNameValuePair("fields[PHONE][" + phoneIndex + "][VALUE_TYPE]", "WORK"));

					phoneIndex++;
				}
			}

			/*
			 * if (companyRequest.getTelefone() != null) { String telefone = "0015" +
			 * companyRequest.getTelefone(); params.add(new
			 * BasicNameValuePair("fields[PHONE][" + phoneIndex + "][VALUE]", telefone));
			 * params.add(new BasicNameValuePair("fields[PHONE][" + phoneIndex +
			 * "][VALUE_TYPE]", "WORK"));
			 * 
			 * phoneIndex++; }
			 * 
			 * if (companyRequest.getCelular() != null) { String telefone = "0015" +
			 * companyRequest.getCelular(); params.add(new
			 * BasicNameValuePair("fields[PHONE][" + phoneIndex + "][VALUE]", telefone));
			 * params.add(new BasicNameValuePair("fields[PHONE][" + phoneIndex +
			 * "][VALUE_TYPE]", "WORK"));
			 * 
			 * phoneIndex++; }
			 * 
			 * if (companyRequest.getWhats() != null) { String telefone = "55" +
			 * companyRequest.getWhats(); params.add(new BasicNameValuePair("fields[PHONE]["
			 * + phoneIndex + "][VALUE]", telefone)); params.add(new
			 * BasicNameValuePair("fields[PHONE][" + phoneIndex + "][VALUE_TYPE]", "WORK"));
			 * 
			 * phoneIndex++; }
			 */

			System.out.println("BITRIX NEW COMPANY");
			System.out.println(params);

			JSONObject response = postData("crm.company.add", params);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean createDeal(DealRequest dealRequest) {
		try {
			List<NameValuePair> params = new ArrayList<>();

			fillDeal(params, dealRequest);

			JSONObject response = postData("crm.deal.add", params);

			Long dealId = (Long) response.get("result");

			dealRequest.setId(String.valueOf(dealId));
			return true;
		} catch (Exception e) {
			e.printStackTrace();

			throw new WorkflowException("Erro ao efetuar consulta no bitrix: " + e.getMessage());
		}
	}

	private String addMask(String cnpj, String maskStr) {
		try {
			MaskFormatter mask = new MaskFormatter(maskStr);
			mask.setValueContainsLiteralCharacters(false);
			System.out.println("CNPJ : " + mask.valueToString(cnpj));

			return mask.valueToString(cnpj);
		} catch (Exception ex) {
			return cnpj;
		}
	}

	public boolean createComment(CommentRequest request) {
		List<NameValuePair> params = new ArrayList<>();
		
		if(request.getFile() != null) {
			String file64 = Conversor.encodeFileToBase64(request.getFile());
			params.add(new BasicNameValuePair("fields[FILES][]", file64));			
		}
		params.add(new BasicNameValuePair("fields[COMMENT]", request.getComment()));
		params.add(new BasicNameValuePair("fields[ENTITY_ID]", request.getEnityId()));
		params.add(new BasicNameValuePair("fields[ENTITY_TYPE]", request.getEntityType()));
		try {
			postData("crm.timeline.comment.add", params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new WorkflowException("Erro ao efetuar consulta no bitrix: " + e.getMessage());
		}
		return false;
	}

	public boolean updateDeal(DealRequest dealRequest, String dealId) {
		try {
			List<NameValuePair> params = new ArrayList<>();

			params.add(new BasicNameValuePair("id", dealId));

			fillDeal(params, dealRequest);

			JSONObject response = postData("crm.deal.update", params);

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	private void fillDeal(List<NameValuePair> params, DealRequest dealRequest) {

		if (dealRequest.getTitle() != null) {
			params.add(new BasicNameValuePair("fields[TITLE]", dealRequest.getTitle()));
		}
		
		if(dealRequest.getCategoria() != null) {
			params.add(new BasicNameValuePair("fields[CATEGORY_ID]", dealRequest.getCategoria()));
		}
		
		if (dealRequest.getId() != null) {
			params.add(new BasicNameValuePair("fields[ID]", dealRequest.getId().toString()));
		}
		if (dealRequest.getCompanyId() != null) {
			params.add(new BasicNameValuePair("fields[COMPANY_ID]", dealRequest.getCompanyId().toString()));
		}
		if (dealRequest.getStageId() != null) {
			params.add(new BasicNameValuePair("fields[STAGE_ID]", dealRequest.getStageId()));
		}

		if (dealRequest.getOpportunity() != null) {
			params.add(new BasicNameValuePair("fields[OPPORTUNITY]", dealRequest.getOpportunity()));
		}

		if (dealRequest.getRequestNumber() != null) {
			params.add(new BasicNameValuePair("fields[UF_CRM_1572961645966]", dealRequest.getRequestNumber()));
		}

		if (dealRequest.getResponsavel() != null) {
			params.add(new BasicNameValuePair("fields[ASSIGNED_BY_ID]", dealRequest.getResponsavel()));
			params.add(new BasicNameValuePair("fields[CREATED_BY_ID]", dealRequest.getResponsavel()));
		}

	}
}