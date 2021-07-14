/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { change } from "redux-form";
import Grid from "@material-ui/core/Grid";
import { Lead, LeadStatus, Sale, Tag, User } from "@api/model";
import Button from "@material-ui/core/Button";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { contactLabelCondition, defaultContactName, getContactName, openContactLink } from "../../contacts/utils";
import RelationsCommon from "../../common/components/RelationsCommon";
import { EditViewProps } from "../../../../model/common/ListView";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import { normalizeNumberToZero } from "../../../../common/utils/numbers/numbersNormalizing";
import { mapSelectItems } from "../../../../common/utils/common";
import EntityService from "../../../../common/services/EntityService";
import { decimalMul, decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";

const statusItems = Object.keys(LeadStatus).map(mapSelectItems);

interface Props extends EditViewProps<Lead> {
  tags?: Tag[];
  users?: User[];
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
          `${sel.type}Product`,
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
    manualLink,
    onCloseClick,
    dirty,
    invalid,
    users
  } = props;

  useEffect( () => {
    if (isNew) {
      asyncUpdateEstimatedValue(dispatch, form, values.relatedSellables, values.studentCount).catch(e => console.error(e));
    }
  }, [values.relatedSellables, values.studentCount, isNew]);

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

  const contactIdField = (
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
    />
  );

  return (
    <>
      {twoColumn && (
        <CustomAppBar>
          <Grid container className="flex-fill">
            <Grid item xs={6}>
              {contactIdField}
            </Grid>
          </Grid>
          <div>
            {manualLink && (
              <AppBarHelpMenu
                created={values?.createdOn ? new Date(values.createdOn) : null}
                modified={values?.modifiedOn ? new Date(values.modifiedOn) : null}
                auditsUrl={`audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                manualUrl={manualLink}
              />
            )}
            <Button onClick={onCloseClick} className="closeAppBarButton">
              Close
            </Button>
            <FormSubmitButton
              disabled={(!isNew && !dirty)}
              invalid={invalid}
            />
          </div>
        </CustomAppBar>
      )}
      <div className="generalRoot">
        {!twoColumn && (
          <Grid item xs={12}>
            {contactIdField}
          </Grid>
        )}
        <div className="pt-2">
          {!isNew
            && (
            <FormField
              type="searchSelect"
              name="assignToId"
              label="Assigned to"
              selectValueMark="id"
              selectLabelCondition={contactLabelCondition}
              itemRenderer={ContactSelectItemRenderer}
              defaultDisplayValue={defaultContactName(values.assignTo)}
              disabled={!users}
              items={users}
              rowHeight={55}
              required
            />
          )}
        </div>
        <div>
          <FormField
            type="tags"
            name="tags"
            tags={tags}
            validate={tags && tags.length ? validateTagList : undefined}
          />
        </div>
        <FormField type="number" name="studentCount" label="Number of students" />
        <FormField type="dateTime" name="nextActionOn" label="Next action on" />
        <FormControlLabel
          className="pr-3 mb-2"
          control={<FormField type="checkbox" name="notify" />}
          label="Notify student"
        />
        <FormField
          type="money"
          name="estimatedValue"
          label="Estimated value"
          normalize={normalizeNumberToZero}
        />
        <FormField
          type="select"
          name="status"
          label="Status"
          items={statusItems}
          required
        />
        <CustomFields
          entityName="Lead"
          fieldName="customFields"
          entityValues={values}
          dispatch={dispatch}
          form={form}
        />
        <RelationsCommon
          values={values}
          dispatch={dispatch}
          form={form}
          submitSucceeded={submitSucceeded}
          rootEntity={rootEntity}
          customAqlEntities={["Course", "Product"]}
          dataRowClass="grid-temp-col-2-fr"
        />
      </div>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Lead"],
  users: state.security.activeUsers,
});

export default connect(mapStateToProps)(LeadGeneral);
