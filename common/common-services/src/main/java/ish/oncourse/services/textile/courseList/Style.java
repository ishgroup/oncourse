package ish.oncourse.services.textile.courseList;

public enum Style {
	details("textile/course/detail"),
	titles("textile/course/title");
	private String template;

	private Style(String template)
	{

		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
}
