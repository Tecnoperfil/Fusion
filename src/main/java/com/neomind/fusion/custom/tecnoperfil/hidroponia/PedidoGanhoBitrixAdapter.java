package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import java.math.BigDecimal;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.custom.tecnoperfil.bitrix.BitrixConnect;
import com.neomind.fusion.custom.tecnoperfil.bitrix.DealRequest;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;

public class PedidoGanhoBitrixAdapter implements AdapterInterface{

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start(Task arg0, EntityWrapper wrapper, Activity arg2) {
		try {
			String bitrixID = wrapper.findGenericValue("bitrixId");
			updateDeal(wrapper);
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Erro ao dar ganho no negócio hidroponia");
			}
		
	}

	private void updateDeal(EntityWrapper wrapper) {
		String bitrixID = wrapper.findGenericValue("bitrixId");
		BigDecimal valorTotal = wrapper.findGenericValue("Desconto");
		String numeroPedido = wrapper.findGenericValue("NumOrcH");
		
		DealRequest dRequest = new DealRequest();

		dRequest.setCategoria("6");
		dRequest.setStageId("C6:WON");
		dRequest.setId(bitrixID);
		dRequest.setOpportunity(valorTotal.toString());
		getConnector().updateDeal(dRequest, bitrixID);
		
		EnviarPedidoBitrixAdapter.createComment(bitrixID, dRequest.getId(), numeroPedido, (NeoBaseEntity) wrapper.getObject());
	}

	private BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/8/", "oh712h1nelw14f6n");

		return bConnect;
	}
}
