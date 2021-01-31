package com.lexor.qbsa;

import com.lexor.qbsa.controller.AuthController;
import com.lexor.qbsa.controller.CallbackController;
import com.lexor.qbsa.controller.ConfigurationController;
import com.lexor.qbsa.controller.CustomerController;
import com.lexor.qbsa.controller.HelloRestController;
import com.lexor.qbsa.controller.HelloController;
import com.lexor.qbsa.controller.InvoiceController;
import com.lexor.qbsa.controller.ItemController;
import com.lexor.qbsa.controller.PaymentOnlineController;
import com.lexor.qbsa.controller.PaymentController;
import com.lexor.qbsa.controller.WebhooksController;
import com.lexor.qbsa.util.ApplicationBinder;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class MyResourceConfig extends ResourceConfig {
    public MyResourceConfig() {
        packages("com.lexor.qbsa.resource");
        register(new DataLoader());
        register(JspMvcFeature.class);
        register(new ApplicationBinder());
        register(HelloController.class);
        register(HelloRestController.class);

        register(AuthController.class);
        register(CallbackController.class);
        register(WebhooksController.class);
        register(CustomerController.class);
        register(ItemController.class);
        register(InvoiceController.class);
        register(PaymentController.class);
        register(PaymentOnlineController.class);
        register(ConfigurationController.class);
    }
}