package ish.oncourse.webservices.soap.v4;

import ish.oncourse.services.reference.ReferenceService;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.util.GenericReferenceStub;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import org.apache.cayenne.Persistent;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;


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
@EndpointProperty(key = "soap.no.validate.parts", value = "true")
@Deprecated
public class ReferencePortTypeImpl implements ReferencePortType {

	@Inject
	@Autowired
	private ReferenceService referenceService;
	
	@Inject
	@Autowired
	private ReferenceStubBuilder stubBuilder;

	private static final Logger LOGGER = Logger.getLogger(ReferencePortTypeImpl.class);

	
	/**
	 * Call to find out the most recent version of Reference Data - note that
	 * this is shared across tables.
	 *
	 * @return Map the max version
	 */
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
	public ReferenceResult getRecords(long ishVersion) {

		List<GenericReferenceStub> stubs = new ArrayList<GenericReferenceStub>();
		
		List<Persistent> records = referenceService.getForReplication(ishVersion);
		
		for (Persistent p: records) {
			try {
				GenericReferenceStub stub = stubBuilder.convert(p);
				stubs.add(stub);
			} catch (BuilderNotFoundException e) {
				LOGGER.error("Exception while converting records to stubs", e);
				// Return HTTP 500 code - internal server error
				throw new WebApplicationException();
			}
		}
		
		ReferenceResult result = new ReferenceResult();
		result.getGenericCountryOrLanguageOrModule().addAll(stubs);
		return result;
	}

}
