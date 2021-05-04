/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import {
  Contact,
  ProductItem, ProductItemPayment,
  ProductItemStatus,
  ProductType
} from "@api/model";
import { change, FieldArray } from "redux-form";
import { compareAsc, format as formatDate, startOfDay } from "date-fns";
import { Grid } from "@material-ui/core";
import FormField from "../../../../common/components/form/form-fields/FormField";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { openInternalLink } from "../../../../common/utils/links";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { EEE_D_MMM_YYYY } from "../../../../common/utils/dates/format";
import Uneditable from "../../../../common/components/form/Uneditable";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { contactLabelCondition } from "../../contacts/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { buildUrl, productUrl } from "../utils";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";

interface SalesGeneralViewProps {
  classes?: any;
  twoColumn?: boolean;
  manualLink?: string;
  values?: ProductItem;
  dispatch?: any;
  form?: string;
}

const nameLabel = (type: ProductType) => {
  switch (type) {
    case ProductType.Membership:
      return "Membership name";
    case ProductType.Voucher:
      return "Voucher name";
    default:
      return "Product name";
  }
};

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

const SalesEditView: React.FC<SalesGeneralViewProps> = props => {
  const {
    twoColumn,
    values,
    dispatch,
    form
  } = props;

  const type = values ? values.productType : undefined;

  const redeemableById = values ? values.redeemableById : undefined;

  const contactUrl = buildUrl(redeemableById, "Contacts");

  const formatSaleDate = useCallback(value => (value ? formatDate(new Date(value), EEE_D_MMM_YYYY) : null), []);

  const openLink = useCallback(() => {
    openInternalLink(contactUrl);
  }, [contactUrl]);

  const onRedeemableByIdChange = useCallback(
    (val: Contact) => {
      dispatch(change(form, "redeemableByName", contactLabelCondition(val)));
    },
    [form]
  );

  return values ? (
    <div className="pl-3 pr-3 flex-column h-100">
      <Uneditable
        className="pt-2"
        value={values.productName}
        label={nameLabel(type)}
        url={productUrl(values)}
      />

      <Grid container>
        <Grid item xs={twoColumn ? 6 : 12}>
          <Uneditable
            value={values.purchasedByName}
            label="Purchased by"
            url={buildUrl(values.purchasedById, "Contacts")}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <Uneditable value={formatSaleDate(values.purchasedOn)} label="Purchased on" />
        </Grid>
      </Grid>

      <CustomFields
        entityName={type}
        fieldName="customFields"
        entityValues={values}
        dispatch={dispatch}
        form={form}
      />

      <Grid container>
        {type === ProductType.Voucher && (
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSearchSelect"
              entity="Contact"
              name="redeemableById"
              label="Send invoice on redemption to"
              selectValueMark="id"
              selectLabelCondition={contactLabelCondition}
              defaultDisplayValue={values.redeemableByName}
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
          <Grid item xs={twoColumn ? 6 : 12}>
            <Uneditable value={formatSaleDate(values.validFrom)} label="Valid from" />
          </Grid>
        )}
        {(type === ProductType.Membership || type === ProductType.Voucher) && (
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="date"
              name="expiresOn"
              label="Expires On"
              disabled={
                values.status !== ProductItemStatus.Active
                || compareAsc(startOfDay(new Date()), new Date(values.expiresOn)) > 0
              }
            />
          </Grid>
        )}
      </Grid>
      <Grid container>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.purchasePrice} label="Purchase price" money />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.status} label="Status" />
        </Grid>
      </Grid>
      {type === ProductType.Voucher && (
        <Grid container>
          <Grid item xs={twoColumn ? 2 : 6}>
            <Uneditable value={values.valueRemaining} label="Value remaining" />
          </Grid>
          <Grid item xs={twoColumn ? 2 : 6}>
            <Uneditable value={values.voucherCode} label="Voucher code" />
          </Grid>
        </Grid>
      )}
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
        />
      )}
    </div>
  ) : null;
};

export default SalesEditView;
