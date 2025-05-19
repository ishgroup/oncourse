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

package ish.oncourse.server.cayenne.glue;

import ish.math.Money;
import ish.oncourse.GoogleGuiceInjector;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.util.MapsUtil;
import ish.validation.ValidationFailure;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.PersistentObject;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This is the superclass to be used by all of our entities on the server.
 * <p>
 * At this point, the main purpose of this class is to hook into the Cayenne framework to automatically create replication queue records.
 * </p>
 * <p>
 * I anticipate that in the future there may be additional functionality added here.
 * </p>
 */
public abstract class CayenneDataObject extends org.apache.cayenne.CayenneDataObject implements PersistentObjectI {

	/**
	 *
	 */


	private static final Logger logger = LogManager.getLogger();

	public static final String ID = "id";
	/**
	 * default property name
	 */
	public static final String CREATED_ON = "createdOn";
	/**
	 * default property name
	 */
	public static final String MODIFIED_ON = "modifiedOn";
	/**
	 * default property name
	 */
	public static final String THIS_OBJECT = "this_object";

	public static final String CREATED_ON_PROPERTY = "createdOn";

	public static final String MODIFIED_ON_PROPERTY = "modifiedOn";

	private String temporaryKey = null;

	/**
	 * @return true if record can be deleted from database
	 */
	public boolean canDeleteRecord() {
		return true;
	}

	/**
	 * @return object pk as Number
	 */
	public Number getPrimaryKeyValue() {
		return (Number) Cayenne.pkForObject(this);
	}

	/**
	 * @return the primary key map for this object
	 * @see Cayenne#compoundPKForObject(org.apache.cayenne.Persistent)
	 */
	public Map<String, Object> getPrimaryKeyMap() {
		return Cayenne.compoundPKForObject(this);
	}

	/**
	 * @return true if object is new
	 */
	public boolean isNewRecord() {
		return getPersistenceState() == PersistenceState.NEW;
	}

	/**
	 * method fired "Within "ObjectContext.newObject()" after ObjectId and ObjectContext are set."
	 *
	 * @see org.apache.cayenne.LifecycleListener#prePersist(Object)
	 */
	protected void postAdd() {
		logger.entry(getClass().getSimpleName());

		if (isNewRecord()) {
			this.temporaryKey = getObjectId().toString();
		}
		onEntityCreation();
	}

	protected void prePersist() {
		logger.entry(getClass().getSimpleName());

		if (isNewRecord()) {
			this.temporaryKey = getObjectId().toString();
		}
	}

	/**
	 * Before an object is deleted inside "ObjectContext.deleteObjects()"; also includes all objects that will be deleted as a result of CASCADE delete rule.
	 *
	 * @see org.apache.cayenne.LifecycleListener#preRemove(Object)
	 */
	protected void preRemove() {
		logger.entry(getClass().getSimpleName());

		if (!canDeleteRecord() && !isNewRecord()) {
			IllegalStateException e;

			e = new IllegalStateException(Cayenne.getObjEntity(this).getDbEntityName() + " records cannot be deleted.");
			logger.catching(e);
			throw e;
		}
	}

	/**
	 * Prior to commit (and prior to "validateFor*") within "ObjectContext.commitChanges()" and "ObjectContext.commitChangesToParent()"
	 *
	 * @see org.apache.cayenne.LifecycleListener#preUpdate(Object)
	 */
	protected void preUpdate() {
		logger.entry(getClass().getSimpleName());

		onEntityModification();
	}

	/**
	 * Within "ObjectContext.commitChanges()", after commit of a deleted object is done.
	 *
	 * @see org.apache.cayenne.LifecycleListener#postRemove(Object)
	 */
	protected void postRemove() {
		logger.entry(getClass().getSimpleName());

	}

	/**
	 * Within "ObjectContext.commitChanges()", after commit of a modified object is done.
	 *
	 * @see org.apache.cayenne.LifecycleListener#postUpdate(Object)
	 */
	protected void postUpdate() {
		logger.entry(getClass().getSimpleName());

	}

