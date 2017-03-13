package ish.oncourse.model;

import org.apache.commons.lang.StringUtils;

public class TextileFormField {

	private String label;

	private boolean required;

	private Integer textAreaRows;

	private String beforeFieldMarkUp;

	private Integer maxLength;
	
	private boolean isText;

	private boolean isRadio;
	
	private boolean isPopup;

	private String defaultValue;

	private String[] options;

	public String getLabel() {
		if (label == null) {
			return StringUtils.EMPTY;
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Integer getTextAreaRows() {
		return textAreaRows;
	}

	public void setTextAreaRows(Integer textAreaRows) {
		this.textAreaRows = textAreaRows;
	}

	public String getBeforeFieldMarkUp() {
		return beforeFieldMarkUp;
	}

	public void setBeforeFieldMarkUp(String beforeFieldMarkUp) {
		this.beforeFieldMarkUp = beforeFieldMarkUp;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isRadio() {
		return isRadio;
	}

	public void setRadio(boolean isRadio) {
		this.isRadio = isRadio;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public boolean isPopup() {
		return isPopup;
	}

	public void setPopup(boolean isPopup) {
		this.isPopup = isPopup;
	}

	public boolean isText() {
		return isText;
	}

	public void setText(boolean isText) {
		this.isText = isText;
	}
}
