/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { Application, ApplicationStatus } from "@api/model";
import { Grid } from "@mui/material";
import FormField from "../../../../common/components/form/formFields/FormField";
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

  const gridItemProps = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  } as any;

  return (
    <Grid container columnSpacing={3} className="generalRoot mt-2">
      <Grid item xs={12}>
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
      </Grid>
      <Grid item xs={12}>
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
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="tags"
          name="tags"
          tags={tags}
          validate={tags && tags.length ? validateTagList : undefined}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="date"
          name="applicationDate"
          label="Application Date"
          disabled
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField type="text" name="source" label="Source" disabled />
      </Grid>
      <Grid item {...gridItemProps}>
        {values && values.status !== ApplicationStatus.Accepted ? (
          <FormField type="select" name="status" label="Status" items={statusItems} />
          ) : (
            <Uneditable value={values.status} label="Status" />
          )}
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="money"
          name="feeOverride"
          label="Fee Override (ex GST)"
          validate={validateNonNegative}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField name="enrolBy" label="Enrol by" type="date" />
      </Grid>
      <Grid item {...gridItemProps}>
        {!isNew && <FormField type="text" name="createdBy" label="Created By" disabled />}
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="text"
          name="reason"
          label="Reason for decision (Student visible)"
          multiline
        />
      </Grid>
      <CustomFields
        entityName="Application"
        fieldName="customFields"
        entityValues={values}
        dispatch={dispatch}
        form={form}
        gridItemProps={{
          xs: twoColumn ? 6 : 12,
          lg: twoColumn ? 4 : 12
        }}
      />
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Application"]
});

export default connect<any, any, any>(mapStateToProps)(ApplicationGeneral);
