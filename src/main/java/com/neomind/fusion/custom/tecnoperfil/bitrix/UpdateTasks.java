package com.neomind.fusion.custom.tecnoperfil.bitrix;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.neomind.fusion.workflow.exception.WorkflowException;

public class UpdateTasks {

	public static void main(String[] args) {
		BitrixConnect connect = getConnector();
		
		try {
			String idUser = (String) findUser("gecinei.cristofolini@tecnoperfil.com.br").get("ID");
			
			System.out.println(idUser);
		} catch (Exception e) {

		}
	}

	private static BitrixConnect getConnector() {
		BitrixConnect connect = new BitrixConnect();

		return connect;
	}

	public static JSONObject findUser(String email) {
		try {
			System.out.println("BITRIX - BUSCA POR EMAIL" + email);

			JSONObject response = getConnector().findUserByEmail(email);
			JSONArray result = (JSONArray) response.get("result");

			if (result.size() > 0) {
				return (JSONObject) result.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();

			throw new WorkflowException("Erro ao efetuar consulta no bitrix");
		}
	}

}
