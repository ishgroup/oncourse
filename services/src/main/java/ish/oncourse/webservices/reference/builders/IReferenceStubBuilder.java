/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.reference.builders;

import ish.oncourse.webservices.util.GenericReferenceStub;
import org.apache.cayenne.Persistent;

/**
 * 
 * @author marek
 */
public interface IReferenceStubBuilder<T extends Persistent> {
	GenericReferenceStub convert(T record);
}
