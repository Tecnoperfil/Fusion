package com.neomind.fusion.custom.tecnoperfil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.doc.NeoStorage;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.entity.InstantiableEntityInfo;
import com.neomind.fusion.entity.NeoPhone;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.persist.QLEqualsFilter;
import com.neomind.fusion.portal.PortalUtil;
import com.neomind.fusion.security.NeoUser;
import com.neomind.fusion.workflow.adapter.AdapterUtils;
import com.neomind.util.NeoCalendarUtils;
import com.neomind.util.NeoUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class TecnoperfilRelatorioPedido {
	private static final Log log = LogFactory.getLog(TecnoperfilRelatorioPedido.class);

	/**
	 * Gera o PDF do relatório, utilizando JasperReports
	 */
	public static File geraPDF(String numeroPedido) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		InputStream is = null;
		String path = "";
		Long pedidoId = Long.parseLong(numeroPedido);

		try {
			InstantiableEntityInfo ieiColaborador = AdapterUtils.getInstantiableEntityInfo("Pedido");
			NeoBaseEntity pedido = (NeoObject) PersistEngine.getObject(ieiColaborador.getEntityClass(),
					new QLEqualsFilter("neoId", pedidoId));

			EntityWrapper pedidoWrapper = new EntityWrapper(pedido);
			
			String nome_modelo = pedidoWrapper.findGenericValue("tipoPedido"); //"tecnoperfilPedidos.jasper";
			if(nome_modelo == null || nome_modelo.isEmpty()) {
				nome_modelo = "tecnoperfilPedidos.jasper";
			}
			// ...files/relatorios
			path = NeoStorage.getDefault().getPath() + File.separator + "relatorios" + File.separator + nome_modelo;
			// obtém os parâmetros
			paramMap = preencheParametros(pedido);
			is = new BufferedInputStream(new FileInputStream(path));

			if (paramMap != null) {

				File file = File.createTempFile("Relatorio_Pedido", ".pdf");
				file.deleteOnExit();

				JasperPrint impressao = JasperFillManager.fillReport(is, paramMap);
				if (impressao != null && file != null) {
					JasperExportManager.exportReportToPdfFile(impressao, file.getAbsolutePath());
					return file;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

			log.error("Erro ao gerar o PDF do Relatório de Posição Física Financeira!!", e);
		}
		return null;
	}

	/**
	 * Preenche o mapa de parâmetros enviados ao relatório.
	 */
	public static Map<String, Object> preencheParametros(NeoBaseEntity pedido) {

		Map<String, Object> paramMap = null;
		String PATH_LOGO = NeoStorage.getDefault().getPath() + File.separator + "relatorios" + File.separator
				+ "logo_tecnoperfil.JPG";
		String subRelatorio = NeoStorage.getDefault().getPath() + File.separator + "relatorios" + File.separator
				+ "tecnoperfilPedidos_itens.jasper";

		try {
			paramMap = new HashMap<String, Object>();
			EntityWrapper wrapper = new EntityWrapper(pedido);

			NeoUser usuarioLogado = PortalUtil.getCurrentUser();
			paramMap.put("pathSubRelatorio1", subRelatorio);
			paramMap.put("pathLogo", PATH_LOGO);
			paramMap.put("numeroNota", NeoUtils.safeOutputString(wrapper.findValue("NumPed")).trim());
			paramMap.put("email", NeoUtils.safeOutputString(usuarioLogado.getEmail()));

			paramMap.put("obsPedido", safeNotNullString(wrapper.findGenericValue("PObsPed")));
			
			paramMap.put("obsobra", safeNotNullString(wrapper.findGenericValue("InforObra")));

			try {
				paramMap.put("descPisCofins", NeoUtils.safeOutputString(wrapper.findValue("AbICMS").toString()).trim());
			} catch (Exception e) {
				paramMap.put("descPisCofins", "");
			}

			try {
				BigDecimal pisAliq = wrapper.findGenericValue("PisCoAli");
				if (pisAliq.floatValue() > 0) {
					paramMap.put("pisAliq", NeoUtils.safeOutputString(pisAliq.toString()));
				} else {
					paramMap.put("pisAliq", null);
				}
			} catch (Exception e) {
				paramMap.put("pisAliq", null);
			}

			try {
				BigDecimal abatIcms = wrapper.findGenericValue("AbICMS");
				if (abatIcms.floatValue() > 0) {
					paramMap.put("abatIcms", NeoUtils.safeOutputString(abatIcms.toString()));
				} else {
					paramMap.put("abatIcms", null);
				}
			} catch (Exception e) {
				paramMap.put("abatIcms", null);
			}

			paramMap.put("cliente", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.nome_empresa")).trim());
			paramMap.put("codigo", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.cod_empresa")).trim());
			String tipoCliente = wrapper.findGenericValue("Client.TipPes.tipo_pessoa");
			String cpfCNPJ = NeoUtils.safeOutputString(wrapper.findValue("WPedido.Client.Client.cgc")).trim();

			if (tipoCliente.equalsIgnoreCase("j")) {
				if (cpfCNPJ.length() < 14) {
					cpfCNPJ = String.format("%014d", Long.parseLong(cpfCNPJ));
				}

				System.out.println("Mascara CNPJ :" + TecnoperfilServletUtils.aplicaMascara(cpfCNPJ, true));
				paramMap.put("cnpj", TecnoperfilServletUtils.aplicaMascara(cpfCNPJ, true));
			} else {
				if (cpfCNPJ.length() < 11) {
					cpfCNPJ = String.format("%011d", Long.parseLong(cpfCNPJ));
				}

				System.out.println("Mascara CPF :" + TecnoperfilServletUtils.aplicaMascara(cpfCNPJ, false));
				paramMap.put("cnpj", TecnoperfilServletUtils.aplicaMascara(cpfCNPJ, false));
			}

			paramMap.put("ie", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.inscr_esta")).trim());
			paramMap.put("suframa", NeoUtils.safeOutputString(wrapper.findValue("Client.SobCLi.inscr_suframa")).trim());

			Long numeroCli = wrapper.findGenericValue("Client.Client.num_end");

			paramMap.put("numeroCli", numeroCli != null ? numeroCli.toString() : "");
			paramMap.put("endereco", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.rua")).trim());
			paramMap.put("bairroCli", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.bairro")).trim());
			paramMap.put("cidadeCli", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.municipio")).trim());
			paramMap.put("ufCli", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.sigla_uf")).trim());
			paramMap.put("cepCli", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.cep")).trim());

			String condPgto;
			String pagamentoPadrao = wrapper.findGenericValue("Client.ConPagP");
			try {
				System.out.println("PAgamento pradrão :" + pagamentoPadrao + "\"");
			} catch (Exception e) {

			}

			if (pagamentoPadrao != null && !pagamentoPadrao.trim().isEmpty() && pagamentoPadrao.trim().length() > 0) {
				condPgto = wrapper.findGenericValue("ConDePaFix.descr_cond_pgto");
			} else {
				condPgto = wrapper.findGenericValue("ConDePag.descr_cond_pgto");
			}
//			String condLocal = wrapper.findGenericValue("Client.ConPagP");
//			if (condLocal != null) {
//				condPgto = wrapper.findGenericValue("ConDePaFix.cod_cond_pgto");
//			} else {
//				condPgto = wrapper.findGenericValue("ConDePag.cod_cond_pgto");
//			}

			paramMap.put("condpgto", condPgto);
			paramMap.put("natoper", NeoUtils.safeOutputString(wrapper.findValue("NatOpe.descr_trans_comercia")).trim());

			String transportadora;

			String tipofrete = wrapper.findGenericValue("TipoDeFrete.Descri");
			if (tipofrete.toUpperCase().contains("FOB")) {
				transportadora = wrapper.findGenericValue("TranFOB.razaosocialtranspor");
			} else {
				transportadora = wrapper.findGenericValue("Trasnp.razaosocialtranspor");
			}

			paramMap.put("transportadora", transportadora);
			String redespacho = NeoUtils.safeBoolean(wrapper.findValue("Redpac"), false) ? "Sim" : "Não";
			//
			if (redespacho.equals("Sim")) {
				String transportadoraRedes = NeoUtils.safeOutputString(wrapper.findValue("TranRed.cod_transpor"))
						+ " - " + NeoUtils.safeOutputString(wrapper.findValue("TranRed.nomefantasiatranspor"));
				paramMap.put("redespacho", transportadoraRedes);
			} else {
				paramMap.put("redespacho", redespacho);
			}

			boolean entregaDiferenciada = NeoUtils.safeBoolean(wrapper.findValue("EntDifer"));

			/*
			 * se for entrega diferenciada, então pega o obsEntrega como endereço e o estado
			 * e cidade caso negativo, deixar em branco
			 */
			if (entregaDiferenciada) {
				paramMap.put("localentrega", NeoUtils.safeOutputString(wrapper.findValue("EndEntr")).trim());
				paramMap.put("bairro", NeoUtils.safeOutputString(wrapper.findValue("Client.Client.bairro")));
				paramMap.put("cidade", NeoUtils.safeOutputString(wrapper.findValue("MunEntr.nome_municipio")));
				paramMap.put("uf", NeoUtils.safeOutputString(wrapper.findValue("UFEntr.sigla_uf")));
				paramMap.put("cep", "");
			} else {
				paramMap.put("localentrega", "");
				paramMap.put("bairro", "");
				paramMap.put("cidade", "");
				paramMap.put("uf", "");
				paramMap.put("cep", "");
			}

			GregorianCalendar emissao = wrapper.findGenericValue("DataEm");
			paramMap.put("dtemissao", NeoCalendarUtils.dateToString(emissao));
			paramMap.put("frete", NeoUtils.safeOutputString(wrapper.findValue("TipoDeFrete.Descri")).trim());
			paramMap.put("ordcompra", NeoUtils.safeOutputString(wrapper.findValue("OrdCom")).trim());
			paramMap.put("representante", NeoUtils.safeOutputString(wrapper.findValue("PVende.fullName")).trim());
			paramMap.put("previsao", NeoCalendarUtils.dateToString((GregorianCalendar) wrapper.findValue("PrevEmb")));

			paramMap.put("emailrepresentante", NeoUtils.safeOutputString(wrapper.findValue("PVende.email")));
			String celCliente;
			try {
				celCliente = NeoUtils.safeOutputString(wrapper.findValue("Client.TeleCel.prefixo_telecom")).trim() + "-"
						+ NeoUtils.safeOutputString(wrapper.findValue("Client.TeleCel.num_aparelho")).trim();
			} catch (Exception e) {
				celCliente = "";
			}

			paramMap.put("celCliente", celCliente);
			String fixoCliente;
			try {
				fixoCliente = NeoUtils.safeOutputString(wrapper.findValue("Client.CTelefo.prefixo_telecom")).trim()
						+ "-" + NeoUtils.safeOutputString(wrapper.findValue("Client.CTelefo.num_aparelho")).trim();
			} catch (Exception e) {
				fixoCliente = "";
			}
			paramMap.put("fixoCliente", fixoCliente);

			try {
				Set<NeoPhone> telefones = wrapper.findGenericValue("PVende.phoneList");
				if (telefones.size() > 0) {
					NeoPhone neoPhone = (NeoPhone) telefones.toArray()[0];
					paramMap.put("contato", neoPhone.getNumber());
				} else {
					paramMap.put("contato", "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			paramMap.put("valortotal",
					formatCurrencyValue(NeoUtils.safeOutputString(wrapper.findValue("VldaDaMerc")).trim()));
			paramMap.put("valorfrete",
					formatCurrencyValue(NeoUtils.safeOutputString(wrapper.findValue("ValFret")).trim()));

			String situacao = NeoUtils.safeOutputString(wrapper.findValue("Situac.Descri"));
			if ("Pedido".equalsIgnoreCase(situacao.trim())) {
				paramMap.put("situacao", "PEDIDO DE VENDAS");
			} else {
				paramMap.put("situacao", "COTAÇÃO DE VENDAS");
			}

			BigDecimal valoripi = (BigDecimal) wrapper.findValue("VltoIPI");// mercadorias
			paramMap.put("valoripi", formatCurrencyValue(NeoUtils.safeOutputString(valoripi).trim()));
			BigDecimal valoripifrete = (BigDecimal) wrapper.findValue("VIPIFre");// frete
			paramMap.put("valoripifrete", formatCurrencyValue(NeoUtils.safeOutputString(valoripifrete).trim()));
			BigDecimal valortotalipi = valoripi.add(valoripifrete);
			paramMap.put("valortotalipi",formatCurrencyValue(NeoUtils.safeOutputString(valortotalipi).trim()));

			paramMap.put("subtrib", formatCurrencyValue(NeoUtils.safeOutputString(wrapper.findValue("VlTotST")).trim()));

			paramMap.put("total", formatCurrencyValue(NeoUtils.safeOutputString(wrapper.findValue("VlTotPe")).trim()));
			Collection<NeoObject> itens = (Collection<NeoObject>) wrapper.findField("PItenPed").getValues();
			Collection<TecnoperfilItensPedidoDataSource> listaItens = populaGrid(itens);
			paramMap.put("listaItens", listaItens);

			return paramMap;
		} catch (Exception e) {
			log.error("Erro ao preencher o mapa de parâmetros da Impressão do Relatório", e);
			e.printStackTrace();
		}
		return paramMap;
	}

	/**
	 * Retorna os dados dos meses.
	 */
	public static Collection<TecnoperfilItensPedidoDataSource> populaGrid(Collection<NeoObject> itens) {
		List<TecnoperfilItensPedidoDataSource> listaItens = new ArrayList();

		try {
			for (NeoObject item : itens) {
				EntityWrapper wItem = new EntityWrapper(item);
				String codigo = NeoUtils.safeOutputString(wItem.findValue("CodItem"));
				String quantidade = NeoUtils.safeOutputString(wItem.findValue("Qtd"));
				String uni = NeoUtils.safeOutputString(wItem.findValue("UniVenL"));
				String descricao = NeoUtils.safeOutputString(wItem.findValue("DesProd"));
				String valor = formatCurrencyValue(NeoUtils.safeOutputString(wItem.findValue("ValUni")));
				String ipi = NeoUtils.safeOutputString(wItem.findValue("AliIPI"));
				String total = NeoUtils.safeOutputString(wItem.findValue("vlmerc"));
				String observacao = NeoUtils.safeOutputString(wItem.findValue("IObsItem"));
				String valoripi = NeoUtils.safeOutputString(wItem.findValue("ValIpi"));
				String VltotCipi = NeoUtils.safeOutputString(wItem.findValue("VltotCipi"));

				TecnoperfilItensPedidoDataSource ped = new TecnoperfilItensPedidoDataSource();
				ped.setCodigo(codigo);
				ped.setQuantidade(quantidade);
				ped.setUnidade(uni);
				ped.setDescItem(descricao + "\r\nOBS DO ITEM:  " + observacao);// \r\n
				ped.setValorUnit(valor);
				ped.setIpi(ipi);
				ped.setTotal(formatCurrencyValue(total));
				ped.setvaloripi(formatCurrencyValue(valoripi));
				ped.setVltotCipi(formatCurrencyValue(VltotCipi));

				listaItens.add(ped);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listaItens;
	}

	public static String formatCurrencyValue(String plainValue) {
		try {
			BigDecimal value = new BigDecimal(plainValue);
			NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

			return nf.getCurrencyInstance().format(value);
		} catch (Exception e) {
				System.out.println("Erro ao aplicar máscara monetária: "+plainValue);
		}
		return "R$ 0,00";
	}

	private static String safeNotNullString(Object value) {
		try {
			if (value instanceof String) {
				String valueStr = (String) value;
				if (valueStr != null && !valueStr.isEmpty()) {
					return valueStr.trim();
				}
			}
		} catch (Exception e) {

		}

		return "";
	}
}
