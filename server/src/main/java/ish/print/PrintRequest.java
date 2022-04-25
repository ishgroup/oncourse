/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.print;

import ish.oncourse.cayenne.PersistentObjectI;
import ish.print.transformations.PrintTransformation;
import org.apache.cayenne.Cayenne;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.*;

/**
 */
public class PrintRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String reportCode;

	/** map of enityname-list of id's */
	private Map<String, List<Long>> ids = new HashMap<>();

	/** map of enityname-print transformations, matching the ids */
	private Map<String, PrintTransformation> printTransformations = new HashMap<>();

	private String background;

	private boolean createPreview = false;

	private Map<String, Object> additionalParameters = new HashMap<>();

	private Map<String, Object> bindings = new HashMap<>();

	private UID uid = new UID();

	public boolean isCreatePreview() {
		return createPreview;
	}

	public void setCreatePreview(boolean savePreview) {
		this.createPreview = savePreview;
	}

	/**
	 * @return the reportCode
	 */
	public String getReportCode() {
		return this.reportCode;
	}

	/**
	 * @param reportCode the reportCode to set
	 */
	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	/**
	 * @return the ids
	 */
	public Map<String, List<Long>> getIds() {
		return Collections.unmodifiableMap(ids);
	}

	/**
	 * sets the map of ids for the print request
	 * @param ids
	 */
	public void setIds(Map<String, List<Long>> ids) {
		this.ids = ids;
	}

	/**
	 * sets a list of records to print, in detail it splits the records into a map entityType-listOfThatType
	 *
	 * @param records
	 */
	public <T extends PersistentObjectI> void setRecords(List<T> records) {

		for (T record : records) {

			String entityKey = record.getObjectId().getEntityName();

			List<Long> idList = ids.get(record.getObjectId().getEntityName());

			if (idList == null) {
				idList = new ArrayList<>();
				ids.put(entityKey, idList);
			}

			idList.add(Cayenne.longPKForObject(record));
		}
	}

	/**
	 * @return the background
	 */
	public String getBackground() {
		return background;
	}

	/**
	 * @param background the background for report
	 */
	public void setBackground(String background) {
		this.background = background;
	}

	public UID getUID() {
		return this.uid;
	}

	/**
	 *
	 * @param key
	 * @return single value associated with param key
	 */
	public Object getValueForKey(String key) {
		return additionalParameters.get(key);
	}


	public void addParameters(Map<String, Object> additionalParameters) {
		this.additionalParameters.putAll(additionalParameters);
	}
	/**
	 * adds a genric key-value mapping to the request, parameters added this way will be available to the JasperFillManager
	 * @param key
	 * @param value
	 */
	public void setValueForKey(String key, Object value) {
		this.additionalParameters.put(key, value);
	}

	/**
	 *
	 * @return map of additional parameters used with this print request
	 */
	public Map<String, Object> getAdditionalParameters() {
		return Collections.unmodifiableMap(additionalParameters);
	}


	public void addBindings(Map<String, Object> additionalParameters) {
		this.bindings.putAll(additionalParameters);
	}

	public Map<String, Object> getBindings() {
		return bindings;
	}

	/**
	 *
	 * @param inputEntity
	 * @return print transformation for the given entity
	 */
	public PrintTransformation getPrintTransformation(String inputEntity) {
		return printTransformations.get(inputEntity);
	}

	/**
	 * adds a transformation to be used to translate the source records into the records used by the report.
	 * @param inputEntity  given source entity to use
	 * @param transformation   transformations to use
	 */
	public void addPrintTransformation(String inputEntity, PrintTransformation transformation) {
		printTransformations.put(inputEntity, transformation);
	}

	/**
	 *
	 * @param inputEntity
	 * @return boolean if printTransformations has transformation for this entity name
	 */
	public boolean containsTransformationFor(String inputEntity){
		return printTransformations.containsKey(inputEntity);
	}

	@Override
	public String toString() {
		return "PrintRequest{" +
				"reportCode='" + reportCode + '\'' +
				", ids=" + ids +
				", additionalParameters=" + additionalParameters +
				", printTransformations=" + printTransformations +
				", uid=" + uid +
				'}';
	}
}
