package com.neomind.fusion.custom.tecnoperfil.industrial;

import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.BigDecimalConverter;
import com.neomind.fusion.eform.converter.OriginEnum;

public class BotaoDescontoFieldConverter extends BigDecimalConverter {
	@Override
	public String getHTMLInput(EFormField field, OriginEnum origin) {
		return super.getHTMLInput(field, origin).concat(buildButtonScript(field));
	}

	private String buildButtonScript(EFormField field) {
		StringBuilder sb = new StringBuilder();
		sb.append(// DesPermWPH
				"<a class='btn edit_buttons' style='color: rgb(90, 112, 137); padding: 3.5px 9px; margin-left: 4px; border: 1px solid rgb(204, 204, 204); background: rgb(255, 255, 255); text-align: center; font-weight: 700; font-size: 11px; cursor: pointer; line-height: normal;' onClick='processaDesconto()'>Aplicar Desconto</a>");

		sb.append("				<script>" + "	function processaDesconto(){"
				+ "						const desconto = $(\"#txt_WPedInd__ApDesPer__\").html().replace('%','').trim();"
				+ "						const descontoGerencial = $(\"#var_WPedInd__DesEspP__\").val().replace('%','').trim();"
				+ "						const descontoAntecipado = $(\"#txt_WPedInd__ApDesCP__\").html().replace('%','').trim();"
				+ "						const pedidoId = $(\"#hid_root\").val();"
				+ "						$('input[name=\"action.send\"]').length ==1? $('input[name=\"action.save\"]').click() : $('input[name=\"action.apply\"]').click();"
				+ "						if((desconto || descontoGerencial || descontoAntecipado)&& pedidoId){"
				+ "							fetch(`custom/pedido_industrial/desconto_industrial.jsp?pedidoId=${pedidoId}&desconto=${desconto}&descontoGerencial=${descontoGerencial}&descontoAntecipado=${descontoAntecipado}`).then(function(response) {"
				+ "							  if(response.ok) {"
				+ "								$('input[name=\"action.send\"]').length ==1? $('input[name=\"action.save\"]').click() : $('input[name=\"action.apply\"]').click();"
				+ "							  } else {"
				+ "							    console.log('Network response was not ok.');"
				+ "							  }" + "							})"
				+ "							.catch(function(error) {"
				+ "							  console.log('ERRO DESCONTO ESPECIAL: ' + error.message);"
				+ "							});" + "						}" + "					}"
				+ "				</script>");
		return sb.toString();
	}
}

