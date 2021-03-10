// The result of the transaction processing will be returned from the processing script as a JSON object. Parse the object to determine success or failure, and alert the user.
function messageFunc(msg)
{
    try {
        responseObj = JSON.parse(msg);
        if (responseObj.ResponseCode === '1') {
            message = "Transaction Successful!<br>Transaction ID: " + responseObj.TransId;
        } else {
            message = "Transaction Unsuccessful.";//+responseObj.messages.message[0].text;
            if (responseObj.ErrorCode !== null)//to do: take care of errors[1] array being parsed into single object
            {
                message += responseObj.ErrorMessage;
            }
            /*else if(responseObj.transactionResponse.errors[0]!=null)
             {
             for(i=0;i<responseObj.transactionResponse.errors.length;i++)
             {
             message+="<br>";
             message+=responseObj.transactionResponse.errors[i].error.errorText;
             }
             }*/
            if (responseObj.TransId !== null)
            {
                message += "<br>";
                message += ("Transaction ID: " + responseObj.TransId)
            }
        }
    } catch (error) {
        console.log("Couldn't parse result string");
        console.log(error);
        message = "Error.";
    }

    //alert(message);

    $('#acceptJSReceiptBody').html(message);
    //jQuery.noConflict();
//    $('#acceptJSPayModal').modal('hide');
    $('#acceptJSReceiptModal').modal('show');
}

// Do an AJAX call to submit the transaction data and the payment none to a separate PHP page to do the actual transaction processing.
function createTransact(dataObj) {

    // Set Amount for demo purposes if not set by callers form
    customer_id = document.getElementById('customer_id').value;
    quickbook_customer_id = document.getElementById('quickbook_customer_id').value;
    order_id = document.getElementById('order_id').value;
    payment_method = $('input[name="paymentMethod"]:checked').val();
    if (payment_method === 'creditcard') {
        myAmt = document.getElementById('amount').value;
    }
    if (payment_method === 'echeck') {
        myAmt = document.getElementById('echeck_amount').value;
    }
    console.log('Amount = ' + myAmt);

    $.ajax({
        url: "/qbsa/api/paymentonline/transactionCaller",
        data: {
            amount: myAmt,
            dataDesc: dataObj.dataDescriptor,
            dataValue: dataObj.dataValue,
            customer_id: customer_id,
            quickbook_customer_id: quickbook_customer_id,
            order_id: order_id,
            payment_method: payment_method
        },
        method: 'POST',
        timeout: 50000

    }).done(function (data) {

        console.log('Success');

    }).fail(function () {

        console.log('Error');

    }).always(function (textStatus) {

        console.log(textStatus);
        messageFunc(textStatus);

    })

}

// Process the response from Authorize.Net to retrieve the two elements of the payment nonce.
// If the data looks correct, record the OpaqueData to the console and call the transaction processing function.
function  responseHandler(response) {
    if (response.messages.resultCode === 'Error') {
        err_msg = '';
        for (var i = 0; i < response.messages.message.length; i++) {
            console.log(response.messages.message[i].code + ':' + response.messages.message[i].text);
            err_msg += response.messages.message[i].text;
        }
        alert("acceptJS library error!" + err_msg);
    } else {
        console.log(response.opaqueData.dataDescriptor);
        console.log(response.opaqueData.dataValue);
        createTransact(response.opaqueData);
    }
}

function acceptJSCaller(payment_method)
{
    var secureData = {}, authData = {}, cardData = {}, bankData = {};

    // Extract the card number and expiration date.
    if (payment_method === 'creditcard') {
        cardData.cardNumber = document.getElementById('creditCardNumber').value;
        cardData.cardCode = document.getElementById('cvv').value;
        cardData.month = document.getElementById('expiryDateMM').value;
        cardData.year = document.getElementById('expiryDateYY').value;
        cardData.zip = document.getElementById('zip').value;
        cardData.fullName = document.getElementById('fullName').value;
        secureData.cardData = cardData;
    }
    if (payment_method === 'echeck') {
        bankData.accountNumber = document.getElementById('accountNumber').value;
        bankData.routingNumber = document.getElementById('routingNumber').value;
        bankData.nameOnAccount = document.getElementById('nameOnAccount').value;
        bankData.accountType = $('input[name="accountType"]:checked').val();
        secureData.bankData = bankData;
    }

    // The Authorize.Net Client Key is used in place of the traditional Transaction Key. The Transaction Key
    // is a shared secret and must never be exposed. The Client Key is a public key suitable for use where
    // someone outside the merchant might see it.

    authData.clientKey = document.getElementById('clientKey').value;
    authData.apiLoginID = document.getElementById('apiLoginID').value;
    secureData.authData = authData;

    // Pass the card number and expiration date to Accept.js for submission to Authorize.Net.
    Accept.dispatchData(secureData, 'responseHandler');
}
