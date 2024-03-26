/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Course, Faculty } from "@api/model";
import React, { useMemo } from "react";
import { change } from "redux-form";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { useAppSelector } from "../../../../common/utils/hooks";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";
import { EditViewProps } from "../../../../model/common/ListView";

const transformCourse = (course: Course): NestedListItem => ({
  id: course.id.toString(),
  entityId: course.id,
  primaryText: course.name,
  secondaryText: course.code,
  link: `/course/${course.id}`,
  active: true
});

const transformPlain = (plain: Course[]) => {
  if (!plain) {
    return [];
  }
  const result = plain.map(transformCourse);
  result.sort((a, b) => (a.secondaryText > b.secondaryText ? 1 : -1));
  return result;
};

function FacultyCoursesTab(
  {
    values,
    dispatch,
    form,
    submitSucceeded
  }: EditViewProps<Faculty>) {
  
  const plainCourses = useAppSelector(state => state.plainSearchRecords["Course"]);

  const courseItemsTransformed = useMemo(() => transformPlain(values.relatedCourses), [values.relatedCourses]);

  const courseSearchItemsTransformed = useMemo(() => transformPlain(plainCourses.items), [plainCourses.items]);

  const clearModuleSearch = (loading: boolean) => dispatch(clearCommonPlainRecords("Module", loading));

  const onAddCourses = (coursesToAdd: NestedListItem[]) => {
    const newModules = values.relatedCourses.concat(coursesToAdd.map(v1 => plainCourses.items.find(v2 => v2.id === v1.entityId)));
    dispatch(change(form, "relatedCourses", newModules));
  };

  const onDeleteCourses = course => {
    const updated = values.relatedCourses.filter(m => m.id !== course.entityId);
    dispatch(change(form, "relatedCourses", updated));
  };

  const searchCourse = search => {
    dispatch(setCommonPlainSearch("Course", search));
    dispatch(getCommonPlainRecords("Course", null, "name,code", true, null, PLAIN_LIST_MAX_PAGE_SIZE));
  };
  
  return <div className="pl-3 pr-3 pt-2">
    <NestedList
      formId={values.id}
      title="Relations"
      values={courseItemsTransformed}
      searchValues={courseSearchItemsTransformed}
      pending={plainCourses.loading}
      onAdd={onAddCourses}
      onDelete={onDeleteCourses}
      onSearch={searchCourse}
      clearSearchResult={clearModuleSearch}
      resetSearch={submitSucceeded}
      aqlEntities={["Course"]}
      aqlQueryError={plainCourses.error}
    />
  </div>;
}

export default FacultyCoursesTab;