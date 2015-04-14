package ish.oncourse.enrol.components;

import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DecimalFormat;
import java.text.Format;

public class AnalyticsTransaction {
	@Inject
	private IWebSiteService webSiteService;

	@Parameter
	@Property
	private Transaction transaction;
	
	@Property
	private String affiliation;
	
	@Property
	private String transactionAccount;
	
	@Property
	private String transactionDomain;
	
	@Property
	private Item transactionItem;
	
	@Property
	private Format moneyFormat = new DecimalFormat("#,##0.00");

	@SetupRender
	void beginRender() {
		if (transaction != null) {
			WebSite currentWebSite = webSiteService.getCurrentWebSite();
			transactionAccount = currentWebSite.getGoogleAnalyticsAccount();
			transactionDomain = currentWebSite.getName();
			affiliation = transaction.getAffiliation();
			
			if (affiliation == null) {
				affiliation = Transaction.DEFAULT_WEB_AFFILIATION;
			}
		}
	}

	public boolean isHasOrderNumber() {
		return hasTransactionAndItems() && transaction.getOrderNumber() != null
				&& transaction.getOrderNumber().length() > 0;
	}

	public boolean hasTransactionAndItems() {
		return transaction != null && transaction.getItems() != null
				&& !transaction.getItems().isEmpty();
	}
}
