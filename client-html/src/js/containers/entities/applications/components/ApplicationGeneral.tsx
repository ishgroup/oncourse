/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Application, ApplicationStatus } from "@api/model";
import { Grid, IconButton } from "@mui/material";
import Launch from "@mui/icons-material/Launch";
import { change } from "redux-form";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import Uneditable from "../../../../common/components/form/Uneditable";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import EntityService from "../../../../common/services/EntityService";
import history from "../../../../constants/History";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";

interface ApplicationGeneralProps extends EditViewProps<Application> {
  classes?: any;
  tags?: any;
}

const statusItems = Object.keys(ApplicationStatus)
  .map(value => ({ label: value, value }))
  .filter(item => item.value !== ApplicationStatus.Accepted);

const validateNonNegative = value => (value < 0 ? "Must be non negative" : undefined);

const ApplicationGeneral: React.FC<ApplicationGeneralProps> = props => {
  const {
    twoColumn,
    tags,
    values,
    isNew,
    form,
    dispatch,
    syncErrors
  } = props;
  
  useEffect(() => {
    if (history.location.search && isNew) {
      const params = new URLSearchParams(history.location.search);
      const leadId = params.get('leadId');
      
      if (leadId) {
        EntityService.getPlainRecords(
          "Lead",
          "customer.id,customer.fullName,items.course.id,items.course.name",
          `id is ${leadId}`,
          1
        )
        .then(res => {
          const contactId = JSON.parse(res.rows[0].values[0]);
          const studentName = res.rows[0].values[1];
          const courseId = JSON.parse(res.rows[0].values[2])[0];
          const courseName = res.rows[0].values[3]?.replace(/[[\]]|,.+/g, "");
          dispatch(change(form, "contactId", contactId));
          dispatch(change(form, "studentName", studentName));
          dispatch(change(form, "courseId", courseId));
          dispatch(change(form, "courseName", courseName));
        })
        .catch(err => instantFetchErrorHandler(dispatch, err));
        
        params.delete('leadId');
        history.replace({
          pathname: history.location.pathname,
          search: decodeURIComponent(params.toString())
        });
      }
    }
  }, []);

  const gridItemProps = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  } as any;

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
      <Grid item xs={12}>
        <FullScreenStickyHeader
          opened={isNew || Object.keys(syncErrors).includes("contactId")}
          disableInteraction={!isNew}
          twoColumn={twoColumn}
          title={(
            <div className="d-inline-flex-center">
              {values && defaultContactName(values.studentName)}
              <IconButton disabled={!values?.contactId} size="small" color="primary" onClick={() => openContactLink(values?.contactId)}>
                <Launch fontSize="inherit" />
              </IconButton>
            </div>
          )}
          fields={(
            <Grid item {...gridItemProps}>
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
          )}
        />
      </Grid>
      <Grid item {...gridItemProps}>
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
          type="tags"
          name="tags"
          tags={tags}
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
        <Uneditable value={values.source} label="Source" />
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
