package ish.oncourse.model;

import ish.oncourse.model.auto._PostcodeDb;

public class PostcodeDb extends _PostcodeDb {

	public String getDisplayName(){
		return getSuburb()+" "+getPostcode();
	}
}
