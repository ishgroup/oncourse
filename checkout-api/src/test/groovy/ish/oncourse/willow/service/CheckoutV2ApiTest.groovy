package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.payment.PaymentRequest;
import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for CheckoutV2Api
 */
public class CheckoutV2ApiTest {

    private CheckoutV2Api api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Validate model and create windcave session OR save model and complete windcave session (depends on X-Validate header),
     * Validate model, save purchase items into db and send payment to DPS
     */
    @Test
    public void makePaymentTest() {
        PaymentRequest paymentRequest = null;
        Boolean xValidate = null;
        String xCollegeKey = null;
	//PaymentResponse response = api.makePayment(paymentRequest, xValidate, xCollegeKey);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
