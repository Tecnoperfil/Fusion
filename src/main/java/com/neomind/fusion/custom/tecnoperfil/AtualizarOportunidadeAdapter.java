package com.neomind.fusion.custom.tecnoperfil;

import java.math.BigDecimal;

import com.neomind.fusion.custom.tecnoperfil.bitrix.BitrixConnect;
import com.neomind.fusion.custom.tecnoperfil.bitrix.DealRequest;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;


public class AtualizarOportunidadeAdapter implements AdapterInterface {
	@Override
	public void start(Task arg0, EntityWrapper arg1, Activity arg2) {
		// TODO Auto-generated method stub
		updateDeal(arg1);
	}
	
	private void updateDeal(EntityWrapper wrapper) {
		
		DealRequest dRequest = new DealRequest();
		
		BigDecimal totalPedido = wrapper.findGenericValue("pedido.totalPedido");
		
		
		//getConnector().updateDeal(dRequest, 0);
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}
	
	private BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/4/", "kfq4rzg7y81rlr5j");

		return bConnect;
	}
}
