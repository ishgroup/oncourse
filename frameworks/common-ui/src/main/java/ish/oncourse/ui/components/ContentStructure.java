package ish.oncourse.ui.components;

import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
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
    private IWebNodeService webNodeService;

    public String getRegionContent() {
        return visibility.getWebContent().accept(new ParsedContentVisitor(textileConverter));
    }

    public String getTemplateId() {
        return (webNodeService.getHomePage().getId() == this.node.getId()) ? IWebNodeService.WELCOME_TEMPLATE_ID
                : "";
    }
}
