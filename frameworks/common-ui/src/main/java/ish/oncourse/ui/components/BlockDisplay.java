package ish.oncourse.ui.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class BlockDisplay {

    @Parameter
    private WebNodeType webNodeType;

    @Parameter
    private String regionKey;

    @Inject
	private ITextileConverter textileConverter;

    @Property
    private List<WebContent> regions;

    @Property
    private WebContent region;

    @SetupRender
	public void beforeRender() {
        this.regions = webNodeType.getContentForRegionKey(regionKey);
    }

    public String getRegionContent() {
        return region.accept(new ParsedContentVisitor(textileConverter));
    }
}
