package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.adapter.AdapterUtils;
import com.neomind.util.NeoUtils;

public class PreencheItensProjetoAdapter implements AdapterInterface {

	String listaProjetos = "ItensProH";
	String listaDeItens = "ItensProHdp";
	String unidade = "PC";

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(Task arg0, EntityWrapper arg1, Activity arg2) {
		List<NeoBaseEntity> projetos = arg1.findGenericValue(listaProjetos);

		for (NeoBaseEntity projeto : projetos) {
			EntityWrapper projetoWrapper = new EntityWrapper(projeto);
			List<NeoBaseEntity> itens = projetoWrapper.findGenericValue(listaDeItens);
			for (NeoBaseEntity item : itens) {
				PersistEngine.removeById((Long)item.id());
			}
			preencherItens(projetoWrapper);
		}

		try {
			NeoBaseEntity projeto = projetos.get(projetos.size() - 1);
			EntityWrapper projetoWrapper = new EntityWrapper(projeto);
			
			Long qtdAdesivo = arg1.findGenericValue("QtdAdes");
			if (qtdAdesivo > 0) {
				String descricaoAdesivo = arg1.findGenericValue("DescricaoDoAdesivo");
				BigDecimal valorAdesivo = getPrecofromObject(arg1.findGenericValue("PreuniAde"));
				BigDecimal totalProjeto = projetoWrapper.findGenericValue("vltotproj");
				BigDecimal totalItem = BigDecimal.ZERO;
				BigDecimal totalCubagem = projetoWrapper.findGenericValue("CubTOP");
				BigDecimal CubagemItem = BigDecimal.ZERO;
				
				
				
				
				
				

				List<NeoObject> itens = projetoWrapper.findGenericValue(listaDeItens);
				NeoObject itemObject = AdapterUtils.createNewEntityInstance("hidItemProj");
				String ferramenta = arg1.findGenericValue("FerraAdesiv");
				
				
				EntityWrapper wrapper = new EntityWrapper(itemObject);

				wrapper.setValue("descricao", descricaoAdesivo);
				wrapper.setValue("quantidade", qtdAdesivo);
				wrapper.setValue("unidade", unidade);
				// wrapper.setValue("CodItemP", codigo);
				wrapper.setValue("PreUniP", valorAdesivo);
				wrapper.setValue("FeritemPr", ferramenta);
				wrapper.setValue("TamanPr", BigDecimal.ZERO);
				wrapper.setValue("CubItemP",CubagemItem);
				wrapper.setValue("QtdVolIP",BigDecimal.ZERO);
				wrapper.setValue("QtdPesItemP",BigInteger.ZERO);
				
				
				
				
				totalItem = valorAdesivo.multiply(new BigDecimal(qtdAdesivo));
				totalProjeto = totalItem.add(totalProjeto);
				wrapper.setValue("PreTotalP", totalItem);
				PersistEngine.persist(wrapper.getObject());

				itens.add(itemObject);

				projetoWrapper.setValue(listaDeItens, itens);
				projetoWrapper.setValue("vltotproj", totalProjeto);
				
				
				totalCubagem = CubagemItem.add(totalCubagem);
				
				projetoWrapper.setValue("CubTOP", totalCubagem);
			}
		} catch (Exception e) {
			System.out.println("Erro ao colocar adesivo");
			e.printStackTrace();
		}

	}

