package com.neomind.fusion.custom.tecnoperfil.workflow;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.dms.DMSEngine;
import com.neomind.fusion.doc.NeoDocument;
import com.neomind.fusion.doc.NeoFile;
import com.neomind.fusion.doc.folder.Folder;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.security.NeoUser;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.adapter.AdapterUtils;
import com.neomind.fusion.workflow.exception.WorkflowException;

public class PublicarDocumentoGED implements AdapterInterface {
	@Override
	public void start(Task task, EntityWrapper ew, Activity activity) {

		try {

			String titulo = ew.findGenericValue("DadosDoDocumento.TituloDoDocumento");
			String code = ew.findGenericValue("DadosDoDocumento.CodigoDoDocumento");
			NeoFile file = ew.findGenericValue("DadosDoDocumento.Arquivo");
			Folder pasta = ew.findGenericValue("DadosDoDocumento.PastaDoDocumentoo");
			NeoBaseEntity tipoDoc = ew.findGenericValue("DadosDoDocumento.Tipo");
			String descricao = ew.findGenericValue("DadosDoDocumento.Tipo.Descricao");
			Boolean imagemTecnoperfil = (Boolean) ew.findGenericValue("DadosDoDocumento.imagemTecoperfil");
			String nomeProduto = ew.findGenericValue("DadosDoDocumento.NomeDoProduto");
			String modeloCodigoTec = ew.findGenericValue("DadosDoDocumento.ModelocodigoTEC");
			String linha = ew.findGenericValue("DadosDoDocumento.Linha");
			String corECodigoDaCor = ew.findGenericValue("DadosDoDocumento.CorECodigoDaCor");
			String local = ew.findGenericValue("DadosDoDocumento.LocalOndeFoiAplicadoCidadeEOuObra");
			NeoObject sistema = (NeoObject) ew.findGenericValue("DadosDoDocumento.TipoDeSistema");
			GregorianCalendar dataInclusao = ew.findGenericValue("DadosDoDocumento.DataDeInclusaoNoBanco");
			NeoFile informacoes = ew.findGenericValue("DadosDoDocumento.info");

			NeoUser publicador = activity.getProcess().getRequester();
			NeoDocument doc = (NeoDocument) AdapterUtils.createNewEntityInstance("ImagemMarketing");
			constroiDoc(doc, titulo, buildCode(descricao, pasta.getName()), publicador);
			publicaDoc(doc, file, pasta, tipoDoc, nomeProduto, modeloCodigoTec, linha, corECodigoDaCor, local, sistema,
					dataInclusao, informacoes, imagemTecnoperfil);

		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e.getMessage());
		}

	}

	private String buildCode(String descricao, String folder) {
		StringBuilder code = new StringBuilder();
		if (descricao.equalsIgnoreCase("hidroponia")) {
			code.append("H");
		} else if (descricao.toLowerCase().contains("const")) {
			code.append("CV");
		} else if (descricao.toLowerCase().contains("ind")) {
			code.append("I");
		} else {
			code.append("DOC");
		}
		GregorianCalendar gc = new GregorianCalendar();
		code.append("-");
		code.append(folder);
		code.append("-");
		code.append(gc.get(Calendar.YEAR));
		code.append(gc.get(Calendar.MONTH) + 1);
		code.append(gc.get(Calendar.DAY_OF_MONTH));
		code.append(gc.get(Calendar.HOUR_OF_DAY));
		code.append(gc.get(Calendar.MINUTE));

		return code.toString();
	}

	@Override
	public void back(EntityWrapper ew, Activity activity) {

	}

	private void constroiDoc(NeoDocument doc, String titulo, String code, NeoUser publicador) {

		doc.setCreationDate(new GregorianCalendar());
		doc.setCode(code);
		doc.setCreator(publicador);

		new EntityWrapper(doc).findField("title").setValue(titulo);

	}

	public void publicaDoc(NeoDocument documento, NeoFile arquivo, Folder pasta, NeoBaseEntity tipoDoc, String nomeProduto,
			String modeloCodigoTec, String linha, String corECodigoDaCor, String local, NeoObject sistema,
			GregorianCalendar dataInclusao, NeoFile informacoes, Boolean imagemTecnoperfil) throws WorkflowException {

		try {

			documento.setFolder(pasta);

			EntityWrapper wDocumento = new EntityWrapper(documento);

			wDocumento.findField("file").setValue(arquivo);
			wDocumento.findField("tipoDoc").setValue(tipoDoc);
			wDocumento.findField("nomeProduto").setValue(nomeProduto);
			wDocumento.findField("modeloCodigoTec").setValue(modeloCodigoTec);
			wDocumento.findField("linha").setValue(linha);
			wDocumento.findField("corECodigoDaCor").setValue(corECodigoDaCor);
			wDocumento.findField("local").setValue(local);
			wDocumento.findField("sistema").setValue(sistema);
			wDocumento.findField("dataInclusao").setValue(dataInclusao);
			wDocumento.findField("imagemTecoperfil").setValue(imagemTecnoperfil);
			wDocumento.findField("info").setValue(informacoes);

			DMSEngine.getInstance().releaseVersion(documento);

		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException("Erro na publicação do arquivo: " + documento.getNeoDocumentTitle());
		}

	}

}
