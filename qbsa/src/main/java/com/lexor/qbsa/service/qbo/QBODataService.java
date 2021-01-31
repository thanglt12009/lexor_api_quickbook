package com.lexor.qbsa.service.qbo;


import com.lexor.qbsa.domain.CompanyConfig;

/**
 * Interface holding methods to call QBP Dataservice api
 *
 */
public interface QBODataService {
	
	public void callDataService(CompanyConfig companyConfig) throws Exception;

}
