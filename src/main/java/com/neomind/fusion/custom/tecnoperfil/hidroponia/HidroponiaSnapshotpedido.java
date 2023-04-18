package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.framework.base.entity.impl.NeoBaseEntityImpl;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.custom.tecnoperfil.TecnoperfilServletUtils;
import com.neomind.fusion.doc.NeoStorage;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.entity.NeoPhone;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.workflow.adapter.AdapterUtils;
import com.neomind.util.NeoCalendarUtils;

public class HidroponiaSnapshotpedido {

	public String buildContent(NeoBaseEntity workfowObject) {
		try {
			ArvoreTabelaConverter arvoreConverter = new ArvoreTabelaConverter();
			String modeloCaminho = NeoStorage.getDefault().getPath() + File.separator + "relatorios" + File.separator
					+ "modelo_hidroponia.html";
			EntityWrapper ew = new EntityWrapper(workfowObject);
			String tabela = arvoreConverter.buildTable((NeoObject) ew.getObject(), true);

			byte[] encoded = Files.readAllBytes(Paths.get(modeloCaminho));
			Map<String, String> parametros = extrairParametros(ew);
			System.out.println("Params:");
			System.out.println(parametros);
			parametros.put("tablePrd", tabela);

			Set<String> params = parametros.keySet();

			String output = new String(encoded, "UTF-8");
			System.out.println("Current output: " + output);
			for (String key : params) {
				String chave = "{{" + key + "}}";
				try {
					output = output.replace(chave, parametros.get(key));
				} catch (Exception e) {
					System.out.println("Erro ao aplicar valor da chave: " + key);
				}
			}
			
			output = output.replace("gridbox", "table")
					.replace("Permitido do Projeto", "");
			output = output.replace("tblFilha","table-bordered");

			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao imprimir";
		}
	}

	public String takeSnapshot(NeoBaseEntity workfowObject) {
		String content = buildContent(workfowObject);
		UUID id = UUID.randomUUID();
		NeoBaseEntity snapshotObject = AdapterUtils.createNewEntityInstance("SnapshotPedido");

		EntityWrapper snapshotWrapper = new EntityWrapper(snapshotObject);
		snapshotWrapper.setValue("content", content);
		snapshotWrapper.setValue("identificador", id.toString());

		PersistEngine.persist(snapshotWrapper.getObject());

		return id.toString();
	}

