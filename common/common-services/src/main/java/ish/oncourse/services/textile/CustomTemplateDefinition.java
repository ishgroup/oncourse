package ish.oncourse.services.textile;

/**
 * The class was introduced to have possibilities redefine tapestry template name in rich textiles.
 */
public class CustomTemplateDefinition {

	private String templateClassName;
	private String templateFileName;


	public String getTemplateClassName() {
		return templateClassName;
	}

	public void setTemplateClassName(String templateClass) {
		this.templateClassName = templateClass;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}


	@Override
	public boolean equals(Object obj) {
		return obj instanceof CustomTemplateDefinition &&
				(templateClassName != null && templateClassName.equals(((CustomTemplateDefinition) obj).templateClassName)) &&
				(templateFileName != null && templateFileName.equals(((CustomTemplateDefinition) obj).templateFileName));
	}

	@Override
	public int hashCode() {
		return String.format("%s%s", templateClassName, templateClassName).hashCode();
	}
}
