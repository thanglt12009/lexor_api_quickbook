package com.lexor.qbsa.service.qbo;

import com.intuit.ipp.services.WebhooksService;

public class WebhooksServiceFactory {

    /**
     * Initializes WebhooksService
     *
     * @return WebhooksService
     */
    public WebhooksService getWebhooksService() {

        //create WebhooksService
        return new WebhooksService();
    }

}
