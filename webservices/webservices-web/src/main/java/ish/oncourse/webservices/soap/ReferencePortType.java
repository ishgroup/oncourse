package ish.oncourse.webservices.soap;


import java.util.HashMap;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.jws.WebService;

import ish.oncourse.webservices.soap.stubs.reference.Country_Stub;
import ish.oncourse.webservices.soap.stubs.reference.Language_Stub;
import ish.oncourse.webservices.soap.stubs.reference.Module_Stub;
import ish.oncourse.webservices.soap.stubs.reference.Qualification_Stub;
import ish.oncourse.webservices.soap.stubs.reference.TrainingPackage_Stub;

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
 *		<li>Call @see #checkVersions() to get the latest Willow version number 
 *			for each entity</li>
 *		<li>For each entity, compare the highest local version number with the 
 *			version sent by Willow and if Willow's version is higher then:</li>
 *			<ol>
 *				<li>Loop through the missing versions, starting with the lowest
 *					through to the highest.<br/><strong>Remember to save the
 *					received set between subsequent calls.</strong></li>
 *			</ol>
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
	 * Call to find out the most recent version of each of the replicable
	 * entities.
	 *
	 * @return Map containing the Entity name as key and the version as value
	 */
	@WebMethod(operationName = "checkVersions")
	HashMap<String, Long> checkVersions();

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@WebMethod(operationName = "getCountries")
	List<Country_Stub> getCountries(
			@WebParam(name = "ishVersion") Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@WebMethod(operationName = "getLanguages")
	List<Language_Stub> getLanguages(
			@WebParam(name = "ishVersion") Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@WebMethod(operationName = "getModules")
	List<Module_Stub> getModules(
			@WebParam(name = "ishVersion") Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@WebMethod(operationName = "getQualifications")
	List<Qualification_Stub> getQualifications(
			@WebParam(name = "ishVersion") Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@WebMethod(operationName = "getTrainingPackages")
	List<TrainingPackage_Stub> getTrainingPackages(
			@WebParam(name = "ishVersion") Long ishVersion);

}