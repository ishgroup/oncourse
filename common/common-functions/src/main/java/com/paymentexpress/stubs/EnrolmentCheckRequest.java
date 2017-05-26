/**
 * EnrolmentCheckRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class EnrolmentCheckRequest  implements java.io.Serializable {
    private java.lang.String amount;

    private java.lang.String cardNumber;

    private java.lang.String dateExpiry;

    private java.lang.String txnDescription;

    private java.lang.String txnRef;

    private java.lang.String currency;

    public EnrolmentCheckRequest() {
    }

    public EnrolmentCheckRequest(
           java.lang.String amount,
           java.lang.String cardNumber,
           java.lang.String dateExpiry,
           java.lang.String txnDescription,
           java.lang.String txnRef,
           java.lang.String currency) {
           this.amount = amount;
           this.cardNumber = cardNumber;
           this.dateExpiry = dateExpiry;
           this.txnDescription = txnDescription;
           this.txnRef = txnRef;
           this.currency = currency;
    }


    /**
     * Gets the amount value for this EnrolmentCheckRequest.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this EnrolmentCheckRequest.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the cardNumber value for this EnrolmentCheckRequest.
     * 
     * @return cardNumber
     */
    public java.lang.String getCardNumber() {
        return cardNumber;
    }


    /**
     * Sets the cardNumber value for this EnrolmentCheckRequest.
     * 
     * @param cardNumber
     */
    public void setCardNumber(java.lang.String cardNumber) {
        this.cardNumber = cardNumber;
    }


    /**
     * Gets the dateExpiry value for this EnrolmentCheckRequest.
     * 
     * @return dateExpiry
     */
    public java.lang.String getDateExpiry() {
        return dateExpiry;
    }


    /**
     * Sets the dateExpiry value for this EnrolmentCheckRequest.
     * 
     * @param dateExpiry
     */
    public void setDateExpiry(java.lang.String dateExpiry) {
        this.dateExpiry = dateExpiry;
    }


    /**
     * Gets the txnDescription value for this EnrolmentCheckRequest.
     * 
     * @return txnDescription
     */
    public java.lang.String getTxnDescription() {
        return txnDescription;
    }


    /**
     * Sets the txnDescription value for this EnrolmentCheckRequest.
     * 
     * @param txnDescription
     */
    public void setTxnDescription(java.lang.String txnDescription) {
        this.txnDescription = txnDescription;
    }


    /**
     * Gets the txnRef value for this EnrolmentCheckRequest.
     * 
     * @return txnRef
     */
    public java.lang.String getTxnRef() {
        return txnRef;
    }


    /**
     * Sets the txnRef value for this EnrolmentCheckRequest.
     * 
     * @param txnRef
     */
    public void setTxnRef(java.lang.String txnRef) {
        this.txnRef = txnRef;
    }


    /**
     * Gets the currency value for this EnrolmentCheckRequest.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this EnrolmentCheckRequest.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EnrolmentCheckRequest)) return false;
        EnrolmentCheckRequest other = (EnrolmentCheckRequest) obj;
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
            ((this.cardNumber==null && other.getCardNumber()==null) || 
             (this.cardNumber!=null &&
              this.cardNumber.equals(other.getCardNumber()))) &&
            ((this.dateExpiry==null && other.getDateExpiry()==null) || 
             (this.dateExpiry!=null &&
              this.dateExpiry.equals(other.getDateExpiry()))) &&
            ((this.txnDescription==null && other.getTxnDescription()==null) || 
             (this.txnDescription!=null &&
              this.txnDescription.equals(other.getTxnDescription()))) &&
            ((this.txnRef==null && other.getTxnRef()==null) || 
             (this.txnRef!=null &&
              this.txnRef.equals(other.getTxnRef()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency())));
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
        if (getCardNumber() != null) {
            _hashCode += getCardNumber().hashCode();
        }
        if (getDateExpiry() != null) {
            _hashCode += getDateExpiry().hashCode();
        }
        if (getTxnDescription() != null) {
            _hashCode += getTxnDescription().hashCode();
        }
        if (getTxnRef() != null) {
            _hashCode += getTxnRef().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EnrolmentCheckRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "EnrolmentCheckRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "amount"));
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
        elemField.setFieldName("dateExpiry");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "dateExpiry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "txnDescription"));
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
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "currency"));
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
