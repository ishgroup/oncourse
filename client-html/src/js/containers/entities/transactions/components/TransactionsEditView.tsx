/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import FormField from '../../../../common/components/form/formFields/FormField';
import { State } from '../../../../reducers/state';

const getFormattedAccounts = (accounts: Account[]) => {
  const formattedAccount = [];
  accounts.forEach(acc => {
    formattedAccount.push({
      value: acc.id,
      label: `${acc.description} ${acc.accountCode}`
    });
  });
  return formattedAccount;
};

const validateAmountField = value => {
  switch (typeof value) {
    case "string": {
      return value
        .split("0")
        .join("")
        .trim()
        ? undefined
        : "Amount has to be specified";
    }

    case "number": {
      return value !== 0 ? undefined : "Amount has to be specified";
    }

    case "boolean": {
      return undefined;
    }

    default: {
      return value ? undefined : "Amount has to be specified";
    }
  }
};

const TransactionsEditView = props => {
  const { twoColumn, accounts, isNew } = props;

  const hasAccounts = accounts && accounts.length !== 0;

  const formattedAccounts = accounts ? getFormattedAccounts(accounts) : [];

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          disabled={!isNew || !hasAccounts}
          name="fromAccount"
          label={isNew ? "From account" : "Account"}
          items={formattedAccounts || []}
          required={isNew}
                  />
      </Grid>
      {isNew ? (
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="select"
            disabled={!hasAccounts}
            name="toAccount"
            label={$t('to_account')}
            required={isNew}
            items={formattedAccounts || []}
                      />
        </Grid>
      ) : null}
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="money"
          disabled={!isNew}
          name="amount"
          label={$t('amount')}
          validate={isNew ? validateAmountField : undefined}
                  />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="date"
          disabled={!isNew}
          name="transactionDate"
          label={$t('transaction_date')}
          required={isNew}
                  />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  accounts: state.plainSearchRecords.Account.items
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(TransactionsEditView);
