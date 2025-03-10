package id.rekaestudigital.idempiere.indonesia.base.component;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.osgi.service.event.Event;

public class IndonesiaBaseEventHandler extends AbstractEventHandler {
	
	CLogger log = CLogger.getCLogger(IndonesiaBaseEventHandler.class);

	@Override
	protected void initialize() {
		log.info("RajaOngkir Event Handler Initialized");

		registerEvent(IEventTopics.AFTER_LOGIN);
	}

	@Override
	protected void doHandleEvent(Event event) {
		String type = event.getTopic();

		PO po = getPO(event);
		log.info(po + " Type: " + type);

		if (type.equals(IEventTopics.AFTER_LOGIN)) {
			Env.setContext(Env.getCtx(), "#INDONESIA_LOCALIZATION", true);
		}
	}

}
