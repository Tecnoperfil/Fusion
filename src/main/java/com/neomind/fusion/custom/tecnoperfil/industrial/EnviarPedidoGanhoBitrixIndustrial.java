package com.neomind.fusion.custom.tecnoperfil.industrial;

import java.math.BigDecimal;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.custom.tecnoperfil.bitrix.BitrixConnect;
import com.neomind.fusion.custom.tecnoperfil.bitrix.DealRequest;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;

public class EnviarPedidoGanhoBitrixIndustrial implements AdapterInterface {

	/*
	 Adapter para alterar status do pedido junto ao bitrix
	 */
	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(Task arg0, EntityWrapper wrapper, Activity arg2) {
		try {
			// BUsca o campo onde é armazenado o bitrixId do negócio
			String bitrixID = wrapper.findGenericValue("bitrixId");
			// Chamada do metodo que faz o update
			updateDeal(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao dar ganho no negócio Industrial");
		}

	}

	private void updateDeal(EntityWrapper wrapper) {
		//Extrção dos dados do eform
		String bitrixID = wrapper.findGenericValue("bitrixId");
		BigDecimal valorTotal = wrapper.findGenericValue("WPedInd.VatTMer");
		String numeroPedido = wrapper.findGenericValue("WPedInd.NumPedI");

		//montagem do objeto que é enviado na requisição;
		DealRequest dRequest = new DealRequest();

		dRequest.setCategoria("8");
		dRequest.setStageId("C8:WON");
		dRequest.setId(bitrixID);
		dRequest.setOpportunity(valorTotal.toString());
		//chama a lasse de conexão e executa a transação
		getConnector().updateDeal(dRequest, bitrixID);

		//envio do comentário
		EnviarPedidoIndustrialBitrixAdapter.createComment(bitrixID, dRequest.getId(), numeroPedido,
				(NeoBaseEntity) wrapper.getObject());
	}

	private BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/8/", "oh712h1nelw14f6n");

		return bConnect;
	}
}
