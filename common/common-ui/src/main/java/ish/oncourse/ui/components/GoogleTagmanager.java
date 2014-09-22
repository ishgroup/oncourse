package ish.oncourse.ui.components;

import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.components.internal.DataLayer;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;

/**
 * The component inits google tag mananger script and sends dataLayer object
 * if google tag manager account is set for this web site.
 */
public class GoogleTagmanager {
	@Inject
	private IWebSiteService siteService;

    @InjectComponent
    private DataLayer dataLayer;

	/**
	 * Google tag mananger event name.
	 */
	@Property
	@Parameter
	private String eventName;

	private Cart cart;

    @SetupRender
    public void setupRender()
    {
        dataLayer.setCart(cart);
    }

	public String getAccount() {
		String account = siteService.getCurrentWebSite().getGoogleTagmanagerAccount();
		return (StringUtils.trimToNull(account) == null) ? null : account.trim();
	}

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
