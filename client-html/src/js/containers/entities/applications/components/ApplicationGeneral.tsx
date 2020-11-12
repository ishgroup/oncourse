/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Application, ApplicationStatus, CourseEnrolmentType } from "@api/model";
import { Dispatch } from "redux";
import { Grid } from "@material-ui/core";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { State } from "../../../../reducers/state";
import { getPlainCourses, setPlainCourses, setPlainCoursesSearch } from "../../courses/actions";
import {
 clearContacts, clearContactsSearch, getContacts, setStudentsSearch
} from "../../contacts/actions";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import Uneditable from "../../../../common/components/form/Uneditable";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";

interface ApplicationGeneralProps {
  classes?: any;
  manualLink?: string;
  twoColumn?: boolean;
  tags?: any;
  showConfirm?: any;
  values?: Application;
  courses?: any[];
  setCoursesSearch?: any;
  getCourses?: any;
  coursesLoading?: boolean;
  coursesRowsCount?: number;
  contacts?: any[];
  setContactsSearch?: any;
  getContacts?: any;
  contactsLoading?: boolean;
  contactsRowsCount?: number;
  isNew?: boolean;
  clearContactsSearch?: any;
  dispatch?: any;
  form?: string;
  clearContacts?: any;
  clearCourses?: any;
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
    classes,
    twoColumn,
    tags,
    values,
    courses,
    getCourses,
    setCoursesSearch,
    coursesLoading,
    coursesRowsCount,
    contacts,
    getContacts,
    setContactsSearch,
    clearContactsSearch,
    contactsLoading,
    contactsRowsCount,
    isNew,
    dispatch,
    form,
    clearContacts,
    clearCourses
  } = props;

  useEffect(() => {
    setContactsSearch("");

    return () => clearContactsSearch();
  }, []);

  return (
    <div className="generalRoot">
      <div className="mt-2">
        <FormField
          type="remoteDataSearchSelect"
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
          items={contacts || []}
          onSearchChange={setContactsSearch}
          onLoadMoreRows={getContacts}
          onClearRows={clearContacts}
          loading={contactsLoading}
          remoteRowCount={contactsRowsCount}
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          required
        />
      </div>
      <div>
        <FormField
          type="remoteDataSearchSelect"
          name="courseId"
          label="Course"
          selectValueMark="id"
          selectLabelMark="name"
          selectFilterCondition={courseFilterCondition}
          defaultDisplayValue={values && values.courseName}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openCourseLink}
              link={values && values.courseId}
              disabled={!values || !values.courseId}
            />
          )}
          items={courses || []}
          onSearchChange={setCoursesSearch}
          onLoadMoreRows={getCourses}
          onClearRows={clearCourses}
          loading={coursesLoading}
          remoteRowCount={coursesRowsCount}
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
  tags: state.tags.entityTags["Application"],
  courses: state.courses.items,
  coursesSearch: state.courses.search,
  coursesLoading: state.courses.loading,
  coursesRowsCount: state.courses.rowsCount,
  contacts: state.contacts.items,
  contactsSearch: state.contacts.search,
  contactsLoading: state.contacts.loading,
  contactsRowsCount: state.contacts.rowsCount
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getCourses: (offset?: number) => dispatch(getPlainCourses(offset, "code,name", true)),
    clearCourses: () => dispatch(setPlainCourses([])),
    setCoursesSearch: (search: string) => dispatch(setPlainCoursesSearch(`~"${search}"`, CourseEnrolmentType["Enrolment by application"])),
    getContacts: (offset?: number) => dispatch(getContacts(offset, null, true)),
    clearContacts: () => dispatch(clearContacts()),
    setContactsSearch: (search: string) => dispatch(setStudentsSearch(search)),
    clearContactsSearch: () => dispatch(clearContactsSearch())
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ApplicationGeneral);
