package com.lexor.qbsa.service.payment.authorizenet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.inject.Inject;

import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.GetHostedPaymentPageController;
import net.authorize.api.controller.base.ApiOperationBase;
import org.jvnet.hk2.annotations.Service;

@Service
public class GetAnAcceptPaymentToken {
    
    @Inject
    private AuthorizeNetConfigService config;

    public String run(Double amount) {

        //Common code to set for all requests
        String apiLoginId = this.config.getApiLoginId();
        String transactionKey = this.config.getTransactionKey();

        ApiOperationBase.setEnvironment(this.config.getEnvironment());

        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(apiLoginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);
        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);

        // Create the payment transaction request
        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setAmount(new BigDecimal(amount).setScale(2, RoundingMode.CEILING));

        SettingType setting1 = new SettingType();
        setting1.setSettingName("hostedPaymentButtonOptions");
        setting1.setSettingValue("{\"text\": \"Pay\"}");

        SettingType setting2 = new SettingType();
        setting2.setSettingName("hostedPaymentOrderOptions");
        setting2.setSettingValue("{\"show\": false}");
        
        SettingType setting3 = new SettingType();
        setting3.setSettingName("hostedPaymentBillingAddressOptions");
        setting3.setSettingValue("{\"show\": true, \"required\": false}");

        SettingType setting4 = new SettingType();
        setting4.setSettingName("hostedPaymentPaymentOptions");
        setting4.setSettingValue("{\"cardCodeRequired\": true, \"showCreditCard\": true, \"showBankAccount\": true, \"customerProfileId\": false}");

        SettingType setting5 = new SettingType();
        setting5.setSettingName("hostedPaymentIFrameCommunicatorUrl");
        setting5.setSettingValue(String.format("{\"url\": \"%s\"}", config.getCommunicateUrl()));

        SettingType setting6 = new SettingType();
        setting6.setSettingName("hostedPaymentReturnOptions");
        setting6.setSettingValue("{\"showReceipt\": false}");

        ArrayOfSetting alist = new ArrayOfSetting();
        alist.getSetting().add(setting1);
        alist.getSetting().add(setting2);
//        alist.getSetting().add(setting3);
//        alist.getSetting().add(setting4);
        alist.getSetting().add(setting5);
        alist.getSetting().add(setting6);

        GetHostedPaymentPageRequest apiRequest = new GetHostedPaymentPageRequest();
        apiRequest.setTransactionRequest(txnRequest);
        apiRequest.setHostedPaymentSettings(alist);

        GetHostedPaymentPageController controller = new GetHostedPaymentPageController(apiRequest);
        controller.execute();

        GetHostedPaymentPageResponse response; // = new GetHostedPaymentPageResponse();
        response = controller.getApiResponse();

        if (response != null) {

            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {

                System.out.println(response.getMessages().getMessage().get(0).getCode());
                System.out.println(response.getMessages().getMessage().get(0).getText());

                return response.getToken();
            } else {
                System.out.println("Failed to get hosted payment page:  " + response.getMessages().getResultCode());
            }
        }
        return null;
    }
}
