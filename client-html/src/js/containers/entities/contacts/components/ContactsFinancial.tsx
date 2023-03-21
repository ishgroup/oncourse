/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import React, {
  useCallback, useMemo, useState
} from "react";
import {
  Cart, Contact, PaymentMethod, Tax
} from "@api/model";
import { change, FieldArray } from "redux-form";
import IconButton from "@mui/material/IconButton";
import LockOpen from "@mui/icons-material/LockOpen";
import Lock from "@mui/icons-material/Lock";
import { connect } from "react-redux";
import { Alert, Grid } from "@mui/material";
import FormField from "../../../../common/components/form/formFields/FormField";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { openInternalLink } from "../../../../common/utils/links";
import { State } from "../../../../reducers/state";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { ContactsState } from "../reducers";
import { EditViewProps } from "../../../../model/common/ListView";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";

interface ContactsFinancialProps extends EditViewProps<Contact> {
  taxTypes?: Tax[];
  defaultTerms?: number;
  paymentTypes?: PaymentMethod[];
  storedCard?: ContactsState["storedCard"];
  access?: AccessState;
}

const getFormattedTaxes = (taxes: Tax[]) => taxes.map(tax => ({
  value: tax.id,
  label: `${tax.code}`
}));

const financialColumns: NestedTableColumn[] = [
  {
    name: "description",
    title: "Type",
    width: 300
  },
  {
    name: "date",
    title: "Created on",
    type: "date",
    width: 160
  },
  {
    name: "referenceNumber",
    title: "Reference"
  },
  {
    name: "status",
    title: "Status",
    width: 100
  },
  {
    name: "owing",
    title: "Owing",
    type: "currency"
  },
  {
    name: "amount",
    title: "Amount",
    type: "currency"
  },
  {
    name: "balance",
    title: "Balance",
    type: "currency"
  }
];

const shopingCartColumns: NestedTableColumn[] = [
  {
    name: "createdOn",
    title: "Created on",
    type: "date",
    width: 160
  },
  {
    name: "totalValue",
    title: "Total",
    type: "currency"
  }
];

const openRow = value => {
  const { type, relatedEntityId } = value;

  const route = type[0].toLowerCase() + type.substring(1);
  openInternalLink(`/${route}/${relatedEntityId}`);
};

const openShopingCartRow = (row: Cart) => {
  openInternalLink(`/checkout?cartId=${row.id}`);
};

const ContactsFinancial: React.FC<ContactsFinancialProps> = props => {
  const {
    twoColumn,
    values,
    defaultTerms,
    taxTypes,
    form,
    dispatch,
    tabIndex,
    expanded,
    setExpanded,
    storedCard,
    access,
    syncErrors
  } = props;

  const [lockedTerms, setLockedTerms] = useState(true);
  
  const removeShopingCartRow = id => {
    console.log(id);
    dispatch(change(form, "abandonedCarts", values.abandonedCarts.filter(c => c.id !== id)));
  };

  const onLockClick = useCallback(e => {
    e.preventDefault();
    if (!lockedTerms) {
      dispatch(change(form, "invoiceTerms", null));
    }

    if (lockedTerms) {
      dispatch(change(form, "invoiceTerms", defaultTerms));
    }

    setLockedTerms(prev => !prev);
  }, [defaultTerms, lockedTerms]);
  
  const financialTableTitle = useMemo(() => {
    const financialRecordsCount = (values && Array.isArray(values.financialData) ? values.financialData.length : 0);

    return financialRecordsCount === 1 ? "financial record" : "financial records";
  }, [values.financialData]);

  const removeStoredCreditCard = () => {
    dispatch(change(form, "removeCChistory", true));
  };

  const paymentInPermissions = access["/a/v1/list/plain?entity=PaymentIn"] && access["/a/v1/list/plain?entity=PaymentIn"]["GET"];

  return values ? (
    <div className="pl-3 pr-3">
      <ExpandableContainer formErrors={syncErrors} index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Financial">
        <Grid container columnSpacing={3} rowSpacing={2} className="pb-3">
          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="number"
              name="invoiceTerms"
              label="Invoice terms (days)"
              defaultValue={defaultTerms}
              disabled={lockedTerms}
              labelAdornment={(
                <span>
                  <IconButton className="inputAdornmentButton" onClick={e => onLockClick(e)}>
                    {!lockedTerms && <LockOpen className="inputAdornmentIcon" />}
                    {lockedTerms && <Lock className="inputAdornmentIcon" />}
                  </IconButton>
                </span>
              )}
            />
          </Grid>
          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="select"
              name="taxId"
              label="Tax type"
              items={getFormattedTaxes(taxTypes) || []}
              placeholder="Not set"
            />
          </Grid>
          {paymentInPermissions && storedCard && !values.removeCChistory
          && (
            <Grid item xs={12} className="centeredFlex mb-3 mt-2">
              <Alert severity="info">
                <Typography variant="body2">
                  <div>
                    A credit card was collected on
                    {' '}
                    {storedCard.created}
                    {' '}
                    and is securely stored for this user.
                  </div>
                  <div className="centeredFlex">
                    {storedCard.creditCardType}
                    {" "}
                    {storedCard.creditCardNumber}
                    <Button
                      onClick={removeStoredCreditCard}
                      size="small"
                      variant="text"
                      className="errorColor ml-2"
                    >
                      delete
                    </Button>
                  </div>
                </Typography>
              </Alert>
            </Grid>
          )}
          <Grid
            item
            xs={12}
            className="flex-column"
          >
            <FieldArray
              name="financialData"
              title={financialTableTitle}
              component={NestedTable}
              columns={financialColumns}
              onRowDoubleClick={openRow}
              rerenderOnEveryChange
              calculateHeight
            />
          </Grid>

          <Grid
            item
            xs={12}
            className="flex-column"
          >
            <FieldArray
              name="abandonedCarts"
              title={`Abandoned shopping cart${values.abandonedCarts?.length !== 1 ? "s" : ""}`}
              component={NestedTable}
              columns={shopingCartColumns}
              onRowDoubleClick={openShopingCartRow}
              onRowDelete={removeShopingCartRow}
              sortBy={(a, b) => new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()}
              rerenderOnEveryChange
              calculateHeight
            />
          </Grid>
        </Grid>
      </ExpandableContainer>
    </div>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  defaultTerms: state.invoices.defaultTerms,
  taxTypes: state.contacts.taxTypes,
  storedCard: state.contacts.storedCard,
  access: state.access
});

export default connect<any, any, any>(mapStateToProps)(ContactsFinancial);