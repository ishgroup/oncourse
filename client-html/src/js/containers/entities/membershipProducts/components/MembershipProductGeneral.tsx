/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, ExpiryType, MembershipProduct, ProductStatus, Tag, Tax } from '@api/model';
import { Grid } from '@mui/material';
import $t from '@t';
import { normalizeNumber } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';
import CustomSelector, { CustomSelectorOption } from '../../../../common/components/custom-selector/CustomSelector';
import { FormEditorField } from '../../../../common/components/form/formFields/FormEditor';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { normalizeString } from '../../../../common/utils/strings';
import { validateSingleMandatoryField } from '../../../../common/utils/validation';
import { EditViewProps } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import { PreferencesState } from '../../../preferences/reducers/state';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import { useTagGroups } from '../../../tags/utils/useTagGroups';
import {
  handleChangeProductAccount,
  handleChangeProductFeeExTax,
  handleChangeProductFeeIncTax,
  handleChangeProductTax
} from '../../common/utils';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';

interface MembershipProductGeneralProps extends EditViewProps<MembershipProduct> {
  accounts?: Account[];
  taxes?: Tax[];
  tags?: Tag[];
  dataCollectionRules?: PreferencesState["dataCollectionRules"];
}

const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);

const productStatusItems = Object.keys(ProductStatus).map(value => ({ value }));

const expiryOptions: CustomSelectorOption[] = [
  { caption: "Never (Lifetime)", body: "" },
  {
    caption: "1st July",
    body: "",
    formFileldProps: {
      type: "text",
      name: "expiryType",
    }
  },
  {
    caption: "1st January",
    body: "",
    formFileldProps: {
      type: "text",
      name: "expiryType",
    }
  },
  {
    caption: "Days",
    body: "",
    formFileldProps: {
      type: "number",
      name: "expiryDays",
      validate: [validateNonNegative, validateSingleMandatoryField],
      normalize: normalizeNumber
    }
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

const MembershipProductGeneral: React.FC<MembershipProductGeneralProps> = props => {
  const {
    twoColumn, accounts, taxes, values, dispatch, form, dataCollectionRules, isNew, syncErrors, tags
  } = props;
  const initialIndexExpiry = getInitialIndexExpiry(values);

  const validateIncomeAccount = useCallback(value => (accounts.find((item: Account) => item.id === value) ? undefined : `Income account is mandatory`), [accounts]);

  const { tagsGrouped, subjectsField } = useTagGroups({ tags, tagsValue: values.tags, dispatch, form });

  const taxRate = useMemo(() => taxes.find(t => t.id === values.taxId)?.rate, [taxes, values.taxId]);

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
                  type="text"
                  label={$t('sku')}
                  name="code"
                  required
                />
              </Grid>
              <Grid item xs={twoColumn ? 4 : 12}>
                <FormField
                  type="text"
                  label={$t('name')}
                  name="name"
                  required
                />
              </Grid>
            </Grid>
          )}
        />
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12}>
        <FormField
          type="tags"
          name="tags"
          tags={tagsGrouped.tags}
          className="mb-2"
        />

        {subjectsField}
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <EntityChecklists
          entity="MembershipProduct"
          form={form}
          entityId={values.id}
          checked={values.tags}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="incomeAccountId"
          label={$t('income_account')}
          validate={validateIncomeAccount}
          onChange={handleChangeProductAccount(values, taxes, accounts, dispatch, form)}
          debounced={false}
          items={accounts}
          selectValueMark="id"
          selectLabelCondition={a => `${a.accountCode}, ${a.description}`}
        />
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormEditorField name="description" label={$t('description')} />
      </Grid>

      <CustomFields
        entityName="MembershipProduct"
        fieldName="customFields"
        entityValues={values}
        form={form}
        gridItemProps={{
          xs: twoColumn ? 6 : 12
        }}
      />
      
      <Grid item xs={twoColumn ? 2 : 4}>
        <FormField
          type="money"
          name="feeExTax"
          validate={[validateSingleMandatoryField, validateNonNegative]}
          onChange={handleChangeProductFeeExTax(taxRate, dispatch, form)}
          debounced={false}
          label={$t('fee_ex_tax')}
        />
      </Grid>
      <Grid item xs={twoColumn ? 2 : 4}>
        <FormField
          type="money"
          name="totalFee"
          validate={validateNonNegative}
          onChange={handleChangeProductFeeIncTax(taxRate, dispatch, form)}
          debounced={false}
          label={$t('total_fee')}
        />
      </Grid>
      <Grid item xs={twoColumn ? 2 : 4}>
        <FormField
          type="select"
          name="taxId"
          onChange={handleChangeProductTax(taxes, dispatch, form, values.feeExTax)}
          debounced={false}
          label={$t('tax')}
          items={taxes}
          selectValueMark="id"
          selectLabelCondition={tax => tax.code}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="status"
          label={$t('status')}
          items={productStatusItems}
          selectLabelMark="value"
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="dataCollectionRuleId"
          label={$t('data_collection_rule')}
          selectValueMark="id"
          selectLabelMark="name"
          items={dataCollectionRules || []}
          format={normalizeString}
          className={twoColumn && "mt-1"}
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
  taxes: state.taxes.items,
  tags: state.tags.entityTags["MembershipProduct"]
});

export default connect<any, any, any>(mapStateToProps)(MembershipProductGeneral);
