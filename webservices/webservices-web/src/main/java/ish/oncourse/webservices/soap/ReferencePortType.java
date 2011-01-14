package ish.oncourse.webservices.soap;


import java.util.HashMap;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.jws.WebService;

import ish.oncourse.webservices.soap.stubs.reference.SoapReference_Stub;

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
 *		<<li>Loop through the missing versions, starting with the lowest
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
@WebService
public interface ReferencePortType {

	/**
	 * Call to find out the most recent version of Reference Data - note that
	 * this is shared across tables.
	 *
	 * @return Map the max version
	 */
	@WebMethod(operationName = "getMaximumVersion")
	Long getMaximumVersion();

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@WebMethod(operationName = "getRecords")
	List<SoapReference_Stub> getRecords(
			@WebParam(name = "ishVersion") Long ishVersion);

}