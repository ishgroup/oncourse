package ish.oncourse.services.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.AbstractSelectModel;

public class GenericSelectModel<T> extends AbstractSelectModel implements
		ValueEncoder<T> {

	private List<T> list;
	private String labelField;
	private String idField;
	private PropertyAccess propertyAccess;

	public GenericSelectModel(PropertyAccess propertyAccess, List<T> list,
			String labelField, String idField) {
		this.propertyAccess = propertyAccess;
		this.list = list;
		this.labelField = labelField;
		this.idField = idField;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		List<OptionModel> optionModelList = new ArrayList<OptionModel>();

		for (T obj : list) {
			String label = (String) propertyAccess.get(obj, labelField);
			optionModelList.add(new OptionModelImpl(label, obj));
		}
		return optionModelList;
	}

	public String toClient(T value) {
		return propertyAccess.get(value, idField).toString();
	}

	public T toValue(String clientValue) {
		Expression expr = ExpressionFactory.matchExp(idField, clientValue);
		List<T> l = expr.filterObjects(list);
		return l.get(0);
	}
}