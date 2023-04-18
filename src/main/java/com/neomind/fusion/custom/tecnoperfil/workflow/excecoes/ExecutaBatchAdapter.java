package com.neomind.fusion.custom.tecnoperfil.workflow.excecoes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.exception.WorkflowException;

public class ExecutaBatchAdapter implements AdapterInterface {

	@Override
	public void start(Task origin, EntityWrapper processEntity, Activity activity) {
		String retorno;
		try {
			String comando = processEntity.findGenericValue("acao.Bat");
			String url = processEntity.findGenericValue("acao.urlcmd");
			retorno = executeCmd(comando, url);
			processEntity.setValue("RetBat", retorno);
		} catch(Exception e) {
			retorno = "Erro ao executar arquivo";
			
			processEntity.setValue("RetBat", retorno);
			throw new WorkflowException("Erro ao processar solicitação");
		}
		
	}

	@Override
	public void back(EntityWrapper processEntity, Activity activity) {
		
	}

	public static void main(String[] args) throws Exception {

		ExecutaBatchAdapter adapter = new ExecutaBatchAdapter();

		String result = adapter.executeCmd("teste.bat", "http://192.168.1.103:8282/listener/execute?token=aTer3r:3d&batch=");

		System.out.println(result);
	}

	private String executeCmd(String command, String url) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url.concat(command));
		
		HttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		String result = convertStreamToString(entity.getContent());
	
		return result;
	}
	
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
	
}
