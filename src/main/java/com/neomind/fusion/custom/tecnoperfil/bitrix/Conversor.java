package com.neomind.fusion.custom.tecnoperfil.bitrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Conversor {
	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static JSONObject stringToJson(String jsonString) throws Exception {
		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(jsonString);

		return json;
	}
	
	public static String encodeFileToBase64(File file) {

	    try {
	        byte[] fileContent = Files.readAllBytes(file.toPath());
	        return Base64.getEncoder().encodeToString(fileContent);
	    } catch (IOException e) {
	        throw new IllegalStateException("Erro ao anexar o arquivo: " + file, e);
	    }
	}
	
	public static String getStateCode(String sigla) {
		Map<String, String> estados = new HashMap<String, String>();

		estados.put("AC", "152");
		estados.put("AL", "154");
		estados.put("AP", "156");
		estados.put("AM", "158");
		estados.put("BA", "160");
		estados.put("CE", "162");
		estados.put("DF", "164");
		estados.put("ES", "166");
		estados.put("GO", "168");
		estados.put("MA", "170");
		estados.put("MT", "172");
		estados.put("MS", "174");
		estados.put("MG", "176");
		estados.put("PA", "178");
		estados.put("PB", "180");
		estados.put("PR", "182");
		estados.put("PE", "184");
		estados.put("PI", "186");
		estados.put("RJ", "188");
		estados.put("RN", "190");
		estados.put("RS", "192");
		estados.put("RO", "194");
		estados.put("RR", "196");
		estados.put("SC", "198");
		estados.put("SP", "200");
		estados.put("SE", "202");
		estados.put("TO", "204");

		return estados.get(sigla);
	}
}