package com.neomind.fusion.custom.tecnoperfil.bitrix;

import java.io.File;

import com.neomind.fusion.custom.tecnoperfil.CriarOportunidadeAdapter;
import com.neomind.fusion.custom.tecnoperfil.ValidaClienteAdapter;

public class TesteBitrix {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		BitrixConnect c = getConnector();
		try {
			DealRequest d = new DealRequest();
			//System.out.println(getConnector().findCompany("teste 1813"));
			/*JSONArray array = (JSONArray) response.get("result");
			JSONObject result = (JSONObject) array.get(0);
			BigDecimal dc = new BigDecimal("14557.98");

			d.setCompanyId((String) result.get("ID"));
			d.setTitle("ORÇAMENTO TESTE");
			d.setOpportunity(dc.toString());
			d.setProposta(TecnoperfilRelatorioPedido.geraPDF("fsfasf"));

			c.createDeal(d);
			*/
			
			CommentRequest cr = new CommentRequest();
			cr.setComment("TESTE - PEDIDO - PDF");
			cr.setEntityType("DEAL");
			cr.setEnityId("21782");
			cr.setFile(new File("C:/amz/Relatorio.pdf"));
			
			c.createComment(cr);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	private static BitrixConnect getConnector() {
		BitrixConnect bConnect = new BitrixConnect();

		bConnect.configure("https://tecnoperfil.bitrix24.com.br/rest/8/", "oh712h1nelw14f6n");

		return bConnect;
	}
}
