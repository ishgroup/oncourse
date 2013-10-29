package ish.oncourse.portal.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class Subscriptions {

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
        return tabId.equals(activeTabId) ? "active": StringUtils.EMPTY;
    }
}
