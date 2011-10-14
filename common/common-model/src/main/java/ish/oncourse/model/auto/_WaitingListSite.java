package ish.oncourse.model.auto;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Site;
import ish.oncourse.model.WaitingList;

/**
 * Class _WaitingListSite was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _WaitingListSite extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String SITE_PROPERTY = "site";
    public static final String WAITING_LIST_PROPERTY = "waitingList";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setSite(Site site) {
        setToOneTarget("site", site, true);
    }

    public Site getSite() {
        return (Site)readProperty("site");
    }


    public void setWaitingList(WaitingList waitingList) {
        setToOneTarget("waitingList", waitingList, true);
    }

    public WaitingList getWaitingList() {
        return (WaitingList)readProperty("waitingList");
    }


}
