/**
 * RiskRuleMatch.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class RiskRuleMatch  implements java.io.Serializable {
    private java.lang.String matchedAmount;

    private int matchedTransactionCount;

    private java.lang.String name;

    private java.lang.String type;

    private java.lang.String action;

    private int period;

    private java.lang.String includedTransactions;

    private java.lang.String amountLimit;

    private int transactionCountLimit;

    public RiskRuleMatch() {
    }

    public RiskRuleMatch(
           java.lang.String matchedAmount,
           int matchedTransactionCount,
           java.lang.String name,
           java.lang.String type,
           java.lang.String action,
           int period,
           java.lang.String includedTransactions,
           java.lang.String amountLimit,
           int transactionCountLimit) {
           this.matchedAmount = matchedAmount;
           this.matchedTransactionCount = matchedTransactionCount;
           this.name = name;
           this.type = type;
           this.action = action;
           this.period = period;
           this.includedTransactions = includedTransactions;
           this.amountLimit = amountLimit;
           this.transactionCountLimit = transactionCountLimit;
    }


    /**
     * Gets the matchedAmount value for this RiskRuleMatch.
     * 
     * @return matchedAmount
     */
    public java.lang.String getMatchedAmount() {
        return matchedAmount;
    }


    /**
     * Sets the matchedAmount value for this RiskRuleMatch.
     * 
     * @param matchedAmount
     */
    public void setMatchedAmount(java.lang.String matchedAmount) {
        this.matchedAmount = matchedAmount;
    }


    /**
     * Gets the matchedTransactionCount value for this RiskRuleMatch.
     * 
     * @return matchedTransactionCount
     */
    public int getMatchedTransactionCount() {
        return matchedTransactionCount;
    }


    /**
     * Sets the matchedTransactionCount value for this RiskRuleMatch.
     * 
     * @param matchedTransactionCount
     */
    public void setMatchedTransactionCount(int matchedTransactionCount) {
        this.matchedTransactionCount = matchedTransactionCount;
    }


    /**
     * Gets the name value for this RiskRuleMatch.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this RiskRuleMatch.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the type value for this RiskRuleMatch.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this RiskRuleMatch.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the action value for this RiskRuleMatch.
     * 
     * @return action
     */
    public java.lang.String getAction() {
        return action;
    }


    /**
     * Sets the action value for this RiskRuleMatch.
     * 
     * @param action
     */
    public void setAction(java.lang.String action) {
        this.action = action;
    }


    /**
     * Gets the period value for this RiskRuleMatch.
     * 
     * @return period
     */
    public int getPeriod() {
        return period;
    }


    /**
     * Sets the period value for this RiskRuleMatch.
     * 
     * @param period
     */
    public void setPeriod(int period) {
        this.period = period;
    }


    /**
     * Gets the includedTransactions value for this RiskRuleMatch.
     * 
     * @return includedTransactions
     */
    public java.lang.String getIncludedTransactions() {
        return includedTransactions;
    }


    /**
     * Sets the includedTransactions value for this RiskRuleMatch.
     * 
     * @param includedTransactions
     */
    public void setIncludedTransactions(java.lang.String includedTransactions) {
        this.includedTransactions = includedTransactions;
    }


    /**
     * Gets the amountLimit value for this RiskRuleMatch.
     * 
     * @return amountLimit
     */
    public java.lang.String getAmountLimit() {
        return amountLimit;
    }


    /**
     * Sets the amountLimit value for this RiskRuleMatch.
     * 
     * @param amountLimit
     */
    public void setAmountLimit(java.lang.String amountLimit) {
        this.amountLimit = amountLimit;
    }


    /**
     * Gets the transactionCountLimit value for this RiskRuleMatch.
     * 
     * @return transactionCountLimit
     */
    public int getTransactionCountLimit() {
        return transactionCountLimit;
    }


    /**
     * Sets the transactionCountLimit value for this RiskRuleMatch.
     * 
     * @param transactionCountLimit
     */
    public void setTransactionCountLimit(int transactionCountLimit) {
        this.transactionCountLimit = transactionCountLimit;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RiskRuleMatch)) return false;
        RiskRuleMatch other = (RiskRuleMatch) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.matchedAmount==null && other.getMatchedAmount()==null) || 
             (this.matchedAmount!=null &&
              this.matchedAmount.equals(other.getMatchedAmount()))) &&
            this.matchedTransactionCount == other.getMatchedTransactionCount() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.action==null && other.getAction()==null) || 
             (this.action!=null &&
              this.action.equals(other.getAction()))) &&
            this.period == other.getPeriod() &&
            ((this.includedTransactions==null && other.getIncludedTransactions()==null) || 
             (this.includedTransactions!=null &&
              this.includedTransactions.equals(other.getIncludedTransactions()))) &&
            ((this.amountLimit==null && other.getAmountLimit()==null) || 
             (this.amountLimit!=null &&
              this.amountLimit.equals(other.getAmountLimit()))) &&
            this.transactionCountLimit == other.getTransactionCountLimit();
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
        if (getMatchedAmount() != null) {
            _hashCode += getMatchedAmount().hashCode();
        }
        _hashCode += getMatchedTransactionCount();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getAction() != null) {
            _hashCode += getAction().hashCode();
        }
        _hashCode += getPeriod();
        if (getIncludedTransactions() != null) {
            _hashCode += getIncludedTransactions().hashCode();
        }
        if (getAmountLimit() != null) {
            _hashCode += getAmountLimit().hashCode();
        }
        _hashCode += getTransactionCountLimit();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RiskRuleMatch.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "riskRuleMatch"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("matchedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "matchedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("matchedTransactionCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "matchedTransactionCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("action");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("period");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "period"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includedTransactions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "includedTransactions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amountLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "amountLimit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionCountLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "transactionCountLimit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
