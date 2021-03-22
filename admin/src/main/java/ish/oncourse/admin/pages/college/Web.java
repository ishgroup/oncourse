package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.services.template.SiteTemplateEncoder;
import ish.oncourse.admin.services.template.SiteTemplateSelectModel;
import ish.oncourse.admin.services.website.CreateNewWebSite;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteDelete;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.configuration.Configuration.AdminProperty.S_ROOT;

public class Web {

    public final static String DEFAULT_HOME_PAGE_NAME = "Home page";
	private static final Logger logger = LogManager.getLogger();

	@Property
	private College college;

	@Property
	@Persist(PersistenceConstants.SESSION)
	private Map<String, WebSite> sites;

	@SuppressWarnings("all")
	@Property
	private List<WebHostName> domains;

	@Property
	private WebHostName currentDomain;

	@SuppressWarnings("all")
	@Property
	private WebSite currentSite;

	@Property
	private String newSiteNameValue;

	@Property
	private String newSiteKeyValue;

	@Property
	private WebSite newSiteTemplateValue;

	@Property
	private SiteTemplateSelectModel templateSelectModel;

	@Property
	private String newSiteGoogleTagmanagerValue;

	@Property
	private String newSiteCoursesRootTagName;

	@Property
	private String newDomainValue;

	@Property
	private String newDomainSite;

	@SuppressWarnings("all")
	@Property
	private String changeSiteUrl;

	@Property
	private String currentSiteKey;

	@SuppressWarnings("all")
	@Property
	@Persist
	private StringSelectModel siteSelectModel;

	@SuppressWarnings("all")
	@Property
	@Persist
	private boolean siteDeleteFailed;

	@SuppressWarnings("all")
	private String selectedSite;

	@Inject
	private ICollegeService collegeService;

	@Inject
	private ICayenneService cayenneService;

    @Inject
    private IWebNodeService webNodeService;

	@Inject
	private Request request;

	@Inject
	private Response response;

    @Inject
    private IWebSiteVersionService webSiteVersionService;

	@Inject
	private IWebSiteService webSiteService;

    @Property
    private ValidateHandler validateHandler = new ValidateHandler();

    @Property
    private String error;

	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
	}

	@SuppressWarnings("unchecked")
	@SetupRender
	void setupRender() {
		this.changeSiteUrl = response.encodeURL(request.getContextPath() + "/college/changeDomainSite");
		this.sites = new HashMap<>();

		ObjectContext context = cayenneService.newContext();

		College college = context.localObject(this.college);

		List<WebSite> sites = ObjectSelect.query(WebSite.class).
				where(WebSite.COLLEGE.eq(college)).
				select(context);
		for (WebSite site : sites) {
			this.sites.put(site.getSiteKey(), site);
		}

		this.domains = ObjectSelect.query(WebHostName.class).
				where(WebHostName.COLLEGE.eq(college)).
				select(context);


		String[] siteKeys = new String[sites.size()];
		int i = 0;
		for (WebSite site : sites) {
			siteKeys[i] = site.getSiteKey();
			i++;
		}
		siteSelectModel = new StringSelectModel(siteKeys);

		templateSelectModel = SiteTemplateSelectModel.valueOf(webSiteService.getSiteTemplates());
	}

	@AfterRender
	void afterRender() {
		this.siteDeleteFailed = false;
	}

	@OnEvent(component="newDomainForm", value="success")
	void addDomain() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		WebHostName domain = context.newObject(WebHostName.class);
		domain.setCollege(context.localObject(college));

		WebSite site = ObjectSelect.query(WebSite.class).
				where(WebSite.SITE_KEY.eq(newDomainSite)).
				selectOne(context);

		domain.setWebSite(site);
		domain.setName(newDomainValue);
		domain.setCreated(new Date());
		domain.setModified(new Date());

		context.commitChanges();
	}

	@OnEvent(component="sitesEditForm", value="success")
	void saveSites() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		for (String key : sites.keySet()) {
			if (sites.get(key) != null) {
				WebSite webSite = context.localObject(sites.get(key));

				if (webSite != null) {
					webSite.setGoogleTagmanagerAccount(sites.get(key).getGoogleTagmanagerAccount());
					webSite.setCoursesRootTagName(sites.get(key).getCoursesRootTagName());
				}
			}
		}

		context.commitChanges();
	}

	@OnEvent(component="sitesForm", value="success")
	void addSite() {
        ObjectContext context = cayenneService.newNonReplicatingContext();

        College college = context.localObject(this.college);

        CreateNewWebSite createNewWebSite = CreateNewWebSite.valueOf(newSiteNameValue,
				newSiteKeyValue, newSiteGoogleTagmanagerValue,
				newSiteCoursesRootTagName, newSiteTemplateValue,
				Configuration.getValue(S_ROOT),
				college, context,
				webSiteVersionService, webNodeService);
        createNewWebSite.create();

        validateHandler.setErrors(createNewWebSite.getErrors());
	}


	Object onActionFromDeleteDomain(Long id) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		WebHostName domain = SelectById.query(WebHostName.class, id).selectOne(context);
		WebSite webSite = domain.getWebSite();

		context.deleteObjects(domain);
		context.commitChanges();
		return null;
	}

	Object onActionFromDeleteSite(String siteKey) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		WebSite site = ObjectSelect.query(WebSite.class).
				where(WebSite.SITE_KEY.eq(siteKey)).
				selectOne(context);

		try {
			WebSiteDelete.valueOf(site, context).delete();
		} catch (CayenneRuntimeException e) {
			this.siteDeleteFailed = true;
			logger.error("Web site could not be deleted", e);
			return null;
		}

		return null;
	}


	public String getSelectedSite() {
		return currentDomain.getWebSite().getSiteKey();
	}

	public WebSite getCurrentWebSite() {
		return this.sites.get(currentSiteKey);
	}

	public SiteTemplateEncoder getSiteTemplateEncoder() {
		return SiteTemplateEncoder.valueOf(webSiteService.getSiteTemplates());
	}
}
