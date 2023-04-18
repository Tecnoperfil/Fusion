package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.OriginEnum;
import com.neomind.fusion.eform.converter.StringConverter;
import com.neomind.fusion.entity.EntityWrapper;

public class ArvoreTabelaConverter extends StringConverter {

	private String fieldFilhoPath = "ItensProH";
	private String fieldNetoPath = "ItensProHdp";
	private Random random = new Random();

	@Override
	public String getHTMLInput(EFormField field, OriginEnum origin) {
		System.out.println("BUldConverter");
		return buildTable(field.getForm().getObject(), false);
	}

	public String buildTable(NeoBaseEntity formObject, boolean addItensAvulsos) {
		EntityWrapper formWrapper = new EntityWrapper(formObject);
		List<NeoBaseEntity> projetos = formWrapper.findGenericValue(fieldFilhoPath);

		StringBuilder sb = new StringBuilder();
		sb.append("<script> function expandRow(id){ $(\".row-hiddable-\"+id).toggle(); } </script>");
		sb.append("<table class=\"gridbox\">");
		sb.append("<thead class='no-print'>\r\n" + "		<tr>\r\n"
				+ "			<th style=\"width:10px\"><img src=\"imagens/icones_final/add_blue_16x16-trans.png\"></th>\r\n"
				+ "			<th>Nome do Projeto</th>\r\n" + "			<th style=\"width:20px\">Total</th>\r\n"
				+ "	</thead>");

		sb.append("<tbody>");
		for (NeoBaseEntity projeto : projetos) {
			EntityWrapper projetoWrapper = new EntityWrapper(projeto);

			int id = random.nextInt();
			sb.append("<tr>");
			// collapse button
			sb.append("<td>");
			sb.append("<a class='no-print' onClick=\"expandRow(" + id + ")\">"
					+ "					<img src=\"imagens/icones_final/add_blue_16x16-trans.png\">" + "</a>");
			sb.append("</td>");
			// Titulo
			appendShortTag("td", projetoWrapper.findGenericValue("NomProj"), "neo-title", sb);
			// Total
			appendShortTag("td", projetoWrapper.findGenericValue("vltotproj"), "neo-title", sb);

			// itens do projeto
			sb.append("<tr class=\"row-hiddable row-hiddable-" + id + "\" style=\"display:none\">");
			buildProjetoTable(sb, projetoWrapper, addItensAvulsos);
			sb.append("</tr>");

		}

		if (addItensAvulsos) {
			addItensAvulsos(sb, formWrapper);
		}

		sb.append("</tbody>");
		sb.append("</table>");

//		try {
//			byte[] encoded = Files.readAllBytes(Paths.get("D:\\amztech\\perfilar\\teste.txt"));
//
//			return new String(encoded);
//		} catch (Exception e) {
//			return "erro";
//		}
		// System.out.println(sb.toString());

		return sb.toString();

	}

