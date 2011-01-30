/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.services.builders;

import ish.oncourse.webservices.v4.stubs.reference.SoapReferenceStub;

import org.apache.cayenne.Persistent;

/**
 * 
 * @author marek
 */
public interface IStubBuilder {
	 SoapReferenceStub convert(Persistent record);
}
