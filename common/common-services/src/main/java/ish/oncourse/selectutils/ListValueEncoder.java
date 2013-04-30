package ish.oncourse.selectutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;

public class ListValueEncoder<T> implements ValueEncoder<T> {

	private PropertyAdapter idFieldAdapter = null;
	private List<T> list;

	public ListValueEncoder(List<T> list, String idField, PropertyAccess access) {
		if (list == null) {
			list = new ArrayList<>();
		}
		if (idField != null && !idField.equalsIgnoreCase("null")) {
			if (!list.isEmpty()) {
				this.idFieldAdapter = access.getAdapter(list.get(0).getClass())
						.getPropertyAdapter(idField);
			}
		}
		this.list = list;
	}

	public String toClient(T obj) {
		if (idFieldAdapter == null) {
			return strRepr(obj);
		} else {
			return strRepr(idFieldAdapter.get(obj));
		}
	}

	public T toValue(String string) {
		if (idFieldAdapter == null) {
			for (T obj : list) {
				if (strRepr(obj).equals(string))
					return obj;
			}
		} else {
			for (T obj : list) {
				if (strRepr(idFieldAdapter.get(obj)).equals(string))
					return obj;
			}
		}
		return null;
	}

	private String strRepr(Object o) {
		if (o == null) {
			return "";
		} else {
			return o.toString();
		}
	}
}
