/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/form-fields/FormField";
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
      form
    } = this.props;

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
            validate={tags && tags.length ? this.validateTagList : undefined}
          />
        </div>
        <FormField type="number" name="studentCount" label="Number of students" />
        <FormField
          type="remoteDataSearchSelect"
          entity="Course"
          aqlFilter="allowWaitingLists is true"
          name="courseId"
          label="Course"
          selectValueMark="id"
          selectLabelMark="name"
          selectFilterCondition={courseFilterCondition}
          defaultDisplayValue={values && values.courseName}
          labelAdornment={<LinkAdornment link={values.courseId} linkHandler={openCourseLink} />}
          itemRenderer={CourseItemRenderer}
          rowHeight={55}
          required
        />

        <CustomFields
          entityName="WaitingList"
          fieldName="customFields"
          entityValues={values}
          dispatch={dispatch}
          form={form}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["WaitingList"],
});

export default connect<any, any, any>(mapStateToProps)(WaitingListGeneral);
