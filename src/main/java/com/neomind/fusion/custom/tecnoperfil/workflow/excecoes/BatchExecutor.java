package com.neomind.fusion.custom.tecnoperfil.workflow.excecoes;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BatchExecutor {

	public static void main(String[] args) throws Exception {

		ExecutaBatchAdapter adapter = new ExecutaBatchAdapter();

		String result = executeCmd(args[0].trim());

		System.out.println(result);
	}

	private static String executeCmd(String command) throws Exception {
		Runtime run = Runtime.getRuntime();
		Process process = run.exec(command);
		process.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = buf.readLine()) != null) {
			sb.append(line);
			sb.append("/n");
		}

		return sb.toString();
	}
}
