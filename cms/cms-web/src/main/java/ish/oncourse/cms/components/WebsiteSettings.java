package ish.oncourse.cms.components;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.pages.internal.Page;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class WebsiteSettings {
	
	@Property
	private boolean enableSocialMedia;
	
	@Property
	private boolean enableForCourse;
	
	@Property
	private boolean enableForWebPage;
	
	@Property
	private String addthisProfileId;
	
	@Property
	private boolean saved;
	
	@InjectComponent
	@Property
	private Form websiteSettingsForm;
	
	@Component
	private Zone websiteSettingsZone;
	
	@Inject
	private Request request;
	
	@Inject
	private PreferenceController preferenceController;
	
	@InjectPage
	private Page page;
	
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
		
		if (addthisProfileId != null && addthisProfileId != "") {
			preferenceController.setAddThisProfileId(addthisProfileId);
		}
		
		saved = true;
		
		return websiteSettingsZone.getBody();
	}
	
	public Zone getWebsiteSettingsZone() {
		return websiteSettingsZone;
	}
}
