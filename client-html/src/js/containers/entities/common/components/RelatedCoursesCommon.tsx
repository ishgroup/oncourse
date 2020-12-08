/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { change } from "redux-form";
import {
  Course, EntityRelationType
} from "@api/model";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { formatRelatedSalables, formattedEntityRelationTypes, salesSort } from "../utils";
import { EditViewContainerProps } from "../../../../model/common/ListView";
import { getPlainCourses, setPlainCourses, setPlainCoursesSearch } from "../../courses/actions";
import { BooleanArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import NestedListRelationCell from "./NestedListRelationCell";

interface Props extends EditViewContainerProps {
  coursesPending?: boolean;
  dispatch?: any;
  form?: string;
  courses?: Course[];
  searchCourses?: StringArgFunction;
  clearCoursesSearch?: BooleanArgFunction;
  entityRelationTypes?: EntityRelationType[];
}

const RelatedCoursesCommon: React.FC<Props> = (
  {
    values,
    dispatch,
    form,
    courses,
    coursesPending,
    submitSucceeded,
    searchCourses,
    clearCoursesSearch,
    entityRelationTypes
  }
) => {
  const relationTypes = useMemo(() => formattedEntityRelationTypes(entityRelationTypes), [entityRelationTypes]);

  const listValues = useMemo(() => (values && values.relatedlSalables ? formatRelatedSalables(values.relatedlSalables) : []), [
    values.relatedlSalables
  ]);

  const searchValues = useMemo(() => formatRelatedSalables(courses.filter(c => c.id !== values.id)), [
    courses,
    values.id
  ]);

  const onAdd = useCallback(
    (salesToAdd: NestedListItem[]) => {
      const newSalesList = values.relatedlSalables.concat(
        salesToAdd.map(v1 => {
          const course = courses.find(v2 => String(v2.id) === String(v1.entityId));
          return {
            ...course, tempId: course.id, entityFromId: course.id, relationId: -1
          };
        })
      );
      newSalesList.sort(salesSort);
      dispatch(change(form, "relatedlSalables", newSalesList));
    },
    [form, courses, values.relatedlSalables]
  );

  const onDeleteAll = useCallback(() => {
    dispatch(change(form, "relatedlSalables", []));
  }, [form]);

  const onDelete = useCallback(
    saleToDelete => {
      dispatch(
        change(
          form,
          "relatedlSalables",
          values.relatedlSalables.filter(
            course => course.id !== saleToDelete.id
          )
        )
      );
    },
    [form, values.relatedlSalables]
  );

  const relationCell = props => (
    <NestedListRelationCell
      {...props}
      relationTypes={relationTypes}
      dispatch={dispatch}
      form={form}
    />
  );

  return (
    <NestedList
      title={`${listValues.length || ""} Related courses`}
      searchPlaceholder="Find courses"
      formId={values.id}
      values={listValues}
      searchValues={searchValues}
      pending={coursesPending}
      onAdd={onAdd}
      onDelete={onDelete}
      onDeleteAll={onDeleteAll}
      onSearch={searchCourses}
      clearSearchResult={clearCoursesSearch}
      clearAdditionalSearchResult={clearCoursesSearch}
      sort={salesSort}
      resetSearch={submitSucceeded}
      dataRowClass="grid-temp-col-3-fr"
      aqlEntity="Course"
      aqlEntityTags={["Course"]}
      CustomCell={relationCell}
    />
);
};

const mapStateToProps = (state: State) => ({
  courses: state.courses.items,
  coursesPending: state.courses.loading,
  entityRelationTypes: state.preferences.entityRelationTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchCourses: (search: string) => {
    dispatch(setPlainCoursesSearch(search));
    dispatch(getPlainCourses(null, null, true));
  },
  clearCoursesSearch: (loading?: boolean) => {
    dispatch(setPlainCourses([], null, null, loading));
  }
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(RelatedCoursesCommon);
