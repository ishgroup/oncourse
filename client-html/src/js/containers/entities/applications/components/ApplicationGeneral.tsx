/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { Application, ApplicationStatus } from "@api/model";
import { Grid } from "@material-ui/core";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { State } from "../../../../reducers/state";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import Uneditable from "../../../../common/components/form/Uneditable";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { EditViewProps } from "../../../../model/common/ListView";

interface ApplicationGeneralProps extends EditViewProps<Application> {
  classes?: any;
  tags?: any;
}

const statusItems = Object.keys(ApplicationStatus)
  .map(value => ({ label: value, value }))
  .filter(item => item.value !== ApplicationStatus.Accepted);

const validateTagList = (value, allValues, props) => {
  const { tags } = props;
  return validateTagsList(tags && tags.length > 0 ? tags : [], value, allValues, props);
};

const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);

const ApplicationGeneral: React.FC<ApplicationGeneralProps> = props => {
  const {
    twoColumn,
    tags,
    values,
    isNew,
    dispatch,
    form
  } = props;

  return (
    <div className="generalRoot">
      <div className="mt-2">
        <FormField
          type="remoteDataSearchSelect"
          entity="Contact"
          aqlFilter="isStudent is true"
          name="contactId"
          label="Student"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          disabled={!isNew}
          defaultDisplayValue={values && defaultContactName(values.studentName)}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openContactLink}
              link={values && values.contactId}
              disabled={!values || !values.contactId}
            />
          )}
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          required
        />
      </div>
      <div>
        <FormField
          type="remoteDataSearchSelect"
          entity="Course"
          aqlFilter="enrolmentType is ENROLMENT_BY_APPLICATION"
          name="courseId"
          label="Course"
          selectValueMark="id"
          selectLabelMark="name"
          selectFilterCondition={courseFilterCondition}
          selectLabelCondition={courseFilterCondition}
          defaultDisplayValue={values && values.courseName}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openCourseLink}
              link={values && values.courseId}
              disabled={!values || !values.courseId}
            />
          )}
          disabled={!isNew}
          itemRenderer={CourseItemRenderer}
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
      <Grid container>
        <Grid item xs={twoColumn ? 3 : 6}>
          <FormField
            type="date"
            name="applicationDate"
            label="Application Date"
            disabled
          />
        </Grid>
        <Grid item xs={twoColumn ? 3 : 6}>
          <FormField type="text" name="source" label="Source" disabled />
        </Grid>
      </Grid>
      {values && values.status !== ApplicationStatus.Accepted ? (
        <FormField type="select" name="status" label="Status" items={statusItems} />
      ) : (
        <Uneditable value={values.status} label="Status" />
      )}
      <Grid container>
        <Grid item xs={twoColumn ? 3 : 6}>
          <FormField
            type="money"
            name="feeOverride"
            label="Fee Override (ex GST)"
            validate={validateNonNegative}
          />
        </Grid>
        <Grid item xs={twoColumn ? 3 : 6}>
          <FormField name="enrolBy" label="Enrol by" type="date" />
        </Grid>
      </Grid>
      {!isNew && <FormField type="text" name="createdBy" label="Created By" disabled />}
      <FormField
        type="text"
        name="reason"
        label="Reason for decision (Student visible)"
        multiline
      />
      <CustomFields
        entityName="Application"
        fieldName="customFields"
        entityValues={values}
        dispatch={dispatch}
        form={form}
      />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Application"]
});

export default connect<any, any, any>(mapStateToProps)(ApplicationGeneral);
