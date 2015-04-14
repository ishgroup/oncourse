package ish.oncourse.ui.components.internal;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ContentStructure {

    @Parameter
    @Property
    private WebNode node;

    @Property
    private WebContentVisibility visibility;

    @Inject
    private ITextileConverter textileConverter;
    
    @Inject
    private PreferenceController preferenceController;

    public String getRegionContent() {
        return visibility.getWebContent().accept(new ParsedContentVisitor(textileConverter));
    }
    
    public boolean getAddThisEnabled() {
    	return preferenceController.getEnableSocialMediaLinks() && preferenceController.getEnableSocialMediaLinksWebPage();
    }
    
    public String getAddThisProfileId() {
    	return preferenceController.getAddThisProfileId();
    }

}
