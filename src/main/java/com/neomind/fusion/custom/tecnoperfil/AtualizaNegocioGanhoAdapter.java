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

public class AtualizaNegocioGanhoAdapter implements AdapterInterface {

	@Override
	public void start(Task arg0, EntityWrapper wrapper, Activity arg2) {
		// TODO Auto-generated method stub
		try {
		String bitrixID = wrapper.findGenericValue("bitrixId");
		updateDeal(wrapper);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao dar ganho no negócio");
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

		dRequest.setStageId("WON");
		dRequest.setId(bitrixID);
		dRequest.setOpportunity(valorTotal.toString());
		
		if(tipoPedido != null && tipoPedido.toLowerCase().contains("ind")) {
			dRequest.setCategoria("8");
			dRequest.setStageId("C8:WON");
		}

		getConnector().updateDeal(dRequest, bitrixID);

		Long pedidoId = wrapper.findGenericValue("WPedido.neoId");
		createComment(pedidoId.toString(), dRequest.getId(),numeroPedido);

	}

	private void createComment(String idPedido, String dealId, String numeroPedido) {
		File proposta = TecnoperfilRelatorioPedido.geraPDF(idPedido);
		CommentRequest cr = new CommentRequest();

		cr.setComment("Documento - Proposta Pedido " + numeroPedido);
		cr.setEnityId(dealId);
		cr.setEntityType("DEAL");
		cr.setFile(proposta);
		getConnector().createComment(cr);
	}

	private BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/8/", "oh712h1nelw14f6n");

		return bConnect;
	}

}