	private Map<String, String> extrairParametros(EntityWrapper ew) {
		Map<String, String> p = new HashMap<String, String>();

		/*
		 * Title = WorkPedHI.ClienCadH.Client.nome_empresa ValorTotal = WorkPedHI
		 * .Desconto numeroPedido = WorkPedHI.NumOrcH
		 * numeroPedidoBitrix.appnd(numeroPedido)
		 */
		StringBuilder telefoneCliente =  new StringBuilder();
		
		try {
			String prefixoCliente = ew.findGenericValue("ClienCadH.CTelefo.prefixo_telecom");
			String numeroCliente = ew.findGenericValue("ClienCadH.CTelefo.num_aparelho");
			telefoneCliente.append(prefixoCliente.trim());
			telefoneCliente.append("-");
			telefoneCliente.append(numeroCliente.trim());

			if (ew.findGenericValue("ClienCadH.TeleCel") != null) {
				String celularddd = ew.findGenericValue("ClienCadH.TeleCel.prefixo_telecom");
				String celularNr = ew.findGenericValue("ClienCadH.TeleCel.num_aparelho");
				StringBuilder celular = new StringBuilder();
				
				celular.append(celularddd.trim());
				celular.append("-");
				celular.append(celularNr.trim());

				telefoneCliente.append(" | ");
				telefoneCliente.append(celular);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder cidadeEstado = new StringBuilder();
		cidadeEstado.append(((String) ew.findGenericValue("ClienCadH.Client.municipio")).trim());
		cidadeEstado.append(" - ");
		cidadeEstado.append((String) ew.findGenericValue("ClienCadH.Client.sigla_uf"));

		try {
			Set<NeoPhone> telefones = ew.findGenericValue("VendHdp.phoneList");
			if (telefones.size() > 0) {
				NeoPhone neoPhone = (NeoPhone) telefones.toArray()[0];
				p.put("foneVendedor", neoPhone.getNumber());
			} else {
				p.put("foneVendedor", "VendHdp.name");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		GregorianCalendar emissao = ew.findGenericValue("DtEmissaoH");

		String nomeCli = ew.findGenericValue("ClienCadH.Client.nome_empresa");
		String codCli = ew.findGenericValue("ClienCadH.CCoDCli");
		BigDecimal valorFrete = ew.findGenericValue("VlFretHDP");
		BigDecimal descontoAvista = ew.findGenericValue("PerDesAv");
		String obsOrc = ew.findGenericValue("ObsOrc");
		String styleOrc = "";
		
		if(obsOrc == null || obsOrc.isEmpty()) {
			styleOrc ="display: none;";
		}
		
		
		String desconto = "0";
		String exibirFrete = "";

		if(descontoAvista != null) {
			desconto = descontoAvista.toString();
		}
		
		if (valorFrete.floatValue() == 0 ) {
			exibirFrete = "none";
		}

		p.put("foneCliente", telefoneCliente.toString());
		p.put("nroOrcamento", ew.findGenericValue("NumOrcH"));
		p.put("cidadeEstado", cidadeEstado.toString());
		p.put("dtOrcamento", NeoCalendarUtils.dateToString(emissao));
		p.put("validadeProposta", "10 dias");
		p.put("nomeCliente", codCli.trim().concat(" - ").concat(nomeCli));
		p.put("nomeVendedor", ew.findGenericValue("VendHdp.name"));
		p.put("obsOrc", obsOrc);
		p.put("styleOrc", styleOrc);

		p.put("vlTotal", formatCurrencyValue((BigDecimal) ew.findGenericValue("TotalGeral")));
		p.put("pctdescav","%".concat(desconto));
		p.put("descAv", formatCurrencyValue((BigDecimal) ew.findGenericValue("Desconto")));
		p.put("exibirFrete", exibirFrete);
		p.put("vlFrete", formatCurrencyValue(valorFrete));
		p.put("vltotalPed", formatCurrencyValue((BigDecimal) ew.findGenericValue("VlTotoRc")));
        
		// Linhas Adicionada por Gecinei para Impressao
		
		p.put("inscricaoestadual", ew.findGenericValue("ClienCadH.CInsEst"));
		p.put("emailvend", ew.findGenericValue("VendHdp.email"));
		
		
		String nomePIC = ew.findGenericValue("NomAgenExH.nome_empresa");
		
		if(nomePIC == null || nomePIC.isEmpty()) {
			nomePIC ="";
		}
        p.put("nomePIC",nomePIC);
		
		
		p.put("contnegociacao", ew.findGenericValue("ContNeg"));
		p.put("condpag", ew.findGenericValue("CondPagHDP.descr_cond_pag"));
		
		if (getValue(ew, "ClienCadH.TipPes.tipo_pessoa").toUpperCase().equals("J")) {
			p.put("clienteDOC", TecnoperfilServletUtils.aplicaMascara(getValue(ew, "ClienCadH.CNPJCPF"), true));
		} else {
			p.put("clienteDOC", TecnoperfilServletUtils.aplicaMascara(getValue(ew, "ClienCadH.CPFCLI"), false));
		}
		
		return p;
	}
      
		private String getValue(EntityWrapper wrapper, String key) {
		try {
			Object value = wrapper.findGenericValue(key);
			if (value != null) {
				if (value instanceof String) {
					return (String) value;
				}
				if (value instanceof BigDecimal) {
					BigDecimal num = (BigDecimal) value;

					return num.toString();
				}
				if (value instanceof Long) {
					Long num = (Long) value;

					return num.toString();
				}
				return value.toString();
			}
		} catch (Exception e) {
			System.out.println("Erro ao extrair " + key);

			return "";
		}

		return "";
	}
		
		// fim das linhas adicionada por gecinei
     
	public static String formatCurrencyValue(BigDecimal value) {
		if (value == null) {
			return "";
		}
		try {
			NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

			return nf.getCurrencyInstance().format(value);
		} catch (Exception e) {
			System.out.println("Erro ao aplicar máscara monetária: " + value.toEngineeringString());
		}
		return "R$ 0,00";
	}
}
