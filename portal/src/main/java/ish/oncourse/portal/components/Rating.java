package ish.oncourse.portal.components;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.corelib.components.Loop;
import org.apache.tapestry5.corelib.components.RadioGroup;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

public class Rating extends AbstractField {

	private Integer value;

	@SuppressWarnings("unused")
	@Component(parameters = { "value=prop:value" })
	private RadioGroup radioGroup;

	@SuppressWarnings("unused")
	@Component(parameters = { "source=prop:source", "value=loopValue" })
	private Loop loop;

	private Integer loopValue;

	private List<Integer> source;

	@Inject
	private Request request;

	@Override
	protected void processSubmission(String elementName) {

	}

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
			source = new ArrayList<Integer>();
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

	StreamResponse onActionFromUpdate(String value) {
		if (request.isXHR() && StringUtils.isNumeric(value))
		{
			this.value = new Integer(value);
		}
		return null;
	}
}
