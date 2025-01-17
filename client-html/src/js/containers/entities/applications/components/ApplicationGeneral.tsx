/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Application, ApplicationStatus } from "@api/model";
import { Grid } from "@mui/material";
import { LinkAdornment } from "ish-ui";
import React, { useEffect } from "react";
import { connect } from "react-redux";
import { change } from "redux-form";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import {
  ContactLinkAdornment,
  HeaderContactTitle
} from "../../../../common/components/form/formFields/FieldAdornments";
import FormField from "../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import EntityService from "../../../../common/services/EntityService";
import history from "../../../../constants/History";
import { EditViewProps } from "../../../../model/common/ListView";
import { State } from "../../../../reducers/state";
import { EntityChecklists } from "../../../tags/components/EntityChecklists";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { getContactFullName } from "../../contacts/utils";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";

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
      const contactId = params.get('contactId');
      const contactName = params.get('contactName');
      
      const clearParams = () => {
        history.replace({
          pathname: history.location.pathname,
          search: decodeURIComponent(params.toString())
        });
      };

      if (contactId) {
        dispatch(change(form, "contactId", Number(contactId)));
        params.delete('contactId');
      }

      if (contactName) {
        dispatch(change(form, "studentName", contactName));
        params.delete('contactName');
      }
      
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
        .catch(err => instantFetchErrorHandler(dispatch, err))
        .finally(() => {
          params.delete('leadId');
          clearParams();
        });
      } else {
        clearParams();
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
            <HeaderContactTitle name={values?.studentName} id={values?.contactId} />
          )}
          fields={(
            <Grid item {...gridItemProps}>
              <FormField
                type="remoteDataSelect"
                entity="Contact"
                aqlFilter="isStudent is true"
                name="contactId"
                label="Student"
                selectValueMark="id"
                selectLabelCondition={getContactFullName}
                disabled={!isNew}
                defaultValue={values?.studentName}
                labelAdornment={(
                  <ContactLinkAdornment id={values?.contactId} />
                )}
                itemRenderer={ContactSelectItemRenderer}
                rowHeight={55}
                required
              />
            </Grid>
          )}
        />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 8 : 12}>
        <FormField
          type="tags"
          name="tags"
          tags={tags}
        />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
        <EntityChecklists
          entity="Application"
          form={form}
          entityId={values.id}
          checked={values.tags}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="remoteDataSelect"
          entity="Course"
          aqlFilter="enrolmentType is ENROLMENT_BY_APPLICATION"
          name="courseId"
          label="Course"
          selectValueMark="id"
          selectLabelMark="name"
          selectFilterCondition={courseFilterCondition}
          selectLabelCondition={courseFilterCondition}
          defaultValue={values && values.courseName}
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
