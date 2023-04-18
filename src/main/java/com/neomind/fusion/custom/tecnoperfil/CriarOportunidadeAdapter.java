package com.neomind.fusion.custom.tecnoperfil;

import java.io.File;
import java.math.BigDecimal;

import org.json.simple.JSONObject;

import com.neomind.fusion.custom.tecnoperfil.bitrix.BitrixConnect;
import com.neomind.fusion.custom.tecnoperfil.bitrix.CommentRequest;
import com.neomind.fusion.custom.tecnoperfil.bitrix.DealRequest;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.util.NeoUtils;

public class CriarOportunidadeAdapter implements AdapterInterface {

	@Override
	public void start(Task arg0, EntityWrapper wrapper, Activity arg2) {
		// TODO Auto-generated method stub
		try {
			String bitrixID = wrapper.findGenericValue("bitrixId");
			if(NeoUtils.safeIsNull(bitrixID)) {
				createDeal(wrapper);			
			} else {
				updateDeal(wrapper);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}
	
	public void updateDeal(EntityWrapper wrapper) {
		String bitrixID = wrapper.findGenericValue("bitrixId");
		BigDecimal valorTotal = wrapper.findGenericValue("WPedido.ValMer");
		String numeroPedido = wrapper.findGenericValue("WPedido.NumPed");
		String title = wrapper.findGenericValue("WPedido.Client.Client.nome_empresa");
		String tipoPedido =  wrapper.findGenericValue("WPedido.tipoPedido");

		DealRequest dRequest = new DealRequest();

		// dRequest.setStageId("NEW");
		dRequest.setId(bitrixID);
		dRequest.setOpportunity(valorTotal.toString());
		System.out.println("-------------------");
		/*if(tipoPedido != null && tipoPedido.contains("ind")) {
			System.out.println("Setando CATEGORIA");
			dRequest.setCategoria("8");
			dRequest.setStageId("C8:NEW");
		}*/

		getConnector().updateDeal(dRequest, bitrixID);

		Long pedidoId = wrapper.findGenericValue("WPedido.neoId");
		createComment(pedidoId.toString(), dRequest.getId(), numeroPedido);
	}

	public void createDeal(EntityWrapper wrapper) {
		DealRequest dRequest = new DealRequest();
		StringBuilder numerPedidoBitrix = new StringBuilder();

		String title = wrapper.findGenericValue("WPedido.Client.Client.nome_empresa");
		BigDecimal valorTotal = wrapper.findGenericValue("WPedido.ValMer");
		String numeroPedido = wrapper.findGenericValue("WPedido.NumPed");
		String tipoPedido =  wrapper.findGenericValue("WPedido.tipoPedido");
		numerPedidoBitrix.append(numeroPedido);

		dRequest.setTitle(title);
		dRequest.setStageId("NEW");
		dRequest.setOpportunity(valorTotal.toString());
		dRequest.setRequestNumber(numerPedidoBitrix.toString());
		System.out.println("-------------------");
		System.out.println("TIPO-PEDIDO: "+tipoPedido);
		if(tipoPedido != null && tipoPedido.toLowerCase().contains("ind")) {
			System.out.println("Setando CATEGORIA");
			dRequest.setCategoria("8");
			dRequest.setStageId("C8:NEW");
		}


		String codigoKugel = wrapper.findGenericValue("WPedido.Client.Client.cod_empresa");
		JSONObject clienteJson = ValidaClienteAdapter.findCliente(title, codigoKugel);
		if (clienteJson != null) {
			dRequest.setCompanyId((String) clienteJson.get("ID"));
			dRequest.setResponsavel((String) clienteJson.get("ASSIGNED_BY_ID"));
		}

		getConnector().createDeal(dRequest);

		Long pedidoId = wrapper.findGenericValue("WPedido.neoId");
		createComment(pedidoId.toString(), dRequest.getId(),numeroPedido);

		wrapper.setValue("bitrixId", dRequest.getId());
	}

	private void createComment(String idPEd, String dealId, String numeroPedido) {
		try {
		File proposta = TecnoperfilRelatorioPedido.geraPDF(idPEd);
		CommentRequest cr = new CommentRequest();

		cr.setComment("Documento - Proposta Pedido " + numeroPedido);
		cr.setEnityId(dealId);
		cr.setEntityType("DEAL");
		cr.setFile(proposta);
		getConnector().createComment(cr);
		}
		catch(Exception e) {
			System.out.println("Não foi possível preencher proposta :"+idPEd);
			e.printStackTrace();
		}
	}

	private BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/8/", "oh712h1nelw14f6n");

		return bConnect;
	}

}
