package com.neomind.fusion.custom.tecnoperfil.importacao;


import java.io.File;
import java.util.Scanner;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.doc.NeoFile;
import com.neomind.fusion.doc.NeoStorage;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.WFProcess;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.adapter.AdapterUtils;

public class ImportTXTImagemAdapter implements AdapterInterface {

	@Override
	public void start(Task arg0, EntityWrapper wrapper, Activity arg2) {
		NeoFile arquivoTXT = wrapper.findGenericValue("field001");
		
		Scanner reader;
		try {
			reader = new Scanner(arquivoTXT.getAsFile());
			 while(reader.hasNextLine()) {
			        String linha = reader.nextLine();
			        
			        if (linha.isEmpty()) {
			        	System.out.println("Linha em branco");
			        } else {
			        	 File imagem = new File(linha);
					        
					        if (imagem.isFile()) {
					        	
					        	NeoFile tempFile = NeoStorage.createFile(imagem.getAbsolutePath());
					        	
					        	NeoBaseEntity documento = AdapterUtils.createDocument(tempFile, "Teste");
					        	NeoBaseEntity processo = AdapterUtils.createNewEntityInstance("MainLiberacaoDeDocumentos");
					        	
					        	
					        	EntityWrapper wrapperLiberacao = new EntityWrapper(processo);
					        	wrapperLiberacao.setValue("DadosDoDocumento", documento);
					        	
					        	WFProcess wf = AdapterUtils.startWFProcess("Liberação de Documento", (NeoObject)processo, arg0.getUser());
					        	wf.start();
					        	
					        	//wrapperLiberacao.setValue("DadosDoDocumento.TituloDoDocumento", "Oláááá");
					        	
					        } else {
					        	System.out.println("Não foi possível ler o arquivo " + linha);
					        }
			        }
			 }
			 reader.close();
			 System.out.println("Fim do arquivo");
		} catch (Exception e) {
			System.out.println("Erro ao executar a classe ImportTXTImagemAdapter: ");
			e.printStackTrace();
		}
		 
		
		
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {

	}

}
