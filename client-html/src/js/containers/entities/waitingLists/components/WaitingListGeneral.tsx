/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Grid } from "@material-ui/core";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";

class WaitingListGeneral extends React.PureComponent<any, any> {
  validateTagList = (value, allValues, props) => {
    const { tags } = this.props;
    return validateTagsList(tags, value, allValues, props);
  };

  render() {
    const {
      values,
      tags,
      dispatch,
      form,
      twoColumn
    } = this.props;

    const gridItemProps: any = {
      xs: twoColumn ? 6 : 12
    };

    return (
      <Grid container className="generalRoot">
        <Grid item {...gridItemProps} className="pt-2">
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
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="tags"
            name="tags"
            tags={tags}
            validate={tags && tags.length ? this.validateTagList : undefined}
          />
        </Grid>
        <Grid item {...gridItemProps}>
          <FormField type="number" name="studentCount" label="Number of students" />
        </Grid>
        <Grid item {...gridItemProps}>
          <FormField
            type="remoteDataSearchSelect"
            entity="Course"
            aqlFilter="allowWaitingLists is true"
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
        </Grid>
        <CustomFields
          entityName="WaitingList"
          fieldName="customFields"
          entityValues={values}
          dispatch={dispatch}
          form={form}
          gridItemProps={gridItemProps}
        />
      </Grid>
    );
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["WaitingList"],
});

export default connect<any, any, any>(mapStateToProps)(WaitingListGeneral);