	private void preencherItens(EntityWrapper projeto) {
		List<NeoObject> lista = new ArrayList<NeoObject>();
		BigDecimal totalProjeto = new BigDecimal(0);
		
		// Criado Pelo Gecinei totalCubagem,totalvolume,totalpeso
		BigDecimal totalCubagem = projeto.findGenericValue("TotCubMI");
		projeto.setValue("CubTOP", totalCubagem);
				
	    Long totalvolume = projeto.findGenericValue("TOTVolMI");   
	    projeto.setValue("QtdVolpro",  totalvolume);
	    
	    BigDecimal totalpeso = projeto.findGenericValue("TotPesMI");    
	    projeto.setValue("QtdPesoDoProjeto", totalpeso);
			
		
		
		String ferramentaBarra = projeto.findGenericValue("FerBarra");

		Long qtdBarra1LadoA = projeto.findGenericValue("QtdTLAB1");
		Long qtdBarra1LadoB = projeto.findGenericValue("QtdLBB1");
		Long qtdBarra2LadoA = projeto.findGenericValue("QtdTLAB2");
		Long qtdBarra2LadoB = projeto.findGenericValue("QtdTLBB2");
		Long qtdBarra3LadoA = projeto.findGenericValue("QtdTLAB3");
		Long qtdBarra3LadoB = projeto.findGenericValue("QtdTLBB3");
		Long qtdBarra4LadoA = projeto.findGenericValue("QtdTLAB4");
		Long qtdBarra4LadoB = projeto.findGenericValue("QtdTLBB4");
		Long qtdBarra5LadoA = projeto.findGenericValue("QtdTLAB5");
		Long qtdBarra5LadoB = projeto.findGenericValue("QtdTLBB5");

		Long qtdTampaEntrada = projeto.findGenericValue("QtdToTE");
		Long qtdTampaSaida = projeto.findGenericValue("QtdToTS");
		Long qtdEmenda = projeto.findGenericValue("QtdToEM");
		Long qtdcoletorAntigo = projeto.findGenericValue("QtdToPECo");
		Long qtdcoletorNovo = projeto.findGenericValue("qtdToPeCoN");
		Long qtdTravessaNova = projeto.findGenericValue("QtdTrCN");
		Long qtdTravessaAntiga = projeto.findGenericValue("QtdToTRCA");
		Long qtdColunaNova = projeto.findGenericValue("QtdToCCNo");
		Long qtdColunaAntiga = projeto.findGenericValue("QtdTPCCA");
		Long qtdSuporteColetor = projeto.findGenericValue("QtdToSuCo");
		Long qtdInjetor = projeto.findGenericValue("QtdToInj");
		Long qtdPresilhas = projeto.findGenericValue("QtdToPres");

		// Barra1A
		// Incluido POr Gecinei CubLAB1,QtdVLAB1,QtdPLAB1
		if (validateLong(qtdBarra1LadoA)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr1");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPeB1"), qtdBarra1LadoA,
					unidade, "", projeto.findGenericValue("PrUniBar1A"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLAB1"),projeto.findGenericValue("QtdVLAB1"),projeto.findGenericValue("QtdPLAB1")));
			
		}

		// Barra1B
		// Incluido POr Gecinei CubLBB1,QtdVLBB1,QtdPLBB1
		if (validateLong(qtdBarra1LadoB)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr1");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPeLBB1"), qtdBarra1LadoB,
					unidade, "", projeto.findGenericValue("PreLaBB1"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLBB1"),projeto.findGenericValue("QtdVLBB1"),projeto.findGenericValue("QtdPLBB1")));
			
		}
		
		

		// Barra2A
		// Incluido POr Gecinei CubLAB2,QtdVLAB2,QtdPLAB2
		if (validateLong(qtdBarra2LadoA)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr2");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPELAB2"), qtdBarra2LadoA,
					unidade, "", projeto.findGenericValue("PreLaAB2"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLAB2"),projeto.findGenericValue("QtdVLAB2"),projeto.findGenericValue("QtdPLAB2")));
			
		}

		// Barra2B
		// Incluido POr Gecinei CubLBB2,QtdVLBB2,QtdPLBB2
		
		if (validateLong(qtdBarra2LadoB)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr2");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPeLaBB2"), qtdBarra2LadoB,
					unidade, "", projeto.findGenericValue("PreLaBB2"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLBB2"),projeto.findGenericValue("QtdVLBB2"),projeto.findGenericValue("QtdPLBB2")));
			
			
		}

		// Barra3A
		// Incluido POr Gecinei CubLAB3,QtdVLAB3,QtdPLAB3
		if (validateLong(qtdBarra3LadoA)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr3");

			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPeLAB3"), qtdBarra3LadoA,
					unidade, "", projeto.findGenericValue("PreLAB3"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLAB3"),projeto.findGenericValue("QtdVLAB3"),projeto.findGenericValue("QtdPLAB3")));
			
		}

		// Barra3B
		// Incluido POr Gecinei CubLBB3,QtdVLBB3,QtdPLBB3
		if (validateLong(qtdBarra3LadoB)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr3");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPELBB3"), qtdBarra3LadoB,
					unidade, "", projeto.findGenericValue("PreLaBB3"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLBB3"),projeto.findGenericValue("QtdVLBB3"),projeto.findGenericValue("QtdPLBB3")));
			
		}

		// Barra4A
		// Incluido POr Gecinei CubLAB4,QtdVLAB4,QtdPLAB4
		if (validateLong(qtdBarra4LadoA)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr4");

			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPELAB4"), qtdBarra4LadoA,
					unidade, "", projeto.findGenericValue("PreLaAB4"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLAB4"),projeto.findGenericValue("QtdVLAB4"),projeto.findGenericValue("QtdPLAB4")));
			
		}

		// Barra4B
		// Incluido POr Gecinei CubLBB4,QtdVLBB4,QtdPLBB4
		if (validateLong(qtdBarra4LadoB)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr4");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPELBB4"), qtdBarra4LadoB,
					unidade, "", projeto.findGenericValue("PreLaBB4"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLBB4"),projeto.findGenericValue("QtdVLBB4"),projeto.findGenericValue("QtdPLBB4")));
			
		}

		// Barra5A
		// Incluido POr Gecinei CubLAB5,QtdVLAB5,QtdPLAB5
		if (validateLong(qtdBarra5LadoA)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr5");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPELAB5"), qtdBarra5LadoA,
					unidade, "", projeto.findGenericValue("PreLaAB5"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLAB5"),projeto.findGenericValue("QtdVLAB5"),projeto.findGenericValue("QtdPLAB5")));
			
		}

		// Barra5B
		// Incluido POr Gecinei CubLBB5,QtdVLBB5,QtdPLBB5
		if (validateLong(qtdBarra5LadoB)) {
			BigDecimal tamanho = projeto.findGenericValue("Tmbarr5");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DesPeLBB5"), qtdBarra5LadoB,
					unidade, "", projeto.findGenericValue("PreLaBB5"), ferramentaBarra, tamanho,
					projeto.findGenericValue("CubLBB5"),projeto.findGenericValue("QtdVLBB5"),projeto.findGenericValue("QtdPLBB5")));
			
		}

		// Tampa Entrada
		// Incluido POr Gecinei CubTAE,QtdVTE,QtdPTE
		if (validateLong(qtdTampaEntrada)) {
			String ferramenta = projeto.findGenericValue("FerTampEnt.descricao");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescTE"), qtdTampaEntrada,
					unidade, "", getPrecofromObject(projeto.findGenericValue("PreTaEN")), ferramenta, BigDecimal.ZERO,
					projeto.findGenericValue("CubTAE"),projeto.findGenericValue("QtdVTE"),projeto.findGenericValue("QtdPTE")));
			
		}

		// Tampa Saida
		// Incluido POr Gecinei CubTAS,QtdVTS,QtdPTS 
		if (validateLong(qtdTampaSaida)) {
			String ferramenta = projeto.findGenericValue("FerTampS.descricao");

			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescTS"), qtdTampaSaida,
					unidade, "", getPrecofromObject(projeto.findGenericValue("PreTaSa")), ferramenta, BigDecimal.ZERO,
					projeto.findGenericValue("CubTAS"),projeto.findGenericValue("QtdVTS"),projeto.findGenericValue("QtdPTS")));
			
		}
		// Emenda
		// Incluido POr Gecinei CubEme,QtdVEme,QtdPEme
		
		if (validateLong(qtdEmenda)) {
			String ferramenta = projeto.findGenericValue("FerEmen.descricao");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescEmen"), qtdEmenda,
					unidade, "", getPrecofromObject(projeto.findGenericValue("PreEmen")), ferramenta, BigDecimal.ZERO,
					projeto.findGenericValue("CubEme"),projeto.findGenericValue("QtdVEme"),projeto.findGenericValue("QtdPEme")));
			
			
		}

		// Coletor Antigo
		// Incluido POr Gecinei CubCOA,QtdVCOA,QtdPeCOA
		if (validateLong(qtdcoletorAntigo)) {
			String ferramenta = projeto.findGenericValue("FerCoA");
			BigDecimal tamanho = projeto.findGenericValue("LargHi");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescPeCo"), qtdcoletorAntigo,
					unidade, "", projeto.findGenericValue("PrColAnt"), ferramenta, tamanho,
					projeto.findGenericValue("CubCOA"),projeto.findGenericValue("QtdVCOA"),projeto.findGenericValue("QtdPeCOA")));
			
		}

		// Coletor Novo
		// Incluido POr Gecinei CubCON, QtdVCON,QtdPCON
		if (validateLong(qtdcoletorNovo)) {
			String ferramenta = projeto.findGenericValue("FerColN");
			BigDecimal tamanho = projeto.findGenericValue("LargHi");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("descrPeCoN"), qtdcoletorNovo,
					unidade, "", projeto.findGenericValue("PreUColN"), ferramenta, tamanho,
					projeto.findGenericValue("CubCON"),projeto.findGenericValue("QtdVCON"),projeto.findGenericValue("QtdPCON")));
			
		}

		// Travessa Nova
		// Incluido POr Gecinei CubTRN,QtdVTRN,QtdPTRN
		if (validateLong(qtdTravessaNova)) {
			String ferramenta = projeto.findGenericValue("FerTravno");
			BigDecimal tamanho = projeto.findGenericValue("LargHi");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescTrCN"), qtdTravessaNova,
					unidade, "", projeto.findGenericValue("PreuTraN"), ferramenta, tamanho,
					projeto.findGenericValue("CubTRN"),projeto.findGenericValue("QtdVTRN"),projeto.findGenericValue("QtdPTRN")));
			
		}

		// Travessa Antiga 
		// Incluido POr Gecinei CubTRA,QtdVTRA,QtdPTRA
		if (validateLong(qtdTravessaAntiga)) {
			String ferramenta = projeto.findGenericValue("FerTravve");
			BigDecimal tamanho = projeto.findGenericValue("LargHi");
			totalProjeto = totalProjeto
					.add(addItenToList(lista, projeto.findGenericValue("DescTrCoA"), qtdTravessaAntiga, unidade, "",
							projeto.findGenericValue("PrUTraA"), ferramenta, tamanho,
							projeto.findGenericValue("CubTRA"),projeto.findGenericValue("QtdVTRA"),projeto.findGenericValue("QtdPTRA")));
			
		}

		// Coluna Nova
		// Incluido POr Gecinei CubColN ,QtdVColN,QtdPColN
		if (validateLong(qtdColunaNova)) {
			String ferramenta = projeto.findGenericValue("FerrCoNo");
			BigDecimal tamanho = projeto.findGenericValue("Coluna");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescrPerCCN"), qtdColunaNova,
					unidade, "", projeto.findGenericValue("PreUColNo"), ferramenta, tamanho,
					projeto.findGenericValue("CubColN"),projeto.findGenericValue("QtdVColN"),projeto.findGenericValue("QtdPColN") ));
			
			
		}

		// Coluna Antiga
		// Incluido POr Gecinei CubColA,QtdVColA,QtdPColA
		if (validateLong(qtdColunaAntiga)) {
			String ferramenta = projeto.findGenericValue("FerCoAn");
			BigDecimal tamanho = projeto.findGenericValue("Coluna");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescPCCA"), qtdColunaAntiga,
					unidade, "", projeto.findGenericValue("PreUClANt"), ferramenta, tamanho,
					projeto.findGenericValue("CubColA"),projeto.findGenericValue("QtdVColA"),projeto.findGenericValue("QtdPColA")));
			
		}

		// Suporte Coletor
		// Incluido POr Gecinei CubSuC,QtdVSC,QtdPSC
		
		if (validateLong(qtdSuporteColetor)) {
			String ferramenta = projeto.findGenericValue("FerSuCol");
			totalProjeto = totalProjeto
					.add(addItenToList(lista, projeto.findGenericValue("DescSuCOl"), qtdSuporteColetor, unidade, "",
							getPrecofromObject(projeto.findGenericValue("PreuniSC")), ferramenta, BigDecimal.ZERO,
							projeto.findGenericValue("CubSuC"),projeto.findGenericValue("QtdVSC"),projeto.findGenericValue("QtdPSC")));
			
			
		}

		// Injetor
		// Incluido POr Gecinei CubINJ,QtdVINJ,QtdPINJ
		if (validateLong(qtdInjetor)) {
			String ferramenta = projeto.findGenericValue("FerInjet");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescInjet"), qtdInjetor,
					unidade, "", getPrecofromObject(projeto.findGenericValue("PreUNIInH")), ferramenta, BigDecimal.ZERO,
					projeto.findGenericValue("CubINJ"),projeto.findGenericValue("QtdVINJ"),projeto.findGenericValue("QtdPINJ")));
			
		}

		// Presilhas
		// Incluido POr Gecinei CubPre,QtdVPre,QtdPPre
		if (validateLong(qtdPresilhas)) {
			String ferramenta = projeto.findGenericValue("FerrPres.descricao");
			totalProjeto = totalProjeto.add(addItenToList(lista, projeto.findGenericValue("DescPres"), qtdPresilhas,
					unidade, "", getPrecofromObject(projeto.findGenericValue("PreUPres")), ferramenta, BigDecimal.ZERO,
					projeto.findGenericValue("CubPre"),projeto.findGenericValue("QtdVPre"),projeto.findGenericValue("QtdPPre")));
			
			
		}

		
		projeto.setValue(listaDeItens, lista);
		projeto.setValue("vltotproj", totalProjeto);
		
		
		
		
		
	}
   // Incluido por Gecinei BigDecimal Cubagem, volume,peso
	private BigDecimal addItenToList(List<NeoObject> lista, String descricao, Long quantidade, String unidade,
			String codigo, BigDecimal preco, String ferramenta, BigDecimal tamanho,BigDecimal cubagem, Long volume,BigDecimal peso) {
		BigDecimal totalItem = BigDecimal.ZERO;
		

		if (quantidade != null && quantidade > 0) {

			System.out.println("Adicionando item: " + descricao);
			System.out.println("Ferramenta: " + ferramenta);
			System.out.println("Tamanho: " + tamanho);
			NeoObject itemObject = AdapterUtils.createNewEntityInstance("hidItemProj");
			EntityWrapper wrapper = new EntityWrapper(itemObject);

			wrapper.setValue("descricao", descricao);
			wrapper.setValue("quantidade", quantidade);
			wrapper.setValue("unidade", unidade);
			wrapper.setValue("CodItemP", codigo);
			wrapper.setValue("PreUniP", preco);
			wrapper.setValue("FeritemPr", ferramenta);
			wrapper.setValue("TamanPr", tamanho);
			wrapper.setValue("CubItemP", cubagem);
			wrapper.setValue("QtdVolIP", volume);
			wrapper.setValue("QtdPesItemP", peso);

			if (preco != null) {
				totalItem = preco.multiply(new BigDecimal(quantidade));
				wrapper.setValue("PreTotalP", totalItem);
			}
			
			

			PersistEngine.persist(wrapper.getObject());

			lista.add(itemObject);
		}

		return totalItem;
	}
    
	
		
		
		
	
	private boolean validateLong(Long valor) {
		if (valor == null || valor < 1) {
			return false;
		}

		return true;
	}

	private BigDecimal getPrecofromObject(NeoObject tabelPreco) {
		try {
			EntityWrapper wrapper = new EntityWrapper(tabelPreco);

			BigDecimal valor = wrapper.findGenericValue("valorbase");
			if (NeoUtils.safeIsNotNull(valor)) {
				return valor;
			}
		} catch (Exception e) {

		}

		return BigDecimal.ZERO;
	}

}
