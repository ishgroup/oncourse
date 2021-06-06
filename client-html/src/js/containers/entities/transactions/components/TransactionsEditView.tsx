/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import { connect } from "react-redux";
import { Account } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { State } from "../../../../reducers/state";

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
    <Grid container className="p-3">
      <Grid item lg={twoColumn ? 6 : 12} md={twoColumn ? 8 : 12} xs={12}>
        <Grid container>
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              disabled={!isNew || !hasAccounts}
              name="fromAccount"
              label={isNew ? "From account" : "Account"}
              validate={isNew ? validateSingleMandatoryField : undefined}
              items={formattedAccounts || []}
              fullWidth
            />
            {isNew ? (
              <FormField
                type="select"
                disabled={!hasAccounts}
                name="toAccount"
                label="To account"
                validate={isNew ? validateSingleMandatoryField : undefined}
                items={formattedAccounts || []}
                fullWidth
              />
            ) : null}
            <FormField
              type="money"
              disabled={!isNew}
              name="amount"
              label="Amount"
              validate={isNew ? validateAmountField : undefined}
              fullWidth
            />
            <FormField
              type="date"
              disabled={!isNew}
              name="transactionDate"
              label="Transaction date"
              validate={isNew ? validateSingleMandatoryField : undefined}
              fullWidth
            />
          </Grid>
        </Grid>
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
