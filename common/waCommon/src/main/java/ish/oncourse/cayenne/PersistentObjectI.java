/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.validation.ValidationResult;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * @author marcin
 */
public interface PersistentObjectI extends Persistent, Transferable {

	public static final DataFlavor DATA_FLAVOUR = new DataFlavor(PersistentObjectI.class, "PersistentObjectI");

	public int getPersistenceState();

	public Object getValueForKey(String key);

	public void setValueForKey(String key, Object value);

	public void removeValueForKey(String key, Object value);

	public void addValueForKey(String key, Object value);

	public Expression getExpressionForKey(String key);

	public void validateForSave(ValidationResult validationResult);

	public boolean isValidRecord();

	public boolean isNewAndEdited();

	public void setIsNewAndEdited(boolean isNewAndEdited);

	public boolean isModifiedRecord();

	public boolean isNewRecord();

	public void nullifyRelationships();

	public String defaultCacheKeyForEntity();

	public ObjectContext getContext();

	public Color getSpecialColorForTableBackground();

	public Color getSpecialColorForTableForeground();

	public String getValidationTooltip();

	public boolean equalsIgnoreContext(PersistentObjectI obj);

}
