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
package ish.oncourse.server.cayenne

import ish.common.types.DataType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._CustomFieldType

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.Date

/**
 * A definition of a custom field which can be used to extend the Contact object.
 */
@API
@QueueableEntity
class CustomFieldType extends _CustomFieldType implements Queueable {



	/**
	 * @return the date and time this record was created
	 */
	@Nonnull @API @Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * When a new records is created, the custom field can be populated with a default value
	 *
	 * @return default value for this custom field type
	 */
	@Nullable @API @Override
	String getDefaultValue() {
		return super.getDefaultValue()
	}

	/**
	 * @return if true, the custom field must be not null and not empty in order to save the Contact record
	 */
	@Nonnull @API @Override
	Boolean getIsMandatory() {
		return super.getIsMandatory()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull @API @Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * Name of the custom field, mandatory, can be changed
	 *
	 * @return the human readable name of the field
	 */
	@Nonnull @API @Override
	String getName() {
		return super.getName()
	}

	/**
	 * Key, by which custom field may be referenced (for example in groovy DSL). Once set, cannot be changed, unlike Name
	 * E.g. for getting value from contact custom field "Passport number" with key "passNum" following expression can be
	 * used: 'contact.passNum' as alternative to 'contact.customField("Passport Number")'
	 *
	 * @return set fieldKey value
	 */
	@Nonnull @API @Override
	String getKey() {
		return super.getKey()
	}

    @Override
	void onEntityCreation() {
	    if (getSortOrder() == null) {
	        setSortOrder(0L)
		}
        super.onEntityCreation()
	}

	/**
	 * Custom fields can have a data type to constrain the data which can be stored. This cannot be changed once the field has been created.
	 *
	 * @return
	 */
	@Nonnull @API @Override
	DataType getDataType() {
		return super.getDataType()
	}

	/**
	 * The entity (eg. Contact, Enrolment) to which this custom field type is bound.
	 * @return
	 */
	@Nonnull @API @Override
	String getEntityIdentifier() {
		return super.getEntityIdentifier()
	}

	/**
	 * The entity (eg. Contact, Enrolment) to which this custom field type is bound.
	 * However, in case when entity is 'Article' we will change to 'Product' for appropriate value of the enumeration
	 * @return
	 */
	@Nonnull
	String getEntityIdentifierExtended() {
		return entityIdentifier == 'Article' ? 'Product' : entityIdentifier
	}

	/**
	 * Set value of entity (eg. Contact, Enrolment) to which this custom field type is bound.
	 * However, in case when entity is 'Product' we will change to 'Article' for correct next working.
	 */
	@Nonnull
	void setEntityIdentifierExtended(String value) {
		entityIdentifier = value == 'Product' ? 'Article' : value
	}
}
