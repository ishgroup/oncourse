/**
 * EnrolmentCheckResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.paymentexpress.stubs;

public class EnrolmentCheckResult  implements java.io.Serializable {
    private java.lang.String enrolled;

    private java.lang.String paReq;

    private java.lang.String acsURL;

    public EnrolmentCheckResult() {
    }

    public EnrolmentCheckResult(
           java.lang.String enrolled,
           java.lang.String paReq,
           java.lang.String acsURL) {
           this.enrolled = enrolled;
           this.paReq = paReq;
           this.acsURL = acsURL;
    }


    /**
     * Gets the enrolled value for this EnrolmentCheckResult.
     * 
     * @return enrolled
     */
    public java.lang.String getEnrolled() {
        return enrolled;
    }


    /**
     * Sets the enrolled value for this EnrolmentCheckResult.
     * 
     * @param enrolled
     */
    public void setEnrolled(java.lang.String enrolled) {
        this.enrolled = enrolled;
    }


    /**
     * Gets the paReq value for this EnrolmentCheckResult.
     * 
     * @return paReq
     */
    public java.lang.String getPaReq() {
        return paReq;
    }


    /**
     * Sets the paReq value for this EnrolmentCheckResult.
     * 
     * @param paReq
     */
    public void setPaReq(java.lang.String paReq) {
        this.paReq = paReq;
    }


    /**
     * Gets the acsURL value for this EnrolmentCheckResult.
     * 
     * @return acsURL
     */
    public java.lang.String getAcsURL() {
        return acsURL;
    }


    /**
     * Sets the acsURL value for this EnrolmentCheckResult.
     * 
     * @param acsURL
     */
    public void setAcsURL(java.lang.String acsURL) {
        this.acsURL = acsURL;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EnrolmentCheckResult)) return false;
        EnrolmentCheckResult other = (EnrolmentCheckResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.enrolled==null && other.getEnrolled()==null) || 
             (this.enrolled!=null &&
              this.enrolled.equals(other.getEnrolled()))) &&
            ((this.paReq==null && other.getPaReq()==null) || 
             (this.paReq!=null &&
              this.paReq.equals(other.getPaReq()))) &&
            ((this.acsURL==null && other.getAcsURL()==null) || 
             (this.acsURL!=null &&
              this.acsURL.equals(other.getAcsURL())));
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
        if (getEnrolled() != null) {
            _hashCode += getEnrolled().hashCode();
        }
        if (getPaReq() != null) {
            _hashCode += getPaReq().hashCode();
        }
        if (getAcsURL() != null) {
            _hashCode += getAcsURL().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EnrolmentCheckResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://PaymentExpress.com", "EnrolmentCheckResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enrolled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "enrolled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "paReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://PaymentExpress.com", "acsURL"));
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
