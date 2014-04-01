package ish.oncourse.portal.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Subscriptions {

    @Inject

    private Messages messages;

    @Property
    @Persist
    private String activeTabId;


    @SetupRender
    void beforeRender() {
        if (activeTabId == null)
            activeTabId = "tab-waiting";
    }

    @OnEvent(value = "setActiveTab")
    public void setActiveTab(String activeTabId)
    {
        this.activeTabId = activeTabId;
    }

    public String getActiveClass(String tabId)
    {
        return tabId.equals(activeTabId) ? messages.get("class.active") : StringUtils.EMPTY;
    }
}
