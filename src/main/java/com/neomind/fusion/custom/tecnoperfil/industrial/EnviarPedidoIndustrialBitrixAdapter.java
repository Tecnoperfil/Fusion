package com.neomind.fusion.custom.tecnoperfil.industrial;

import java.math.BigDecimal;

import org.json.simple.JSONObject;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.custom.tecnoperfil.ValidaClienteAdapter;
import com.neomind.fusion.custom.tecnoperfil.bitrix.BitrixConnect;
import com.neomind.fusion.custom.tecnoperfil.bitrix.CommentRequest;
import com.neomind.fusion.custom.tecnoperfil.bitrix.DealRequest;
import com.neomind.fusion.custom.tecnoperfil.hidroponia.HidroponiaSnapshotpedido;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.portal.PortalUtil;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.util.NeoUtils;

public class EnviarPedidoIndustrialBitrixAdapter implements AdapterInterface {
	// variavel global que vai armazenar todo html do pdf a ser exibido
	private String snapshot;
	/*
	 Adapter para criar um novo negócio junto ao bitrix
	 */
	@Override
	public void start(Task arg0, EntityWrapper arg1, Activity arg2) {
		//Instanciação da classe responsavel pela criação do html 
		IndustrialSnapShotPedido snapshotPedido = new IndustrialSnapShotPedido();

		try {
			//criação e extração do html com os dados do processo
			snapshot = snapshotPedido.buildContent((NeoBaseEntity) arg1.getObject());
			String bitrixID = arg1.findGenericValue("bitrixId");
			
			//valida se ja existe um numero de pedido bitrix vinculado
			if (NeoUtils.safeIsNull(bitrixID)) {
				//se não existe, chama essa função que cria um
				createDeal(arg1);
			} else {
				//se existe chama esta função que altera o pedido bitri atual
				updateDeal(arg1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateDeal(EntityWrapper wrapper) {
		String bitrixID = wrapper.findGenericValue("bitrixId");
		DealRequest dRequest = new DealRequest();
		// StringBuilder numerPedidoBitrix = new StringBuilder();

		BigDecimal valorTotal = wrapper.findGenericValue("WPedInd.VatTMer");
		String numeroPedido = wrapper.findGenericValue("WPedInd.NumPedI");
		// numerPedidoBitrix.append(numeroPedido);

		dRequest.setId(bitrixID);
		dRequest.setCategoria("8");
		dRequest.setStageId("C8:NEW");
		dRequest.setOpportunity(valorTotal.toString());

		getConnector().updateDeal(dRequest, bitrixID);
		createComment(bitrixID, dRequest.getId(), numeroPedido, (NeoObject) wrapper.getObject());
	}

	private void createDeal(EntityWrapper wrapper) {
		DealRequest dRequest = new DealRequest();
		StringBuilder numerPedidoBitrix = new StringBuilder();

		String title = wrapper.findGenericValue("WPedInd.Cliind.nome_empresa");
		String codCli = wrapper.findGenericValue("WPedInd.Cliind.Client.cod_empresa");
		BigDecimal valorTotal = wrapper.findGenericValue("WPedInd.VatTMer");
		String numeroPedido = wrapper.findGenericValue("WPedInd.NumPedI");

		numerPedidoBitrix.append(numeroPedido);

		dRequest.setTitle(codCli + " - " + title + " - " + numeroPedido);
		dRequest.setCategoria("8");
		dRequest.setStageId("C8:NEW");
		dRequest.setOpportunity(valorTotal.toString());
		dRequest.setRequestNumber(numerPedidoBitrix.toString());
		System.out.println("-------------------");

		String codigoKugel = wrapper.findGenericValue("WPedInd.Cliind.Client.cod_empresa");
		JSONObject clienteJson = ValidaClienteAdapter.findCliente(title, codigoKugel);
		if (clienteJson != null) {
			dRequest.setCompanyId((String) clienteJson.get("ID"));
			dRequest.setResponsavel((String) clienteJson.get("ASSIGNED_BY_ID"));
		} else {
			System.out.println("Erro Cliente Idustrial naão encontrado " + title);
		}

		getConnector().createDeal(dRequest);

		Long pedidoId = wrapper.findGenericValue("WPedido.neoId");
		createComment(pedidoId.toString(), dRequest.getId(), numeroPedido, (NeoObject) wrapper.getObject());

		wrapper.setValue("bitrixId", dRequest.getId());
	}

	public static void createComment(String idPEd, String dealId, String numeroPedido, NeoBaseEntity pedido) {
		try {
			// File proposta = TecnoperfilRelatorioPedido.geraPDF(idPEd);
			CommentRequest cr = new CommentRequest();
			IndustrialSnapShotPedido hsp = new IndustrialSnapShotPedido();

			String snapshotId = hsp.takeSnapshot(pedido);

			String url = PortalUtil.getBaseURL();
			url = url.concat("custom/pedido_industrial/resumo_industrial.jsp?resumoId=" + snapshotId);

			cr.setComment("Documento - Proposta Pedido " + numeroPedido + "\n " + url);
			cr.setEnityId(dealId);
			cr.setEntityType("DEAL");
			getConnector().createComment(cr);
		} catch (Exception e) {
			System.out.println("Não foi possível preencher proposta :" + idPEd);
			e.printStackTrace();
		}
	}

	private static BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/8/", "oh712h1nelw14f6n");

		return bConnect;
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}

}
