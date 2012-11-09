package ish.oncourse.enrol.checkout;

import java.util.Map;

public interface IDelegate {

	void setErrors(Map<String, String> errors);
	Map<String,String> getErrors();
}