	/**
	 * <ul>
	 * <li>Within "ObjectContext.performQuery()" after the object is fetched.</li>
	 * <li>Within "ObjectContext.rollbackChanges()" after the object is reverted.</li>
	 * <li>Anytime a faulted object is resolved (i.e. if a relationship is fetched.</li>
	 * </ul>
	 *
	 * @see org.apache.cayenne.LifecycleListener#postLoad(Object)
	 */
	protected void postLoad() {
		logger.entry(getClass().getSimpleName());

	}

	/**
	 * Within "ObjectContext.commitChanges()", after commit of a new object is done.
	 *
	 * @see org.apache.cayenne.LifecycleListener#postPersist(Object)
	 */
	protected void postPersist() {
		logger.entry(getClass().getSimpleName());

	}

	/**
	 * fired by the lifcycle listener whenever a new record is created
	 */
	public void onEntityCreation() {
		logger.entry(getClass().getSimpleName());

		final var date = new Date();
		try {
			if (hasAttribute(_Course.CREATED_ON_PROPERTY) && readProperty(_Course.CREATED_ON_PROPERTY) == null) {
				writeProperty(_Course.CREATED_ON_PROPERTY, date);
			}
			if (hasAttribute(_Course.MODIFIED_ON_PROPERTY) && readProperty(_Course.MODIFIED_ON_PROPERTY) == null) {
				writeProperty(_Course.MODIFIED_ON_PROPERTY, date);
			}

		} catch (final Exception e) {
			logger.error("On entity creation failed. ({})", getClass().getSimpleName(), e);
		}
		onEntityModification();
	}

	/**
	 * fired by the lifcycle listener whenever a new record is modified
	 */
	protected void onEntityModification() {
		try {
			if (hasAttribute(_Course.MODIFIED_ON_PROPERTY)) {
				writeProperty(_Course.MODIFIED_ON_PROPERTY, new Date());
			}
		} catch (final Exception e) {

		}
	}

	/**
	 * @see org.apache.cayenne.CayenneDataObject#validateForSave(ValidationResult)
	 */
	@Override
	public void validateForSave(final ValidationResult validationResult) {
		super.validateForSave(validationResult);
	}

	/**
	 * @see org.apache.cayenne.CayenneDataObject#validateForDelete(ValidationResult)
	 */
	@Override
	public void validateForDelete(final ValidationResult validationResult) {
		super.validateForDelete(validationResult);
	}

	/**
	 * @return name of the entity
	 */
	public String getEntityName() {
		return getClass().getSimpleName();
	}

