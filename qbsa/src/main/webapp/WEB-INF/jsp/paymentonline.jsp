<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">

        <title>Lexor payment page</title>

        <!-- Bootstrap core CSS -->
        <link href="/qbsa/static/js/authorizenet/scripts/bootstrap.min.css" rel="stylesheet">

        <style type="text/css">

            .navbar {min-height: 0px; margin-bottom: 0px; border: 0px;}
            .nav>li {display: inline-block;}
            .navbar-centered .nav > li > a {color: white}
            .navbar-inverse { background-color: #555  } /* #7B7B7B */
            .navbar-centered .nav > li > a:hover{ background-color: white; color: black }
            .navbar-centered .nav .active > a,.navbar-centered .navbar-nav > .active > a:focus { background-color: white; color: black; font-weight:bold; }
            .navbar-centered .navbar-nav { float: none; text-align: center; }
            .navbar-centered .navbar-nav > li { float: none; }
            .navbar-centered .nav > li { display: inline; }
            .navbar-centered .nav > li > a {display: inline-block; }
            #home { color:ivory; margin-left: 15%; margin-right: 15%;}

            @media (min-width: 768px) {
                .navbar-centered .nav > li > a { width:15%; }
                #home { font-size: 30px}
            }

            @media (min-width:360px ) and (max-width: 768px){
                .navbar-centered .nav > li > a {font-size: 12px}
                #home { font-size: 20px}
            }

            @media (max-width: 360px) {
                .navbar-centered .nav > li > a {font-size: 10px}
                #home { font-size: 15px}
            }

            @media (min-width: 1022px) {
                .modal-dialog { width: 850px}
                #add_shipping { height: 300px }
            }

            /* vertically center the Bootstrap modals */
            .modal {
                text-align: center;
                padding: 0!important;
            }

            .modal:before {
                content: '';
                display: inline-block;
                height: 100%;
                vertical-align: middle;
                margin-right: -4px;
            }

            .modal-dialog {
                display: inline-block;
                text-align: left;
                vertical-align: middle;
            }

            .apple-pay-button-with-text {
                --apple-pay-scale: 1; /* (height / 32) */
                display: inline-flex;
                justify-content: center;
                font-size: 12px;
                border-radius: 5px;
                padding: 0px;
                box-sizing: border-box;
                min-width: 200px;
                min-height: 32px;
                max-height: 64px;
                cursor: pointer;
            }

            .apple-pay-button-white-with-text {
                background-color: white;
                color: black;
            }

            .apple-pay-button-white-with-line-with-text {
                background-color: white;
                color: black;
                border: .5px solid black;
            }

            .apple-pay-button-with-text.apple-pay-button-white-with-text > .logo {
                background-image: -webkit-named-image(apple-pay-logo-black);
                background-color: white;
            }

            .apple-pay-button-with-text.apple-pay-button-white-with-line-with-text > .logo {
                background-image: -webkit-named-image(apple-pay-logo-black);
                background-color: white;
            }

            .apple-pay-button-with-text > .text {
                font-family: -apple-system;
                font-size: calc(1em * var(--apple-pay-scale));
                font-weight: 300;
                align-self: center;
                margin-right: calc(2px * var(--apple-pay-scale));
            }

            .apple-pay-button-with-text > .logo {
                width: calc(35px * var(--scale));
                height: 100%;
                background-size: 100% 60%;
                background-repeat: no-repeat;
                background-position: 0 50%;
                margin-left: calc(2px * var(--apple-pay-scale));
                border: none;
            }

        </style>

        <script src="/qbsa/static/js/authorizenet/scripts/jquery-2.1.4.min.js"></script>
        <script src="/qbsa/static/js/authorizenet/scripts/bootstrap.min.js"></script>
        <script src="/qbsa/static/js/authorizenet/scripts/jquery.cookie.js"></script>
        
        <script src="https://api.paysimple.com/paysimplejs/v1/scripts/client.js"></script>

        <script type="text/javascript">

            var baseUrl = "${it.apiurl}/customer/";
            var onLoad = true;
            tab = "#pay";

            function returnLoaded() {
                console.log("Return Page Called ! ");
                showTab(tab);
            }
            window.CommunicationHandler = {};
            function parseQueryString(str) {
                var vars = [];
                var arr = str.split('&');
                var pair;
                for (var i = 0; i < arr.length; i++) {
                    pair = arr[i].split('=');
                    vars[pair[0]] = unescape(pair[1]);
                }
                return vars;
            }
            CommunicationHandler.onReceiveCommunication = function (argument) {
                params = parseQueryString(argument.qstr)
                parentFrame = argument.parent.split('/')[4];
                console.log(params);
                console.log(parentFrame);
                //alert(params['height']);
                $frame = null;
                switch (parentFrame) {
                    case "manage" 		:
                        $frame = $("#load_profile");
                        break;
                    case "addPayment" 	:
                        $frame = $("#add_payment");
                        break;
                    case "addShipping" 	:
                        $frame = $("#add_shipping");
                        break;
                    case "editPayment" 	:
                        $frame = $("#edit_payment");
                        break;
                    case "editShipping"	:
                        $frame = $("#edit_shipping");
                        break;
                    case "payment"		:
                        $frame = $("#load_payment");
                        break;
                }

                switch (params['action']) {
//                    case "resizeWindow" 	:
//                        if (parentFrame == "manage" && parseInt(params['height']) < 1150)
//                            params['height'] = 1150;
//                        if (parentFrame == "payment" && parseInt(params['height']) < 1000)
//                            params['height'] = 1000;
//                        if (parentFrame == "addShipping" && $(window).width() > 1021)
//                            params['height'] = 350;
//                        $frame.outerHeight(parseInt(params['height']));
//                        break;

                    case "successfulSave" 	:
                        $('#myModal').modal('hide');
                        location.reload(false);
                        break;

                    case "cancel" 			:
                        var currTime = sessionStorage.getItem("lastTokenTime");
                        if (currTime === null || (Date.now() - currTime) / 60000 > 15) {
                            location.reload(true);
                            onLoad = true;
                        }
                        switch (parentFrame) {
                            case "addPayment"   :
                                $("#send_token").attr({"action": baseUrl + "addPayment", "target": "add_payment"}).submit();
                                $("#add_payment").hide();
                                break;
                            case "addShipping"  :
                                $("#send_token").attr({"action": baseUrl + "addShipping", "target": "add_shipping"}).submit();
                                $("#add_shipping").hide();
                                $('#myModal').modal('toggle');
                                break;
                            case "manage"       :
                                $("#send_token").attr({"action": baseUrl + "manage", "target": "load_profile"}).submit();
                                break;
                            case "editPayment"  :
                                $("#payment").show();
                                $("#addPayDiv").show();
                                break;
                            case "editShipping" :
                                $('#myModal').modal('toggle');
                                $("#shipping").show();
                                $("#addShipDiv").show();
                                break;
                            case "payment"		:
//                                sessionStorage.removeItem("HPTokenTime");
//                                $('#HostedPayment').attr('src', 'about:blank');
                                break;
                        }
                        break;

                    case "transactResponse"	:
//                        $('#HostedPayment').attr('src', 'about:blank');
                        var transResponse = JSON.parse(params['response']);
                        console.log(transResponse);
                        sendTransactResponse(transResponse);
                }
            }

            function showTransactionMessage(msg)
            {
                try {
                    responseObj = JSON.parse(msg);
                    message = "Transaction Successful!<br>Transaction ID: " + responseObj.TransId;
                } catch (error) {
                    console.log("Couldn't parse result string");
                    console.log(error);
                    message = "Error.";
                }
                $('#acceptJSReceiptBody').html(message);
                $('#acceptJSReceiptModal').modal('show');
            }

            function sendTransactResponse(dataObj) {

                // Set Amount for demo purposes if not set by callers form
                customer_id = document.getElementById('customer_id').value;
                quickbook_customer_id = document.getElementById('quickbook_customer_id').value;
                order_id = document.getElementById('order_id').value;
                console.log('Amount = ' + dataObj.totalAmount);

                $.ajax({
                    url: "/qbsa/api/paymentonline/transactionResponse",
                    data: {
                        amount: dataObj.totalAmount,
                        transId: dataObj.transId,
                        authorization: dataObj.authorization,
                        customer_id: customer_id,
                        quickbook_customer_id: quickbook_customer_id,
                        order_id: order_id
                    },
                    method: 'POST',
                    timeout: 50000

                }).done(function (data) {

                    console.log('Success');

                }).fail(function () {

                    console.log('Error');

                }).always(function (textStatus) {

                    console.log(textStatus);
                    showTransactionMessage(textStatus);

                })

            }

            function showTab(target) {
                console.log(target);
                onLoad = false;

                switch (target) {
                    case "#pay" 		:
                        $("#paymentPanel").show();
                        $("#load_payment").show();
                        break;
                }
            }

            $(function () {
                console.log("Tab : " + tab);
                showTab(tab);

            });
        </script>

    </head>

    <body>

        <input type='hidden' id='cardinalRequestJwt' value='123456'>
        <button class="AcceptUI hidden btn btn-primary btn-lg col-md-3 col-sm-offset-1 col-sm-4 col-xs-offset-2 col-xs-8"
                style="font-weight: bolder; font-size: 24px; margin-top: 10px; margin-bottom: 10px"
                type="button" id="acceptUIPayButton"
                data-billingaddressoptions="{&quot;show&quot;:true, &quot;required&quot;:true}"
                data-apiloginid="${it.apiLoginID}"
                data-clientkey="${it.clientKey}"
                data-acceptuiformbtntxt="Subscribe"
                data-acceptuiformheadertxt="Payment Information" data-responsehandler="responseHandler">Pay (Accept UI)</button>

        <div class="container-fluid" style="width: 100%; margin: 0; padding:0">
            <div class="navbar navbar-inverse" role="navigation">
                <div class="container-fluid navbar-centered">
                    <ul class="nav navbar-nav" style="margin-top: 0px; margin-bottom:0px; margin-left:auto">
                        <li role="presentation"><a href="#pay" data-toggle="tab">PAYMENT PAGE</a></li>
                    </ul>
                </div>
            </div>
            <br/>
            <div id="acceptJSReceiptModal" class="modal fade" role="dialog">
                <div class="modal-dialog" style="display: inline-block; vertical-align: middle;">
                    <div class="modal-content">
                        <div class="modal-header" id="acceptJSReceiptHeader">
                            <h4 class="modal-title">PAYMENT RECEIPT</h4>
                        </div>
                        <div class="modal-body" id="acceptJSReceiptBody">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="tab-content panel-group">

            <div class="panel panel-info tab-pane center-block" id="paymentPanel" style="width:50%">
                <div class="panel-heading">
                    <h3 class="panel-title text-center"><b>Lexor payment page</b></h3>
                </div>
                <div class="panel-body">
                    <div class="row" id="request_payment">
                        <div class="form-group col-xs-6">
                            <input type="radio" id="authorize_gate" name="payment_gate" value="authorize" checked="checked">
                            <label for="authorize_gate">Authorize.Net</label><br>
                        </div>
                        <div class="form-group col-xs-6">
                            <input type="radio" id="paysimple_gate" name="payment_gate" value="paysimple">
                            <label for="paysimple_gate">Pay Simple</label>
                        </div>
                    </div>
                    <div class="row authorize" id="request_payment">
                        <div class="form-group col-xs-12">
                            <label for="amount">AMOUNT</label>
                            <input type="text" class="form-control" id="amount" placeholder="0.5">
                            <button type="button" id="btnRequestPayment" class="btn btn-primary" style="margin: 10px;">Request payment</button>
                        </div>
                    </div>
                    <iframe id="load_payment" class="authorize embed-responsive-item" name="load_payment" width="100%" height="650px" frameborder="0" scrolling="no" hidden="true">
                    </iframe>
                    <input type="hidden" id="customer_id" value="${it.customer_id}"/>
                    <input type="hidden" id="quickbook_customer_id" value="${it.quickbook_customer_id}"/>
                    <input type="hidden" id="order_id" value="${it.order_id}"/>
                    <input type="hidden" id="clientKey" value="${it.clientKey}"/>
                    <input type="hidden" id="apiLoginID" value="${it.apiLoginID}"/>
                    <form class="authorize" id="send_hptoken" action="${it.apiurl}/payment/payment" method="post" target="load_payment" >
                        <input type="hidden" id="hp_token" name="token" />
                    </form>
                    <div class="row paysimple">
                        <div class="form-group col-xs-12">
                            <form id="paysimple-form">
                                <div class="merchant-form hidden">
                                    <div class="form-field">
                                        <label for="firstName">First Name</label>
                                        <input type="text" id="firstName" />
                                    </div>
                                    <div class="form-field">
                                        <label for="lastName">Last Name</label>
                                        <input type="text" id="lastName" />
                                    </div>
                                    <div class="form-field">
                                        <label for="email">Email</label>
                                        <input type="email" id="email" />
                                    </div>
                                </div>

                                <div class="psjs">
                                    <div id="psjs">
                                        <!-- a PaySimpleJS Payment Form will be inserted here -->
                                    </div>
                                </div>

                                <div class="merchant-form">
                                    <button type="submit" id="submit">Complete Checkout</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </body>

    <script>
        function loadPaysimpleJs(responseObj) {
            auth = responseObj.hp_token;
            auth = {
                        token: responseObj.hp_token
                   };
            firstName = responseObj.firstName;
            lastName = responseObj.lastName;
            cellPhone = responseObj.cellPhone;
            busPhone = responseObj.busPhone;
            email = responseObj.email;
            document.getElementById('firstName').value = firstName;
            document.getElementById('lastName').value = lastName;
            document.getElementById('email').value = email;

            var paysimplejs = window.paysimpleJs({
                // the element that will contain the iframe
                container: document.querySelector('#psjs'),
                // checkout_token is in auth
                auth: auth,
                isLoggedIn: 0,
                autoRefresh: null,
                // allows entry of international postal codes if true
                bypassPostalCodeValidation: false,
                // Attempts to prevent browsers from using autocompletion to pre-populate 
                // or suggest input previously stored by the user. Turn on for point of 
                // sale or kiosk type applications where many different customers 
                // will be using the same browser to enter payment information.
                preventAutocomplete : false,
                // customized styles are optional
                styles: {
                  body: {
                    // set the background color of the payment page
                    backgroundColor: '#f9f9f9'
                  }
                }
            });
            paysimplejs.send.setMode('cc-key-enter');
//            paysimplejs.send.setMode('ach-key-enter');
            paysimplejs.on('httpError', function(error) {
                // where error = {
                // "errorKey": <"timeout" | "bad_request" | "server_error"
                // | "unauthorized" | "unknown">,
                // "errors": <array of { field: <string>, message: <string> }>,
                // "status": <number - http status code returned>
                // }
                // Add your error handling
                alert(error.errors[0].message);
            });
            paysimplejs.on('accountRetrieved', function(accountInfo) {
                /* Example accountInfo:
                              * {
                              *    "account": {
                              *        "id": 7313702
                              *    },
                              *    "customer": {
                              *        "id": 8041997,
                              *        "firstName": "John",
                              *        "lastName": "Snow",
                              *        "email": "john@snow.com"
                              *    },
                              *    "paymentToken": "e1f1bb19-9fe4-4c96-a35e-cd921298d8e6"
                              * }
                              */

                // Send the accountInfo to your server to collect a payment
                // for an existing customer
                var xhr = new XMLHttpRequest();
                // Replace with url of your endpoint
                xhr.open('POST', '/qbsa/api/paymentonline/completePayment');
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.onload = function (e) {
                    if (xhr.status < 300) {
                        var data = JSON.parse(this.response);
                        message = "Transaction Successful!<br>Transaction ID: " + data.TraceNumber;
                    } else {
                        message = "Transaction Unsuccessful." + xhr.responseText;
                    }
                    $('#acceptJSReceiptBody').html(message);
                    $('#acceptJSReceiptModal').modal('show');
                };
                accountInfo.amount = document.querySelector('#amount').value;
                xhr.send(JSON.stringify(accountInfo));
            });
        };

        $('#btnRequestPayment').click(function (e) {
            e.preventDefault();
            payment_gate = '';
            if (document.getElementById('authorize_gate').checked) {
                payment_gate = document.getElementById('authorize_gate').value;
            }
            if (document.getElementById('paysimple_gate').checked) {
                payment_gate = document.getElementById('paysimple_gate').value;
            }
            $.ajax({
                url: "/qbsa/api/paymentonline/requestPayment",
                data: {
                    amount: document.getElementById('amount').value,
                    paymentgate: payment_gate,
                    customer_id: document.getElementById('customer_id').value
                },
                method: 'POST',
                timeout: 50000

            }).done(function (data) {

                responseObj = JSON.parse(data);
                if (document.getElementById('authorize_gate').checked) {
                    document.getElementById('hp_token').value = responseObj.hp_token;
                    setTimeout(function () {
                        $("#send_hptoken").submit();
                    }, 100);
                }
                if (document.getElementById('paysimple_gate').checked) {
                    loadPaysimpleJs(responseObj);
                }

            });
        });
        $(document).ready(function () {
            $('ul.nav li a').click();
            $("#acceptJSReceiptModal").on('hide.bs.modal', function () {
                location.reload();
            });
        });
    </script>
</html>