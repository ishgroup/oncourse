/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Contact, ProductItem, ProductItemPayment, ProductItemStatus, ProductType } from '@api/model';
import Launch from '@mui/icons-material/Launch';
import { Grid, IconButton } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { compareAsc, format as formatDate, startOfDay } from 'date-fns';
import { EEE_D_MMM_YYYY, LinkAdornment, openInternalLink } from 'ish-ui';
import React, { useCallback } from 'react';
import { change, FieldArray } from 'redux-form';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import OwnApiNotes from '../../../../common/components/form/notes/OwnApiNotes';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import NestedTable from '../../../../common/components/list-view/components/list/ReactTableNestedList';
import { useAppSelector } from '../../../../common/utils/hooks';
import { EditViewProps } from '../../../../model/common/ListView';
import { NestedTableColumn } from '../../../../model/common/NestedTable';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import ContactSelectItemRenderer from '../../contacts/components/ContactSelectItemRenderer';
import { getContactFullName } from '../../contacts/utils';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import { buildUrl, getSaleEntityName, productUrl } from '../utils';

interface SalesGeneralViewProps extends EditViewProps<ProductItem> {
}

const paymentColumns: NestedTableColumn[] = [
  {
    name: "createdOn",
    title: "Created on",
    type: "date-time",
    width: 200
  },
  {
    name: "source",
    title: "Source"
  },
  {
    name: "invoiceNo",
    title: "Invoice number",
    width: 120
  }
];

const columnsByType = (type: ProductType) => {
  if (type === ProductType.Voucher) {
    return paymentColumns.concat([{ name: "amount", title: "Redeemed for", type: "currency" }]);
  }
  return paymentColumns;
};

const openRow = (value: ProductItemPayment) => {
  openInternalLink(`/invoice/${value.id}`);
};

const defaultTags = [];

const SalesEditView: React.FC<SalesGeneralViewProps> = props => {
  const {
    twoColumn,
    values,
    dispatch,
    form,
    showConfirm
  } = props;

  const type = values ? values.productType : undefined;

  const customFieldType: any = type === "Product" ? "Article" : type;

  const redeemableById = values ? values.redeemableById : undefined;

  const contactUrl = buildUrl(redeemableById, "Contacts");

  const formatSaleDate = useCallback(value => (value ? formatDate(new Date(value), EEE_D_MMM_YYYY) : null), []);

  const openLink = useCallback(() => {
    openInternalLink(contactUrl);
  }, [contactUrl]);

  const onRedeemableByIdChange = useCallback(
    (val: Contact) => {
      dispatch(change(form, "redeemableByName", getContactFullName(val)));
    },
    [form]
  );

  const gridItemProps = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  } as any;

  const tags = useAppSelector(state => state.tags?.entityTags[getSaleEntityName(values?.productType)] || defaultTags);

  return values ? (
    <Grid container columnSpacing={3} rowSpacing={2} className={clsx("p-3", twoColumn && "pt-1")}>
      <Grid item xs={12}>
        <FullScreenStickyHeader
          disableInteraction
          twoColumn={twoColumn}
          title={(
            <div className="d-inline-flex-center">
              {values && values.productName}
              <IconButton disabled={!values?.productId} size="small" color="primary" onClick={() => openInternalLink(productUrl(values))}>
                <Launch fontSize="inherit" />
              </IconButton>
            </div>
            )}
        />
      </Grid>
      <Grid item xs={twoColumn ? 8 : 12}>
        <FormField
          type="tags"
          name="tags"
          tags={tags}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <EntityChecklists
          entity={customFieldType}
          form={form}
          entityId={values.id}
          checked={values.tags}
        />
      </Grid>

      <Grid item {...gridItemProps}>
        <Uneditable
          value={values.purchasedByName}
          label={$t('purchased_by')}
          labelAdornment={
            <ContactLinkAdornment id={values.purchasedById} />
          }
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={formatSaleDate(values.purchasedOn)} label={$t('purchased_on')}  />
      </Grid>

      <Grid item container columnSpacing={3} rowSpacing={2} xs={12}>
        <CustomFields
          entityName={customFieldType}
          fieldName="customFields"
          entityValues={values}
          form={form}
          gridItemProps={gridItemProps}
        />
      </Grid>

      {type === ProductType.Voucher && (
        <Grid item {...gridItemProps}>
          <FormField
            type="remoteDataSelect"
            entity="Contact"
            name="redeemableById"
            label={$t('send_invoice_on_redemption_to')}
            selectValueMark="id"
            selectLabelCondition={getContactFullName}
            defaultValue={values.redeemableByName}
            labelAdornment={(
              <LinkAdornment
                link={values.redeemableById}
                clickHandler={openLink}
              />
                )}
            disabled={values.status !== ProductItemStatus.Active}
            itemRenderer={ContactSelectItemRenderer}
            onInnerValueChange={onRedeemableByIdChange}
            rowHeight={55}
            allowEmpty
          />
        </Grid>
        )}
      {type === ProductType.Membership && (
        <Grid item {...gridItemProps}>
          <Uneditable value={formatSaleDate(values.validFrom)} label={$t('valid_from')} />
        </Grid>
      )}
      {(type === ProductType.Membership || type === ProductType.Voucher) && (
        <Grid item {...gridItemProps}>
          <FormField
            type="date"
            name="expiresOn"
            label={$t('expires_on')}
            disabled={
              values.status !== ProductItemStatus.Active
              || compareAsc(startOfDay(new Date()), new Date(values.expiresOn)) > 0
            }
          />
        </Grid>
      )}

      <Grid item {...gridItemProps}>
        <Uneditable value={values.purchasePrice} label={$t('purchase_price')} money />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.status} label={$t('status')} />
      </Grid>
   
      {type === ProductType.Voucher && (
        <Grid item xs={12} container columnSpacing={3} rowSpacing={2}>
          <Grid item {...gridItemProps} className="money">
            <Uneditable value={values.valueRemaining} label={$t('value_remaining')} />
          </Grid>
          <Grid item {...gridItemProps}>
            <Uneditable value={values.voucherCode} label={$t('voucher_code')} />
          </Grid>
        </Grid>
      )}

      <Grid item xs={12} className="mb-3">
        <FieldArray
          name="documents"
          label={$t('documents')}
          entity="ProductItem"
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
        <OwnApiNotes {...props}/>
      </Grid>

      <Grid item xs={12}>
        {type !== ProductType.Product && (
          <FieldArray
            name="payments"
            className="saveButtonTableOffset"
            title={(values && values.payments && values.payments.length) === 1 ? "Payment record" : "Payment records"}
            component={NestedTable}
            columns={columnsByType(values.productType)}
            onRowDoubleClick={openRow}
            rerenderOnEveryChange
            sortBy={(a, b) => new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()}
            calculateHeight
          />
          )}
      </Grid>
    </Grid>
  ) : null;
};

export default SalesEditView;
