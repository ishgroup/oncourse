package ish.oncourse.services.ui;

import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class SelectModelService implements ISelectModelService {
	
	@Inject
	private PropertyAccess propertyAccess;

	public <T> SelectModel newSelectModel(List<T> list,
			String labelField, String idField) {
		return new GenericSelectModel<T>(propertyAccess, list, labelField, idField);
	}
}
