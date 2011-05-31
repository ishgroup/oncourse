/**
 * TransactionResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class TransactionResult  implements java.io.Serializable {
    private java.lang.String amount;

    private java.lang.String authCode;

    private java.lang.String authorized;

    private java.lang.String billingId;

    private java.lang.String cardHolderHelpText;

    private java.lang.String cardHolderName;

    private java.lang.String cardHolderResponseDescription;

    private java.lang.String cardHolderResponseText;

    private java.lang.String cardName;

    private java.lang.String cardNumber;

    private java.lang.String currencyId;

    private java.lang.String currencyName;

    private java.lang.String currencyRate;

    private java.lang.String cvc2;

    private java.lang.String dateExpiry;

    private java.lang.String dateSettlement;

    private java.lang.String dpsBillingId;

    private java.lang.String dpsTxnRef;

    private java.lang.String helpText;

    private java.lang.String merchantHelpText;

    private java.lang.String merchantReference;

    private java.lang.String merchantResponseDescription;

    private java.lang.String merchantResponseText;

    private java.lang.String reco;

    private java.lang.String responseText;

    private java.lang.String retry;

    private java.lang.String statusRequired;

    private java.lang.String testMode;

    private java.lang.String txnRef;

    private java.lang.String txnType;

    public TransactionResult() {
    }

    public TransactionResult(
           java.lang.String amount,
           java.lang.String authCode,
           java.lang.String authorized,
           java.lang.String billingId,
           java.lang.String cardHolderHelpText,
           java.lang.String cardHolderName,
           java.lang.String cardHolderResponseDescription,
           java.lang.String cardHolderResponseText,
           java.lang.String cardName,
           java.lang.String cardNumber,
           java.lang.String currencyId,
           java.lang.String currencyName,
           java.lang.String currencyRate,
           java.lang.String cvc2,
           java.lang.String dateExpiry,
           java.lang.String dateSettlement,
           java.lang.String dpsBillingId,
           java.lang.String dpsTxnRef,
           java.lang.String helpText,
           java.lang.String merchantHelpText,
           java.lang.String merchantReference,
           java.lang.String merchantResponseDescription,
           java.lang.String merchantResponseText,
           java.lang.String reco,
           java.lang.String responseText,
           java.lang.String retry,
           java.lang.String statusRequired,
           java.lang.String testMode,
           java.lang.String txnRef,
           java.lang.String txnType) {
           this.amount = amount;
           this.authCode = authCode;
           this.authorized = authorized;
           this.billingId = billingId;
           this.cardHolderHelpText = cardHolderHelpText;
           this.cardHolderName = cardHolderName;
           this.cardHolderResponseDescription = cardHolderResponseDescription;
           this.cardHolderResponseText = cardHolderResponseText;
           this.cardName = cardName;
           this.cardNumber = cardNumber;
           this.currencyId = currencyId;
           this.currencyName = currencyName;
           this.currencyRate = currencyRate;
           this.cvc2 = cvc2;
           this.dateExpiry = dateExpiry;
           this.dateSettlement = dateSettlement;
           this.dpsBillingId = dpsBillingId;
           this.dpsTxnRef = dpsTxnRef;
           this.helpText = helpText;
           this.merchantHelpText = merchantHelpText;
           this.merchantReference = merchantReference;
           this.merchantResponseDescription = merchantResponseDescription;
           this.merchantResponseText = merchantResponseText;
           this.reco = reco;
           this.responseText = responseText;
           this.retry = retry;
           this.statusRequired = statusRequired;
           this.testMode = testMode;
           this.txnRef = txnRef;
           this.txnType = txnType;
    }


    /**
     * Gets the amount value for this TransactionResult.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this TransactionResult.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the authCode value for this TransactionResult.
     * 
     * @return authCode
     */
    public java.lang.String getAuthCode() {
        return authCode;
    }


    /**
     * Sets the authCode value for this TransactionResult.
     * 
     * @param authCode
     */
    public void setAuthCode(java.lang.String authCode) {
        this.authCode = authCode;
    }


    /**
     * Gets the authorized value for this TransactionResult.
     * 
     * @return authorized
     */
    public java.lang.String getAuthorized() {
        return authorized;
    }


    /**
     * Sets the authorized value for this TransactionResult.
     * 
     * @param authorized
     */
    public void setAuthorized(java.lang.String authorized) {
        this.authorized = authorized;
    }


    /**
     * Gets the billingId value for this TransactionResult.
     * 
     * @return billingId
     */
    public java.lang.String getBillingId() {
        return billingId;
    }


    /**
     * Sets the billingId value for this TransactionResult.
     * 
     * @param billingId
     */
    public void setBillingId(java.lang.String billingId) {
        this.billingId = billingId;
    }


    /**
     * Gets the cardHolderHelpText value for this TransactionResult.
     * 
     * @return cardHolderHelpText
     */
    public java.lang.String getCardHolderHelpText() {
        return cardHolderHelpText;
    }


    /**
     * Sets the cardHolderHelpText value for this TransactionResult.
     * 
     * @param cardHolderHelpText
     */
    public void setCardHolderHelpText(java.lang.String cardHolderHelpText) {
        this.cardHolderHelpText = cardHolderHelpText;
    }


    /**
     * Gets the cardHolderName value for this TransactionResult.
     * 
     * @return cardHolderName
     */
    public java.lang.String getCardHolderName() {
        return cardHolderName;
    }


    /**
     * Sets the cardHolderName value for this TransactionResult.
     * 
     * @param cardHolderName
     */
    public void setCardHolderName(java.lang.String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the cardHolderResponseDescription value for this TransactionResult.
     * 
     * @return cardHolderResponseDescription
     */
    public java.lang.String getCardHolderResponseDescription() {
        return cardHolderResponseDescription;
    }


    /**
     * Sets the cardHolderResponseDescription value for this TransactionResult.
     * 
     * @param cardHolderResponseDescription
     */
    public void setCardHolderResponseDescription(java.lang.String cardHolderResponseDescription) {
        this.cardHolderResponseDescription = cardHolderResponseDescription;
    }


    /**
     * Gets the cardHolderResponseText value for this TransactionResult.
     * 
     * @return cardHolderResponseText
     */
    public java.lang.String getCardHolderResponseText() {
        return cardHolderResponseText;
    }


    /**
     * Sets the cardHolderResponseText value for this TransactionResult.
     * 
     * @param cardHolderResponseText
     */
    public void setCardHolderResponseText(java.lang.String cardHolderResponseText) {
        this.cardHolderResponseText = cardHolderResponseText;
    }


    /**
     * Gets the cardName value for this TransactionResult.
     * 
     * @return cardName
     */
    public java.lang.String getCardName() {
        return cardName;
    }


    /**
     * Sets the cardName value for this TransactionResult.
     * 
     * @param cardName
     */
    public void setCardName(java.lang.String cardName) {
        this.cardName = cardName;
    }


    /**
     * Gets the cardNumber value for this TransactionResult.
     * 
     * @return cardNumber
     */
    public java.lang.String getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this TransactionResult.
     * 
     * @param cardNumber
     */
    public void setCardNumber(java.lang.String cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the currencyId value for this TransactionResult.
     * 
     * @return currencyId
     */
    public java.lang.String getCurrencyId() {
        return currencyId;
    }


    /**
     * Sets the currencyId value for this TransactionResult.
     * 
     * @param currencyId
     */
    public void setCurrencyId(java.lang.String currencyId) {
        this.currencyId = currencyId;
    }


    /**
     * Gets the currencyName value for this TransactionResult.
     * 
     * @return currencyName
     */
    public java.lang.String getCurrencyName() {
        return currencyName;
    }


    /**
     * Sets the currencyName value for this TransactionResult.
     * 
     * @param currencyName
     */
    public void setCurrencyName(java.lang.String currencyName) {
        this.currencyName = currencyName;
    }


    /**
     * Gets the currencyRate value for this TransactionResult.
     * 
     * @return currencyRate
     */
    public java.lang.String getCurrencyRate() {
        return currencyRate;
    }


    /**
     * Sets the currencyRate value for this TransactionResult.
     * 
     * @param currencyRate
     */
    public void setCurrencyRate(java.lang.String currencyRate) {
        this.currencyRate = currencyRate;
    }


    /**
     * Gets the cvc2 value for this TransactionResult.
     * 
     * @return cvc2
     */
    public java.lang.String getCvc2() {
        return cvc2;
    }


    /**
     * Sets the cvc2 value for this TransactionResult.
     * 
     * @param cvc2
     */
    public void setCvc2(java.lang.String cvc2) {
        this.cvc2 = cvc2;
    }


    /**
     * Gets the dateExpiry value for this TransactionResult.
     * 
     * @return dateExpiry
     */
    public java.lang.String getDateExpiry() {
        return dateExpiry;
    }


    /**
     * Sets the dateExpiry value for this TransactionResult.
     * 
     * @param dateExpiry
     */
    public void setDateExpiry(java.lang.String dateExpiry) {
        this.dateExpiry = dateExpiry;
    }


    /**
     * Gets the dateSettlement value for this TransactionResult.
     * 
     * @return dateSettlement
     */
    public java.lang.String getDateSettlement() {
        return dateSettlement;
    }


    /**
     * Sets the dateSettlement value for this TransactionResult.
     * 
     * @param dateSettlement
     */
    public void setDateSettlement(java.lang.String dateSettlement) {
        this.dateSettlement = dateSettlement;
    }


    /**
     * Gets the dpsBillingId value for this TransactionResult.
     * 
     * @return dpsBillingId
     */
    public java.lang.String getDpsBillingId() {
        return dpsBillingId;
    }


    /**
     * Sets the dpsBillingId value for this TransactionResult.
     * 
     * @param dpsBillingId
     */
    public void setDpsBillingId(java.lang.String dpsBillingId) {
        this.dpsBillingId = dpsBillingId;
    }


    /**
     * Gets the dpsTxnRef value for this TransactionResult.
     * 
     * @return dpsTxnRef
     */
    public java.lang.String getDpsTxnRef() {
        return dpsTxnRef;
    }


    /**
     * Sets the dpsTxnRef value for this TransactionResult.
     * 
     * @param dpsTxnRef
     */
    public void setDpsTxnRef(java.lang.String dpsTxnRef) {
        this.dpsTxnRef = dpsTxnRef;
    }


    /**
     * Gets the helpText value for this TransactionResult.
     * 
     * @return helpText
     */
    public java.lang.String getHelpText() {
        return helpText;
    }


    /**
     * Sets the helpText value for this TransactionResult.
     * 
     * @param helpText
     */
    public void setHelpText(java.lang.String helpText) {
        this.helpText = helpText;
    }


    /**
     * Gets the merchantHelpText value for this TransactionResult.
     * 
     * @return merchantHelpText
     */
    public java.lang.String getMerchantHelpText() {
        return merchantHelpText;
    }


    /**
     * Sets the merchantHelpText value for this TransactionResult.
     * 
     * @param merchantHelpText
     */
    public void setMerchantHelpText(java.lang.String merchantHelpText) {
        this.merchantHelpText = merchantHelpText;
    }


    /**
     * Gets the merchantReference value for this TransactionResult.
     * 
     * @return merchantReference
     */
    public java.lang.String getMerchantReference() {
        return merchantReference;
    }


    /**
     * Sets the merchantReference value for this TransactionResult.
     * 
     * @param merchantReference
     */
    public void setMerchantReference(java.lang.String merchantReference) {
        this.merchantReference = merchantReference;
    }


    /**
     * Gets the merchantResponseDescription value for this TransactionResult.
     * 
     * @return merchantResponseDescription
     */
    public java.lang.String getMerchantResponseDescription() {
        return merchantResponseDescription;
    }


    /**
     * Sets the merchantResponseDescription value for this TransactionResult.
     * 
     * @param merchantResponseDescription
     */
    public void setMerchantResponseDescription(java.lang.String merchantResponseDescription) {
        this.merchantResponseDescription = merchantResponseDescription;
    }


    /**
     * Gets the merchantResponseText value for this TransactionResult.
     * 
     * @return merchantResponseText
     */
    public java.lang.String getMerchantResponseText() {
        return merchantResponseText;
    }


    /**
     * Sets the merchantResponseText value for this TransactionResult.
     * 
     * @param merchantResponseText
     */
    public void setMerchantResponseText(java.lang.String merchantResponseText) {
        this.merchantResponseText = merchantResponseText;
    }


    /**
     * Gets the reco value for this TransactionResult.
     * 
     * @return reco
     */
    public java.lang.String getReco() {
        return reco;
    }


    /**
     * Sets the reco value for this TransactionResult.
     * 
     * @param reco
     */
    public void setReco(java.lang.String reco) {
        this.reco = reco;
    }


    /**
     * Gets the responseText value for this TransactionResult.
     * 
     * @return responseText
     */
    public java.lang.String getResponseText() {
        return responseText;
    }


    /**
     * Sets the responseText value for this TransactionResult.
     * 
     * @param responseText
     */
    public void setResponseText(java.lang.String responseText) {
        this.responseText = responseText;
    }


    /**
     * Gets the retry value for this TransactionResult.
     * 
     * @return retry
     */
    public java.lang.String getRetry() {
        return retry;
    }


    /**
     * Sets the retry value for this TransactionResult.
     * 
     * @param retry
     */
    public void setRetry(java.lang.String retry) {
        this.retry = retry;
    }


    /**
     * Gets the statusRequired value for this TransactionResult.
     * 
     * @return statusRequired
     */
    public java.lang.String getStatusRequired() {
        return statusRequired;
    }


    /**
     * Sets the statusRequired value for this TransactionResult.
     * 
     * @param statusRequired
     */
    public void setStatusRequired(java.lang.String statusRequired) {
        this.statusRequired = statusRequired;
    }


    /**
     * Gets the testMode value for this TransactionResult.
     * 
     * @return testMode
     */
    public java.lang.String getTestMode() {
        return testMode;
    }


    /**
     * Sets the testMode value for this TransactionResult.
     * 
     * @param testMode
     */
    public void setTestMode(java.lang.String testMode) {
        this.testMode = testMode;
    }


    /**
     * Gets the txnRef value for this TransactionResult.
     * 
     * @return txnRef
     */
    public java.lang.String getTxnRef() {
        return txnRef;
    }


    /**
     * Sets the txnRef value for this TransactionResult.
     * 
     * @param txnRef
     */
    public void setTxnRef(java.lang.String txnRef) {
        this.txnRef = txnRef;
    }


    /**
     * Gets the txnType value for this TransactionResult.
     * 
     * @return txnType
     */
    public java.lang.String getTxnType() {
        return txnType;
    }


    /**
     * Sets the txnType value for this TransactionResult.
     * 
     * @param txnType
     */
    public void setTxnType(java.lang.String txnType) {
        this.txnType = txnType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransactionResult)) return false;
        TransactionResult other = (TransactionResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.authCode==null && other.getAuthCode()==null) || 
             (this.authCode!=null &&
              this.authCode.equals(other.getAuthCode()))) &&
            ((this.authorized==null && other.getAuthorized()==null) || 
             (this.authorized!=null &&
              this.authorized.equals(other.getAuthorized()))) &&
            ((this.billingId==null && other.getBillingId()==null) || 
             (this.billingId!=null &&
              this.billingId.equals(other.getBillingId()))) &&
            ((this.cardHolderHelpText==null && other.getCardHolderHelpText()==null) || 
             (this.cardHolderHelpText!=null &&
              this.cardHolderHelpText.equals(other.getCardHolderHelpText()))) &&
            ((this.cardHolderName==null && other.getCardHolderName()==null) || 
             (this.cardHolderName!=null &&
              this.cardHolderName.equals(other.getCardHolderName()))) &&
            ((this.cardHolderResponseDescription==null && other.getCardHolderResponseDescription()==null) || 
             (this.cardHolderResponseDescription!=null &&
              this.cardHolderResponseDescription.equals(other.getCardHolderResponseDescription()))) &&
            ((this.cardHolderResponseText==null && other.getCardHolderResponseText()==null) || 
             (this.cardHolderResponseText!=null &&
              this.cardHolderResponseText.equals(other.getCardHolderResponseText()))) &&
            ((this.cardName==null && other.getCardName()==null) || 
             (this.cardName!=null &&
              this.cardName.equals(other.getCardName()))) &&
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            ((this.currencyId==null && other.getCurrencyId()==null) || 
             (this.currencyId!=null &&
              this.currencyId.equals(other.getCurrencyId()))) &&
            ((this.currencyName==null && other.getCurrencyName()==null) || 
             (this.currencyName!=null &&
              this.currencyName.equals(other.getCurrencyName()))) &&
            ((this.currencyRate==null && other.getCurrencyRate()==null) || 
             (this.currencyRate!=null &&
              this.currencyRate.equals(other.getCurrencyRate()))) &&
            ((this.cvc2==null && other.getCvc2()==null) || 
             (this.cvc2!=null &&
              this.cvc2.equals(other.getCvc2()))) &&
            ((this.dateExpiry==null && other.getDateExpiry()==null) || 
             (this.dateExpiry!=null &&
              this.dateExpiry.equals(other.getDateExpiry()))) &&
            ((this.dateSettlement==null && other.getDateSettlement()==null) || 
             (this.dateSettlement!=null &&
              this.dateSettlement.equals(other.getDateSettlement()))) &&
            ((this.dpsBillingId==null && other.getDpsBillingId()==null) || 
             (this.dpsBillingId!=null &&
              this.dpsBillingId.equals(other.getDpsBillingId()))) &&
            ((this.dpsTxnRef==null && other.getDpsTxnRef()==null) || 
             (this.dpsTxnRef!=null &&
              this.dpsTxnRef.equals(other.getDpsTxnRef()))) &&
            ((this.helpText==null && other.getHelpText()==null) || 
             (this.helpText!=null &&
              this.helpText.equals(other.getHelpText()))) &&
            ((this.merchantHelpText==null && other.getMerchantHelpText()==null) || 
             (this.merchantHelpText!=null &&
              this.merchantHelpText.equals(other.getMerchantHelpText()))) &&
            ((this.merchantReference==null && other.getMerchantReference()==null) || 
             (this.merchantReference!=null &&
              this.merchantReference.equals(other.getMerchantReference()))) &&
            ((this.merchantResponseDescription==null && other.getMerchantResponseDescription()==null) || 
             (this.merchantResponseDescription!=null &&
              this.merchantResponseDescription.equals(other.getMerchantResponseDescription()))) &&
            ((this.merchantResponseText==null && other.getMerchantResponseText()==null) || 
             (this.merchantResponseText!=null &&
              this.merchantResponseText.equals(other.getMerchantResponseText()))) &&
            ((this.reco==null && other.getReco()==null) || 
             (this.reco!=null &&
              this.reco.equals(other.getReco()))) &&
            ((this.responseText==null && other.getResponseText()==null) || 
             (this.responseText!=null &&
              this.responseText.equals(other.getResponseText()))) &&
            ((this.retry==null && other.getRetry()==null) || 
             (this.retry!=null &&
              this.retry.equals(other.getRetry()))) &&
            ((this.statusRequired==null && other.getStatusRequired()==null) || 
             (this.statusRequired!=null &&
              this.statusRequired.equals(other.getStatusRequired()))) &&
            ((this.testMode==null && other.getTestMode()==null) || 
             (this.testMode!=null &&
              this.testMode.equals(other.getTestMode()))) &&
            ((this.txnRef==null && other.getTxnRef()==null) || 
             (this.txnRef!=null &&
              this.txnRef.equals(other.getTxnRef()))) &&
            ((this.txnType==null && other.getTxnType()==null) || 
             (this.txnType!=null &&
              this.txnType.equals(other.getTxnType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getAuthCode() != null) {
            _hashCode += getAuthCode().hashCode();
        }
        if (getAuthorized() != null) {
            _hashCode += getAuthorized().hashCode();
        }
        if (getBillingId() != null) {
            _hashCode += getBillingId().hashCode();
        }
        if (getCardHolderHelpText() != null) {
            _hashCode += getCardHolderHelpText().hashCode();
        }
        if (getCardHolderName() != null) {
            _hashCode += getCardHolderName().hashCode();
        }
        if (getCardHolderResponseDescription() != null) {
            _hashCode += getCardHolderResponseDescription().hashCode();
        }
        if (getCardHolderResponseText() != null) {
            _hashCode += getCardHolderResponseText().hashCode();
        }
        if (getCardName() != null) {
            _hashCode += getCardName().hashCode();
        }
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        if (getCurrencyId() != null) {
            _hashCode += getCurrencyId().hashCode();
        }
        if (getCurrencyName() != null) {
            _hashCode += getCurrencyName().hashCode();
        }
        if (getCurrencyRate() != null) {
            _hashCode += getCurrencyRate().hashCode();
        }
        if (getCvc2() != null) {
            _hashCode += getCvc2().hashCode();
        }
        if (getDateExpiry() != null) {
            _hashCode += getDateExpiry().hashCode();
        }
        if (getDateSettlement() != null) {
            _hashCode += getDateSettlement().hashCode();
        }
        if (getDpsBillingId() != null) {
            _hashCode += getDpsBillingId().hashCode();
        }
        if (getDpsTxnRef() != null) {
            _hashCode += getDpsTxnRef().hashCode();
        }
        if (getHelpText() != null) {
            _hashCode += getHelpText().hashCode();
        }
        if (getMerchantHelpText() != null) {
            _hashCode += getMerchantHelpText().hashCode();
        }
        if (getMerchantReference() != null) {
            _hashCode += getMerchantReference().hashCode();
        }
        if (getMerchantResponseDescription() != null) {
            _hashCode += getMerchantResponseDescription().hashCode();
        }
        if (getMerchantResponseText() != null) {
            _hashCode += getMerchantResponseText().hashCode();
        }
        if (getReco() != null) {
            _hashCode += getReco().hashCode();
        }
        if (getResponseText() != null) {
            _hashCode += getResponseText().hashCode();
        }
        if (getRetry() != null) {
            _hashCode += getRetry().hashCode();
        }
        if (getStatusRequired() != null) {
            _hashCode += getStatusRequired().hashCode();
        }
        if (getTestMode() != null) {
            _hashCode += getTestMode().hashCode();
        }
        if (getTxnRef() != null) {
            _hashCode += getTxnRef().hashCode();
        }
        if (getTxnType() != null) {
            _hashCode += getTxnType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "TransactionResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "authCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorized");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "authorized"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "billingId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderHelpText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardHolderHelpText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardHolderName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderResponseDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardHolderResponseDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardHolderResponseText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardHolderResponseText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "currencyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "currencyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyRate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "currencyRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvc2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cvc2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateExpiry");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "dateExpiry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateSettlement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "dateSettlement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dpsBillingId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "dpsBillingId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dpsTxnRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "dpsTxnRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("helpText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "helpText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantHelpText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "merchantHelpText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantReference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "merchantReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantResponseDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "merchantResponseDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantResponseText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "merchantResponseText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reco");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "reco"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "responseText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retry");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "retry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusRequired");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "statusRequired"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testMode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "testMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "txnRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "txnType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
