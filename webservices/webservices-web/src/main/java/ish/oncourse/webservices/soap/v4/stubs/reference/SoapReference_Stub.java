/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.v4.stubs.reference;

import javax.xml.bind.annotation.XmlSeeAlso;



/**
 *
 * @author marek
 */
 @XmlSeeAlso({
	Country_Stub.class,
	Language_Stub.class,
	Module_Stub.class,
	Qualification_Stub.class,
	TrainingPackage_Stub.class
 })
public abstract class SoapReference_Stub {

	private Long willowId;

	
	public Long getWillowId() {
		return willowId;
	}

	public void setWillowId(Long willowId) {
		this.willowId = willowId;
	}
}
