package com.neomind.fusion.custom.tecnoperfil.industrial;

import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.OriginEnum;
import com.neomind.fusion.eform.converter.StringConverter;
import com.neomind.fusion.portal.PortalUtil;

public class BotaoImprimirIndustrialConverter extends StringConverter {
	
	/*
	 este converter também adiciona um javascript para abrir uma noa aba de acordo com neoid(campo hid_root)
	 */
	@Override
	protected String getHTMLView(EFormField field, OriginEnum origin) {
		String url = PortalUtil.getBaseURL();
		String botao = "<script>function opnreport(idPedido){" + "var url = '" + url
				+ "custom/pedido_industrial/resumo_industrial.jsp?pedidoId=" + "'+idPedido+'" + "';"
				+ "window,open(url);" + "} </script> "
				+ "<input type='button' class='input_button' value='Exportar Pedido' onclick='opnreport($(\"#hid_root\").val())'>";
		return botao;
	}
}
