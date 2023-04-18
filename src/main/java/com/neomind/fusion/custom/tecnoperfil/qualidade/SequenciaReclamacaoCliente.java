package com.neomind.fusion.custom.tecnoperfil.qualidade;

import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.eform.EFormField;
import com.neomind.fusion.eform.converter.OriginEnum;
import com.neomind.fusion.eform.converter.StringConverter;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.util.NeoUtils;

public class SequenciaReclamacaoCliente extends StringConverter{
	
	@Override
	protected String getHTMLView(EFormField field, OriginEnum origin)
	{
		return super.getHTMLView(field, origin);
	}
	
	@Override
	protected String getHTMLInput(EFormField field, OriginEnum origin) 
	{
		
				NeoObject RECCLI = (NeoObject)field.getForm().getCaller().getObject();
				
					
						Long sequencia = (Long) new EntityWrapper(RECCLI).findField("SequeSO").getValue();
						sequencia = sequencia+1;
						field.setValue(NeoUtils.safeOutputString(sequencia));
					
					
				
					
		
			return super.getHTMLInput(field, origin);
	}

}
