/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.builders;

import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;

import org.apache.cayenne.Persistent;

/**
 * 
 * @author marek
 */
public interface IReferenceStubBuilder<T extends Persistent> {
	ReferenceStub convert(T record);
}
