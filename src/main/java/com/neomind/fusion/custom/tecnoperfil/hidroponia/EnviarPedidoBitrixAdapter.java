package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import java.io.File;
import java.math.BigDecimal;

import org.json.simple.JSONObject;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.custom.tecnoperfil.TecnoperfilRelatorioPedido;
import com.neomind.fusion.custom.tecnoperfil.ValidaClienteAdapter;
import com.neomind.fusion.custom.tecnoperfil.bitrix.BitrixConnect;
import com.neomind.fusion.custom.tecnoperfil.bitrix.CommentRequest;
import com.neomind.fusion.custom.tecnoperfil.bitrix.DealRequest;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.portal.PortalUtil;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.util.NeoUtils;

public class EnviarPedidoBitrixAdapter implements AdapterInterface {
	private String snapshot;

	@Override
	public void start(Task arg0, EntityWrapper arg1, Activity arg2) {
		HidroponiaSnapshotpedido snapshotPedido = new HidroponiaSnapshotpedido();

		try {
			snapshot = snapshotPedido.buildContent((NeoBaseEntity)arg1.getObject());
			String bitrixID = arg1.findGenericValue("bitrixId");
			if (NeoUtils.safeIsNull(bitrixID)) {
				createDeal(arg1);
			} else {
				updateDeal(arg1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateDeal(EntityWrapper wrapper) {
		String bitrixID = wrapper.findGenericValue("bitrixId");
		DealRequest dRequest = new DealRequest();
		//StringBuilder numerPedidoBitrix = new StringBuilder();

		//String title = wrapper.findGenericValue("ClienCadH.Client.nome_empresa");
		BigDecimal valorTotal = wrapper.findGenericValue("Desconto");
		if(valorTotal == null) {
			valorTotal = wrapper.findGenericValue("TotalGeral");
		}
		String numeroPedido = wrapper.findGenericValue("NumOrcH");
	//	numerPedidoBitrix.append(numeroPedido);

		dRequest.setId(bitrixID);
		//dRequest.setTitle(title);
		dRequest.setCategoria("6");
		dRequest.setStageId("C6:NEW");
		dRequest.setOpportunity(valorTotal.toString());
		//dRequest.setRequestNumber(numerPedidoBitrix.toString());
		
		getConnector().updateDeal(dRequest, bitrixID);
		createComment(bitrixID, dRequest.getId(), numeroPedido, (NeoObject) wrapper.getObject());
	}

	private void createDeal(EntityWrapper wrapper) {
		DealRequest dRequest = new DealRequest();
		StringBuilder numerPedidoBitrix = new StringBuilder();

		String title = wrapper.findGenericValue("ClienCadH.Client.nome_empresa");
		String codCli = wrapper.findGenericValue("ClienCadH.CCoDCli");
		BigDecimal valorTotal = wrapper.findGenericValue("Desconto");
		if(valorTotal == null) {
			valorTotal = wrapper.findGenericValue("TotalGeral");
		}
		String numeroPedido = wrapper.findGenericValue("NumOrcH");
		numerPedidoBitrix.append(numeroPedido);

		dRequest.setTitle(codCli + " - "+title +" - "+numeroPedido);
		dRequest.setCategoria("6");
		dRequest.setStageId("C6:NEW");
		dRequest.setOpportunity(valorTotal.toString());
		dRequest.setRequestNumber(numerPedidoBitrix.toString());
		System.out.println("-------------------");


		String codigoKugel = wrapper.findGenericValue("ClienCadH.Client.cod_empresa");
		JSONObject clienteJson = ValidaClienteAdapter.findCliente(title, codigoKugel);
		if (clienteJson != null) {
			dRequest.setCompanyId((String) clienteJson.get("ID"));
			dRequest.setResponsavel((String) clienteJson.get("ASSIGNED_BY_ID"));
		}

		getConnector().createDeal(dRequest);

		Long pedidoId = wrapper.findGenericValue("WPedido.neoId");
		createComment(pedidoId.toString(), dRequest.getId(), numeroPedido,(NeoObject) wrapper.getObject());

		wrapper.setValue("bitrixId", dRequest.getId());
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}

	public static void createComment(String idPEd, String dealId, String numeroPedido, NeoBaseEntity pedido) {
		try {
			// File proposta = TecnoperfilRelatorioPedido.geraPDF(idPEd);
			CommentRequest cr = new CommentRequest();
			HidroponiaSnapshotpedido hsp = new HidroponiaSnapshotpedido(); 
			
			String snapshotId = hsp.takeSnapshot(pedido);
			
			String url = PortalUtil.getBaseURL();
			url = url.concat("custom/hidroponia/resumo_hidroponia.jsp?resumoId="+snapshotId);
			
			cr.setComment("Documento - Proposta Pedido " + numeroPedido + "\n "+url);
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
}
