package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.ui.components.internal.PageStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Session;

import java.io.IOException;

public class Page extends APage{
    private static final Logger logger = LogManager.getLogger();


    @Persist
    @Property
	private WebNode editNode;

    @InjectComponent
    @Property
    private PageStructure pageStructure;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private Block reloadPageBlock;

    @Override
    public boolean beforeRender() throws IOException {
        WebNode currentNode = getWebNodeService().getCurrentNode();

        if (currentNode != null) {
            this.editNode = cayenneService.newContext().localObject(currentNode);
            //we need set the same value for node property in APage parent.
            setNode(this.editNode);
            getRequest().setAttribute(IWebNodeService.CURRENT_WEB_NODE_LAYOUT,  this.editNode.getWebNodeType().getLayoutKey());
        } else {
            logger.error("CurrentNode is null in {}/{}",
                    getRequest().getServerName(),
                    getRequest().getPath());
            this.editNode = null;
            //we need set the same value for node property in APage parent.
            this.setNode(null);
        }
        return true;
    }

	@AfterRender
	void afterRender() {
		final Session session =  getRequest().getSession(false);
		session.setAttribute(WebNode.LOADED_NODE, null);
	}

	public Block getReloadPageBlock() {
		return reloadPageBlock;
	}
}
