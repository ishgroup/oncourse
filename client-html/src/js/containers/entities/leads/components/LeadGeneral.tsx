/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import clsx from "clsx";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { change } from "redux-form";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";

const items = [{ label: "Open", value: true }, { label: "Close", value: false }];

const LeadGeneral = (props: any) => {
  const validateTagList = (value, allValues) => {
    const { tags } = props;
    return validateTagsList(tags, value, allValues, props);
  };

  const changeValue = (e, checked) => {
    const { form, dispatch } = props;

    dispatch(change(form, "notify", checked));
  };

  const {
    values,
    tags,
    dispatch,
    form
  } = props;

  return (
    <div className="generalRoot">
      <div className="pt-2">
        <FormField
          type="remoteDataSearchSelect"
          entity="Contact"
          aqlFilter="isStudent is true"
          name="contactId"
          label="Student"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={values && defaultContactName(values.studentName)}
          labelAdornment={
            <LinkAdornment linkHandler={openContactLink} link={values.contactId} disabled={!values.contactId} />
          }
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          required
        />
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
      />
      <FormField
        type="remoteDataSearchSelect"
        entity="Course"
        // aqlFilter="allowLeads is true"
        name="courseId"
        label="Course"
        selectValueMark="id"
        selectLabelCondition={v => v.name}
        selectFilterCondition={courseFilterCondition}
        defaultDisplayValue={values && values.courseName}
        labelAdornment={<LinkAdornment link={values.courseId} linkHandler={openCourseLink} />}
        itemRenderer={CourseItemRenderer}
        rowHeight={55}
        required
      />
      <CustomFields
        entityName="Lead"
        fieldName="customFields"
        entityValues={values}
        dispatch={dispatch}
        form={form}
      />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Lead"],
});

export default connect<any, any, any>(mapStateToProps)(LeadGeneral);
