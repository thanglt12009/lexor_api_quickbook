package com.lexor.qbsa.service;

import com.intuit.ipp.util.Logger;
import com.lexor.qbsa.domain.CompanyConfig;
import com.lexor.qbsa.repository.CompanyConfigRepository;
import com.lexor.qbsa.service.security.SecurityService;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

/**
 * Service class to store and retrieve CompanyConfig data from database During
 * save access token and secret is encrypted During retrieve the tokens are
 * decrypted
 *
 */

@Service
public class CompanyConfigServiceImpl implements CompanyConfigService {

    private static final org.slf4j.Logger LOG = Logger.getLogger();
    private static final java.util.logging.Logger LOGG = java.util.logging.Logger.getLogger(CompanyConfigServiceImpl.class.getName());


    @Inject
    private CompanyConfigRepository companyConfigRepository;
    
    @Inject
    private SecurityService securityService;

    @Override
    public Iterable<CompanyConfig> getAllCompanyConfigs() {
        List<CompanyConfig> companyConfigs = null;
        try {
            companyConfigs = companyConfigRepository.findAll();
            companyConfigs.forEach(config -> config.setAccessToken(decrypt(config.getAccessToken())));
            companyConfigs.forEach(config -> config.setAccessTokenSecret(decrypt(config.getAccessTokenSecret())));
        } catch (SQLException ex) {
            LOG.error("Error loading company configs", ex.getCause());
        }
        return companyConfigs;
    }

    @Override
    public CompanyConfig getCompanyConfigByRealmId(String realmId) {

        CompanyConfig companyConfig;
        try {
            companyConfig = companyConfigRepository.findByRealmId(realmId);
            if (companyConfig != null) {
                LOGG.info("start decrypt");
                companyConfig.setAccessToken(decrypt(companyConfig.getAccessToken()));
                companyConfig.setAccessTokenSecret(decrypt(companyConfig.getAccessTokenSecret()));
                LOGG.info("done decrypt");
            }
            return companyConfig;
        } catch (SQLException ex) {
            LOG.error("Error loading company configs", ex.getCause());
            return null;
        }
    }

    @Override
    public CompanyConfig getCompanyConfigById(Integer id) {
        CompanyConfig companyConfig;
        try {
            companyConfig = companyConfigRepository.get(id);
            if (companyConfig != null) {
                companyConfig.setAccessToken(decrypt(companyConfig.getAccessToken()));
                companyConfig.setAccessTokenSecret(decrypt(companyConfig.getAccessTokenSecret()));
            }
            return companyConfig;
        } catch (SQLException ex) {
            LOG.error("Error loading company configs", ex.getCause());
            return null;
        }
    }

    @Override
    public void save(CompanyConfig companyConfig) {
        try {
            companyConfig.setAccessToken(encrypt(companyConfig.getAccessToken()));
            companyConfig.setAccessTokenSecret(encrypt(companyConfig.getAccessTokenSecret()));
            companyConfigRepository.persist(companyConfig);
        } catch (SQLException ex) {
            LOG.error("Error loading company configs", ex.getCause());
        }
    }
    @Override
    public void update(CompanyConfig companyConfig) {
        try {
            companyConfig.setAccessToken(encrypt(companyConfig.getAccessToken()));
            companyConfig.setAccessTokenSecret(encrypt(companyConfig.getAccessTokenSecret()));
            companyConfigRepository.update(companyConfig.getId(), companyConfig);
        } catch (SQLException ex) {
            LOG.error("Error loading company configs", ex.getCause());
        }
    }

    public String decrypt(String string) {
        try {
            return securityService.decrypt(string);
        } catch (Exception ex) {
            LOG.error("Error decrypting", ex.getCause());
            return null;
        }
    }

    public String encrypt(String string) {
        try {
            return securityService.encrypt(string);
        } catch (Exception ex) {
            LOG.error("Error encrypting", ex.getCause());
            return null;
        }
    }
}
