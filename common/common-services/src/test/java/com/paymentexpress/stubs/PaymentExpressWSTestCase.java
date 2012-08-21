/**
 * PaymentExpressWSTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class PaymentExpressWSTestCase extends junit.framework.TestCase {
    public PaymentExpressWSTestCase(java.lang.String name) {
        super(name);
    }

    public void testPaymentExpressWSSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.paymentexpress.stubs.PaymentExpressWSLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1PaymentExpressWSSoapSubmitTransaction() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.submitTransaction(new java.lang.String(), new java.lang.String(), new com.paymentexpress.stubs.TransactionDetails());
        // TBD - validate results
    }

    public void test2PaymentExpressWSSoapSubmitTransaction2() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.submitTransaction2(new java.lang.String(), new java.lang.String(), new com.paymentexpress.stubs.TransactionDetails());
        // TBD - validate results
    }

    public void test3PaymentExpressWSSoapCheck3DsEnrollment() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.EnrolmentCheckResult value = null;
        value = binding.check3DsEnrollment(new java.lang.String(), new java.lang.String(), new com.paymentexpress.stubs.EnrolmentCheckRequest());
        // TBD - validate results
    }

    public void test4PaymentExpressWSSoapGetStatus() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.getStatus(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test5PaymentExpressWSSoapGetStatus2() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.getStatus2(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test6PaymentExpressWSSoapUpdateCard() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap_BindingStub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.updateCard(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void testPaymentExpressWSSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.paymentexpress.stubs.PaymentExpressWSLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test7PaymentExpressWSSoap12SubmitTransaction() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap12Stub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap12Stub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.submitTransaction(new java.lang.String(), new java.lang.String(), new com.paymentexpress.stubs.TransactionDetails());
        // TBD - validate results
    }

    public void test8PaymentExpressWSSoap12SubmitTransaction2() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap12Stub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap12Stub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.submitTransaction2(new java.lang.String(), new java.lang.String(), new com.paymentexpress.stubs.TransactionDetails());
        // TBD - validate results
    }

    public void test9PaymentExpressWSSoap12Check3DsEnrollment() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap12Stub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap12Stub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.EnrolmentCheckResult value = null;
        value = binding.check3DsEnrollment(new java.lang.String(), new java.lang.String(), new com.paymentexpress.stubs.EnrolmentCheckRequest());
        // TBD - validate results
    }

    public void test10PaymentExpressWSSoap12GetStatus() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap12Stub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap12Stub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.getStatus(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test11PaymentExpressWSSoap12GetStatus2() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap12Stub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap12Stub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.paymentexpress.stubs.TransactionResult2 value = null;
        value = binding.getStatus2(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test12PaymentExpressWSSoap12UpdateCard() throws Exception {
        com.paymentexpress.stubs.PaymentExpressWSSoap12Stub binding;
        try {
            binding = (com.paymentexpress.stubs.PaymentExpressWSSoap12Stub)
                          new com.paymentexpress.stubs.PaymentExpressWSLocator().getPaymentExpressWSSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.updateCard(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

}
