package com.neomind.fusion.custom.tecnoperfil.workflow.excecoes;

import com.neomind.framework.base.entity.NeoBaseEntity;
import com.neomind.fusion.common.NeoObject;
import com.neomind.fusion.entity.EntityInfo;
import com.neomind.fusion.entity.EntityWrapper;
import com.neomind.fusion.persist.PersistEngine;
import com.neomind.fusion.workflow.Activity;
import com.neomind.fusion.workflow.Task;
import com.neomind.fusion.workflow.adapter.AdapterInterface;
import com.neomind.fusion.workflow.adapter.AdapterUtils;
import com.neomind.util.NeoUtils;

public class CriaControleAdapter implements AdapterInterface{

	
	@Override
	public void start(Task arg0, EntityWrapper arg1, Activity arg2) {
		// TODO Auto-generated method stub
		
		NeoBaseEntity neoControleObject = AdapterUtils.createNewEntityInstance("CoDeExec");
		EntityWrapper controleWrapper = new EntityWrapper(neoControleObject);
		controleWrapper.setValue("IdDoProc", arg1.findValue("neoId"));
		controleWrapper.setValue("iterator", Long.valueOf(0));
		PersistEngine.getInstance().persist(controleWrapper.getObject());	
		PersistEngine.getInstance().closeConnection(PersistEngine.getInstance().getConnection());
		PersistEngine.getInstance().commit(true);
		PersistEngine.getInstance().getEntityManager().flush();
	}

	@Override
	public void back(EntityWrapper arg0, Activity arg1) {
		// TODO Auto-generated method stub
		
	}

}
