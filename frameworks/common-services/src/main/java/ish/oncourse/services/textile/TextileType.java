package ish.oncourse.services.textile;

public enum TextileType {
	IMAGE("\\{image(\\s+.*?)}|\\{image}");
	
	private String regexp;
	
	private TextileType(String regexp) {
		this.regexp = regexp;
	}
	
	public String getRegexp(){
		return regexp;
	}
}