	/**
	 * @param key
	 * @return true if the entity defines the key
	 */
	public boolean hasAttribute(final String key) {
		for (final Object at : Cayenne.getObjEntity(this).getAttributes()) {
			if (((Attribute) at).getName().equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks for PersistentObject equability ignoring the context
	 *
	 * @param obj
	 * @return
	 */
	public boolean equalsIgnoreContext(Object obj) {
		if (obj instanceof CayenneDataObject) {
			return super.equals(obj) || getObjectId().equals(((CayenneDataObject) obj).getObjectId());
		}
		return false;
	}

	public void validateRelatedObject(String propertyKey, ValidationResult result) {
		var objectToValidate = readProperty(propertyKey);
		if (objectToValidate != null && objectToValidate instanceof CayenneDataObject) {
			((CayenneDataObject) objectToValidate).validateForSave(result);

			// we need to add additional validation exceptions with whole path:
			for (var vf : result.getFailures(objectToValidate)) {
				if (vf instanceof ValidationFailure) {
					var localvf = (ValidationFailure) vf;
					result.addFailure(ValidationFailure.validationFailure(this, propertyKey + "." + localvf.getProperty(), localvf.getError()));
				}
			}
		}
	}

	/**
	 * Gets data type for given key.
	 *
	 * @return Class - data type for given key
	 * @param key - Property String
	 */
	public static Class<?> getDataTypeForKey(String key) {
		if (key == null) {
			logger.warn("some component has property key not set, but yet asking for data type !");
			return null;
		}
		if (key.indexOf(".") > 0) {
			var className = "ish.oncourse.server.cayenne." + key.substring(0, 1).toUpperCase() + key.substring(1, key.indexOf("."));
			try {
				var entityClass = Class.forName(className);
				var m = entityClass.getMethod("getDataTypeForKey", String.class);
				return (Class<?>) m.invoke(entityClass, key.substring(1 + key.indexOf(".")));
			} catch (Exception e) {
				logger.error("Error while executing getDataTypeForKey for entity class: {}", className, e);
			}
			return null;
		}
		return null;
	}

	public static final String defaultCacheKeyForEntity(Class<? extends PersistentObjectI> c) {
		return c.getName().replace('.', '_');
	}

	public final String defaultCacheKeyForEntity() {
		return CayenneDataObject.defaultCacheKeyForEntity(this.getClass());
	}

	// only here for compatibility purposes
	public Expression getExpressionForKey(String key) {
		return null;
	}

	// only here for compatibility purposes
	public Color getSpecialColorForTableBackground() {
		return null;
	}

	// only here for compatibility purposes
	public Color getSpecialColorForTableForeground() {
		return null;
	}

	// only here for compatibility purposes
	public String getValidationTooltip() {
		return null;
	}

	/**
	 * Convenience method to check if the object persistence state is modified
	 *
	 * @return boolean
	 */
	public boolean isModifiedRecord() {
		return getPersistenceState() == PersistenceState.MODIFIED;
	}

	public boolean isNewAndEdited() {
		throw new RuntimeException("not implemented");
	}

	/**
	 * Convenience method to check if the object passes validation
	 *
	 * @return boolean
	 */
	public boolean isValidRecord() {
		var result = new ValidationResult();
		validateForSave(result);
		return !result.hasFailures();
	}

	public void nullifyRelationships() {
		throw new RuntimeException("not implemented");
	}

	public void removeValueForKey(String key, Object value) {
		throw new RuntimeException("should be intercepted by subclass, if not consider implementing this method as in client");
	}

	public void addValueForKey(String key, Object value) {
		throw new RuntimeException("should be intercepted by subclass, if not consider implementing this method as in client");
	}

	public void setIsNewAndEdited(boolean isNewAndEdited) {
		throw new RuntimeException("not implemented");
	}

	public void setValueForKey(String key, Object value) {
		writeProperty(key, value);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		throw new RuntimeException("not implemented");
	}

	public DataFlavor[] getTransferDataFlavors() {
		throw new RuntimeException("not implemented");
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		throw new RuntimeException("not implemented");
	}

	public Object getValueForKey(String key) {
		return readProperty(key);
	}

	public void setExported() {
		// nothing by default
	}

	/**
	 * @see PersistentObjectI#equalsIgnoreContext(PersistentObjectI)
	 */
	/**
	 * checks for PersistentObject equality (even if in another context)
	 *
	 * @param obj a persistent object to compare to
	 * @return true if the same object
	 */
	public boolean equalsIgnoreContext(PersistentObjectI obj) {
		if (obj instanceof PersistentObject) {
			return super.equals(obj) || getObjectId().equals(obj.getObjectId());
		}
		return false;
	}

	/**
	 * @see PersistentObjectI#getContext()
	 */
	public ObjectContext getContext() {
		return getObjectContext();
	}

	public CayenneDataObject detectDuplicate(String[] properties) {
		return detectDuplicate(properties, null);
	}

	public CayenneDataObject detectDuplicate(String[] properties, boolean[] nullIsAMatch) {
		return detectDuplicate(properties, nullIsAMatch, false);
	}

	public CayenneDataObject detectDuplicate(String[] properties, boolean[] nullIsAMatch, boolean caseInsensitive) {
		if (properties == null) {
			throw new IllegalArgumentException("null properties specified");
		}

		if (nullIsAMatch != null && properties.length != nullIsAMatch.length) {
			throw new IllegalArgumentException("lenght of two array parameters must be equal");
		}

		// if the object is going to be deleted then dont worry about it.
		if (PersistenceState.DELETED == getPersistenceState() || PersistenceState.TRANSIENT == getPersistenceState()) {
			return null;
		}

		List<PersistentObject> similarObjectsInContext = new ArrayList<>();
		// first check for duplicates in context, maybe it is not worth executing query
		for (var o : getObjectContext().getGraphManager().registeredNodes()) {
			if (o instanceof CayenneDataObject) {
				var po = (CayenneDataObject) o;
				// check if the class matches
				if (po.getClass() == this.getClass()) {
					// check if this is not the current object
					if (!equals(po)) {
						// check if the object is not deleted
						if (!(PersistenceState.DELETED == po.getPersistenceState() || PersistenceState.TRANSIENT == po.getPersistenceState())) {
							var propertiesMatch = true;
							for (var i = 0; i < properties.length; i++) {
								var property = properties[i];
								var temp = false;

								if (nullIsAMatch != null && nullIsAMatch[i] && getValueForKey(property) == null) {
									temp = po.getValueForKey(property) == null;
								} else if (caseInsensitive && String.class.equals(getDataTypeForKey(property)) && po.getValueForKey(property) != null) {
									temp = getValueForKey(property).toString().equalsIgnoreCase(po.getValueForKey(property).toString());
								} else if (getValueForKey(property) != null) {
									temp = getValueForKey(property).equals(po.getValueForKey(property));
								}

								propertiesMatch = propertiesMatch && temp;
							}
							if (propertiesMatch) {
								return po;
							}
						}
						similarObjectsInContext.add(po);
					}
				}
			}
		}
		// now assembly query and check for duplicates in db
		Expression expr = null;
		for (var i = 0; i < properties.length; i++) {
			var property = properties[i];

			var hasValue = getValueForKey(property) != null;
			var matchingNull = nullIsAMatch != null && nullIsAMatch[i];
			if (hasValue || matchingNull) {
				Expression anExpression;
				if (hasValue && caseInsensitive && String.class.equals(getDataTypeForKey(property))) {
					anExpression = ExpressionFactory.likeIgnoreCaseExp(property, getValueForKey(property));
				} else {
					anExpression = ExpressionFactory.matchExp(property, getValueForKey(property));
				}
				expr = expr == null ? anExpression : expr.andExp(anExpression);
			}
		}
		// NOTE: using local context to allow easy object comparison
		var list = getObjectContext().select(SelectQuery.query(this.getClass(), expr));
		// if empty result there is nothing in the db
		if (list == null || list.size() == 0) {
			return null;
		}

		// we need to find a reason for exclude all objects returned by query
		for (CayenneDataObject po : list) {
			if (!po.equalsIgnoreContext(this)) {
				if (similarObjectsInContext.indexOf(po) < 0) {
					// the object is not in context, duplicate:
					return po;
				}
				// the in-context validation did not fail, assume that there is no duplicate
			}
		}
		return null;
	}

	/**
	 * A convenience method to allow to get a key mapped to a value in a Map
	 *
	 * @param value
	 * @param map
	 * @return the related key
	 */
	public static Object getKeyForValue(Object value, Map<?, ?> map) {
		return MapsUtil.getKeyForValue(value, map);
	}

	@Override
	public void setObjectContext(ObjectContext objectContext) {
		super.setObjectContext(objectContext);
		if (GoogleGuiceInjector.getInstance()  != null) {
			GoogleGuiceInjector.getInstance().injectMembers(this);
		}
	}

	public boolean isAuditAllowed() {
		return true;
	}

	public String getSummaryDescription() {
		if (values.containsKey("name"))
			return readProperty("name").toString();
		else
			return ID + ' ' + getPrimaryKeyValue();
	}


	@Override
	public Object readProperty(String propertyName) {
		var object = super.readProperty(propertyName);
		if(object instanceof Money)
			return Money.of(((Money)object).toBigDecimal());

		return object;
	}
}
