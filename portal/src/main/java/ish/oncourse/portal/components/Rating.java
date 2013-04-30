package ish.oncourse.portal.components;

import ish.oncourse.portal.services.ValueChangeDelegate;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Loop;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

public class Rating {

	@Parameter
	private Integer value;
	
	@Parameter
	private ValueChangeDelegate<Integer> delegate;

	@SuppressWarnings("unused")
	@Component(parameters = { "source=prop:source", "value=loopValue" })
	private Loop loop;

	private Integer loopValue;

	private List<Integer> source;

	@Inject
	private Request request;

	@Parameter
	private boolean editable;

	public Integer getValue() {
		if (value == null)
		{
			value = getSource().get(0);
		}
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public List<Integer> getSource() {
		if (source == null)
		{
			source = new ArrayList<>();
			source.add(0);
			source.add(1);
			source.add(2);
			source.add(3);
			source.add(4);
		}
		return source;
	}

	public void setSource(List<Integer> source) {
		this.source = source;
	}

	public Integer getLoopValue() {
		return loopValue;
	}

	public void setLoopValue(Integer loopValue) {
		this.loopValue = loopValue;
	}
	
	public String getRatingChoiceClass() {
		return loopValue < getValue() ? "rating-choice-selected" : "rating-choice";
	}

	public String getEditableClass() {
		return editable ? FormatUtils.EMPTY_STRING : "readonly";
	}


	StreamResponse onActionFromUpdate(String value) {
		if (request.isXHR() && StringUtils.isNumeric(value))
		{
			this.value = new Integer(value);
			
			if (delegate != null) {
				this.delegate.changeValue(this.value);
			}
		}
		return null;
	}
}
