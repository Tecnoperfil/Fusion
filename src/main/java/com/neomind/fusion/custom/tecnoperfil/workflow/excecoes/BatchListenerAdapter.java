package com.neomind.fusion.custom.tecnoperfil.workflow.excecoes;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.jpa.internal.EntityManagerFactoryImpl;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.entity.InstantiableEntityInfo;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.persist.QLEqualsFilter;
import com.neomind.fusion.persist.QLGroupFilter;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.adapter.AdapterUtils;

public class BatchListenerAdapter implements AdapterInterface {

	@Override
	public void start(Task arg0, EntityWrapper wrapper, Activity arg2) {
		// TODO Auto-generated method stub
		try {
			InstantiableEntityInfo CoDeExecClazz = AdapterUtils.getInstantiableEntityInfo("CoDeExec");
			
			PersistEngine.getInstance().getEntityManager().flush();
			NeoBaseEntity controleObjec = (NeoBaseEntity) PersistEngine.getObject(CoDeExecClazz.getEntityClass(),
					new QLEqualsFilter("IdDoProc", (Long)wrapper.findGenericValue("neoId")));
			
			EntityWrapper controleWrapper = new EntityWrapper(controleObjec);
			
			Long iterator = controleWrapper.findGenericValue("iterator");
			String retorno = controleWrapper.findGenericValue("StaDoBat");
			
			if (iterator < 2 && (retorno == null || retorno.isEmpty())) {
				controleWrapper.setValue("iterator", Long.valueOf(iterator + 1));
				
				PersistEngine.getInstance().persist(controleWrapper.getObject());	
				PersistEngine.getInstance().closeConnection(PersistEngine.getInstance().getConnection());
				PersistEngine.getInstance().commit(true);
				PersistEngine.getInstance().getEntityManager().flush();
			} else {
				wrapper.setValue("Concluido", true);
			}
		} catch (Exception e) {
			wrapper.setValue("Concluido", true);
			wrapper.setValue("RetornoTEC", "Erro durante a execução :"+e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub

	}

}
