/**
 * PaymentExpressWSSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public interface PaymentExpressWSSoap extends java.rmi.Remote {

    /**
     * Send transaction data to Payment Express Server.
     */
    public com.paymentexpress.stubs.TransactionResult submitTransaction(java.lang.String postUsername, java.lang.String postPassword, com.paymentexpress.stubs.TransactionDetails transactionDetails) throws java.rmi.RemoteException;

    /**
     * Send transaction data. Includes Acquirer Response in result.
     */
    public com.paymentexpress.stubs.TransactionResult2 submitTransaction2(java.lang.String postUsername, java.lang.String postPassword, com.paymentexpress.stubs.TransactionDetails transactionDetails) throws java.rmi.RemoteException;

    /**
     * Check whether a card is 3DS enrolled.
     */
    public com.paymentexpress.stubs.EnrolmentCheckResult check3DsEnrollment(java.lang.String postUsername, java.lang.String postPassword, com.paymentexpress.stubs.EnrolmentCheckRequest transactionDetails) throws java.rmi.RemoteException;

    /**
     * Query status of a transaction.
     */
    public com.paymentexpress.stubs.TransactionResult getStatus(java.lang.String postUsername, java.lang.String postPassword, java.lang.String txnRef) throws java.rmi.RemoteException;

    /**
     * Query status of a transaction. Includes Acquirer Response in
     * result.
     */
    public com.paymentexpress.stubs.TransactionResult2 getStatus2(java.lang.String postUsername, java.lang.String postPassword, java.lang.String txnRef) throws java.rmi.RemoteException;

    /**
     * Update stored credit card details.
     */
    public java.lang.String updateCard(java.lang.String postUsername, java.lang.String postPassword, java.lang.String cardDetails) throws java.rmi.RemoteException;
}
