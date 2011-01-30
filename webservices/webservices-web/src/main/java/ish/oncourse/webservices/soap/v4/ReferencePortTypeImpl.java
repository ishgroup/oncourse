package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.services.builders.IStubBuilder;
import ish.oncourse.webservices.services.reference.IReferenceServiceComposite;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v4.stubs.reference.SoapReferenceStub;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * The ReferencePortType defines the API for one-way (Willow to Angel)
 * replication of Reference Data.
 *
 * <p>Reference Data consist of the following Entities:</p>
 *
 * <ul>
 *		<li>@see ish.oncourse.model.Country</li>
 *		<li>@see ish.oncourse.model.Language</li>
 *		<li>@see ish.oncourse.model.Module</li>
 *		<li>@see ish.oncourse.model.Qualification</li>
 *		<li>@see ish.oncourse.model.TrainingPackage</li>
 * </ul>
 *
 * <p>This data is sent to all customers and thus requires that the service is
 * authenticated for tracking purposes (IP/security code) but does not require
 * web services to be setup for the client.</p>
 *
 * <p>The procedure is as follows:</p>
 *
 * <ol>
 *		<li>Call @see #getMaximumVersion() to get the latest Willow version
 *			number</li>
 *		<li>Loop through the missing versions, starting with the lowest
 *			through to the highest.<br/><strong>Remember to save the received
 *			lists between subsequent calls.</strong>
 *		</li>
 * </ol>
 *
 * Please note that the get calls return the <b>entire</b> set of records for
 * that version.
 *
 * @author Marek Wawrzyczny
 */
@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReferencePortType",
			serviceName = "ReferenceService",
			portName = "ReferencePort", targetNamespace="http://ref.v4.soap.webservices.oncourse.ish/")
public class ReferencePortTypeImpl implements ReferencePortType {

	@Inject
	@Autowired
	private IReferenceServiceComposite referenceService;
	
	@Inject
	@Autowired
	private IStubBuilder stubBuilder;
	
	/**
	 * Call to find out the most recent version of Reference Data - note that
	 * this is shared across tables.
	 *
	 * @return Map the max version
	 */
	@Override
	public long getMaximumVersion() {
		return referenceService.findMaxIshVersion();
	}
	
	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@Override
	public ReferenceResult getRecords(long ishVersion) {

		List<SoapReferenceStub> stubs = new ArrayList<SoapReferenceStub>();
		List<Persistent> records = referenceService.getForReplication(ishVersion);
		
		for (Persistent p: records) {
			stubs.add(stubBuilder.convert(p));
		}
		
		ReferenceResult result = new ReferenceResult();
		result.getCountryOrLanguageOrModule().addAll(stubs);

		return result;
	}

}
