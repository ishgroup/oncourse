/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import { connect } from "react-redux";
import clsx from "clsx";
import { Dispatch } from "redux";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { change } from "redux-form";
import Grid from "@material-ui/core/Grid";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import RelationsCommon from "../../common/components/RelationsCommon";
import { setSelectedContact } from "../../invoices/actions";

const items = [{ label: "Open", value: true }, { label: "Close", value: false }];

const LeadGeneral = (props: any) => {
  const {
    values,
    tags,
    dispatch,
    form,
    rootEntity,
    submitSucceeded,
    twoColumn,
    isNew,
    setSelectedContact,
  } = props;

  const validateTagList = (value, allValues) => {
    return validateTagsList(tags, value, allValues, props);
  };

  const changeValue = (e, checked) => {
    dispatch(change(form, "notify", checked));
  };

  const onContactChange = useCallback( value => {
      setSelectedContact(value);
  }, [form]);

  return (
    <div className="generalRoot">
      <div className="pt-2">
        {values.assignedTo && <FormField type="text" name="assignedTo" label="Assigned to" disabled />}
      </div>
      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Contact"
          name="contactId"
          label="Contact"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={values && defaultContactName(values.contactName)}
          labelAdornment={
            <LinkAdornment linkHandler={openContactLink} link={values.contactId} disabled={!values.contactId} />
          }
          onInnerValueChange={onContactChange}
          itemRenderer={ContactSelectItemRenderer}
          disabled={!isNew}
          rowHeight={55}
          required
        />
      </Grid>
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
        className={clsx("pr-3", {
          // "mt-2": !twoColumn
        })}
        control={<Checkbox onChange={changeValue} checked={values.notify} />}
        label="Notify student"
      />
      <FormField
        type="money"
        name="estimatedValue"
        label="Estimated value"
      />
      <FormField
        type="select"
        name="active"
        label="Status"
        items={items}
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
      />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Lead"],
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setSelectedContact: (selectedContact: any) => dispatch(setSelectedContact(selectedContact))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(LeadGeneral);
