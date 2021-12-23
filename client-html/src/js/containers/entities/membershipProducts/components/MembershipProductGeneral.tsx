/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { change } from "redux-form";
import {
 Account, ExpiryType, MembershipProduct, ProductStatus, Tax 
} from "@api/model";
import { connect } from "react-redux";
import { Grid } from "@mui/material";
import { Decimal } from "decimal.js-light";
import EditInPlaceField from "../../../../common/components/form/formFields/EditInPlaceField";
import FormField from "../../../../common/components/form/formFields/FormField";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";
import { State } from "../../../../reducers/state";
import { normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";
import CustomSelector, { CustomSelectorOption } from "../../../../common/components/custom-selector/CustomSelector";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { PreferencesState } from "../../../preferences/reducers/state";
import { normalizeString } from "../../../../common/utils/strings";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { EditViewProps } from "../../../../model/common/ListView";

interface MembershipProductGeneralProps extends EditViewProps<MembershipProduct>{
  accounts?: Account[];
  taxes?: Tax[];
  dataCollectionRules?: PreferencesState["dataCollectionRules"];
}

const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);

const productStatusItems = Object.keys(ProductStatus).map(value => ({ value }));

const expiryOptions: CustomSelectorOption[] = [
  { caption: "Never (Lifetime)", body: "", type: null },
  {
    caption: "1st July",
    body: "",
    type: null,
    fieldName: "expiryType",
    component: EditInPlaceField
  },
  {
    caption: "1st January",
    body: "",
    type: null,
    fieldName: "expiryType",
    component: EditInPlaceField
  },
  {
    caption: "Days",
    body: "",
    type: "number",
    component: EditInPlaceField,
    fieldName: "expiryDays",
    validate: [validateNonNegative, validateSingleMandatoryField],
    normalize: normalizeNumber
  }
];

const onSelectExpiry = props => index => {
  const { dispatch, form } = props;
  if (index === 0) {
    dispatch(change(form, "expiryType", "Never (Lifetime)"));
    dispatch(change(form, "expiryDays", null));
  }
  if (index === 1) {
    dispatch(change(form, "expiryType", "1st July"));
    dispatch(change(form, "expiryDays", null));
  }
  if (index === 2) {
    dispatch(change(form, "expiryType", "1st January"));
    dispatch(change(form, "expiryDays", null));
  }
  if (index === 3) {
    dispatch(change(form, "expiryType", "Days"));
  }
};

const getInitialIndexExpiry = (product: MembershipProduct) => {
  if (!product || product.expiryType === ExpiryType["Never (Lifetime)"]) {
    return 0;
  }
  if (product.expiryType === ExpiryType["1st July"]) {
    return 1;
  }
  if (product.expiryType === ExpiryType["1st January"]) {
    return 2;
  }
  return 3;
};

const handleChangeFeeExTax = (values: MembershipProduct, taxes: Tax[], dispatch, form) => value => {
  const tax = taxes.find(item => item.id === values.taxId);
  const taxRate = tax ? tax.rate : 0;
  dispatch(change(form, "totalFee", new Decimal(value * (1 + taxRate)).toDecimalPlaces(2).toNumber()));
};

const handleChangeFeeIncTax = (values: MembershipProduct, taxes: Tax[], dispatch, form) => value => {
  const tax = taxes.find(item => item.id === values.taxId);
  const taxRate = tax ? tax.rate : 0;
  dispatch(change(form, "feeExTax", new Decimal(value / (1 + taxRate)).toDecimalPlaces(2).toNumber()));
};

const handleChangeTax = (values: MembershipProduct, taxes: Tax[], dispatch, form) => value => {
  const tax = taxes.find(item => item.id === value);
  const taxRate = tax ? tax.rate : 0;
  dispatch(change(form, "totalFee", new Decimal(values.feeExTax * (1 + taxRate)).toDecimalPlaces(2).toNumber()));
};

