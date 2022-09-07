/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Course } from "@api/model";
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { checkPermissions } from "../../../common/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { FilterGroup } from "../../../model/common/ListView";
import { CourseExtended } from "../../../model/entities/Course";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";
import { getListTags } from "../../tags/actions";
import CourseCogWheel from "./components/CourseCogWheel";
import CourseEditView from "./components/CourseEditView";

export const ENTITY_NAME = "Course";

const manualLink = getManualLink("courses");

interface CoursesProps {
  getArticleProductRecord?: () => void;
  getDataCollectionRules?: () => void;
  onInit?: () => void;
  getFilters?: () => void;
  getPermissions?: () => void;
  clearListState?: () => void;
  getTags?: () => void;
  values?: CourseExtended;
  getRelationTypes?: () => void;
}

const Initial: Course = {
  allowWaitingLists: true,
  code: null,
  fieldOfEducation: null,
  id: null,
  isSufficientForQualification: false,
  isTraineeship: false,
  feeHelpClass: false,
  fullTimeLoad: null,
  isVET: false,
  name: null,
  enrolmentType: "Open enrolment",
  status: "Enabled",
  reportableHours: 0,
  webDescription: null,
  customFields: {},
  tags: [],
  documents: [],
  relatedSellables: [],
  modules: [],
  currentlyOffered: true
};

const filterGroups: FilterGroup[] = [
  {
    title: "TYPE",
    filters: [
      {
        name: "Courses",
        expression: "isTraineeship == false",
        active: false
      },
      {
        name: "Traineeships",
        expression: "isTraineeship == true",
        active: false
      }
    ]
  },
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Enabled",
        expression: "(currentlyOffered == true) and (isShownOnWeb == false)",
        active: true
      },
      {
        name: "Enabled and visible online",
        expression: "(currentlyOffered == true) and (isShownOnWeb == true)",
        active: true
      },
      {
        name: "Disabled",
        expression: "(currentlyOffered == false) and (isShownOnWeb == false)",
        active: false
      }
    ]
  }
];

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Course and entityId" },
  { title: "Applications", list: "application", expression: "course.id" },
  { title: "Classes", list: "class", expression: "course.id" },
  {
    title: "Current students",
    list: "contact",
    expression:
    // eslint-disable-next-line max-len
      "student.enrolments.status == SUCCESS and student.enrolments.courseClass.endDateTime > now and student.enrolments.courseClass.course.id"
  },
  {
    title: "Completed students",
    list: "contact",
    expression:
    // eslint-disable-next-line max-len
      "student.enrolments.status == SUCCESS and student.enrolments.courseClass.endDateTime <= now and student.enrolments.courseClass.course.id"
  },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Course and attachmentRelations.entityRecordId"
  },
  { title: "Related courses", list: "course", expression: "allRelatedCourses.id" },
  {
    title: "Related products...",
    items: [
      {
        title: "Related articles",
        list: "product",
        expression: "allRelatedCourses.id"
      },
      {
        title: "Related memberships",
        list: "membership",
        expression: "allRelatedCourses.id"
      },
      {
        title: "Related vouchers",
        list: "voucher",
        expression: "allRelatedCourses.id"
      }
    ]
  },
  { title: "Student feedback", list: "survey", expression: "enrolment.courseClass.course.id" },
  { title: "Timetable", list: "timetable", expression: "courseClass.course.id" },
  { title: "Tutors", list: "contact", expression: "tutor.courseClassRoles.courseClass.course.id" },
  { title: "Units of competency", list: "module", expression: "courses.id" },
  { title: "Voucher types", list: "voucher", expression: "voucherProductCourses.course.id" },
  { title: "Waiting list", list: "waitingList", expression: "course.id" },
  { title: "Waitlist contacts", list: "contact", expression: "student.waitingLists.course.id" }
];

const preformatBeforeSubmit = (value: CourseExtended): Course => {
  delete value.notes;

  if (value.qualificationId) {
    delete value.fieldOfEducation;
  }

  if (value.relatedSellables.length) {
    value.relatedSellables.forEach((s: any) => {
      if (s.tempId) {
        s.id = null;
        delete s.tempId;
      }
    });
  }

  return value;
};

const setRowClasses = ({ currentlyOffered, isShownOnWeb }) => {
  if (currentlyOffered === "Yes" && isShownOnWeb === "Yes") return undefined;
  if (currentlyOffered === "Yes") return "text-op065";
  if (currentlyOffered === "No" && isShownOnWeb === "No") return "text-op05";

  return undefined;
};

const Courses: React.FC<CoursesProps> = props => {
  const {
    getDataCollectionRules,
    getFilters,
    clearListState,
    onInit,
    getTags,
    getPermissions,
    getRelationTypes
  } = props;

  useEffect(() => {
    getDataCollectionRules();
    getFilters();
    getTags();
    getPermissions();
    getRelationTypes();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        setRowClasses,
        primaryColumn: "name",
        secondaryColumn: "code"
      }}
      editViewProps={{
        manualLink,
        asyncValidate: notesAsyncValidate,
        asyncChangeFields: ["notes[].message"],
        hideTitle: true
      }}
      EditViewContent={CourseEditView}
      rootEntity={ENTITY_NAME}
      onInit={onInit}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      preformatBeforeSubmit={preformatBeforeSubmit}
      CogwheelAdornment={CourseCogWheel}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getTags: () => {
    dispatch(getListTags(ENTITY_NAME));
  },
  getPermissions: () => {
    dispatch(checkPermissions({ keyCode: "VET_COURSE" }));
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
  },
  getDataCollectionRules: () => dispatch(getDataCollectionRules()),
  getFilters: () => dispatch(getFilters(ENTITY_NAME)),
  clearListState: () => dispatch(clearListState()),
  getRelationTypes: () => dispatch(getEntityRelationTypes())
});

export default connect<any, any, any>(null, mapDispatchToProps)(Courses);