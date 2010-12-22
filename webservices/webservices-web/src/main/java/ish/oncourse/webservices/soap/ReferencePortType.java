package ish.oncourse.webservices.soap;


import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;

import ish.oncourse.webservices.soap.stubs.Country_Stub;
import ish.oncourse.webservices.soap.stubs.Language_Stub;
import ish.oncourse.webservices.soap.stubs.Module_Stub;
import ish.oncourse.webservices.soap.stubs.Qualification_Stub;
import ish.oncourse.webservices.soap.stubs.TrainingPackage_Stub;

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
 * This data is sent to all customers.
 *
 *
 * @author Marek Wawrzyczny
 */
@WebService
public interface ReferencePortType {

	/**
	 * Call to find out the most recent version of each of the replicable
	 * entities.
	 *
	 * @return Map containing the Entity name as key and the version as value
	 */
	HashMap<String, Long> checkVersions();

	/**
	 * Call to get the next batch of records from the queue.
	 *
	 * <p>An empty list is returned when there are no more records to
	 * replicate.</p>
	 *
	 * <p><i>Please ensure that the angelVersion is the last successfully
	 * replicated version in Angel <b>and not the current max version</b>!
	 * </i></p>
	 *
	 * @param angelVersion - the last successful replicated version on Angel
	 * @param batchNumber  - the batch being requested
	 *
	 * @return
	 */
	List<Country_Stub> getCountries(Long angelVersion, Integer batchNumber);

	/**
	 * Call to get the next batch of records from the queue.
	 *
	 * <p>An empty list is returned when there are no more records to
	 * replicate.</p>
	 *
	 * <p><i>Please ensure that the angelVersion is the last successfully
	 * replicated version in Angel <b>and not the current max version</b>!
	 * </i></p>
	 *
	 * @param angelVersion - the last successful replicated version on Angel
	 * @param batchNumber  - the batch being requested
	 *
	 * @return
	 */
	List<Language_Stub> getLanguages(Long angelVersion, Integer batchNumber);

	/**
	 * Call to get the next batch of records from the queue.
	 *
	 * <p>An empty list is returned when there are no more records to
	 * replicate.</p>
	 *
	 * <p><i>Please ensure that the angelVersion is the last successfully
	 * replicated version in Angel <b>and not the current max version</b>!
	 * </i></p>
	 *
	 * @param angelVersion - the last successful replicated version on Angel
	 * @param batchNumber  - the batch being requested
	 *
	 * @return
	 */
	List<Module_Stub> getModules(Long angelVersion, Integer batchNumber);

	/**
	 * Call to get the next batch of records from the queue.
	 *
	 * <p>An empty list is returned when there are no more records to
	 * replicate.</p>
	 *
	 * <p><i>Please ensure that the angelVersion is the last successfully
	 * replicated version in Angel <b>and not the current max version</b>!
	 * </i></p>
	 *
	 * @param angelVersion - the last successful replicated version on Angel
	 * @param batchNumber  - the batch being requested
	 *
	 * @return
	 */
	List<Qualification_Stub> getQualifications(Long angelVersion, Integer batchNumber);

	/**
	 * Call to get the next batch of records from the queue.
	 *
	 * <p>An empty list is returned when there are no more records to
	 * replicate.</p>
	 *
	 * <p><i>Please ensure that the angelVersion is the last successfully
	 * replicated version in Angel <b>and not the current max version</b>!
	 * </i></p>
	 *
	 * @param angelVersion - the last successful replicated version on Angel
	 * @param batchNumber  - the batch being requested
	 *
	 * @return
	 */
	List<TrainingPackage_Stub> getTrainingPackages(Long angelVersion, Integer batchNumber);

}