const handleChangeAccount = (values: MembershipProduct, taxes: Tax[], accounts: Account[], dispatch, form) => value => {
  const account = accounts.find(item => item.id === value);
  const tax = taxes.find(item => item.id === Number(account["tax.id"]));
  if (tax.id !== values.taxId) {
    const taxRate = tax ? tax.rate : 0;
    dispatch(change(form, "taxId", tax.id));
    dispatch(change(form, "totalFee", new Decimal(values.feeExTax * (1 + taxRate)).toDecimalPlaces(2).toNumber()));
  }
};

const MembershipProductGeneral: React.FC<MembershipProductGeneralProps> = props => {
  const {
    twoColumn, accounts, taxes, values, dispatch, form, dataCollectionRules, isNew, syncErrors
  } = props;
  const initialIndexExpiry = getInitialIndexExpiry(values);
  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
      <Grid item container xs={12}>
        <FullScreenStickyHeader
          opened={isNew || Object.keys(syncErrors).some(k => ['code', 'name'].includes(k))}
          twoColumn={twoColumn}
          title={twoColumn ? (
            <div className="d-inline-flex-center">
              <span>
                {values && values.code}
              </span>
              <span className="ml-2">
                {values && values.name}
              </span>
            </div>
          ) : (
            <div>
              <div>
                {values && values.code}
              </div>
              <div className="mt-2">
                {values && values.name}
              </div>
            </div>
          )}
          fields={(
            <Grid container columnSpacing={3} rowSpacing={2}>
              <Grid item xs={twoColumn ? 2 : 12}>
                <FormField
                  label="Code"
                  name="code"
                  required
                  fullWidth
                />
              </Grid>
              <Grid item xs={twoColumn ? 4 : 12}>
                <FormField
                  label="SKU"
                  name="name"
                  required
                  fullWidth
                />
              </Grid>
            </Grid>
          )}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="incomeAccountId"
          label="Income account"
          validate={value => (accounts.find((item: Account) => item.id === value) ? undefined : `Mandatory field`)}
          onChange={handleChangeAccount(values, taxes, accounts, dispatch, form)}
          items={accounts}
          selectValueMark="id"
          selectLabelCondition={a => `${a.accountCode}, ${a.description}`}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormEditorField name="description" label="Description" />
      </Grid>
      
      <Grid item xs={twoColumn ? 2 : 4}>
        <FormField
          type="money"
          name="feeExTax"
          validate={[validateSingleMandatoryField, validateNonNegative]}
          onChange={handleChangeFeeExTax(values, taxes, dispatch, form)}
          props={{
            label: "Fee ex tax"
          }}
        />
      </Grid>
      <Grid item xs={twoColumn ? 2 : 4}>
        <FormField
          type="money"
          name="totalFee"
          validate={validateNonNegative}
          onChange={handleChangeFeeIncTax(values, taxes, dispatch, form)}
          props={{
            label: "Total fee"
          }}
        />
      </Grid>
      <Grid item xs={twoColumn ? 2 : 4}>
        <FormField
          type="select"
          name="taxId"
          onChange={handleChangeTax(values, taxes, dispatch, form)}
          required
          props={{
            label: "Tax",
            items: taxes,
            selectValueMark: "id",
            selectLabelCondition: tax => tax.code
          }}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="status"
          label="Status"
          items={productStatusItems}
          selectLabelMark="value"
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="dataCollectionRuleId"
          label="Data collection rule"
          selectValueMark="id"
          selectLabelMark="name"
          items={dataCollectionRules || []}
          format={normalizeString}
          required
          sort
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 12}>
        <CustomSelector
          caption="Expires"
          options={expiryOptions}
          onSelect={onSelectExpiry(props)}
          initialIndex={initialIndexExpiry}
        />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  dataCollectionRules: state.preferences.dataCollectionRules,
  accounts: state.plainSearchRecords.Account.items,
  taxes: state.taxes.items
});

export default connect<any, any, any>(mapStateToProps)(MembershipProductGeneral);
