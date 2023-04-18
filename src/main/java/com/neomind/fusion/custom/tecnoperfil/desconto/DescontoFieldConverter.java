package com.neomind.fusion.custom.tecnoperfil.desconto;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.stream.Stream;

import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.BigDecimalConverter;
import com.neomind.fusion.eform.converter.DefaultConverter;
import com.neomind.fusion.eform.converter.OriginEnum;

public class DescontoFieldConverter extends BigDecimalConverter {

	@Override
	protected String getHTMLView(EFormField field, OriginEnum origin) {
		return super.getHTMLView(field, origin);
	}

	@Override
	public String getHTMLInput(EFormField field, OriginEnum origin) {
		return super.getHTMLInput(field, origin).concat(buildButtonScript());
	}

	private String buildButtonScript() {
		return "<a class='btn edit_buttons' style='color: rgb(90, 112, 137); padding: 3.5px 9px; margin-left: 4px; border: 1px solid rgb(204, 204, 204); background: rgb(255, 255, 255); text-align: center; font-weight: 700; font-size: 11px; cursor: pointer; line-height: normal;' onClick='processaDesconto()'>Aplicar Desconto</a>"
				+ "				<script>"
				+ "					function processaDesconto(){"
				+ "						const desconto = $(\"#var_WPedido__PDeEspI__\").val();"
				+ "						const pedidoId = $(\"#hid_WPedido__\").val();"
				+ "						$('input[name=\"action.send\"]').length ==1? $('input[name=\"action.save\"]').click() : $('input[name=\"action.apply\"]').click();"
				+ "						if(desconto && pedidoId){"
				+ "							fetch(`custom/desconto_pedido_construcao_civil.jsp?pedidoId=${pedidoId}&desconto=${desconto}`).then(function(response) {"
				+ "							  if(response.ok) {"
				+ "								$('input[name=\"action.send\"]').length ==1? $('input[name=\"action.save\"]').click() : $('input[name=\"action.apply\"]').click();"
				+ "							  } else {"
				+ "							    console.log('Network response was not ok.');"
				+ "							  }"
				+ "							})"
				+ "							.catch(function(error) {"
				+ "							  console.log('There has been a problem with your fetch operation: ' + error.message);"
				+ "							});"
				+ "						}"
				+ "					}"
				+ "				</script>";
			
		/*try {
			byte[] encoded = Files.readAllBytes(Paths.get("C:\\Ambiente_neomind\\evaljs.txt"));

			return new String(encoded);
		} catch (Exception e) {
			return " erro ".concat(e.getMessage());
		}*/
	}
}
