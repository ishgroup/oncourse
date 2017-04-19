package ish.oncourse.cayenne;

import java.util.List;

public interface IExpandable {

	List<? extends ICustomField> getCustomFields();
}
