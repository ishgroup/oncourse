package ish.oncourse.ui.services.template;

import org.apache.tapestry5.ioc.ServiceAdvisor;


/**
 * Intercepts component template creation by Tapestry, potentially supplying
 * alternative template.
 */
public interface IComponentTemplateSourceAdvisor extends ServiceAdvisor {}
