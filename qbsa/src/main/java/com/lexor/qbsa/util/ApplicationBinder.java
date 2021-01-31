package com.lexor.qbsa.util;

import com.lexor.qbsa.repository.CompanyConfigRepository;
import com.lexor.qbsa.repository.ConfigurationsRepository;
import com.lexor.qbsa.repository.PayloadQueueRepository;
import com.lexor.qbsa.service.CompanyConfigService;
import com.lexor.qbsa.service.CompanyConfigServiceImpl;
import com.lexor.qbsa.service.payment.authorizenet.AuthorizeNetConfigService;
import com.lexor.qbsa.service.payment.authorizenet.CreateAnAcceptPaymentTransaction;
import com.lexor.qbsa.service.qbo.CDCService;
import com.lexor.qbsa.service.qbo.OAuth2PlatformClientFactory;
import com.lexor.qbsa.service.qbo.QBODataService;
import com.lexor.qbsa.service.qbo.WebhooksServiceFactory;
import com.lexor.qbsa.service.queue.QueryService;
import com.lexor.qbsa.service.queue.QueueProcessor;
import com.lexor.qbsa.service.queue.QueueService;
import com.lexor.qbsa.service.security.SecurityService;
import com.lexor.qbsa.util.qualifier.QueryAPIQualifier;
import com.lexor.qbsa.util.qualifier.CdcAPIQualifier;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.internal.inject.AbstractBinder;

@Provider
public class ApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        // repositories
        bindAsContract(ConfigHelper.class);
        bindAsContract(DbConnectionHelper.class);
        bindAsContract(CompanyConfigRepository.class);
        bindAsContract(ConfigurationsRepository.class);
        bindAsContract(PayloadQueueRepository.class);
        bindAsContract(SecurityService.class);
        bindAsContract(OAuth2PlatformClientFactory.class);
        bindAsContract(QBOServiceHelper.class);
        bindAsContract(WebhooksServiceFactory.class);
        bindAsContract(QueueProcessor.class);
        bindAsContract(QueueService.class);
        bindAsContract(AuthorizeNetConfigService.class);
        bindAsContract(CreateAnAcceptPaymentTransaction.class);

        bind(CompanyConfigServiceImpl.class).to(CompanyConfigService.class);
        bind(QueryService.class).to(QBODataService.class).qualifiedBy(new QueryAPIQualifier());
        bind(CDCService.class).to(QBODataService.class).qualifiedBy(new CdcAPIQualifier());
    }
}
