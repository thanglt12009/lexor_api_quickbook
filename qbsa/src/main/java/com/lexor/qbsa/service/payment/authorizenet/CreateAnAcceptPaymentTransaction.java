package com.lexor.qbsa.service.payment.authorizenet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.OpaqueDataType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.PaymentType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;
import org.jvnet.hk2.annotations.Service;

@Service
public class CreateAnAcceptPaymentTransaction {

    @Inject
    private AuthorizeNetConfigService config;

    public Map<String, String> run(Double amount, String dataDesc, String dataValue) {

        //Common code to set for all requests
        String apiLoginId = this.config.getApiLoginId();
        String transactionKey = this.config.getTransactionKey();

        ApiOperationBase.setEnvironment(this.config.getEnvironment());

        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(apiLoginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);
        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);

        // Populate the payment data
        PaymentType paymentType = new PaymentType();
        OpaqueDataType OpaqueData = new OpaqueDataType();
        OpaqueData.setDataDescriptor(dataDesc);
        OpaqueData.setDataValue(dataValue);
        paymentType.setOpaqueData(OpaqueData);

        // Create the payment transaction request
        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setPayment(paymentType);
        txnRequest.setAmount(new BigDecimal(amount).setScale(2, RoundingMode.CEILING));

        // Make the API Request
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setTransactionRequest(txnRequest);
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response = controller.getApiResponse();

        Map<String, String> payload = new HashMap<>();
        String json = "";

        if (response != null) {
            // If API Response is ok, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                TransactionResponse result = response.getTransactionResponse();
                if (result.getMessages() != null) {
                    payload.put("TransId", result.getTransId());
                    payload.put("ResponseCode", result.getResponseCode());
                    payload.put("MessageCode", result.getMessages().getMessage().get(0).getCode());
                    payload.put("Description", result.getMessages().getMessage().get(0).getDescription());
                    payload.put("AuthCode", result.getAuthCode());
                } else {
                    System.out.println("Failed Transaction.");
                    if (response.getTransactionResponse().getErrors() != null) {
                        payload.put("ErrorCode", response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
                        payload.put("ErrorMessage", response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                    }
                }
            } else {
                System.out.println("Failed Transaction.");
                if (response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null) {
                    payload.put("ErrorCode", response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
                    payload.put("ErrorMessage", response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                } else {
                    payload.put("ErrorCode", response.getMessages().getMessage().get(0).getCode());
                    payload.put("ErrorMessage", response.getMessages().getMessage().get(0).getText());
                }
            }
        } else {
            System.out.println("Null Response.");
        }

        return payload;

    }

}
