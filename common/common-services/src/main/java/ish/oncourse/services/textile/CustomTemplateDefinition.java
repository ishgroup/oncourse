package ish.oncourse.services.textile;

import org.apache.tapestry5.internal.util.MultiKey;

import java.util.Locale;

/**
 * The class was intruduced to have posibilites redifine tapestry template name in rich textiles.
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


	public static MultiKey getMultiKeyBy(String className, CustomTemplateDefinition ctd, String serverName, Locale locale)
	{
		//we should use anouther key to cache ComponentAssembler for component when user defines custom template
		if (ctd != null && className.endsWith(ctd.getTemplateClassName()))
			return new MultiKey(className, ctd.getTemplateFileName(), locale, serverName);
		else
			return new MultiKey(className, locale, serverName);
	}



}
