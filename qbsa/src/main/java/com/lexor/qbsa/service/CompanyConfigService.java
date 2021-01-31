package com.lexor.qbsa.service;


import com.lexor.qbsa.domain.CompanyConfig;

public interface CompanyConfigService {

	public Iterable<CompanyConfig> getAllCompanyConfigs();
	
	public CompanyConfig getCompanyConfigByRealmId(String realmId);
	
	public CompanyConfig getCompanyConfigById(Integer id);

	public void save(CompanyConfig companyConfig);
        public void update(CompanyConfig companyConfig);
}
