/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import Grid from "@mui/material/Grid";
import {
 Lead, LeadStatus, Sale, Tag, User
} from "@api/model";
import Chip from "@mui/material/Chip";
import clsx from "clsx";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import {
 contactLabelCondition, defaultContactName, getContactName, openContactLink
} from "../../contacts/utils";
import RelationsCommon from "../../common/components/RelationsCommon";
import { EditViewProps } from "../../../../model/common/ListView";
import { normalizeNumberToZero } from "../../../../common/utils/numbers/numbersNormalizing";
import { mapSelectItems } from "../../../../common/utils/common";
import EntityService from "../../../../common/services/EntityService";
import { decimalMul, decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { getProductAqlType } from "../../sales/utils";
import { makeAppStyles } from "../../../../common/styles/makeStyles";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const statusItems = Object.keys(LeadStatus).map(mapSelectItems);

const useStyles = makeAppStyles()(() => ({
  chipButton: {
    fontSize: "12px",
    height: "20px",
  }
}));

interface Props extends EditViewProps<Lead> {
  tags?: Tag[];
  users?: User[];
  isScrolling?: boolean;
}

const asyncUpdateEstimatedValue = async (dispatch: Dispatch, form: string, relatedSellables: Sale[], places: number) => {
  let sum = 0;
  await relatedSellables.map(sel => () => {
      if (sel.type === 'Course') {
        return EntityService.getPlainRecords(
          'CourseClass',
          'feeExGst',
          `course.id is ${sel.id} and isActive is true and isCancelled is false`,
          1,
          0,
          'startDateTime',
          false
        ).then(res => {
          if (res.rows.length) {
            const fee = parseFloat(res.rows[0].values[0]);
            sum = decimalPlus(sum, decimalMul(fee, places));
          }
        });
      }
      if (['Product', 'Membership', 'Voucher'].includes(sel.type)) {
        return EntityService.getPlainRecords(
          getProductAqlType(sel.type),
          'priceExTax',
          `id is ${sel.id}`,
          1
        ).then(res => {
          if (res.rows.length) {
            const fee = parseFloat(res.rows[0].values[0]);
            sum = decimalPlus(sum, decimalMul(fee, places));
          }
        });
      }
      return Promise.resolve();
    }).reduce(async (a, b) => {
    await a;
    await b();
  }, Promise.resolve());

  dispatch(change(form, 'estimatedValue', sum));
};

const LeadGeneral = (props: Props) => {
  const {
    values,
    tags,
    dispatch,
    form,
    rootEntity,
    submitSucceeded,
    twoColumn,
    isNew,
    users
  } = props;

  const { classes } = useStyles();

  const validateTagList = (value, allValues) => validateTagsList(tags, value, allValues, props);

  const onContactChange = value => {
    dispatch(change(form, "contactName", getContactName(value)));
  };

  const contactIdTwoColumnProps = useMemo(
    () => ({
      placeholder: "Contact",
      endAdornment: (
        <LinkAdornment
          link={values.contactId}
          linkHandler={openContactLink}
          linkColor="inherit"
          className="appHeaderFontSize pl-0-5"
        />
      ),
      formatting: "inline",
      fieldClasses: {
        text: "appHeaderFontSize primaryContarstText primaryContarstHover text-nowrap text-truncate",
        input: "primaryContarstText",
        underline: "primaryContarstUnderline",
        selectMenu: "textPrimaryColor",
        loading: "primaryContarstText",
        editIcon: "primaryContarstText"
      }
    }),
    [values.contactId]
  );

  const contactIdThreecolumnProps = useMemo(
    () => ({
      labelAdornment: <LinkAdornment link={values.contactId} linkHandler={openContactLink} />,
      label: "Contact"
    }),
    [values.contactId]
  );

  const contactIdField = (inHeader: boolean = false) => (
    <FormField
      type="remoteDataSearchSelect"
      entity="Contact"
      name="contactId"
      selectValueMark="id"
      selectLabelCondition={contactLabelCondition}
      defaultDisplayValue={defaultContactName(values.contactName)}
      onInnerValueChange={onContactChange}
      itemRenderer={ContactSelectItemRenderer}
      props={twoColumn ? contactIdTwoColumnProps : contactIdThreecolumnProps}
      disabled={!isNew}
      rowHeight={55}
      required
      inHeader
    />
  );

  return (
    <>
      {twoColumn && (
        <FullScreenStickyHeader
          twoColumn={twoColumn}
          title={(
            <div className="centeredFlex">
              <span className="d-none">{contactIdField(true)}</span>
              <span className="mr-1">
                {values && defaultContactName(values.contactName)}
              </span>
              <LinkAdornment
                linkHandler={() => openContactLink(values.contactId)}
                link={values.contactId}
                className="appHeaderFontSize"
              />
            </div>
          )}
        />
      )}
      <Grid container columnSpacing={3} className="generalRoot mt-2">
        {!twoColumn && (
          <Grid item xs={12} className="pt-2">
            {contactIdField(false)}
          </Grid>
        )}
        <Grid item xs={twoColumn ? 6 : 12}>
          {!isNew
            && (
            <FormField
              type="searchSelect"
              name="assignToId"
              label="Assigned to"
              selectValueMark="id"
              selectLabelCondition={contactLabelCondition}
              defaultDisplayValue={defaultContactName(values.assignTo)}
              disabled={!users}
              items={users}
              required
            />
          )}
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="tags"
            name="tags"
            tags={tags}
            validate={tags && tags.length ? validateTagList : undefined}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="number" name="studentCount" label="Number of students" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="dateTime" name="nextActionOn" label="Next action on" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <div className="centeredFlex">
            <FormField
              type="money"
              name="estimatedValue"
              label="Estimated value"
              normalize={normalizeNumberToZero}
            />
            <Chip
              size="small"
              label="Calculate"
              className={clsx(classes.chipButton, "ml-2, mt-1")}
              onClick={() => (
                asyncUpdateEstimatedValue(dispatch, form, values.relatedSellables, values.studentCount).catch(e => console.error(e))
              )}
            />
          </div>
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="select"
            name="status"
            label="Status"
            items={statusItems}
            required
          />
        </Grid>
        <CustomFields
          entityName="Lead"
          fieldName="customFields"
          entityValues={values}
          dispatch={dispatch}
          form={form}
          gridItemProps={{
            xs: twoColumn ? 6 : 12,
          }}
        />
        <Grid item xs={12}>
          <RelationsCommon
            values={values}
            dispatch={dispatch}
            form={form}
            submitSucceeded={submitSucceeded}
            rootEntity={rootEntity}
            customAqlEntities={["Course", "Product"]}
            dataRowClass="grid-temp-col-2-fr"
          />
        </Grid>
      </Grid>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Lead"],
  users: state.security.activeUsers,
});

export default connect(mapStateToProps)(LeadGeneral);
