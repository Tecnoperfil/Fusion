package com.neomind.fusion.custom.tecnoperfil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.MaskFormatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.persist.QLEqualsFilter;
import com.neomind.fusion.persist.QLFilterIsNull;
import com.neomind.fusion.persist.QLGroupFilter;
import com.neomind.fusion.workflow.adapter.AdapterUtils;
import com.neomind.util.NeoUtils;

@WebServlet(name = "TecnoperfilServletUtils", urlPatterns = { "/servlet/TecnoperfilServletUtils" })
public class TecnoperfilServletUtils extends HttpServlet{

	private static final Log log = LogFactory.getLog(TecnoperfilServletUtils.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		this.doGet(req, resp);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		String action = req.getParameter("action");

		if (NeoUtils.safeIsNotNull(new Object[] { action }))
		{
			if (action.equalsIgnoreCase("geraRelatorioPedido"))
				geraRelatorioPedido(req, resp);
			if (action.equalsIgnoreCase("validaDescontoPromocional"))
				validaDescontoPromocional(req, resp);
			if (action.equalsIgnoreCase("validaDescontoFinanceiro"))
				validaDescontoFinanceiro(req, resp);
			if (action.equalsIgnoreCase("validaQuantidade"))
				validaQuantidade(req, resp);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validaDescontoPromocional(HttpServletRequest request, HttpServletResponse response)
	{
		String grupoProduto = NeoUtils.safeOutputString(request.getParameter("grupoProduto"));
		String nomeFerramenta = NeoUtils.safeOutputString(request.getParameter("nomeFerramenta"));
		String cor = NeoUtils.safeOutputString(request.getParameter("cor"));
		String pedido = NeoUtils.safeOutputString(request.getParameter("root"));
		String alicotaICMS = NeoUtils.safeOutputString(request.getParameter("grupoProduto"));
		
		BigDecimal desconto = buscaDescontoPromocional(grupoProduto, nomeFerramenta, cor, pedido, alicotaICMS);
		
		String retorno = "";
		if(!desconto.equals(new BigDecimal(0)))
		{
			retorno = "1;" + desconto.longValue();
		}
		else 
		{
			retorno = "0; Não foi encontrado um valor de desconto promocional. Favor entrar em contato com o administrador.";
		}
		
		try
		{
			final PrintWriter out = response.getWriter();
			out.print(retorno);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static BigDecimal buscaDescontoPromocional(String grupoProduto, String nomeFerramenta, String cor, String pedido, String alicotaICMS)
	{
		QLGroupFilter gp = new QLGroupFilter("AND");
		System.out.println("Buscando desconto Promocional....");
		if(grupoProduto != null && !grupoProduto.equals("") && !grupoProduto.equals("-1"))
		{
			System.out.println("Grupo do Produto: " + grupoProduto);
			
			QLGroupFilter gp2 = new QLGroupFilter("OR");
			gp2.addFilter(new QLEqualsFilter("grupoProdutos.neoId", Long.parseLong(grupoProduto)));
			gp.addFilter(gp2);
		}else
		{
			System.out.println("Grupo vazio ");
			gp.addFilter(new QLFilterIsNull("grupoProdutos"));
		}
		
		if(nomeFerramenta != null && !nomeFerramenta.equals("") && !nomeFerramenta.equals("-1"))
		{
			System.out.println("Ferramenta: " + nomeFerramenta);
			QLGroupFilter gp2 = new QLGroupFilter("OR");
			gp2.addFilter(new QLEqualsFilter("nomeFerramenta.neoId", Long.parseLong(nomeFerramenta)));
			gp2.addFilter(new QLFilterIsNull("nomeFerramenta"));
			gp.addFilter(gp2);
		}
		else
		{
			System.out.println("Ferramente vazio ");
			gp.addFilter(new QLFilterIsNull("nomeFerramenta"));
		}
		
		if(cor != null && !cor.equals("") && !cor.equals("-1"))
		{
			System.out.println("Cor: " + cor);
			QLGroupFilter gp2 = new QLGroupFilter("OR");
			gp2.addFilter(new QLEqualsFilter("cor.neoId", Long.parseLong(cor)));
			gp2.addFilter(new QLFilterIsNull("cor"));
			gp.addFilter(gp2);
		}
		else
		{
			System.out.println("Cor vazio ");
			gp.addFilter(new QLFilterIsNull("cor"));
		}
		
		if(!pedido.equals(""))
		{
			System.out.println("Buscando pedido.... ");
			NeoObject pedidoObj = PersistEngine.getObject(AdapterUtils.getEntityClass("TecnoPerfilWkfPedido"), new QLEqualsFilter("neoId", Long.parseLong(pedido)));
			
			if(pedidoObj != null)
			{
				System.out.println("Pedido: " + pedido);
				EntityWrapper w = new EntityWrapper(pedidoObj);
				
				String finalidade = NeoUtils.safeOutputString(w.findValue("pedido.finalidade.neoId"));
				if(finalidade != null && !finalidade.equals(""))
				{
					System.out.println("Finalidade: " + finalidade);
					QLGroupFilter gp2 = new QLGroupFilter("OR");
					gp2.addFilter(new QLEqualsFilter("finalidade.neoId", Long.parseLong(finalidade.trim())));
					gp2.addFilter(new QLFilterIsNull("finalidade"));
					gp.addFilter(gp2);
				}
				else
				{
					System.out.println("Finalidade vazio ");
					gp.addFilter(new QLFilterIsNull("finalidade"));
				}
				
				String categoria = NeoUtils.safeOutputString(w.findValue("pedido.clientes.categoria_empresa"));
				if(categoria != null && !categoria.equals(""))
				{
					System.out.println("Categoria: " + categoria);
					QLGroupFilter gp2 = new QLGroupFilter("OR");
					gp2.addFilter(new QLEqualsFilter("categoriaEmpresa.cod_gr_empresa", categoria));
					gp2.addFilter(new QLFilterIsNull("categoriaEmpresa"));
					gp.addFilter(gp2);
				}
				else
				{
					System.out.println("Categoria vazio ");
					gp.addFilter(new QLFilterIsNull("categoriaEmpresa"));
				}
			}
		}
		
		if(!alicotaICMS.equals(""))
		{
			System.out.println("ICMS...");
			NeoObject noTecnoPerfilNomeTabela = PersistEngine.getNeoObject(Long.parseLong(alicotaICMS));
			EntityWrapper wrapperTecnoPerilNomeTabela = new EntityWrapper(noTecnoPerfilNomeTabela);
			String codigo = (String) wrapperTecnoPerilNomeTabela.findValue("codigo");
			NeoObject alicota = PersistEngine.getObject(AdapterUtils.getEntityClass("TecnoPerfilNomeTabela"), new QLEqualsFilter("codigo", Long.parseLong(codigo)));
			if(alicota != null)
			{
				System.out.println("ICMS: " + alicotaICMS);
				EntityWrapper w = new EntityWrapper(alicota);
				BigDecimal icms = (BigDecimal) w.findValue("icms");
				System.out.println("ICMS valor: " + icms);
				QLGroupFilter gp2 = new QLGroupFilter("OR");
				gp2.addFilter(new QLEqualsFilter("aliquotaICMS", icms.setScale(0,BigDecimal.ROUND_UP).longValueExact()));
				gp2.addFilter(new QLFilterIsNull("aliquotaICMS"));
				gp.addFilter(gp2);
			}
			else
			{
				System.out.println("ICMS vazio ");
				gp.addFilter(new QLFilterIsNull("aliquotaICMS"));
			}
			
		}
		else
		{
			gp.addFilter(new QLFilterIsNull("aliquotaICMS"));
		}
		
		List<NeoObject> descontos = PersistEngine.getObjects(AdapterUtils.getEntityClass("descontoPromocional"), gp);
		
		BigDecimal desconto = new BigDecimal(0);
				
		if(descontos.size() >= 1)
		{
			EntityWrapper wDesconto = new EntityWrapper(descontos.get(0));
			desconto = wDesconto.findGenericValue("descontoPromocional");
		}
		return desconto;
	}
	
	@SuppressWarnings("unchecked")
	private void validaDescontoFinanceiro(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html; charset=UTF-8");
		
		String desc = NeoUtils.safeOutputString(request.getParameter("desc")).replace(",", ".");
		String condPgto = NeoUtils.safeOutputString(request.getParameter("condPgto"));
		
		NeoObject condicao = PersistEngine.getNeoObject(NeoUtils.safeLong(condPgto));
		
		System.out.println("Desconto: " + desc);
		System.out.println("Cond. Pagamento: " + condPgto);
		System.out.println("Neoid Cond. Pgto.: " + condicao.getNeoId());
		
		String retorno = "1";
		if(condicao != null)
		{
			EntityWrapper wCond = new EntityWrapper(condicao);
			BigDecimal valor = new BigDecimal(desc);
			BigDecimal valorMax = (BigDecimal) wCond.findValue("perc_desc_finan");
			
			System.out.println("Comparando: " + NeoUtils.safeOutputString(valorMax) + " com " + NeoUtils.safeOutputString(valor));
			
			if(valorMax.compareTo(valor) < 0)
			{
				System.out.println("Errado!!");
				retorno = "0; O Desconto é superior ao permitido  ("+NeoUtils.safeOutputString(valorMax)+"%)";
			}
			
		}
		else
		{
			retorno = "0; O Desconto Permitido  não é possível, a Condição de Pagamento do pedido não foi identificado.";
		}
		
		try
		{
			final PrintWriter out = response.getWriter();
			out.print(retorno);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validaQuantidade(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html; charset=UTF-8");
		
		String produto = NeoUtils.safeOutputString(request.getParameter("Produto"));
		String qtd = NeoUtils.safeOutputString(request.getParameter("Qtd"));
		
		NeoObject produtoObj = PersistEngine.getNeoObject(NeoUtils.safeLong(produto));
		
		String retorno = "1";
		if(produtoObj != null)
		{
			EntityWrapper wProduto = new EntityWrapper(produtoObj);
			String grupoProduto = NeoUtils.safeOutputString(wProduto.findValue("GruProDi")).trim();
			
			if((grupoProduto.equals("FOR"))|| (grupoProduto.equals("MOD")) || (grupoProduto.equals("REV")))
			{
				BigDecimal quantidade = new BigDecimal(qtd.replace(",", "."));
				
				BigDecimal tamanho = (BigDecimal) wProduto.findValue("Tamanh");
				BigDecimal zero = new BigDecimal(0);
				if(tamanho.compareTo(zero) != 0)
				{
					BigDecimal fator = (BigDecimal) wProduto.findValue("fator");
					
					BigDecimal resultado = (quantidade.multiply(fator));
					
					if(resultado.remainder(tamanho).doubleValue() > 0)
					{
						retorno = "0; A quantidade informada não é válida para o produto selecionado (Tamanho: "+NeoUtils.safeOutputString(tamanho)+", Fator: "+NeoUtils.safeOutputString(fator)+")";
					}
						
				}
			}
		}
		
		try
		{
			final PrintWriter out = response.getWriter();
			out.print(retorno);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void geraRelatorioPedido(HttpServletRequest request, HttpServletResponse response)
	{
		String numeroPedido = NeoUtils.safeOutputString(request.getParameter("pedido"));

		try
		{
			if (numeroPedido != null && !numeroPedido.isEmpty()) 
			{
				File fileProposta = TecnoperfilRelatorioPedido.geraPDF(numeroPedido);
				
				if(fileProposta == null) {
					System.out.println("Erro ao montar arquivo");
					return;
				}
				
				OutputStream out = response.getOutputStream();
				try
				{
					String sContentType = null;
					sContentType = "application/pdf";
					response.setContentType(sContentType);
					response.addHeader("content-disposition", "attachment; filename=" + fileProposta.getName());
					//response.setCharacterEncoding("ISO-8859-1" );
					InputStream in = null;
					in = new BufferedInputStream(new FileInputStream(fileProposta));
					if (in != null)
					{
						response.setContentLength((int) in.available());
						int l;
						byte b[] = new byte[1024];
						while ((l = in.read(b, 0, b.length)) != -1)
						{
							out.write(b, 0, l);
						}
						out.flush();
						in.close();
					}
					else
					{
						System.out.println("Trying to download an invalid file: ");
					}
				}
				catch (Exception e)
				{
					System.out.println("Error trying to download file ");
					e.printStackTrace();
				}
				finally
				{
					out.close();
				}
			}
		}
		catch (Exception e)
		{
			log.error("Erro ao imprimir o relatório de posição física financeira!", e);
		}	
	}
	
	/*
	 * se isCPNJ = false, então é mascara de CPF
	 */
	public static String aplicaMascara(String valor, boolean isCNPJ)
	{
		try
		{
			if (valor.contains(".") || valor.contains("/") || valor.contains("-") || valor.equals("")
					|| valor.contains("_"))
				return valor;

			MaskFormatter msk = new MaskFormatter(getCNPJMask());
			if(isCNPJ)
				msk = new MaskFormatter(getCNPJMask());
			else
				msk = new MaskFormatter(getCPFMask());
			msk.setValueContainsLiteralCharacters(false);
			if (msk != null)
			{
				return msk.valueToString(valor);
			}
		}
		catch (Exception e)
		{
			log.error("Erro ao aplicar a máscara de CNPJ no valor!", e);
		}
		return "";
	}
	
	public static String getCNPJMask()
	{
		return "##.###.###/####-##";
	}
	
	public static String getCPFMask()
	{
		return "###.###.###-##";
	}
}
