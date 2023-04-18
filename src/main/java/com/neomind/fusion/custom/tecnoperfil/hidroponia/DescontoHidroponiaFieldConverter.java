package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.BigDecimalConverter;
import com.neomind.fusion.eform.converter.OriginEnum;

public class DescontoHidroponiaFieldConverter extends BigDecimalConverter {
	@Override
	public String getHTMLInput(EFormField field, OriginEnum origin) {
		return super.getHTMLInput(field, origin).concat(buildButtonScript(field));
	}

	private String buildButtonScript(EFormField field) {
		StringBuilder sb = new StringBuilder();
		sb.append(// DesPermWPH
				"<a class='btn edit_buttons' style='color: rgb(90, 112, 137); padding: 3.5px 9px; margin-left: 4px; border: 1px solid rgb(204, 204, 204); background: rgb(255, 255, 255); text-align: center; font-weight: 700; font-size: 11px; cursor: pointer; line-height: normal;' onClick='processaDesconto()'>Aplicar Desconto</a>");

		sb.append("				<script>" + "					function processaDesconto(){"
				+ "						const desconto = $(\"#var_DesPermWPH__\").val().replace('%','');"
				+ "						const pedidoId = $(\"#hid_root\").val();"
				+ "						$('input[name=\"action.send\"]').length ==1? $('input[name=\"action.save\"]').click() : $('input[name=\"action.apply\"]').click();"
				+ "						if(desconto && pedidoId){"
				+ "							fetch(`custom/desconto_pedido_hidroponia.jsp?pedidoId=${pedidoId}&desconto=${desconto}`).then(function(response) {"
				+ "							  if(response.ok) {"
				+ "								$('input[name=\"action.send\"]').length ==1? $('input[name=\"action.save\"]').click() : $('input[name=\"action.apply\"]').click();"
				+ "							  } else {"
				+ "							    console.log('Network response was not ok.');"
				+ "							  }" + "							})"
				+ "							.catch(function(error) {"
				+ "							  console.log('There has been a problem with your fetch operation: ' + error.message);"
				+ "							});" + "						}" + "					}"
				+ "				</script>");
		return sb.toString();
	}
}
