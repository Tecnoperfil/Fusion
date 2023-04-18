package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import static java.math.BigDecimal.ROUND_HALF_EVEN;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.framework.base.entity.impl.NeoBaseEntityImpl;
import com.neomind.fusion.custom.tecnoperfil.DescontoHelper;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.exception.WorkflowException;

public class DescontoAvistaAdapter implements AdapterInterface {

	@Override
	public void start(Task arg0, EntityWrapper arg1, Activity arg2) {
		try {
			BigDecimal descAv = arg1.findGenericValue("PerDesAv");
			BigDecimal valorFrete = BigDecimal.ZERO;
			BigDecimal totalPedido = BigDecimal.ZERO;
			Long tipoFrete = arg1.findGenericValue("TFretHDP.Codtf");

			List<NeoBaseEntity> projetos = arg1.findGenericValue("ItensProH");
			List<NeoBaseEntity> itensAvulsos = arg1.findGenericValue("IteAvProH");

			for (NeoBaseEntity projeto : projetos) {
				BigDecimal totalProjeto = BigDecimal.ZERO;
				EntityWrapper projetoWrapper = new EntityWrapper(projeto);
				List<NeoBaseEntityImpl> itens = projetoWrapper.findGenericValue("ItensProHdp");

				for (NeoBaseEntityImpl item : itens) {
					EntityWrapper itemWrapper = new EntityWrapper(item);
					Long quantidade = itemWrapper.findGenericValue("quantidade");
					if (quantidade < 1l) {
						continue;
					}
					BigDecimal valorTotal = itemWrapper.findGenericValue("PrCDesPr");

					if (valorTotal == null || valorTotal.floatValue() == 0) {
						valorTotal = itemWrapper.findGenericValue("PrDesPePR");
					}

					if (valorTotal == null || valorTotal.floatValue() == 0) {
						valorTotal = itemWrapper.findGenericValue("PreUniP");
					}

					BigDecimal valorAvista = DescontoHelper.aplicarDesconto(valorTotal, descAv);
					totalProjeto = totalProjeto.add(valorAvista.multiply(new BigDecimal(quantidade)));

					itemWrapper.setValue("PvItenPr", valorAvista);
					PersistEngine.persist(itemWrapper.getObject());
				}

				totalPedido = totalPedido.add(totalProjeto);
				// projetoWrapper.setValue("vltotproj", totalProjeto);
				PersistEngine.persist(projetoWrapper.getObject());
			}

			for (NeoBaseEntity item : itensAvulsos) {
				EntityWrapper itemWrapper = new EntityWrapper(item);
				String quantidadeStr = itemWrapper.findGenericValue("Qtd");
				Long quantidade = Long.parseLong(quantidadeStr);
				if (quantidade < 1l) {
					continue;
				}
				BigDecimal valorTotal = itemWrapper.findGenericValue("PreComDes");

				if (valorTotal == null || valorTotal.floatValue() == 0) {
					valorTotal = itemWrapper.findGenericValue("PreDeGeral");
				}

				BigDecimal valorAvista = DescontoHelper.aplicarDesconto(valorTotal, descAv);
				totalPedido = totalPedido.add(valorAvista.multiply(new BigDecimal(quantidade)));

				itemWrapper.setValue("PvItenAv", valorAvista);
				PersistEngine.persist(itemWrapper.getObject());
			}
			if (tipoFrete == 2) {
				try {
					BigDecimal perFrete = arg1.findGenericValue("PerFretH");
					valorFrete = totalPedido.multiply(perFrete.divide(new BigDecimal(100), ROUND_HALF_EVEN));
				} catch (Exception e) {
					System.out.println("Sem valor de frete");
				}
			}

			arg1.setValue("Desconto", totalPedido);

			totalPedido = totalPedido.add(valorFrete);
			arg1.setValue("VlTotoRc", totalPedido);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException("Erro ao calcular desconto a  vista");
		}
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}

}
