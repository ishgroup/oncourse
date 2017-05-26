/**
 * ArrayOfRiskRuleMatch.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class ArrayOfRiskRuleMatch  implements java.io.Serializable {
    private com.paymentexpress.stubs.RiskRuleMatch[] riskRuleMatch;

    public ArrayOfRiskRuleMatch() {
    }

    public ArrayOfRiskRuleMatch(
           com.paymentexpress.stubs.RiskRuleMatch[] riskRuleMatch) {
           this.riskRuleMatch = riskRuleMatch;
    }


    /**
     * Gets the riskRuleMatch value for this ArrayOfRiskRuleMatch.
     * 
     * @return riskRuleMatch
     */
    public com.paymentexpress.stubs.RiskRuleMatch[] getRiskRuleMatch() {
        return riskRuleMatch;
    }


    /**
     * Sets the riskRuleMatch value for this ArrayOfRiskRuleMatch.
     * 
     * @param riskRuleMatch
     */
    public void setRiskRuleMatch(com.paymentexpress.stubs.RiskRuleMatch[] riskRuleMatch) {
        this.riskRuleMatch = riskRuleMatch;
    }

    public com.paymentexpress.stubs.RiskRuleMatch getRiskRuleMatch(int i) {
        return this.riskRuleMatch[i];
    }

    public void setRiskRuleMatch(int i, com.paymentexpress.stubs.RiskRuleMatch _value) {
        this.riskRuleMatch[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfRiskRuleMatch)) return false;
        ArrayOfRiskRuleMatch other = (ArrayOfRiskRuleMatch) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.riskRuleMatch==null && other.getRiskRuleMatch()==null) || 
             (this.riskRuleMatch!=null &&
              java.util.Arrays.equals(this.riskRuleMatch, other.getRiskRuleMatch())));
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
        if (getRiskRuleMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRiskRuleMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRiskRuleMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfRiskRuleMatch.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "ArrayOfRiskRuleMatch"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riskRuleMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "riskRuleMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "riskRuleMatch"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
