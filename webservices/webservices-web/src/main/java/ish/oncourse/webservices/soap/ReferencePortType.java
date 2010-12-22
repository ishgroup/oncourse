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
 * <p>This data is sent to all customers.</p>
 *
 * <p>The procedure is as follows:</p>
 *
 * <ol>
 *		<li>Call @see #checkVersions() to get the latest Willow version number for each
 *			entity</li>
 *		<li>Verify that a newer version(s) exist and call
 *			get{Entity}(ishVersion) for each i++ such that i is
 *			AngelVersion < i <= WillowVersion</li>
 *		<li>Repeat the above for every Entity that needs updating.</li>
 * </ol>
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
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	List<Country_Stub> getCountries(Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	List<Language_Stub> getLanguages(Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	List<Module_Stub> getModules(Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	List<Qualification_Stub> getQualifications(Long ishVersion);

	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	List<TrainingPackage_Stub> getTrainingPackages(Long ishVersion);

}