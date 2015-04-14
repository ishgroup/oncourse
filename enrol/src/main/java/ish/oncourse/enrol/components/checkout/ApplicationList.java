/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Application;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class ApplicationList {
	
	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Parameter(required = true)
	@Property
	private Contact contact;

	@Property
	private Application application;

	@Property
	private Integer index;

	@Inject
	private Request request;

	@Property
	@Parameter(required = false)
	private Block blockToRefresh;


	public Integer getContactIndex() {
		return purchaseController.getModel().getContacts().indexOf(contact);
	}

	public Boolean getChecked() {
		return purchaseController.getModel().isApplicationEnabled(application);
	}

	public String getApplicationError() {
		return purchaseController.getModel().getErrorBy(application);
	}

	public List<Application> getApplications() {
		return purchaseController.getModel().getAllApplications(contact);
	}

	public ApplicationItem.ApplicationItemDelegate getApplicationItemDelegate() {
		return new ApplicationItem.ApplicationItemDelegate() {
			@Override
			public void onChange(Integer contactIndex, Integer applicationIndex) {
				Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
				Application application = purchaseController.getModel().getAllApplications(contact).get(applicationIndex);
				Boolean isSelected = purchaseController.getModel().isApplicationEnabled(application);
				PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(isSelected ? PurchaseController.Action.disableApplication : PurchaseController.Action.enableApplication);
				actionParameter.setValue(application);
				purchaseController.performAction(actionParameter);
			}

		};
	}
}
