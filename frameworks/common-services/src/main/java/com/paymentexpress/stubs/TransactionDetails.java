/**
 * TransactionDetails.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class TransactionDetails  implements java.io.Serializable {
    private java.lang.String amount;

    private java.lang.String billingId;

    private java.lang.String cardHolderName;

    private java.lang.String cardNumber;

    private java.lang.String clientInfo;

    private java.lang.String cvc2;

    private java.lang.String dateExpiry;

    private java.lang.String dpsBillingId;

    private java.lang.String dpsTxnRef;

    private java.lang.String enableAddBillCard;

    private java.lang.String enablePaxInfo;

    private java.lang.String inputCurrency;

    private java.lang.String merchantReference;

    private java.lang.String paxCarrier;

    private java.lang.String paxCarrier2;

    private java.lang.String paxCarrier3;

    private java.lang.String paxCarrier4;

    private java.lang.String paxDateDepart;

    private java.lang.String paxDate2;

    private java.lang.String paxDate3;

    private java.lang.String paxDate4;

    private java.lang.String paxTime1;

    private java.lang.String paxTime2;

    private java.lang.String paxTime3;

    private java.lang.String paxTime4;

    private java.lang.String paxLeg1;

    private java.lang.String paxLeg2;

    private java.lang.String paxLeg3;

    private java.lang.String paxLeg4;

    private java.lang.String paxClass1;

    private java.lang.String paxClass2;

    private java.lang.String paxClass3;

    private java.lang.String paxClass4;

    private java.lang.String paxStopOverCode1;

    private java.lang.String paxStopOverCode2;

    private java.lang.String paxStopOverCode3;

    private java.lang.String paxStopOverCode4;

    private java.lang.String paxFareBasis1;

    private java.lang.String paxFareBasis2;

    private java.lang.String paxFareBasis3;

    private java.lang.String paxFareBasis4;

    private java.lang.String paxFlightNumber1;

    private java.lang.String paxFlightNumber2;

    private java.lang.String paxFlightNumber3;

    private java.lang.String paxFlightNumber4;

    private java.lang.String paxName;

    private java.lang.String paxOrigin;

    private java.lang.String paxTicketNumber;

    private java.lang.String paxTravelAgentInfo;

    private java.lang.String txnData1;

    private java.lang.String txnData2;

    private java.lang.String txnData3;

    private java.lang.String txnRef;

    private java.lang.String txnType;

    private java.lang.String dateStart;

    private java.lang.String issueNumber;

    private java.lang.String enableAvsData;

    private java.lang.String avsAction;

    private java.lang.String avsPostCode;

    private java.lang.String avsStreetAddress;

    private java.lang.String enable3DSecure;

    private java.lang.String paRes;

    private java.lang.String clientType;

    public TransactionDetails() {
    }

    public TransactionDetails(
           java.lang.String amount,
           java.lang.String billingId,
           java.lang.String cardHolderName,
           java.lang.String cardNumber,
           java.lang.String clientInfo,
           java.lang.String cvc2,
           java.lang.String dateExpiry,
           java.lang.String dpsBillingId,
           java.lang.String dpsTxnRef,
           java.lang.String enableAddBillCard,
           java.lang.String enablePaxInfo,
           java.lang.String inputCurrency,
           java.lang.String merchantReference,
           java.lang.String paxCarrier,
           java.lang.String paxCarrier2,
           java.lang.String paxCarrier3,
           java.lang.String paxCarrier4,
           java.lang.String paxDateDepart,
           java.lang.String paxDate2,
           java.lang.String paxDate3,
           java.lang.String paxDate4,
           java.lang.String paxTime1,
           java.lang.String paxTime2,
           java.lang.String paxTime3,
           java.lang.String paxTime4,
           java.lang.String paxLeg1,
           java.lang.String paxLeg2,
           java.lang.String paxLeg3,
           java.lang.String paxLeg4,
           java.lang.String paxClass1,
           java.lang.String paxClass2,
           java.lang.String paxClass3,
           java.lang.String paxClass4,
           java.lang.String paxStopOverCode1,
           java.lang.String paxStopOverCode2,
           java.lang.String paxStopOverCode3,
           java.lang.String paxStopOverCode4,
           java.lang.String paxFareBasis1,
           java.lang.String paxFareBasis2,
           java.lang.String paxFareBasis3,
           java.lang.String paxFareBasis4,
           java.lang.String paxFlightNumber1,
           java.lang.String paxFlightNumber2,
           java.lang.String paxFlightNumber3,
           java.lang.String paxFlightNumber4,
           java.lang.String paxName,
           java.lang.String paxOrigin,
           java.lang.String paxTicketNumber,
           java.lang.String paxTravelAgentInfo,
           java.lang.String txnData1,
           java.lang.String txnData2,
           java.lang.String txnData3,
           java.lang.String txnRef,
           java.lang.String txnType,
           java.lang.String dateStart,
           java.lang.String issueNumber,
           java.lang.String enableAvsData,
           java.lang.String avsAction,
           java.lang.String avsPostCode,
           java.lang.String avsStreetAddress,
           java.lang.String enable3DSecure,
           java.lang.String paRes,
           java.lang.String clientType) {
           this.amount = amount;
           this.billingId = billingId;
           this.cardHolderName = cardHolderName;
           this.cardNumber = cardNumber;
           this.clientInfo = clientInfo;
           this.cvc2 = cvc2;
           this.dateExpiry = dateExpiry;
           this.dpsBillingId = dpsBillingId;
           this.dpsTxnRef = dpsTxnRef;
           this.enableAddBillCard = enableAddBillCard;
           this.enablePaxInfo = enablePaxInfo;
           this.inputCurrency = inputCurrency;
           this.merchantReference = merchantReference;
           this.paxCarrier = paxCarrier;
           this.paxCarrier2 = paxCarrier2;
           this.paxCarrier3 = paxCarrier3;
           this.paxCarrier4 = paxCarrier4;
           this.paxDateDepart = paxDateDepart;
           this.paxDate2 = paxDate2;
           this.paxDate3 = paxDate3;
           this.paxDate4 = paxDate4;
           this.paxTime1 = paxTime1;
           this.paxTime2 = paxTime2;
           this.paxTime3 = paxTime3;
           this.paxTime4 = paxTime4;
           this.paxLeg1 = paxLeg1;
           this.paxLeg2 = paxLeg2;
           this.paxLeg3 = paxLeg3;
           this.paxLeg4 = paxLeg4;
           this.paxClass1 = paxClass1;
           this.paxClass2 = paxClass2;
           this.paxClass3 = paxClass3;
           this.paxClass4 = paxClass4;
           this.paxStopOverCode1 = paxStopOverCode1;
           this.paxStopOverCode2 = paxStopOverCode2;
           this.paxStopOverCode3 = paxStopOverCode3;
           this.paxStopOverCode4 = paxStopOverCode4;
           this.paxFareBasis1 = paxFareBasis1;
           this.paxFareBasis2 = paxFareBasis2;
           this.paxFareBasis3 = paxFareBasis3;
           this.paxFareBasis4 = paxFareBasis4;
           this.paxFlightNumber1 = paxFlightNumber1;
           this.paxFlightNumber2 = paxFlightNumber2;
           this.paxFlightNumber3 = paxFlightNumber3;
           this.paxFlightNumber4 = paxFlightNumber4;
           this.paxName = paxName;
           this.paxOrigin = paxOrigin;
           this.paxTicketNumber = paxTicketNumber;
           this.paxTravelAgentInfo = paxTravelAgentInfo;
           this.txnData1 = txnData1;
           this.txnData2 = txnData2;
           this.txnData3 = txnData3;
           this.txnRef = txnRef;
           this.txnType = txnType;
           this.dateStart = dateStart;
           this.issueNumber = issueNumber;
           this.enableAvsData = enableAvsData;
           this.avsAction = avsAction;
           this.avsPostCode = avsPostCode;
           this.avsStreetAddress = avsStreetAddress;
           this.enable3DSecure = enable3DSecure;
           this.paRes = paRes;
           this.clientType = clientType;
    }


    /**
     * Gets the amount value for this TransactionDetails.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this TransactionDetails.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the billingId value for this TransactionDetails.
     * 
     * @return billingId
     */
    public java.lang.String getBillingId() {
        return billingId;
    }


    /**
     * Sets the billingId value for this TransactionDetails.
     * 
     * @param billingId
     */
    public void setBillingId(java.lang.String billingId) {
        this.billingId = billingId;
    }


    /**
     * Gets the cardHolderName value for this TransactionDetails.
     * 
     * @return cardHolderName
     */
    public java.lang.String getCardHolderName() {
        return cardHolderName;
    }


    /**
     * Sets the cardHolderName value for this TransactionDetails.
     * 
     * @param cardHolderName
     */
    public void setCardHolderName(java.lang.String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the cardNumber value for this TransactionDetails.
     * 
     * @return cardNumber
     */
    public java.lang.String getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this TransactionDetails.
     * 
     * @param cardNumber
     */
    public void setCardNumber(java.lang.String cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the clientInfo value for this TransactionDetails.
     * 
     * @return clientInfo
     */
    public java.lang.String getClientInfo() {
        return clientInfo;
    }


    /**
     * Sets the clientInfo value for this TransactionDetails.
     * 
     * @param clientInfo
     */
    public void setClientInfo(java.lang.String clientInfo) {
        this.clientInfo = clientInfo;
    }


    /**
     * Gets the cvc2 value for this TransactionDetails.
     * 
     * @return cvc2
     */
    public java.lang.String getCvc2() {
        return cvc2;
    }


    /**
     * Sets the cvc2 value for this TransactionDetails.
     * 
     * @param cvc2
     */
    public void setCvc2(java.lang.String cvc2) {
        this.cvc2 = cvc2;
    }


    /**
     * Gets the dateExpiry value for this TransactionDetails.
     * 
     * @return dateExpiry
     */
    public java.lang.String getDateExpiry() {
        return dateExpiry;
    }


    /**
     * Sets the dateExpiry value for this TransactionDetails.
     * 
     * @param dateExpiry
     */
    public void setDateExpiry(java.lang.String dateExpiry) {
        this.dateExpiry = dateExpiry;
    }


    /**
     * Gets the dpsBillingId value for this TransactionDetails.
     * 
     * @return dpsBillingId
     */
    public java.lang.String getDpsBillingId() {
        return dpsBillingId;
    }


    /**
     * Sets the dpsBillingId value for this TransactionDetails.
     * 
     * @param dpsBillingId
     */
    public void setDpsBillingId(java.lang.String dpsBillingId) {
        this.dpsBillingId = dpsBillingId;
    }


    /**
     * Gets the dpsTxnRef value for this TransactionDetails.
     * 
     * @return dpsTxnRef
     */
    public java.lang.String getDpsTxnRef() {
        return dpsTxnRef;
    }


    /**
     * Sets the dpsTxnRef value for this TransactionDetails.
     * 
     * @param dpsTxnRef
     */
    public void setDpsTxnRef(java.lang.String dpsTxnRef) {
        this.dpsTxnRef = dpsTxnRef;
    }


    /**
     * Gets the enableAddBillCard value for this TransactionDetails.
     * 
     * @return enableAddBillCard
     */
    public java.lang.String getEnableAddBillCard() {
        return enableAddBillCard;
    }


    /**
     * Sets the enableAddBillCard value for this TransactionDetails.
     * 
     * @param enableAddBillCard
     */
    public void setEnableAddBillCard(java.lang.String enableAddBillCard) {
        this.enableAddBillCard = enableAddBillCard;
    }


    /**
     * Gets the enablePaxInfo value for this TransactionDetails.
     * 
     * @return enablePaxInfo
     */
    public java.lang.String getEnablePaxInfo() {
        return enablePaxInfo;
    }


    /**
     * Sets the enablePaxInfo value for this TransactionDetails.
     * 
     * @param enablePaxInfo
     */
    public void setEnablePaxInfo(java.lang.String enablePaxInfo) {
        this.enablePaxInfo = enablePaxInfo;
    }


    /**
     * Gets the inputCurrency value for this TransactionDetails.
     * 
     * @return inputCurrency
     */
    public java.lang.String getInputCurrency() {
        return inputCurrency;
    }


    /**
     * Sets the inputCurrency value for this TransactionDetails.
     * 
     * @param inputCurrency
     */
    public void setInputCurrency(java.lang.String inputCurrency) {
        this.inputCurrency = inputCurrency;
    }


    /**
     * Gets the merchantReference value for this TransactionDetails.
     * 
     * @return merchantReference
     */
    public java.lang.String getMerchantReference() {
        return merchantReference;
    }


    /**
     * Sets the merchantReference value for this TransactionDetails.
     * 
     * @param merchantReference
     */
    public void setMerchantReference(java.lang.String merchantReference) {
        this.merchantReference = merchantReference;
    }


    /**
     * Gets the paxCarrier value for this TransactionDetails.
     * 
     * @return paxCarrier
     */
    public java.lang.String getPaxCarrier() {
        return paxCarrier;
    }


    /**
     * Sets the paxCarrier value for this TransactionDetails.
     * 
     * @param paxCarrier
     */
    public void setPaxCarrier(java.lang.String paxCarrier) {
        this.paxCarrier = paxCarrier;
    }


    /**
     * Gets the paxCarrier2 value for this TransactionDetails.
     * 
     * @return paxCarrier2
     */
    public java.lang.String getPaxCarrier2() {
        return paxCarrier2;
    }


    /**
     * Sets the paxCarrier2 value for this TransactionDetails.
     * 
     * @param paxCarrier2
     */
    public void setPaxCarrier2(java.lang.String paxCarrier2) {
        this.paxCarrier2 = paxCarrier2;
    }


    /**
     * Gets the paxCarrier3 value for this TransactionDetails.
     * 
     * @return paxCarrier3
     */
    public java.lang.String getPaxCarrier3() {
        return paxCarrier3;
    }


    /**
     * Sets the paxCarrier3 value for this TransactionDetails.
     * 
     * @param paxCarrier3
     */
    public void setPaxCarrier3(java.lang.String paxCarrier3) {
        this.paxCarrier3 = paxCarrier3;
    }


    /**
     * Gets the paxCarrier4 value for this TransactionDetails.
     * 
     * @return paxCarrier4
     */
    public java.lang.String getPaxCarrier4() {
        return paxCarrier4;
    }


    /**
     * Sets the paxCarrier4 value for this TransactionDetails.
     * 
     * @param paxCarrier4
     */
    public void setPaxCarrier4(java.lang.String paxCarrier4) {
        this.paxCarrier4 = paxCarrier4;
    }


    /**
     * Gets the paxDateDepart value for this TransactionDetails.
     * 
     * @return paxDateDepart
     */
    public java.lang.String getPaxDateDepart() {
        return paxDateDepart;
    }


    /**
     * Sets the paxDateDepart value for this TransactionDetails.
     * 
     * @param paxDateDepart
     */
    public void setPaxDateDepart(java.lang.String paxDateDepart) {
        this.paxDateDepart = paxDateDepart;
    }


    /**
     * Gets the paxDate2 value for this TransactionDetails.
     * 
     * @return paxDate2
     */
    public java.lang.String getPaxDate2() {
        return paxDate2;
    }


    /**
     * Sets the paxDate2 value for this TransactionDetails.
     * 
     * @param paxDate2
     */
    public void setPaxDate2(java.lang.String paxDate2) {
        this.paxDate2 = paxDate2;
    }


    /**
     * Gets the paxDate3 value for this TransactionDetails.
     * 
     * @return paxDate3
     */
    public java.lang.String getPaxDate3() {
        return paxDate3;
    }


    /**
     * Sets the paxDate3 value for this TransactionDetails.
     * 
     * @param paxDate3
     */
    public void setPaxDate3(java.lang.String paxDate3) {
        this.paxDate3 = paxDate3;
    }


    /**
     * Gets the paxDate4 value for this TransactionDetails.
     * 
     * @return paxDate4
     */
    public java.lang.String getPaxDate4() {
        return paxDate4;
    }


    /**
     * Sets the paxDate4 value for this TransactionDetails.
     * 
     * @param paxDate4
     */
    public void setPaxDate4(java.lang.String paxDate4) {
        this.paxDate4 = paxDate4;
    }


    /**
     * Gets the paxTime1 value for this TransactionDetails.
     * 
     * @return paxTime1
     */
    public java.lang.String getPaxTime1() {
        return paxTime1;
    }


    /**
     * Sets the paxTime1 value for this TransactionDetails.
     * 
     * @param paxTime1
     */
    public void setPaxTime1(java.lang.String paxTime1) {
        this.paxTime1 = paxTime1;
    }


    /**
     * Gets the paxTime2 value for this TransactionDetails.
     * 
     * @return paxTime2
     */
    public java.lang.String getPaxTime2() {
        return paxTime2;
    }


    /**
     * Sets the paxTime2 value for this TransactionDetails.
     * 
     * @param paxTime2
     */
    public void setPaxTime2(java.lang.String paxTime2) {
        this.paxTime2 = paxTime2;
    }


    /**
     * Gets the paxTime3 value for this TransactionDetails.
     * 
     * @return paxTime3
     */
    public java.lang.String getPaxTime3() {
        return paxTime3;
    }


    /**
     * Sets the paxTime3 value for this TransactionDetails.
     * 
     * @param paxTime3
     */
    public void setPaxTime3(java.lang.String paxTime3) {
        this.paxTime3 = paxTime3;
    }


    /**
     * Gets the paxTime4 value for this TransactionDetails.
     * 
     * @return paxTime4
     */
    public java.lang.String getPaxTime4() {
        return paxTime4;
    }


    /**
     * Sets the paxTime4 value for this TransactionDetails.
     * 
     * @param paxTime4
     */
    public void setPaxTime4(java.lang.String paxTime4) {
        this.paxTime4 = paxTime4;
    }


    /**
     * Gets the paxLeg1 value for this TransactionDetails.
     * 
     * @return paxLeg1
     */
    public java.lang.String getPaxLeg1() {
        return paxLeg1;
    }


    /**
     * Sets the paxLeg1 value for this TransactionDetails.
     * 
     * @param paxLeg1
     */
    public void setPaxLeg1(java.lang.String paxLeg1) {
        this.paxLeg1 = paxLeg1;
    }


    /**
     * Gets the paxLeg2 value for this TransactionDetails.
     * 
     * @return paxLeg2
     */
    public java.lang.String getPaxLeg2() {
        return paxLeg2;
    }


    /**
     * Sets the paxLeg2 value for this TransactionDetails.
     * 
     * @param paxLeg2
     */
    public void setPaxLeg2(java.lang.String paxLeg2) {
        this.paxLeg2 = paxLeg2;
    }


    /**
     * Gets the paxLeg3 value for this TransactionDetails.
     * 
     * @return paxLeg3
     */
    public java.lang.String getPaxLeg3() {
        return paxLeg3;
    }


    /**
     * Sets the paxLeg3 value for this TransactionDetails.
     * 
     * @param paxLeg3
     */
    public void setPaxLeg3(java.lang.String paxLeg3) {
        this.paxLeg3 = paxLeg3;
    }


    /**
     * Gets the paxLeg4 value for this TransactionDetails.
     * 
     * @return paxLeg4
     */
    public java.lang.String getPaxLeg4() {
        return paxLeg4;
    }


    /**
     * Sets the paxLeg4 value for this TransactionDetails.
     * 
     * @param paxLeg4
     */
    public void setPaxLeg4(java.lang.String paxLeg4) {
        this.paxLeg4 = paxLeg4;
    }


    /**
     * Gets the paxClass1 value for this TransactionDetails.
     * 
     * @return paxClass1
     */
    public java.lang.String getPaxClass1() {
        return paxClass1;
    }


    /**
     * Sets the paxClass1 value for this TransactionDetails.
     * 
     * @param paxClass1
     */
    public void setPaxClass1(java.lang.String paxClass1) {
        this.paxClass1 = paxClass1;
    }


    /**
     * Gets the paxClass2 value for this TransactionDetails.
     * 
     * @return paxClass2
     */
    public java.lang.String getPaxClass2() {
        return paxClass2;
    }


    /**
     * Sets the paxClass2 value for this TransactionDetails.
     * 
     * @param paxClass2
     */
    public void setPaxClass2(java.lang.String paxClass2) {
        this.paxClass2 = paxClass2;
    }


    /**
     * Gets the paxClass3 value for this TransactionDetails.
     * 
     * @return paxClass3
     */
    public java.lang.String getPaxClass3() {
        return paxClass3;
    }


    /**
     * Sets the paxClass3 value for this TransactionDetails.
     * 
     * @param paxClass3
     */
    public void setPaxClass3(java.lang.String paxClass3) {
        this.paxClass3 = paxClass3;
    }


    /**
     * Gets the paxClass4 value for this TransactionDetails.
     * 
     * @return paxClass4
     */
    public java.lang.String getPaxClass4() {
        return paxClass4;
    }


    /**
     * Sets the paxClass4 value for this TransactionDetails.
     * 
     * @param paxClass4
     */
    public void setPaxClass4(java.lang.String paxClass4) {
        this.paxClass4 = paxClass4;
    }


    /**
     * Gets the paxStopOverCode1 value for this TransactionDetails.
     * 
     * @return paxStopOverCode1
     */
    public java.lang.String getPaxStopOverCode1() {
        return paxStopOverCode1;
    }


    /**
     * Sets the paxStopOverCode1 value for this TransactionDetails.
     * 
     * @param paxStopOverCode1
     */
    public void setPaxStopOverCode1(java.lang.String paxStopOverCode1) {
        this.paxStopOverCode1 = paxStopOverCode1;
    }


    /**
     * Gets the paxStopOverCode2 value for this TransactionDetails.
     * 
     * @return paxStopOverCode2
     */
    public java.lang.String getPaxStopOverCode2() {
        return paxStopOverCode2;
    }


    /**
     * Sets the paxStopOverCode2 value for this TransactionDetails.
     * 
     * @param paxStopOverCode2
     */
    public void setPaxStopOverCode2(java.lang.String paxStopOverCode2) {
        this.paxStopOverCode2 = paxStopOverCode2;
    }


    /**
     * Gets the paxStopOverCode3 value for this TransactionDetails.
     * 
     * @return paxStopOverCode3
     */
    public java.lang.String getPaxStopOverCode3() {
        return paxStopOverCode3;
    }


    /**
     * Sets the paxStopOverCode3 value for this TransactionDetails.
     * 
     * @param paxStopOverCode3
     */
    public void setPaxStopOverCode3(java.lang.String paxStopOverCode3) {
        this.paxStopOverCode3 = paxStopOverCode3;
    }


    /**
     * Gets the paxStopOverCode4 value for this TransactionDetails.
     * 
     * @return paxStopOverCode4
     */
    public java.lang.String getPaxStopOverCode4() {
        return paxStopOverCode4;
    }


    /**
     * Sets the paxStopOverCode4 value for this TransactionDetails.
     * 
     * @param paxStopOverCode4
     */
    public void setPaxStopOverCode4(java.lang.String paxStopOverCode4) {
        this.paxStopOverCode4 = paxStopOverCode4;
    }


    /**
     * Gets the paxFareBasis1 value for this TransactionDetails.
     * 
     * @return paxFareBasis1
     */
    public java.lang.String getPaxFareBasis1() {
        return paxFareBasis1;
    }


    /**
     * Sets the paxFareBasis1 value for this TransactionDetails.
     * 
     * @param paxFareBasis1
     */
    public void setPaxFareBasis1(java.lang.String paxFareBasis1) {
        this.paxFareBasis1 = paxFareBasis1;
    }


    /**
     * Gets the paxFareBasis2 value for this TransactionDetails.
     * 
     * @return paxFareBasis2
     */
    public java.lang.String getPaxFareBasis2() {
        return paxFareBasis2;
    }


    /**
     * Sets the paxFareBasis2 value for this TransactionDetails.
     * 
     * @param paxFareBasis2
     */
    public void setPaxFareBasis2(java.lang.String paxFareBasis2) {
        this.paxFareBasis2 = paxFareBasis2;
    }


    /**
     * Gets the paxFareBasis3 value for this TransactionDetails.
     * 
     * @return paxFareBasis3
     */
    public java.lang.String getPaxFareBasis3() {
        return paxFareBasis3;
    }


    /**
     * Sets the paxFareBasis3 value for this TransactionDetails.
     * 
     * @param paxFareBasis3
     */
    public void setPaxFareBasis3(java.lang.String paxFareBasis3) {
        this.paxFareBasis3 = paxFareBasis3;
    }


    /**
     * Gets the paxFareBasis4 value for this TransactionDetails.
     * 
     * @return paxFareBasis4
     */
    public java.lang.String getPaxFareBasis4() {
        return paxFareBasis4;
    }


    /**
     * Sets the paxFareBasis4 value for this TransactionDetails.
     * 
     * @param paxFareBasis4
     */
    public void setPaxFareBasis4(java.lang.String paxFareBasis4) {
        this.paxFareBasis4 = paxFareBasis4;
    }


    /**
     * Gets the paxFlightNumber1 value for this TransactionDetails.
     * 
     * @return paxFlightNumber1
     */
    public java.lang.String getPaxFlightNumber1() {
        return paxFlightNumber1;
    }


    /**
     * Sets the paxFlightNumber1 value for this TransactionDetails.
     * 
     * @param paxFlightNumber1
     */
    public void setPaxFlightNumber1(java.lang.String paxFlightNumber1) {
        this.paxFlightNumber1 = paxFlightNumber1;
    }


    /**
     * Gets the paxFlightNumber2 value for this TransactionDetails.
     * 
     * @return paxFlightNumber2
     */
    public java.lang.String getPaxFlightNumber2() {
        return paxFlightNumber2;
    }


    /**
     * Sets the paxFlightNumber2 value for this TransactionDetails.
     * 
     * @param paxFlightNumber2
     */
    public void setPaxFlightNumber2(java.lang.String paxFlightNumber2) {
        this.paxFlightNumber2 = paxFlightNumber2;
    }


    /**
     * Gets the paxFlightNumber3 value for this TransactionDetails.
     * 
     * @return paxFlightNumber3
     */
    public java.lang.String getPaxFlightNumber3() {
        return paxFlightNumber3;
    }


    /**
     * Sets the paxFlightNumber3 value for this TransactionDetails.
     * 
     * @param paxFlightNumber3
     */
    public void setPaxFlightNumber3(java.lang.String paxFlightNumber3) {
        this.paxFlightNumber3 = paxFlightNumber3;
    }


    /**
     * Gets the paxFlightNumber4 value for this TransactionDetails.
     * 
     * @return paxFlightNumber4
     */
    public java.lang.String getPaxFlightNumber4() {
        return paxFlightNumber4;
    }


    /**
     * Sets the paxFlightNumber4 value for this TransactionDetails.
     * 
     * @param paxFlightNumber4
     */
    public void setPaxFlightNumber4(java.lang.String paxFlightNumber4) {
        this.paxFlightNumber4 = paxFlightNumber4;
    }


    /**
     * Gets the paxName value for this TransactionDetails.
     * 
     * @return paxName
     */
    public java.lang.String getPaxName() {
        return paxName;
    }


    /**
     * Sets the paxName value for this TransactionDetails.
     * 
     * @param paxName
     */
    public void setPaxName(java.lang.String paxName) {
        this.paxName = paxName;
    }


    /**
     * Gets the paxOrigin value for this TransactionDetails.
     * 
     * @return paxOrigin
     */
    public java.lang.String getPaxOrigin() {
        return paxOrigin;
    }


    /**
     * Sets the paxOrigin value for this TransactionDetails.
     * 
     * @param paxOrigin
     */
    public void setPaxOrigin(java.lang.String paxOrigin) {
        this.paxOrigin = paxOrigin;
    }


    /**
     * Gets the paxTicketNumber value for this TransactionDetails.
     * 
     * @return paxTicketNumber
     */
    public java.lang.String getPaxTicketNumber() {
        return paxTicketNumber;
    }


    /**
     * Sets the paxTicketNumber value for this TransactionDetails.
     * 
     * @param paxTicketNumber
     */
    public void setPaxTicketNumber(java.lang.String paxTicketNumber) {
        this.paxTicketNumber = paxTicketNumber;
    }


    /**
     * Gets the paxTravelAgentInfo value for this TransactionDetails.
     * 
     * @return paxTravelAgentInfo
     */
    public java.lang.String getPaxTravelAgentInfo() {
        return paxTravelAgentInfo;
    }


    /**
     * Sets the paxTravelAgentInfo value for this TransactionDetails.
     * 
     * @param paxTravelAgentInfo
     */
    public void setPaxTravelAgentInfo(java.lang.String paxTravelAgentInfo) {
        this.paxTravelAgentInfo = paxTravelAgentInfo;
    }


    /**
     * Gets the txnData1 value for this TransactionDetails.
     * 
     * @return txnData1
     */
    public java.lang.String getTxnData1() {
        return txnData1;
    }


    /**
     * Sets the txnData1 value for this TransactionDetails.
     * 
     * @param txnData1
     */
    public void setTxnData1(java.lang.String txnData1) {
        this.txnData1 = txnData1;
    }


    /**
     * Gets the txnData2 value for this TransactionDetails.
     * 
     * @return txnData2
     */
    public java.lang.String getTxnData2() {
        return txnData2;
    }


    /**
     * Sets the txnData2 value for this TransactionDetails.
     * 
     * @param txnData2
     */
    public void setTxnData2(java.lang.String txnData2) {
        this.txnData2 = txnData2;
    }


    /**
     * Gets the txnData3 value for this TransactionDetails.
     * 
     * @return txnData3
     */
    public java.lang.String getTxnData3() {
        return txnData3;
    }


    /**
     * Sets the txnData3 value for this TransactionDetails.
     * 
     * @param txnData3
     */
    public void setTxnData3(java.lang.String txnData3) {
        this.txnData3 = txnData3;
    }


    /**
     * Gets the txnRef value for this TransactionDetails.
     * 
     * @return txnRef
     */
    public java.lang.String getTxnRef() {
        return txnRef;
    }


    /**
     * Sets the txnRef value for this TransactionDetails.
     * 
     * @param txnRef
     */
    public void setTxnRef(java.lang.String txnRef) {
        this.txnRef = txnRef;
    }


    /**
     * Gets the txnType value for this TransactionDetails.
     * 
     * @return txnType
     */
    public java.lang.String getTxnType() {
        return txnType;
    }


    /**
     * Sets the txnType value for this TransactionDetails.
     * 
     * @param txnType
     */
    public void setTxnType(java.lang.String txnType) {
        this.txnType = txnType;
    }


    /**
     * Gets the dateStart value for this TransactionDetails.
     * 
     * @return dateStart
     */
    public java.lang.String getDateStart() {
        return dateStart;
    }


    /**
     * Sets the dateStart value for this TransactionDetails.
     * 
     * @param dateStart
     */
    public void setDateStart(java.lang.String dateStart) {
        this.dateStart = dateStart;
    }


    /**
     * Gets the issueNumber value for this TransactionDetails.
     * 
     * @return issueNumber
     */
    public java.lang.String getIssueNumber() {
        return issueNumber;
    }


    /**
     * Sets the issueNumber value for this TransactionDetails.
     * 
     * @param issueNumber
     */
    public void setIssueNumber(java.lang.String issueNumber) {
        this.issueNumber = issueNumber;
    }


    /**
     * Gets the enableAvsData value for this TransactionDetails.
     * 
     * @return enableAvsData
     */
    public java.lang.String getEnableAvsData() {
        return enableAvsData;
    }


    /**
     * Sets the enableAvsData value for this TransactionDetails.
     * 
     * @param enableAvsData
     */
    public void setEnableAvsData(java.lang.String enableAvsData) {
        this.enableAvsData = enableAvsData;
    }


    /**
     * Gets the avsAction value for this TransactionDetails.
     * 
     * @return avsAction
     */
    public java.lang.String getAvsAction() {
        return avsAction;
    }


    /**
     * Sets the avsAction value for this TransactionDetails.
     * 
     * @param avsAction
     */
    public void setAvsAction(java.lang.String avsAction) {
        this.avsAction = avsAction;
    }


    /**
     * Gets the avsPostCode value for this TransactionDetails.
     * 
     * @return avsPostCode
     */
    public java.lang.String getAvsPostCode() {
        return avsPostCode;
    }


    /**
     * Sets the avsPostCode value for this TransactionDetails.
     * 
     * @param avsPostCode
     */
    public void setAvsPostCode(java.lang.String avsPostCode) {
        this.avsPostCode = avsPostCode;
    }


    /**
     * Gets the avsStreetAddress value for this TransactionDetails.
     * 
     * @return avsStreetAddress
     */
    public java.lang.String getAvsStreetAddress() {
        return avsStreetAddress;
    }


    /**
     * Sets the avsStreetAddress value for this TransactionDetails.
     * 
     * @param avsStreetAddress
     */
    public void setAvsStreetAddress(java.lang.String avsStreetAddress) {
        this.avsStreetAddress = avsStreetAddress;
    }


    /**
     * Gets the enable3DSecure value for this TransactionDetails.
     * 
     * @return enable3DSecure
     */
    public java.lang.String getEnable3DSecure() {
        return enable3DSecure;
    }


    /**
     * Sets the enable3DSecure value for this TransactionDetails.
     * 
     * @param enable3DSecure
     */
    public void setEnable3DSecure(java.lang.String enable3DSecure) {
        this.enable3DSecure = enable3DSecure;
    }


    /**
     * Gets the paRes value for this TransactionDetails.
     * 
     * @return paRes
     */
    public java.lang.String getPaRes() {
        return paRes;
    }


    /**
     * Sets the paRes value for this TransactionDetails.
     * 
     * @param paRes
     */
    public void setPaRes(java.lang.String paRes) {
        this.paRes = paRes;
    }


    /**
     * Gets the clientType value for this TransactionDetails.
     * 
     * @return clientType
     */
    public java.lang.String getClientType() {
        return clientType;
    }


    /**
     * Sets the clientType value for this TransactionDetails.
     * 
     * @param clientType
     */
    public void setClientType(java.lang.String clientType) {
        this.clientType = clientType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransactionDetails)) return false;
        TransactionDetails other = (TransactionDetails) obj;
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
            ((this.billingId==null && other.getBillingId()==null) || 
             (this.billingId!=null &&
              this.billingId.equals(other.getBillingId()))) &&
            ((this.cardHolderName==null && other.getCardHolderName()==null) || 
             (this.cardHolderName!=null &&
              this.cardHolderName.equals(other.getCardHolderName()))) &&
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            ((this.clientInfo==null && other.getClientInfo()==null) || 
             (this.clientInfo!=null &&
              this.clientInfo.equals(other.getClientInfo()))) &&
            ((this.cvc2==null && other.getCvc2()==null) || 
             (this.cvc2!=null &&
              this.cvc2.equals(other.getCvc2()))) &&
            ((this.dateExpiry==null && other.getDateExpiry()==null) || 
             (this.dateExpiry!=null &&
              this.dateExpiry.equals(other.getDateExpiry()))) &&
            ((this.dpsBillingId==null && other.getDpsBillingId()==null) || 
             (this.dpsBillingId!=null &&
              this.dpsBillingId.equals(other.getDpsBillingId()))) &&
            ((this.dpsTxnRef==null && other.getDpsTxnRef()==null) || 
             (this.dpsTxnRef!=null &&
              this.dpsTxnRef.equals(other.getDpsTxnRef()))) &&
            ((this.enableAddBillCard==null && other.getEnableAddBillCard()==null) || 
             (this.enableAddBillCard!=null &&
              this.enableAddBillCard.equals(other.getEnableAddBillCard()))) &&
            ((this.enablePaxInfo==null && other.getEnablePaxInfo()==null) || 
             (this.enablePaxInfo!=null &&
              this.enablePaxInfo.equals(other.getEnablePaxInfo()))) &&
            ((this.inputCurrency==null && other.getInputCurrency()==null) || 
             (this.inputCurrency!=null &&
              this.inputCurrency.equals(other.getInputCurrency()))) &&
            ((this.merchantReference==null && other.getMerchantReference()==null) || 
             (this.merchantReference!=null &&
              this.merchantReference.equals(other.getMerchantReference()))) &&
            ((this.paxCarrier==null && other.getPaxCarrier()==null) || 
             (this.paxCarrier!=null &&
              this.paxCarrier.equals(other.getPaxCarrier()))) &&
            ((this.paxCarrier2==null && other.getPaxCarrier2()==null) || 
             (this.paxCarrier2!=null &&
              this.paxCarrier2.equals(other.getPaxCarrier2()))) &&
            ((this.paxCarrier3==null && other.getPaxCarrier3()==null) || 
             (this.paxCarrier3!=null &&
              this.paxCarrier3.equals(other.getPaxCarrier3()))) &&
            ((this.paxCarrier4==null && other.getPaxCarrier4()==null) || 
             (this.paxCarrier4!=null &&
              this.paxCarrier4.equals(other.getPaxCarrier4()))) &&
            ((this.paxDateDepart==null && other.getPaxDateDepart()==null) || 
             (this.paxDateDepart!=null &&
              this.paxDateDepart.equals(other.getPaxDateDepart()))) &&
            ((this.paxDate2==null && other.getPaxDate2()==null) || 
             (this.paxDate2!=null &&
              this.paxDate2.equals(other.getPaxDate2()))) &&
            ((this.paxDate3==null && other.getPaxDate3()==null) || 
             (this.paxDate3!=null &&
              this.paxDate3.equals(other.getPaxDate3()))) &&
            ((this.paxDate4==null && other.getPaxDate4()==null) || 
             (this.paxDate4!=null &&
              this.paxDate4.equals(other.getPaxDate4()))) &&
            ((this.paxTime1==null && other.getPaxTime1()==null) || 
             (this.paxTime1!=null &&
              this.paxTime1.equals(other.getPaxTime1()))) &&
            ((this.paxTime2==null && other.getPaxTime2()==null) || 
             (this.paxTime2!=null &&
              this.paxTime2.equals(other.getPaxTime2()))) &&
            ((this.paxTime3==null && other.getPaxTime3()==null) || 
             (this.paxTime3!=null &&
              this.paxTime3.equals(other.getPaxTime3()))) &&
            ((this.paxTime4==null && other.getPaxTime4()==null) || 
             (this.paxTime4!=null &&
              this.paxTime4.equals(other.getPaxTime4()))) &&
            ((this.paxLeg1==null && other.getPaxLeg1()==null) || 
             (this.paxLeg1!=null &&
              this.paxLeg1.equals(other.getPaxLeg1()))) &&
            ((this.paxLeg2==null && other.getPaxLeg2()==null) || 
             (this.paxLeg2!=null &&
              this.paxLeg2.equals(other.getPaxLeg2()))) &&
            ((this.paxLeg3==null && other.getPaxLeg3()==null) || 
             (this.paxLeg3!=null &&
              this.paxLeg3.equals(other.getPaxLeg3()))) &&
            ((this.paxLeg4==null && other.getPaxLeg4()==null) || 
             (this.paxLeg4!=null &&
              this.paxLeg4.equals(other.getPaxLeg4()))) &&
            ((this.paxClass1==null && other.getPaxClass1()==null) || 
             (this.paxClass1!=null &&
              this.paxClass1.equals(other.getPaxClass1()))) &&
            ((this.paxClass2==null && other.getPaxClass2()==null) || 
             (this.paxClass2!=null &&
              this.paxClass2.equals(other.getPaxClass2()))) &&
            ((this.paxClass3==null && other.getPaxClass3()==null) || 
             (this.paxClass3!=null &&
              this.paxClass3.equals(other.getPaxClass3()))) &&
            ((this.paxClass4==null && other.getPaxClass4()==null) || 
             (this.paxClass4!=null &&
              this.paxClass4.equals(other.getPaxClass4()))) &&
            ((this.paxStopOverCode1==null && other.getPaxStopOverCode1()==null) || 
             (this.paxStopOverCode1!=null &&
              this.paxStopOverCode1.equals(other.getPaxStopOverCode1()))) &&
            ((this.paxStopOverCode2==null && other.getPaxStopOverCode2()==null) || 
             (this.paxStopOverCode2!=null &&
              this.paxStopOverCode2.equals(other.getPaxStopOverCode2()))) &&
            ((this.paxStopOverCode3==null && other.getPaxStopOverCode3()==null) || 
             (this.paxStopOverCode3!=null &&
              this.paxStopOverCode3.equals(other.getPaxStopOverCode3()))) &&
            ((this.paxStopOverCode4==null && other.getPaxStopOverCode4()==null) || 
             (this.paxStopOverCode4!=null &&
              this.paxStopOverCode4.equals(other.getPaxStopOverCode4()))) &&
            ((this.paxFareBasis1==null && other.getPaxFareBasis1()==null) || 
             (this.paxFareBasis1!=null &&
              this.paxFareBasis1.equals(other.getPaxFareBasis1()))) &&
            ((this.paxFareBasis2==null && other.getPaxFareBasis2()==null) || 
             (this.paxFareBasis2!=null &&
              this.paxFareBasis2.equals(other.getPaxFareBasis2()))) &&
            ((this.paxFareBasis3==null && other.getPaxFareBasis3()==null) || 
             (this.paxFareBasis3!=null &&
              this.paxFareBasis3.equals(other.getPaxFareBasis3()))) &&
            ((this.paxFareBasis4==null && other.getPaxFareBasis4()==null) || 
             (this.paxFareBasis4!=null &&
              this.paxFareBasis4.equals(other.getPaxFareBasis4()))) &&
            ((this.paxFlightNumber1==null && other.getPaxFlightNumber1()==null) || 
             (this.paxFlightNumber1!=null &&
              this.paxFlightNumber1.equals(other.getPaxFlightNumber1()))) &&
            ((this.paxFlightNumber2==null && other.getPaxFlightNumber2()==null) || 
             (this.paxFlightNumber2!=null &&
              this.paxFlightNumber2.equals(other.getPaxFlightNumber2()))) &&
            ((this.paxFlightNumber3==null && other.getPaxFlightNumber3()==null) || 
             (this.paxFlightNumber3!=null &&
              this.paxFlightNumber3.equals(other.getPaxFlightNumber3()))) &&
            ((this.paxFlightNumber4==null && other.getPaxFlightNumber4()==null) || 
             (this.paxFlightNumber4!=null &&
              this.paxFlightNumber4.equals(other.getPaxFlightNumber4()))) &&
            ((this.paxName==null && other.getPaxName()==null) || 
             (this.paxName!=null &&
              this.paxName.equals(other.getPaxName()))) &&
            ((this.paxOrigin==null && other.getPaxOrigin()==null) || 
             (this.paxOrigin!=null &&
              this.paxOrigin.equals(other.getPaxOrigin()))) &&
            ((this.paxTicketNumber==null && other.getPaxTicketNumber()==null) || 
             (this.paxTicketNumber!=null &&
              this.paxTicketNumber.equals(other.getPaxTicketNumber()))) &&
            ((this.paxTravelAgentInfo==null && other.getPaxTravelAgentInfo()==null) || 
             (this.paxTravelAgentInfo!=null &&
              this.paxTravelAgentInfo.equals(other.getPaxTravelAgentInfo()))) &&
            ((this.txnData1==null && other.getTxnData1()==null) || 
             (this.txnData1!=null &&
              this.txnData1.equals(other.getTxnData1()))) &&
            ((this.txnData2==null && other.getTxnData2()==null) || 
             (this.txnData2!=null &&
              this.txnData2.equals(other.getTxnData2()))) &&
            ((this.txnData3==null && other.getTxnData3()==null) || 
             (this.txnData3!=null &&
              this.txnData3.equals(other.getTxnData3()))) &&
            ((this.txnRef==null && other.getTxnRef()==null) || 
             (this.txnRef!=null &&
              this.txnRef.equals(other.getTxnRef()))) &&
            ((this.txnType==null && other.getTxnType()==null) || 
             (this.txnType!=null &&
              this.txnType.equals(other.getTxnType()))) &&
            ((this.dateStart==null && other.getDateStart()==null) || 
             (this.dateStart!=null &&
              this.dateStart.equals(other.getDateStart()))) &&
            ((this.issueNumber==null && other.getIssueNumber()==null) || 
             (this.issueNumber!=null &&
              this.issueNumber.equals(other.getIssueNumber()))) &&
            ((this.enableAvsData==null && other.getEnableAvsData()==null) || 
             (this.enableAvsData!=null &&
              this.enableAvsData.equals(other.getEnableAvsData()))) &&
            ((this.avsAction==null && other.getAvsAction()==null) || 
             (this.avsAction!=null &&
              this.avsAction.equals(other.getAvsAction()))) &&
            ((this.avsPostCode==null && other.getAvsPostCode()==null) || 
             (this.avsPostCode!=null &&
              this.avsPostCode.equals(other.getAvsPostCode()))) &&
            ((this.avsStreetAddress==null && other.getAvsStreetAddress()==null) || 
             (this.avsStreetAddress!=null &&
              this.avsStreetAddress.equals(other.getAvsStreetAddress()))) &&
            ((this.enable3DSecure==null && other.getEnable3DSecure()==null) || 
             (this.enable3DSecure!=null &&
              this.enable3DSecure.equals(other.getEnable3DSecure()))) &&
            ((this.paRes==null && other.getPaRes()==null) || 
             (this.paRes!=null &&
              this.paRes.equals(other.getPaRes()))) &&
            ((this.clientType==null && other.getClientType()==null) || 
             (this.clientType!=null &&
              this.clientType.equals(other.getClientType())));
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
        if (getBillingId() != null) {
            _hashCode += getBillingId().hashCode();
        }
        if (getCardHolderName() != null) {
            _hashCode += getCardHolderName().hashCode();
        }
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        if (getClientInfo() != null) {
            _hashCode += getClientInfo().hashCode();
        }
        if (getCvc2() != null) {
            _hashCode += getCvc2().hashCode();
        }
        if (getDateExpiry() != null) {
            _hashCode += getDateExpiry().hashCode();
        }
        if (getDpsBillingId() != null) {
            _hashCode += getDpsBillingId().hashCode();
        }
        if (getDpsTxnRef() != null) {
            _hashCode += getDpsTxnRef().hashCode();
        }
        if (getEnableAddBillCard() != null) {
            _hashCode += getEnableAddBillCard().hashCode();
        }
        if (getEnablePaxInfo() != null) {
            _hashCode += getEnablePaxInfo().hashCode();
        }
        if (getInputCurrency() != null) {
            _hashCode += getInputCurrency().hashCode();
        }
        if (getMerchantReference() != null) {
            _hashCode += getMerchantReference().hashCode();
        }
        if (getPaxCarrier() != null) {
            _hashCode += getPaxCarrier().hashCode();
        }
        if (getPaxCarrier2() != null) {
            _hashCode += getPaxCarrier2().hashCode();
        }
        if (getPaxCarrier3() != null) {
            _hashCode += getPaxCarrier3().hashCode();
        }
        if (getPaxCarrier4() != null) {
            _hashCode += getPaxCarrier4().hashCode();
        }
        if (getPaxDateDepart() != null) {
            _hashCode += getPaxDateDepart().hashCode();
        }
        if (getPaxDate2() != null) {
            _hashCode += getPaxDate2().hashCode();
        }
        if (getPaxDate3() != null) {
            _hashCode += getPaxDate3().hashCode();
        }
        if (getPaxDate4() != null) {
            _hashCode += getPaxDate4().hashCode();
        }
        if (getPaxTime1() != null) {
            _hashCode += getPaxTime1().hashCode();
        }
        if (getPaxTime2() != null) {
            _hashCode += getPaxTime2().hashCode();
        }
        if (getPaxTime3() != null) {
            _hashCode += getPaxTime3().hashCode();
        }
        if (getPaxTime4() != null) {
            _hashCode += getPaxTime4().hashCode();
        }
        if (getPaxLeg1() != null) {
            _hashCode += getPaxLeg1().hashCode();
        }
        if (getPaxLeg2() != null) {
            _hashCode += getPaxLeg2().hashCode();
        }
        if (getPaxLeg3() != null) {
            _hashCode += getPaxLeg3().hashCode();
        }
        if (getPaxLeg4() != null) {
            _hashCode += getPaxLeg4().hashCode();
        }
        if (getPaxClass1() != null) {
            _hashCode += getPaxClass1().hashCode();
        }
        if (getPaxClass2() != null) {
            _hashCode += getPaxClass2().hashCode();
        }
        if (getPaxClass3() != null) {
            _hashCode += getPaxClass3().hashCode();
        }
        if (getPaxClass4() != null) {
            _hashCode += getPaxClass4().hashCode();
        }
        if (getPaxStopOverCode1() != null) {
            _hashCode += getPaxStopOverCode1().hashCode();
        }
        if (getPaxStopOverCode2() != null) {
            _hashCode += getPaxStopOverCode2().hashCode();
        }
        if (getPaxStopOverCode3() != null) {
            _hashCode += getPaxStopOverCode3().hashCode();
        }
        if (getPaxStopOverCode4() != null) {
            _hashCode += getPaxStopOverCode4().hashCode();
        }
        if (getPaxFareBasis1() != null) {
            _hashCode += getPaxFareBasis1().hashCode();
        }
        if (getPaxFareBasis2() != null) {
            _hashCode += getPaxFareBasis2().hashCode();
        }
        if (getPaxFareBasis3() != null) {
            _hashCode += getPaxFareBasis3().hashCode();
        }
        if (getPaxFareBasis4() != null) {
            _hashCode += getPaxFareBasis4().hashCode();
        }
        if (getPaxFlightNumber1() != null) {
            _hashCode += getPaxFlightNumber1().hashCode();
        }
        if (getPaxFlightNumber2() != null) {
            _hashCode += getPaxFlightNumber2().hashCode();
        }
        if (getPaxFlightNumber3() != null) {
            _hashCode += getPaxFlightNumber3().hashCode();
        }
        if (getPaxFlightNumber4() != null) {
            _hashCode += getPaxFlightNumber4().hashCode();
        }
        if (getPaxName() != null) {
            _hashCode += getPaxName().hashCode();
        }
        if (getPaxOrigin() != null) {
            _hashCode += getPaxOrigin().hashCode();
        }
        if (getPaxTicketNumber() != null) {
            _hashCode += getPaxTicketNumber().hashCode();
        }
        if (getPaxTravelAgentInfo() != null) {
            _hashCode += getPaxTravelAgentInfo().hashCode();
        }
        if (getTxnData1() != null) {
            _hashCode += getTxnData1().hashCode();
        }
        if (getTxnData2() != null) {
            _hashCode += getTxnData2().hashCode();
        }
        if (getTxnData3() != null) {
            _hashCode += getTxnData3().hashCode();
        }
        if (getTxnRef() != null) {
            _hashCode += getTxnRef().hashCode();
        }
        if (getTxnType() != null) {
            _hashCode += getTxnType().hashCode();
        }
        if (getDateStart() != null) {
            _hashCode += getDateStart().hashCode();
        }
        if (getIssueNumber() != null) {
            _hashCode += getIssueNumber().hashCode();
        }
        if (getEnableAvsData() != null) {
            _hashCode += getEnableAvsData().hashCode();
        }
        if (getAvsAction() != null) {
            _hashCode += getAvsAction().hashCode();
        }
        if (getAvsPostCode() != null) {
            _hashCode += getAvsPostCode().hashCode();
        }
        if (getAvsStreetAddress() != null) {
            _hashCode += getAvsStreetAddress().hashCode();
        }
        if (getEnable3DSecure() != null) {
            _hashCode += getEnable3DSecure().hashCode();
        }
        if (getPaRes() != null) {
            _hashCode += getPaRes().hashCode();
        }
        if (getClientType() != null) {
            _hashCode += getClientType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionDetails.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "TransactionDetails"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "amount"));
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
        elemField.setFieldName("cardHolderName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "cardHolderName"));
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
        elemField.setFieldName("clientInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "clientInfo"));
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
        elemField.setFieldName("enableAddBillCard");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "enableAddBillCard"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enablePaxInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "enablePaxInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inputCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "inputCurrency"));
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
        elemField.setFieldName("paxCarrier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxCarrier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxCarrier2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxCarrier2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxCarrier3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxCarrier3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxCarrier4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxCarrier4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxDateDepart");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxDateDepart"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxDate2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxDate2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxDate3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxDate3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxDate4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxDate4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxTime1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxTime1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxTime2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxTime2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxTime3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxTime3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxTime4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxTime4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxLeg1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxLeg1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxLeg2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxLeg2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxLeg3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxLeg3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxLeg4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxLeg4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxClass1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxClass1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxClass2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxClass2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxClass3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxClass3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxClass4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxClass4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxStopOverCode1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxStopOverCode1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxStopOverCode2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxStopOverCode2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxStopOverCode3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxStopOverCode3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxStopOverCode4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxStopOverCode4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFareBasis1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFareBasis1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFareBasis2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFareBasis2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFareBasis3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFareBasis3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFareBasis4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFareBasis4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFlightNumber1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFlightNumber1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFlightNumber2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFlightNumber2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFlightNumber3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFlightNumber3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxFlightNumber4");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxFlightNumber4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxOrigin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxOrigin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxTicketNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxTicketNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paxTravelAgentInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paxTravelAgentInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnData1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "txnData1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnData2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "txnData2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnData3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "txnData3"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateStart");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "dateStart"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issueNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "issueNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enableAvsData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "enableAvsData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "avsAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsPostCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "avsPostCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsStreetAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "avsStreetAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enable3DSecure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "enable3DSecure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paRes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paRes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "clientType"));
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
