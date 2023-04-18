package com.neomind.fusion.custom.tecnoperfil.hidroponia;

import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.OriginEnum;
import com.neomind.fusion.eform.converter.StringConverter;
import com.neomind.fusion.portal.PortalUtil;

public class BotaoImprimirConverter extends StringConverter{
	@Override
	protected String getHTMLView(EFormField field, OriginEnum origin) {
		String url = PortalUtil.getBaseURL();
		String botao = "<script>function opnreport(){"
				+ "var url = '"
				+ url
				+ "custom/hidroponia/resumo_hidroponia.jsp?pedidoId="
				+ field.getForm().getObject().getNeoId()
				+ "';"
				+ "window,open(url);"
				+ "} </script> "
				+ "<input type='button' class='input_button' value='Exportar Pedido' onclick='opnreport()'>";
		return botao;
	}
}
