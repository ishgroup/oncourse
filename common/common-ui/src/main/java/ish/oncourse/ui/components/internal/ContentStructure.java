package ish.oncourse.ui.components.internal;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.visitor.IParsedContentVisitor;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ContentStructure extends ISHCommon {

    @Parameter
    @Property
    private WebNode node;

    @Property
    private WebContentVisibility visibility;

	@Inject
	private IParsedContentVisitor visitor;
    
    @Inject
    private PreferenceController preferenceController;

    public String getRegionContent() {
        return visitor.visitWebContent(visibility.getWebContent());
    }
    
    public boolean getAddThisEnabled() {
    	return preferenceController.getEnableSocialMediaLinks() && preferenceController.getEnableSocialMediaLinksWebPage();
    }
    
    public String getAddThisProfileId() {
    	return preferenceController.getAddThisProfileId();
    }

}
