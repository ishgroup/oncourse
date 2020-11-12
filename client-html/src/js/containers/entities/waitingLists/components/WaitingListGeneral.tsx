/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import {
  clearContacts, clearContactsSearch, getContacts, setStudentsSearch
} from "../../contacts/actions";
import { getPlainCourses, setPlainCourses, setPlainCoursesSearch } from "../../courses/actions";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import CourseItemRenderer from "../../courses/components/CourseItemRenderer";
import { courseFilterCondition, openCourseLink } from "../../courses/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";

class WaitingListGeneral extends React.PureComponent<any, any> {
  componentDidMount() {
    const { setContactsSearch } = this.props;
    setContactsSearch();
  }

  componentWillUnmount() {
    this.props.clearContactsSearch();
  }

  validateTagList = (value, allValues, props) => {
    const { tags } = this.props;
    return validateTagsList(tags, value, allValues, props);
  };

  render() {
    const {
      values,
      tags,
      contacts,
      setContactsSearch,
      getContacts,
      contactsLoading,
      contactsRowsCount,
      courses,
      setCoursesSearch,
      getCourses,
      coursesLoading,
      coursesRowsCount,
      dispatch,
      form,
      clearContacts,
      clearCourses
    } = this.props;

    return (
      <div className="generalRoot">
        <div className="pt-2">
          <FormField
            type="remoteDataSearchSelect"
            name="contactId"
            label="Student"
            selectValueMark="id"
            selectLabelCondition={contactLabelCondition}
            defaultDisplayValue={values && defaultContactName(values.studentName)}
            labelAdornment={
              <LinkAdornment linkHandler={openContactLink} link={values.contactId} disabled={!values.contactId} />
            }
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
            type="tags"
            name="tags"
            tags={tags}
            validate={tags && tags.length ? this.validateTagList : undefined}
          />
        </div>
        <FormField type="number" name="studentCount" label="Number of students" />
        <FormField
          type="remoteDataSearchSelect"
          name="courseId"
          label="Course"
          selectValueMark="id"
          selectLabelMark="name"
          selectFilterCondition={courseFilterCondition}
          defaultDisplayValue={values && values.courseName}
          labelAdornment={<LinkAdornment link={values.courseId} linkHandler={openCourseLink} />}
          items={courses || []}
          onSearchChange={setCoursesSearch}
          onLoadMoreRows={getCourses}
          loading={coursesLoading}
          onClearRows={clearCourses}
          remoteRowCount={coursesRowsCount}
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
  contacts: state.contacts.items,
  contactsSearch: state.contacts.search,
  contactsLoading: state.contacts.loading,
  contactsRowsCount: state.contacts.rowsCount,
  courses: state.courses.items,
  coursesSearch: state.courses.search,
  coursesLoading: state.courses.loading,
  coursesRowsCount: state.courses.rowsCount
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getContacts: (offset?: number) => dispatch(getContacts(offset, null, true)),
  clearContacts: () => dispatch(clearContacts()),
  setContactsSearch: (search: string) => dispatch(setStudentsSearch(search)),
  clearContactsSearch: () => dispatch(clearContactsSearch()),
  getCourses: (offset?: number) => dispatch(getPlainCourses(offset, "code,name", true)),
  clearCourses: () => dispatch(setPlainCourses([])),
  setCoursesSearch: (search: string) => dispatch(setPlainCoursesSearch(`~"${search}" and allowWaitingLists is true`))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(WaitingListGeneral);
