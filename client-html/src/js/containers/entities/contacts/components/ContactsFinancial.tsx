/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import React, {
 useCallback, useState
} from "react";
import { Contact, PaymentMethod, Tax } from "@api/model";
import { change, FieldArray } from "redux-form";
import IconButton from "@material-ui/core/IconButton";
import LockOpen from "@material-ui/icons/LockOpen";
import Lock from "@material-ui/icons/Lock";
import { connect } from "react-redux";
import { Grid } from "@material-ui/core";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { openInternalLink } from "../../../../common/utils/links";
import { State } from "../../../../reducers/state";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { ContactsState } from "../reducers";
import { getTableWrapperHeight } from "../utils";
import { EditViewProps } from "../../../../model/common/ListView";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";

interface ContactsFinancialProps extends EditViewProps<Contact> {
  taxTypes?: Tax[];
  defaultTerms?: number;
  paymentTypes?: PaymentMethod[];
  storedCard?: ContactsState["storedCard"];
  access?: AccessState;
}

const getFormattedTaxes = (taxes: Tax[]) => {
  const formattedTaxes = taxes.map(tax => ({
    value: tax.id,
    label: `${tax.code}`
  }));
  formattedTaxes.push({ value: null, label: "Not set" });
  return formattedTaxes;
};

const financialColumns: NestedTableColumn[] = [
  {
    name: "description",
    title: "Type",
    width: 300
  },
  {
    name: "date",
    title: "Date",
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

const openRow = value => {
  const { type, relatedEntityId } = value;

  const route = type[0].toLowerCase() + type.substring(1);
  openInternalLink(`/${route}/${relatedEntityId}`);
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
    access
  } = props;

  const [lockedTerms, setLockedTerms] = useState(true);

  const onLockClick = useCallback(() => {
    if (!lockedTerms) {
      dispatch(change(form, "invoiceTerms", null));
    }

    if (lockedTerms) {
      dispatch(change(form, "invoiceTerms", defaultTerms));
    }

    setLockedTerms(prev => !prev);
  }, [defaultTerms, lockedTerms]);

  const getFinancialRecordsCount = useCallback(
    () => (values && Array.isArray(values.financialData) ? values.financialData.length : 0),
    [values.financialData]
  );

  const getFinancialTableTitle = () => (getFinancialRecordsCount() === 1 ? "financial record" : "financial records");

  const removeStoredCreditCard = () => {
    dispatch(change(form, "removeCChistory", true));
  };

  const paymentInPermissions = access["/a/v1/list/plain?entity=PaymentIn"] && access["/a/v1/list/plain?entity=PaymentIn"]["GET"];

  return values ? (
    <div className="p-3">
      <ExpandableContainer index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Financial">
        <Grid container>
          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="number"
              name="invoiceTerms"
              label="Invoice terms (days)"
              defaultValue={defaultTerms}
              disabled={lockedTerms}
              labelAdornment={(
                <span>
                  <IconButton className="inputAdornmentButton" onClick={onLockClick}>
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
              fullWidth
            />
          </Grid>
          {paymentInPermissions && storedCard && !values.removeCChistory
          && (
            <Grid item xs={12} className="centeredFlex mb-3">
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
            </Grid>
          )}
          <Grid
            item
            xs={12}
            className="flex-column"
            style={{
              height: values.financialData && getTableWrapperHeight(values.financialData.length)
            }}
          >
            <FieldArray
              name="financialData"
              title={getFinancialTableTitle()}
              component={NestedTable}
              columns={financialColumns}
              onRowDoubleClick={openRow}
              rerenderOnEveryChange
              hideHeader
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

export default connect<any, any, any>(mapStateToProps, null)(ContactsFinancial);
