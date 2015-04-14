package ish.oncourse.selectutils;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import java.util.ArrayList;
import java.util.List;

public class StringSelectModel extends AbstractSelectModel implements
		ValueEncoder<String> {

	private String[] labels;

	public StringSelectModel(String... strs) {
		this.labels = strs;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {

		List<OptionModel> optionModels = new ArrayList<>(
				labels.length);

		for (String label : labels) {
			optionModels.add(new OptionModelImpl(label));
		}

		return optionModels;
	}

	public String toClient(String value) {
		return value;
	}

	public String toValue(String clientValue) {
		return clientValue;
	}
}
