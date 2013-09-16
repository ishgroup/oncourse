package ish.oncourse.services.textile.attrs;

public enum  CourseStyle {
	details("textile/course/detail"),
	titles("textile/course/title");
	private String template;

	private CourseStyle(String template)
	{

		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
}
