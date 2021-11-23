/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { change, FieldArray } from "redux-form";
import {
 Account, ArticleProduct, ProductStatus, Tag, Tax 
} from "@api/model";
import { connect } from "react-redux";
import { Grid } from "@mui/material";
import { Decimal } from "decimal.js-light";
import FormField from "../../../../common/components/form/formFields/FormField";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";
import { State } from "../../../../reducers/state";
import RelationsCommon from "../../common/components/RelationsCommon";
import { EditViewProps } from "../../../../model/common/ListView";
import { PreferencesState } from "../../../preferences/reducers/state";
import { normalizeString } from "../../../../common/utils/strings";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";

interface ArticleProductGeneralProps extends EditViewProps<ArticleProduct> {
  accounts?: Account[];
  taxes?: Tax[];
  tags?: Tag[];
  dataCollectionRules?: PreferencesState["dataCollectionRules"];
}

const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);

const productStatusItems = Object.keys(ProductStatus).map(value => ({ value }));

const handleChangeFeeExTax = (values: ArticleProduct, taxes: Tax[], dispatch, form) => value => {
  const tax = taxes.find(item => item.id === values.taxId);
  const taxRate = tax ? tax.rate : 0;
  dispatch(change(form, "totalFee", new Decimal(value * (1 + taxRate)).toDecimalPlaces(2).toNumber()));
};

const handleChangeFeeIncTax = (values: ArticleProduct, taxes: Tax[], dispatch, form) => value => {
  const tax = taxes.find(item => item.id === values.taxId);
  const taxRate = tax ? tax.rate : 0;
  dispatch(change(form, "feeExTax", new Decimal(value / (1 + taxRate)).toDecimalPlaces(2).toNumber()));
};

const handleChangeTax = (values: ArticleProduct, taxes: Tax[], dispatch, form) => value => {
  const tax = taxes.find(item => item.id === value);
  const taxRate = tax ? tax.rate : 0;
  dispatch(change(form, "totalFee", new Decimal(values.feeExTax * (1 + taxRate)).toDecimalPlaces(2).toNumber()));
};

const handleChangeAccount = (values: ArticleProduct, taxes: Tax[], accounts: Account[], dispatch, form) => value => {
  const account = accounts.find(item => item.id === value);
  const tax = taxes.find(item => item.id === Number(account["tax.id"]));
  if (tax.id !== values.taxId) {
    const taxRate = tax ? tax.rate : 0;
    dispatch(change(form, "taxId", tax.id));
    dispatch(change(form, "totalFee", new Decimal(values.feeExTax * (1 + taxRate)).toDecimalPlaces(2).toNumber()));
  }
};

const ArticleProductGeneral: React.FC<ArticleProductGeneralProps> = props => {
  const {
    twoColumn, accounts, isNew, taxes, showConfirm, tags, values, dispatch, form, syncErrors, submitSucceeded, rootEntity, dataCollectionRules
  } = props;

  const gridItemProps = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  } as any;

  return (

    <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pl-3 pr-3">
      <Grid item container xs={12}>
        <FullScreenStickyHeader
          opened={isNew || Object.keys(syncErrors).includes("name")}
          twoColumn={twoColumn}
          title={<span>{values && values.name}</span>}
          fields={(
            <Grid item {...gridItemProps}>
              <FormField
                name="name"
                label="Name"
                required
                fullWidth
              />
            </Grid>
          )}
        />
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="tags"
          name="tags"
          tags={tags}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="text"
          name="code"
          label="SKU"
          required
        />
      </Grid>

      <Grid item {...gridItemProps}>
        <FormField
          type="text"
          name="code"
          label="SKU"
          required
        />
      </Grid>

      <Grid item {...gridItemProps}>
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

      <Grid item {...gridItemProps}>
        <FormField
          type="money"
          name="feeExTax"
          label="Fee ex tax"
          validate={validateNonNegative}
          onChange={handleChangeFeeExTax(values, taxes, dispatch, form)}
          required
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="money"
          name="totalFee"
          label="Total fee"
          validate={validateNonNegative}
          onChange={handleChangeFeeIncTax(values, taxes, dispatch, form)}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="select"
          label="Tax"
          name="taxId"
          onChange={handleChangeTax(values, taxes, dispatch, form)}
          items={taxes}
          selectValueMark="id"
          selectLabelCondition={tax => tax.code}
          required
        />
      </Grid>

      <Grid item {...gridItemProps}>
        <FormField
          type="select"
          name="status"
          label="Status"
          items={productStatusItems}
          selectLabelMark="value"
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="select"
          name="dataCollectionRuleId"
          label="Data collection rule"
          selectValueMark="id"
          selectLabelMark="name"
          items={dataCollectionRules || []}
          format={normalizeString}
          fullWidth
          required
          sort
        />
      </Grid>

      <Grid item xs={12}>
        <FormEditorField name="description" label="Description" />
      </Grid>

      <Grid item xs={12} className="mb-3">
        <FieldArray
          name="documents"
          label="Documents"
          entity="ArticleProduct"
          component={DocumentsRenderer}
          xsGrid={12}
          mdGrid={twoColumn ? 6 : 12}
          lgGrid={twoColumn ? 4 : 12}
          dispatch={dispatch}
          form={form}
          showConfirm={showConfirm}
          rerenderOnEveryChange
        />
      </Grid>

      <Grid item xs={12}>
        <RelationsCommon
          values={values}
          dispatch={dispatch}
          form={form}
          submitSucceeded={submitSucceeded}
          rootEntity={rootEntity}
        />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  accounts: state.plainSearchRecords.Account.items,
  taxes: state.taxes.items,
  tags: state.tags.entityTags["ArticleProduct"],
  dataCollectionRules: state.preferences.dataCollectionRules
});

export default connect<any, any, any>(mapStateToProps)(ArticleProductGeneral);