	private void addItensAvulsos(StringBuilder sb, EntityWrapper projetoWrapper) {
		List<NeoBaseEntity> itens = projetoWrapper.findGenericValue("IteAvProH");
		if (itens != null && !itens.isEmpty()) {
			sb.append("<tr>");
			// collapse button
			sb.append("<td>");
			sb.append("<a class='no-print' onClick=\"expandRow(027)\"><img src=\"imagens/icones_final/add_blue_16x16-trans.png\">" + "</a>");
			sb.append("</td>");
			// Titulo
			appendShortTag("td", "Itens avulsos", "neo-title", sb);
			sb.append("</tr>");
			sb.append("<tr class=\"row-hiddable row-hiddable-027\" style=\"display:none\">");
			sb.append("<table class=\"gridbox tblFilha\">");
			sb.append("<thead>");
			sb.append("<tr>");
			appendShortTag("th", "Quantidade", "", sb);
			appendShortTag("th", "unidade", "", sb);
			appendShortTag("th", "Descrição", "prddescav", sb);
			appendShortTag("th", "Preço unitário", "preuni", sb);
			appendShortTag("th", "Preço Com Desconto Permitido do Projeto", "pregeral", sb);
			appendShortTag("th", "Preço total", "pretotal", sb);
			// appendShortTag("th", "Código", sb);

			sb.append("</tr>");
			sb.append("</thead>");

			for (NeoBaseEntity item : itens) {
				EntityWrapper itemWrapper = new EntityWrapper(item);
				sb.append("<tr>");
				appendShortTag("td", itemWrapper.findGenericValue("Qtd"), "", sb);
				appendShortTag("td", itemWrapper.findGenericValue("univenH"), "", sb);
				appendShortTag("td", itemWrapper.findGenericValue("DescIav"), "prddescav", sb);
				
				// Criado pelo Gecinei quando o valor do desconto for maior que o unitario o valor unitario passa ser o valor descontos somente impresso em tela
				
				
				BigDecimal valorUni = itemWrapper.findGenericValue("PreUniH");
				BigDecimal valorUniDesc = itemWrapper.findGenericValue("PreComDes");
				
				
				if(valorUniDesc.floatValue() > valorUni.floatValue())   {
					
					appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreComDes")), "preuni", sb);
				
				    }
				 
				else{
					appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreUniH")), "preuni", sb);
				}
				
				
								
				///appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreUniH")), "preuni", sb);
				// Fim de Alteração do Gecinei
				
				
				
				BigDecimal valorUnitario = itemWrapper.findGenericValue("PreUniH");
				BigDecimal valorUniDesconto = itemWrapper.findGenericValue("PreComDes");
				
          			
				
				if(valorUniDesconto == null || valorUniDesconto.floatValue() == 0 || valorUniDesconto.floatValue() == valorUnitario.floatValue()) {
					valorUniDesconto = itemWrapper.findGenericValue("PreComDes");
				}
				
				if(valorUniDesconto != null &&(valorUniDesconto.floatValue() == valorUnitario.floatValue())) {
					valorUniDesconto = BigDecimal.ZERO;
				}
				
				
				
				appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(valorUniDesconto), "pregeral", sb);
				appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PrecToTal")), "pretotal", sb);
				sb.append("</tr>");
			}
			sb.append("</tr>");
			sb.append("</thead>");
			sb.append("<tbody>");

			sb.append("</tbody>");
			sb.append("</tr>");
		}
	}

	private void buildProjetoTable(StringBuilder sb, EntityWrapper projetoWrapper, boolean addItensAvulsos) {
		List<NeoBaseEntity> itens = projetoWrapper.findGenericValue(fieldNetoPath);
		sb.append("<td colspan=\"5\">");
		sb.append("<table class=\"gridbox tblFilha\">");
		sb.append("<thead>");
		sb.append("<tr>");
		appendShortTag("th", "Quantidade", "", sb);
		appendShortTag("th", "unidade", "", sb);
		appendShortTag("th", "Descrição", "prDesc", sb);
		appendShortTag("th", "Preço unitário", "unitPr", sb);
		if (!addItensAvulsos)
			appendShortTag("th", "Desconto Permitido Projeto", "no-print", sb);
		appendShortTag("th", "Preço Com Desconto Permitido do Projeto", "", sb);
		if (!addItensAvulsos)
			appendShortTag("th", "Desconto Gerencial", "no-print", sb);
		if (!addItensAvulsos)
			appendShortTag("th", "Preço Com Desconto Projeto", "deconto-proj", sb);
		appendShortTag("th", "Preço total", "", sb);

		sb.append("</tr>");
		sb.append("</thead>");

		sb.append("<tbody>");
		for (NeoBaseEntity item : itens) {
			EntityWrapper itemWrapper = new EntityWrapper(item);
			sb.append("<tr>");
			appendShortTag("td", itemWrapper.findGenericValue("quantidade"), "", sb);
			appendShortTag("td", itemWrapper.findGenericValue("unidade"), "", sb);
			appendShortTag("td", itemWrapper.findGenericValue("descricao"), "prDesc", sb);
			
			// Alterado Pelo Gecinei quando o Valor Unitario for menor que o valor do preço com desconto na impressão saira o valor unitario saira com o preço com desconto
			
			BigDecimal valorUnip = itemWrapper.findGenericValue("PreUniP");
			BigDecimal valorUniDesp = itemWrapper.findGenericValue("PrCDesPr");
			
			if(valorUniDesp == null) {
			
			appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreUniP")), "unitPr", sb);	
			
			}
			
			if(valorUniDesp != null) {
				
			
			
			if(valorUniDesp.floatValue() > valorUnip.floatValue())   {
				
				appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PrCDesPr")), "unitPr", sb);
			
			    }
			 
			else{
				appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreUniP")), "unitPr", sb);
			}
			
			}
			
										
			  //appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreUniP")),"unitPr", sb);
			 // fim da Alteração de Gecinei
			
			
			if (!addItensAvulsos) {
				appendShortTag("td", itemWrapper.findGenericValue("DesPerPro"), "no-print", sb);appendShortTag("td",HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PrDesPePR")), "", sb);
				appendShortTag("td", itemWrapper.findGenericValue("DesGerePr"), "no-print", sb);
				appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PrCDesPr")),	"deconto-proj", sb);
				
			}
			
			if(addItensAvulsos) {
				
								
				BigDecimal valueWithDesconto =  itemWrapper.findGenericValue("PrCDesPr");
				
				if(valueWithDesconto == null || valueWithDesconto.floatValue() == 0f ) {
					valueWithDesconto =  itemWrapper.findGenericValue("DesGerePr");
				}
				
				if (valueWithDesconto == null) {
					valueWithDesconto = BigDecimal.ZERO;
				}
				
				appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(valueWithDesconto), "vlTotal", sb);
			}

			appendShortTag("td", HidroponiaSnapshotpedido.formatCurrencyValue(itemWrapper.findGenericValue("PreTotalP")), "", sb);

			sb.append("</tr>");
		}
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</td>");
	}

	private void appendShortTag(String tag, Object value, String style, StringBuilder sb) {
		StringBuilder element = new StringBuilder("<" + tag + " class='" + style + "'>");
		element.append(value != null ? value.toString() : "-");
		element.append("</" + tag + ">");

		sb.append(element);
	}
}
