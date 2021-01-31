package com.lexor.qbsa.service.qbo;

import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.services.CDCQueryResult;
import com.intuit.ipp.services.DataService;
import com.lexor.qbsa.domain.CompanyConfig;
import com.lexor.qbsa.util.QBOServiceHelper;
import com.lexor.qbsa.util.Utility;
import com.lexor.qbsa.util.qualifier.CdcAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

/**
 * Class for implementing the QBO CDC api
 *
 */
@CdcAPI
@Service
public class CDCService implements QBODataService {

	private static final java.util.logging.Logger LOG = Logger.getLogger (CDCService.class.getName());

	@Inject
	OAuth2PlatformClientFactory factory;

	@Inject
	public QBOServiceHelper helper;

	@Override
	public void callDataService(CompanyConfig companyConfig) throws Exception {
		
		// create data service
		DataService service = helper.getDataService(Utility.realmId,Utility.bearerToken);

		try {
			LOG.info("Calling CDC ");
			// build entity list for cdc based on entities subscribed for webhooks
			String[] subscribedEntities = companyConfig.getWebhooksSubscribedEntites().split(",");
			List<IEntity> entities = new ArrayList<>();
			for (String subscribedEntity : subscribedEntities) {
				Class<?> className = Class.forName("com.intuit.ipp.data." + subscribedEntity);

				//clock@codemate.vn: be careful with this reflection construction
				IEntity entity = (IEntity) className.getDeclaredConstructor().newInstance();
				entities.add(entity);
			}
			LOG.info("Actually Calling CDC ");
			// call CDC
			List<CDCQueryResult> res = service.executeCDCQuery(entities, companyConfig.getLastCdcTimestamp());
                        System.out.println(res);
                        // TODO - sync data to ERP
			LOG.info("CDC complete ");

		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Error while calling CDC{0}", ex.getMessage());
		}

	}

}
