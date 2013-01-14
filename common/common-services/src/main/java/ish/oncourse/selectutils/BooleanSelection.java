package ish.oncourse.selectutils;

public enum BooleanSelection {
	YES(Boolean.TRUE, "Yes"),
	NO(Boolean.FALSE, "No"),
	DEFAULT_POPUP_OPTION(null, "Not stated");
	public static final String FIELD_LABEL = "label";
	
	private Boolean value;
	private String label;
	
	private BooleanSelection(Boolean value, String label) {
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
	

	public static BooleanSelection valueOf(Boolean value) {
		if (value == null) {
			return DEFAULT_POPUP_OPTION;
		} else if (Boolean.TRUE.equals(value)) {
			return YES;
		} else {
			return NO;
		}
	}
}
