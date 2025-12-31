/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Faculty } from '@api/model';
import React, { useEffect } from 'react';
import { initialize } from 'redux-form';
import { checkPermissions } from '../../../common/actions';
import { notesAsyncValidate } from '../../../common/components/form/notes/utils';
import { getFilters, setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { useAppDispatch } from '../../../common/utils/hooks';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { getEntityRelationTypes } from '../../preferences/actions';
import { getListTags } from '../../tags/actions';
import FacultyEditView from './components/FacultyEditView';

const ENTITY_NAME = "Faculty";

const Initial: Faculty = {
  code: null,
  id: null,
  name: null,
  webDescription: null,
  isShownOnWeb: false,
  tags: [],
  documents: [],
  relatedCourses: [],
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Visible online",
        expression: "isShownOnWeb == true",
        active: true
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Faculty and entityId" },
  { title: "Course", list: "course", expression: "relatedCourses.id" },
  {
    title: "Current students",
    list: "contact",
    expression:
    
      "student.enrolments.status == SUCCESS and student.enrolments.courseClass.endDateTime > now and student.enrolments.courseClass.course.facultyId"
  },
  {
    title: "Completed students",
    list: "contact",
    expression:
    
      "student.enrolments.status == SUCCESS and student.enrolments.courseClass.endDateTime <= now and student.enrolments.courseClass.course.facultyId"
  },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Faculty and attachmentRelations.entityRecordId"
  },
  { title: "Student feedback", list: "survey", expression: "enrolment.courseClass.course.facultyId" },
  { title: "Timetable", list: "timetable", expression: "courseClass.course.facultyId" },
  { title: "Tutors", list: "contact", expression: "tutor.courseClassRoles.courseClass.course.facultyId" },
  { title: "Waiting list", list: "waitingList", expression: "course.facultyId" },
  { title: "Waitlist contacts", list: "contact", expression: "student.waitingLists.course.facultyId" }
];

const setRowClasses = ({ isShownOnWeb }) => {
  if (isShownOnWeb === "Yes") return undefined;
  if (isShownOnWeb === "No") return "text-op05";
  return undefined;
};

function Faculties() {
  const dispatch = useAppDispatch();

  const onInit = () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  };

  useEffect(() => {
    dispatch(getFilters(ENTITY_NAME));
    dispatch(getListTags(ENTITY_NAME));
    dispatch(checkPermissions({ keyCode: "VET_COURSE" }));
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(getEntityRelationTypes());
  }, []);

  return (
    <ListView
      listProps={{
        setRowClasses,
        primaryColumn: "name",
        secondaryColumn: "code"
      }}
      editViewProps={{
        asyncValidate: notesAsyncValidate,
        asyncChangeFields: ["notes[].message"],
        hideTitle: true
      }}
      EditViewContent={FacultyEditView}
      rootEntity={ENTITY_NAME}
      onInit={onInit}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
    />
  );
}

export default Faculties;