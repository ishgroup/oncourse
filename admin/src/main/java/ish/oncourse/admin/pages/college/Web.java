package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.services.CreateSiteFileStructure;
import ish.oncourse.admin.services.template.SiteTemplateSelectModel;
import ish.oncourse.admin.utils.LicenseFeeUtil;
import ish.oncourse.model.*;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteDelete;
import ish.oncourse.services.site.WebSitePublisher;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.util.ContextUtil;
import ish.oncourse.util.ValidateHandler;
import ish.util.SecurityUtil;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectById;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Web {

    public final static String DEFAULT_HOME_PAGE_NAME = "Home page";
	
	@Property
	private College college;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Map<String, WebSite> sites;
	
	@SuppressWarnings("all")
	@Property
	private List<WebHostName> domains;
	
	@SuppressWarnings("all")
	@Property
	private List<WillowUser> cmsUsers;
	
	@Property
	private WebHostName currentDomain;
	
	@SuppressWarnings("all")
	@Property
	private WebSite currentSite;
	
	@SuppressWarnings("all")
	@Property
	private WillowUser currentUser;
	
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
	
	@Property
	private String newUserEmailValue;
	
	@Property
	private String newUserPasswordValue;
	
	@Property
	private String newUserFirstNameValue;
	
	@Property
	private String newUserLastNameValue;
	
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
		
		this.cmsUsers = ObjectSelect.query(WillowUser.class).
				where(WillowUser.COLLEGE.eq(college)).
				cacheStrategy(QueryCacheStrategy.NO_CACHE).
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
		Date now = new Date();

		College college = context.localObject(this.college);
		
		WebSite site = context.newObject(WebSite.class);
		site.setCollege(college);
		site.setName(newSiteNameValue);
		site.setSiteKey(newSiteKeyValue);
		site.setGoogleTagmanagerAccount(newSiteGoogleTagmanagerValue);
		site.setCoursesRootTagName(newSiteCoursesRootTagName);
		site.setCreated(now);
		site.setModified(now);

        //we create staged web site version
		WebSiteVersion stagedVersion = context.newObject(WebSiteVersion.class);
		stagedVersion.setWebSite(site);

        WebSiteLayout webSiteLayout = context.newObject(WebSiteLayout.class);
        webSiteLayout.setLayoutKey(WebNodeType.DEFAULT_LAYOUT_KEY);
        webSiteLayout.setWebSiteVersion(stagedVersion);

		WebNodeType page = context.newObject(WebNodeType.class);
		page.setName(WebNodeType.PAGE);
		page.setCreated(now);
		page.setModified(now);
		page.setWebSiteLayout(webSiteLayout);
		page.setWebSiteVersion(stagedVersion);

        WebNode node = webNodeService.createNewNodeBy(stagedVersion, page, DEFAULT_HOME_PAGE_NAME, DEFAULT_HOME_PAGE_NAME, 1);
		node.setPublished(true);
		
		WebMenu menu = context.newObject(WebMenu.class);
		menu.setName("Home");
		menu.setCreated(now);
		menu.setModified(now);
		menu.setWebSiteVersion(stagedVersion);
		menu.setWeight(1);
		menu.setWebNode(node);

		LicenseFeeUtil.createFee(context, college, site, LicenseFeeUtil.HOSTING_FEE_CODE);
		LicenseFeeUtil.createFee(context, college, site, LicenseFeeUtil.CC_WEB_FEE_CODE);
		LicenseFeeUtil.createFee(context, college, site, LicenseFeeUtil.ECOMMERCE_FEE_CODE);

		context.commitChanges();

		WebUrlAlias urlAlias = context.newObject(WebUrlAlias.class);
		urlAlias.setWebSiteVersion(stagedVersion);
		urlAlias.setUrlPath("/");
		urlAlias.setWebNode(node);
        urlAlias.setDefault(true);

        String sRootPath = ContextUtil.getSRoot();

        boolean result = CreateSiteFileStructure.valueOf(site, new File(sRootPath)).create();

        if (result) {
            context.commitChanges();

            WebSitePublisher publisher = WebSitePublisher.valueOf(stagedVersion, context);
            publisher.publish();
        } else {
            HashMap<String,String> errors = new HashMap<>();
            errors.put("addSite",
                    String.format("Cannot add site with key %s. The file structure can not be created. See log messages.", site.getSiteKey()));
            validateHandler.setErrors(errors);
        }
	}
	
	@OnEvent(component="cmsUsersForm", value="success")
	void addUser() throws UnsupportedEncodingException {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WillowUser user = context.newObject(WillowUser.class);
		user.setCollege(context.localObject(college));
		user.setEmail(newUserEmailValue);
		final String hashedPassword = SecurityUtil.hashPassword(newUserPasswordValue);
		user.setPassword(newUserPasswordValue);//TODO: migrate when found logic which will update old passwords
		user.setFirstName(newUserFirstNameValue);
		user.setLastName(newUserLastNameValue);
		
		context.commitChanges();
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
			return null;
		}
		
		return null;
	}
	
	Object onActionFromDeleteUser(String email) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		WillowUser user = ObjectSelect.query(WillowUser.class).
				where(WillowUser.EMAIL.eq(email).
                        andExp(WillowUser.COLLEGE.eq(college))).
				selectOne(context);

		context.deleteObjects(user);
		context.commitChanges();
		
		return null;
	}
	
	public String getSelectedSite() {
		return currentDomain.getWebSite().getSiteKey();
	}
	
	public WebSite getCurrentWebSite() {
		return this.sites.get(currentSiteKey);
	}
}
