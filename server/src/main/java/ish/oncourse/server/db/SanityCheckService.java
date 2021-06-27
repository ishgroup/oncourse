/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.db;

import com.google.inject.Inject;
import ish.common.types.AccountType;
import ish.oncourse.DefaultAccount;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.IPreferenceController;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.cayenne.Tax;
import ish.oncourse.server.scripting.api.EmailService;
import ish.persistence.Preferences;
import ish.persistence.RecordNotFoundException;
import ish.util.AccountUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;


/**
 * performs check's on essential data: preferences, accounts, taxes etc. verifying the existence and correctness of the records.
 */
public class SanityCheckService {
	private static final Logger logger = LogManager.getLogger();

	private final ICayenneService cayenneService;
	private final IPreferenceController pref;
	private final EmailService emailService;

	/**
	 * @param cayenneService instance of CayenneService
	 * @param pref instance of PreferenceController
	 */
	@Inject
	public SanityCheckService(ICayenneService cayenneService, IPreferenceController pref, EmailService emailService) {
		super();
		this.cayenneService = cayenneService;
		this.pref = pref;
		this.emailService = emailService;
	}

	/**
	 * initiates all the checks of the data validity
	 *
	 * @throws Exception
	 */
	public final void performCheck() throws Exception {
		logger.info("performing sanity check, validating account preferences");
		validateAccountPreferences();
		logger.info("performing sanity check, validating transaction preferences");
	    validateTransactionPreferences();
	}


	/**
	 *
	 * validates if all the account preferences are related to appriopriate accounts
	 *
	 * @throws Exception
	 */
	private void validateAccountPreferences() throws Exception {
		var context = cayenneService.getNewContext();

		for (var defaultAccount: DefaultAccount.values()) {
			//check if the preference exists:
			Long preferenceAccountId;

			try {
				preferenceAccountId = Long.valueOf(pref.getDefaultAccountId(defaultAccount.getPreferenceName()).toString());
			} catch (Exception e) {
				preferenceAccountId=null;
			}

			Account account = null;
			var createAccount = false;
			var resetPreference = false;

			if (preferenceAccountId == null) {
				//preference does not exist, it has to be reset
				resetPreference = true;

				//check if the account exists:
				account = getAccountWithCode(defaultAccount.getCode(), context);
				if (account == null) {
					// account missing, create a new one
					createAccount = true;
				}
			} else {
				// preference exists

				//check if the related account exists
				try {
					account = AccountUtil.getAccountWithId(preferenceAccountId, context, Account.class);
				} catch (RecordNotFoundException e) {

				}

				if (account == null) {
					//account does not exist, maybe its just id mismatch?
					account = getAccountWithCode(defaultAccount.getCode(), context);
					resetPreference = true;
					if (account == null) {
						// account missing, create a new one
						createAccount = true;
					}
				}
			}

			//finally perform the changes in the database
			if (resetPreference) {
				if (createAccount) {
					// account still does not exist, create new one
					account = createNewAccount(context, defaultAccount);
				}
				pref.setDefaultAccountId(defaultAccount.getPreferenceName(), account.getId());
			}
		}
	}

	private Account createNewAccount(DataContext context, DefaultAccount defaultAccount) {
		var account = context.newObject(Account.class);
		account.setAccountCode(defaultAccount.getCode());
		account.setIsEnabled(true);
		account.setDescription(defaultAccount.getDescription());
		account.setType(defaultAccount.getType());
		if (AccountType.INCOME.equals(defaultAccount.getType())) {
			account.setTax(getTax(context));
		}

		context.commitChanges();
		return account;
	}

	private Tax getTax(DataContext context) {
		var taxes = ObjectSelect.query(Tax.class).select(context);

		if (taxes.isEmpty()) {
			//create default taxes for our
			createDefaultTaxes();
			//do select after commit
			return ObjectSelect.query(Tax.class).select(context).get(0);
		} else {
			return taxes.get(0);
		}
	}

	//Fixme: we already set taxes in our app using liquibase.
	//But our unit tests use this service to validate data.
	//Some tests have no sample data (default Taxes) and we need to create them for app and default accounts.
	private void createDefaultTaxes() {
		var newContext = cayenneService.getNewContext();
		var australianGST = newContext.newObject(Tax.class);
		australianGST.setRate(BigDecimal.ONE.divide(BigDecimal.TEN));
		australianGST.setTaxCode("GST");
		australianGST.setDescription("Australian GST");
		australianGST.setIsGSTTaxType(true);
		australianGST.setPayableToAccount(AccountUtil.getDefaultGSTAccount(newContext, Account.class));
		australianGST.setReceivableFromAccount(AccountUtil.getDefaultTaxAccount(newContext, Account.class));

		var nonGST = newContext.newObject(Tax.class);
		nonGST.setRate(BigDecimal.ZERO);
		nonGST.setTaxCode("N");
		nonGST.setDescription("Non GST");
		nonGST.setIsGSTTaxType(false);
		nonGST.setPayableToAccount(AccountUtil.getDefaultGSTAccount(newContext, Account.class));
		nonGST.setReceivableFromAccount(AccountUtil.getDefaultTaxAccount(newContext, Account.class));

		newContext.commitChanges();

	}

	/**
	 *
	 * validates if all the tax preferences are related to appriopriate accounts
	 *
	 * @throws Exception
	 */
	private void validateTransactionPreferences() {
		var context = cayenneService.getNewContext();

		var preference = ObjectSelect.query(Preference.class)
				.where(Preference.NAME.eq(Preferences.FINANCE_TRANSACTION_LOCKED))
				.selectOne(context);

		if (preference == null) {
			//no taxes! need to create some!
			var lockDate = context.newObject(Preference.class);
			lockDate.setName("finance.transaction_locked");
			lockDate.setUniqueKey("finance.transaction_locked");
			lockDate.setValueString("31-Dec-2015");
			context.commitChanges();
		}
	}

	/**
	 * private method helping to find default accounts.
	 * @param accountCode
	 * @param context
	 * @return
	 */
	private static Account getAccountWithCode(String accountCode, DataContext context) {
		ObjectContext theContext = context;


		final var expr = Account.ACCOUNT_CODE.eq(accountCode);
		final var accounts = theContext.select(SelectQuery.query(Account.class, expr));
		if (accounts.size() == 0) {
			return null;
		} else if (accounts.size() > 1) {
			throw new RuntimeException("Account code "+accountCode+" is not unique!");
		}
		return accounts.get(0);
	}
}
