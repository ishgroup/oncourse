package ish.oncourse.selectutils;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.util.AbstractSelectModel;

import java.util.ArrayList;
import java.util.List;

public class ListSelectModel<T> extends AbstractSelectModel {

	private PropertyAdapter labelFieldAdapter = null;

	private List<T> list;

	public ListSelectModel(List<T> list, String labelField,
			PropertyAccess access) {
		if (list == null) {
			list = new ArrayList<>();
		}
		if (labelField != null && !labelField.equalsIgnoreCase("null")) {
			if (!list.isEmpty()) {
				this.labelFieldAdapter = access.getAdapter(
						list.get(0).getClass()).getPropertyAdapter(labelField);
			}
		}
		this.list = list;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		List<OptionModel> optionModelList = new ArrayList<>();
		for (T obj : list) {
			if (labelFieldAdapter == null) {
				optionModelList.add(new OptionModelImpl(strRepr(obj) + "", obj));
			} else {
				optionModelList.add(new OptionModelImpl(strRepr(labelFieldAdapter
						.get(obj)), obj));
			}
		}
		return optionModelList;
	}

	private String strRepr(Object o) {
		if (o == null) {
			return "";
		} else {
			return o.toString();
		}
	}
}
