package ish.oncourse.cms.components;

import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.EnumSelectModel;

public class WebsiteSettings {
	
	@Property
	private boolean enableSocialMedia;
	
	@Property
	private boolean enableForCourse;
	
	@Property
	private boolean enableForWebPage;

	@Property
	private EnumSelectModel hideClassOnWebsiteTypes;

	@Property
	private EnumSelectModel stopWebEnrolmentsTypes;

	@Property
	private String addthisProfileId;
	
	@SuppressWarnings("all")
	@Property
	private boolean saved;

	@Inject
	private Messages messages;

	@InjectComponent
	@Property
	private Form websiteSettingsForm;

	@InjectComponent (value = "hideClassOnWebsite")
	private ClassAgeField hideClassOnWebsite;

	@InjectComponent (value = "stopWebEnrolments")
	private ClassAgeField stopWebEnrolments;

	@Component
	private Zone websiteSettingsZone;
	
	@Inject
	private Request request;
	
	@Inject
	@Property
	private PreferenceController preferenceController;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@InjectPage
	private Page page;

	private void init() {
		hideClassOnWebsiteTypes = new EnumSelectModel(ClassAgeType.class, messages);
		stopWebEnrolmentsTypes = new EnumSelectModel(ClassAgeType.class, messages,
				new ClassAgeType[]{ClassAgeType.afterClassStarts,
						ClassAgeType.beforeClassStarts,
						ClassAgeType.beforeClassEnds});
	}

	@SetupRender
	void beforeRender() {
		this.enableSocialMedia = preferenceController.getEnableSocialMediaLinks();
		this.enableForCourse = preferenceController.getEnableSocialMediaLinksCourse();
		this.enableForWebPage = preferenceController.getEnableSocialMediaLinksWebPage();
		
		if (preferenceController.getAddThisProfileId() != null) {
			this.addthisProfileId = preferenceController.getAddThisProfileId();
		}
		else {
			addthisProfileId = "";
		}
		init();
	}
	
	@AfterRender
	void afterRender() {
		saved = false;
	}

	Object onSuccessFromWebsiteSettingsForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		websiteSettingsForm.clearErrors();
		
		preferenceController.setEnableSocialMediaLinks(enableSocialMedia);
		preferenceController.setEnableSocialMediaLinksCourse(enableForCourse);
		preferenceController.setEnableSocialMediaLinksWebPage(enableForWebPage);
		
		if (StringUtils.trimToNull(addthisProfileId) != null) {
			preferenceController.setAddThisProfileId(addthisProfileId);
		}

		preferenceController.setHideClassOnWebsiteAge(ClassAge.valueOf(hideClassOnWebsite.getDays(), hideClassOnWebsite.getType()));
		preferenceController.setStopWebEnrolmentsAge(ClassAge.valueOf(stopWebEnrolments.getDays(), stopWebEnrolments.getType()));

		saved = true;
		init();
		return websiteSettingsZone.getBody();
	}
	
	public Zone getWebsiteSettingsZone() {
		return websiteSettingsZone;
	}

    @OnEvent(value = "publish")
	public void publish(){
        if (!request.isXHR())
            return;
		webSiteVersionService.publish();
		webSiteVersionService.removeOldWebSiteVersions(webSiteService.getCurrentWebSite());
	}
}
