package ish.oncourse.services.ui;

import java.util.List;

import org.apache.tapestry5.SelectModel;

public interface ISelectModelService {
	<T> SelectModel newSelectModel(List<T> list, String labelField, String idField);
}
