package ish.oncourse.services.textile;

public enum TextileType {
	IMAGE("\\{image([^}]*)}"), 
	VIDEO("\\{video([^}]*)}"), 
	BLOCK("\\{block([^}]*)}"),
	COURSE("\\{course([^}]*)}"),
	PAGE("\\{page([^}]*)}");
	
	private String regexp;
	
	private TextileType(String regexp) {
		this.regexp = regexp;
	}
	
	public String getRegexp(){
		return regexp;
	}
}
