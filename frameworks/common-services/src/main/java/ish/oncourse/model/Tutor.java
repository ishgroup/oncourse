package ish.oncourse.model;

import ish.oncourse.model.auto._Tutor;

public class Tutor extends _Tutor {
	
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

}
