/**
 * ArrayOfNameValueField.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class ArrayOfNameValueField  implements java.io.Serializable {
    private com.paymentexpress.stubs.NameValueField[] nameValueField;

    public ArrayOfNameValueField() {
    }

    public ArrayOfNameValueField(
           com.paymentexpress.stubs.NameValueField[] nameValueField) {
           this.nameValueField = nameValueField;
    }


    /**
     * Gets the nameValueField value for this ArrayOfNameValueField.
     * 
     * @return nameValueField
     */
    public com.paymentexpress.stubs.NameValueField[] getNameValueField() {
        return nameValueField;
    }


    /**
     * Sets the nameValueField value for this ArrayOfNameValueField.
     * 
     * @param nameValueField
     */
    public void setNameValueField(com.paymentexpress.stubs.NameValueField[] nameValueField) {
        this.nameValueField = nameValueField;
    }

    public com.paymentexpress.stubs.NameValueField getNameValueField(int i) {
        return this.nameValueField[i];
    }

    public void setNameValueField(int i, com.paymentexpress.stubs.NameValueField _value) {
        this.nameValueField[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfNameValueField)) return false;
        ArrayOfNameValueField other = (ArrayOfNameValueField) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nameValueField==null && other.getNameValueField()==null) || 
             (this.nameValueField!=null &&
              java.util.Arrays.equals(this.nameValueField, other.getNameValueField())));
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
        if (getNameValueField() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNameValueField());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNameValueField(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfNameValueField.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "ArrayOfNameValueField"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameValueField");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "NameValueField"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "NameValueField"));
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
