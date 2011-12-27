package ish.oncourse.model;

import ish.oncourse.model.auto._PostcodeDb;

public class PostcodeDb extends _PostcodeDb {
	private static final String SPACE_CHARACTER = " ";
	private static final long serialVersionUID = -4566199827275845903L;

	public String getDisplayName(){
		return getSuburb() + SPACE_CHARACTER + getPostcode();
	}
}
