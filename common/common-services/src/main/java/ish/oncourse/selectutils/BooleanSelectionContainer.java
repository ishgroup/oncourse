package ish.oncourse.selectutils;

import java.util.ArrayList;
import java.util.List;

public class BooleanSelectionContainer {
	public static final BooleanSelectionContainer YES = new BooleanSelectionContainer(Boolean.TRUE, "Yes");
	public static final BooleanSelectionContainer NO = new BooleanSelectionContainer(Boolean.FALSE, "No");
	public static final BooleanSelectionContainer NOT_STATED = new BooleanSelectionContainer(null, "Not stated");
	public static final String FIELD_LABEL = "label";
	
	private Boolean value;
	private String label;
	
	public BooleanSelectionContainer(Boolean value, String label) {
		this.value = value;
		this.label = label;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public static List<BooleanSelectionContainer> getPreparedList() {
		List<BooleanSelectionContainer> values = new ArrayList<BooleanSelectionContainer>();
		values.add(NOT_STATED);
		values.add(NO);
		values.add(YES);
		return values;
	}
	
	public static BooleanSelectionContainer getForBooleanValue(Boolean value) {
		if (value == null) {
			return NOT_STATED;
		} else if (Boolean.TRUE.equals(value)) {
			return YES;
		} else {
			return NO;
		}
	}
}
