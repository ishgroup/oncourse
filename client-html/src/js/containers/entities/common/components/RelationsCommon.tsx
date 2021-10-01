/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { change, Validator } from "redux-form";
import { Course, EntityRelationType, Module, Qualification, Sale } from "@api/model";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { formatRelatedSalables, formattedEntityRelationTypes, salesSort } from "../utils";
import { BooleanArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import NestedListRelationCell from "./NestedListRelationCell";
import { clearSales, getSales } from "../../sales/actions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { EntityRelationTypeRendered } from "../../../../model/entities/EntityRelations";
import { EntityName } from "../../../../model/entities/common";

interface Props {
  values: any;
  dispatch: Dispatch;
  submitSucceeded: boolean;
  form: string;
  rootEntity: EntityName;
  customAqlEntities?: string[];
  name?: string;
  validate?: Validator;
  courses?: Course[];
  coursesPending?: boolean;
  searchCourses?: StringArgFunction;
  clearCoursesSearch?: BooleanArgFunction;
  entityRelationTypes?: EntityRelationType[];
  clearSalesSearch?: BooleanArgFunction;
  clearModuleSearch?: BooleanArgFunction;
  clearQualificationsSearch?: BooleanArgFunction;
  searchSales?: StringArgFunction;
  searchModules?: StringArgFunction;
  searchQualifications?: StringArgFunction;
  sales?: Sale[];
  salesPending?: boolean;
  qualifications?: Qualification[];
  qualificationsPending?: boolean;
  modules?: Module[];
  modulesPending?: boolean;
  relationTypesFilter?: {
    entities: EntityName[];
    filter: (relation: EntityRelationTypeRendered) => boolean;
  },
  dataRowClass?: string;
}

const RelationsCommon: React.FC<Props> = (
  {
    values,
    validate,
    name,
    dispatch,
    form,
    submitSucceeded,
    searchCourses,
    clearCoursesSearch,
    entityRelationTypes = [],
    clearSalesSearch,
    clearModuleSearch,
    clearQualificationsSearch,
    searchSales,
    searchModules,
    searchQualifications,
    courses,
    coursesPending,
    sales,
    salesPending,
    qualifications,
    qualificationsPending,
    modules,
    modulesPending,
    rootEntity,
    relationTypesFilter,
    customAqlEntities,
    dataRowClass = "grid-temp-col-3-fr"
  }
) => {
  const relationTypes = useMemo<EntityRelationTypeRendered[]>(() =>
    formattedEntityRelationTypes(entityRelationTypes), [entityRelationTypes]);

  const validateRelations = useCallback((rels: Sale[]) => {
    let error;
    if (rels && relationTypesFilter) {
      rels.forEach(rel => {
        if (relationTypesFilter.entities.includes(rel.type as any) && !relationTypes.filter(relationTypesFilter.filter).length) {
          error = "No available relation types for some of added relations";
        }
      });
    }
    return error;
  }, [relationTypesFilter, relationTypes]);

  const listValues = useMemo(() => (values && values.relatedSellables ? formatRelatedSalables(values.relatedSellables) : []), [
    values.relatedSellables
  ]);

  const searchValues = useMemo(() => [
    ...courses
      ? formatRelatedSalables(rootEntity === "Course" ? courses.filter(c => c.id !== values.id) : courses, "Course")
      : [],
    ...sales
      ? formatRelatedSalables(["VoucherProduct", "MembershipProduct", "ArticleProduct"].includes(rootEntity)
        ? sales.filter(c => c.id !== values.id && c.type !== rootEntity)
        : sales)
      : [],
    ...qualifications
      ? formatRelatedSalables(rootEntity === "Qualification"
        ? qualifications.filter(c => c.id !== values.id)
        : qualifications, "Qualification")
      : [],
    ...modules
      ? formatRelatedSalables(rootEntity === "Module" ? modules.filter(c => c.id !== values.id) : modules, "Module")
      : []
  ], [
    courses,
    sales,
    qualifications,
    modules,
    values.id
  ]);

  const onAdd = (salesToAdd: NestedListItem[]) => {
    const newSalesList = values.relatedSellables.concat(
      salesToAdd.map(s => ({
        id: s.entityId,
        tempId: s.entityId,
        name: s.primaryText,
        code: s.secondaryText,
        active: s.active,
        type: s.entityName,
        expiryDate: null,
        entityFromId: s.entityId,
        entityToId: null,
        relationId: -1
        }))
    );
    newSalesList.sort(salesSort);
    dispatch(change(form, "relatedSellables", newSalesList));
  };

  const onDeleteAll = useCallback(() => {
    dispatch(change(form, "relatedSellables", []));
  }, [form]);

  const onDelete = useCallback(
    saleToDelete => {
      dispatch(
        change(
          form,
          "relatedSellables",
          values.relatedSellables.filter(
            item => item.id !== saleToDelete.id
          )
        )
      );
    },
    [form, values.relatedSellables]
  );

  const relationCell = props => (
    <NestedListRelationCell
      {...props}
      relationTypesFilter={relationTypesFilter}
      relationTypes={relationTypes}
      dispatch={dispatch}
      form={form}
    />
  );

  const aqlEntities = ["Course", "Product", "Module", "Qualification"];

  return (
    <NestedList
      name={name}
      validate={validate ? [validate, validateRelations] : validateRelations}
      title={`${listValues.length || ""} relation${listValues.length === 1 ? "" : "s"}`}
      formId={values.id}
      values={listValues}
      searchValues={searchValues}
      pending={coursesPending || salesPending || qualificationsPending || modulesPending}
      onAdd={onAdd}
      onDelete={onDelete}
      onDeleteAll={onDeleteAll}
      onSearch={(search, entity) => {
        switch (entity) {
          case "Course":
            searchCourses(search);
            break;
          case "Product":
            searchSales(search);
            break;
          case "Module":
            searchModules(search);
            break;
          case "Qualification":
            searchQualifications(search);
            break;
        }
      }}
      clearSearchResult={(pending, entity) => {
        switch (entity) {
          case "Course":
            clearCoursesSearch(pending);
            break;
          case "Product":
            clearSalesSearch(pending);
            break;
          case "Module":
            clearModuleSearch(pending);
            break;
          case "Qualification":
            clearQualificationsSearch(pending);
            break;
        }
      }}
      sort={salesSort}
      resetSearch={submitSucceeded}
      dataRowClass={dataRowClass}
      aqlEntities={customAqlEntities || aqlEntities}
      CustomCell={relationCell}
    />
);
};

const mapStateToProps = (state: State) => ({
  courses: state.plainSearchRecords["Course"].items,
  coursesPending: state.plainSearchRecords["Course"].loading,
  sales: state.sales.items,
  salesPending: state.sales.pending,
  qualifications: state.plainSearchRecords["Qualification"].items,
  qualificationsPending: state.plainSearchRecords["Qualification"].loading,
  modules: state.plainSearchRecords["Module"].items,
  modulesPending: state.plainSearchRecords["Module"].loading,
  entityRelationTypes: state.preferences.entityRelationTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchQualifications: search => {
    dispatch(setCommonPlainSearch("Qualification", search));
    dispatch(getCommonPlainRecords("Qualification", 0, "nationalCode,title,level,fieldOfEducation,isOffered", true, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearQualificationsSearch: (loading?: boolean) => dispatch(clearCommonPlainRecords("Qualification", loading)),
  searchModules: search => {
    dispatch(setCommonPlainSearch("Module", search));
    dispatch(getCommonPlainRecords("Module", 0, "nationalCode,title,nominalHours,isOffered", true, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearModuleSearch: (loading?: boolean) => dispatch(clearCommonPlainRecords("Module", loading)),
  searchSales: (search: string) => {
    if (search) {
      dispatch(getSales(search));
    }
  },
  clearSalesSearch: (loading?: boolean) => dispatch(clearSales(loading)),
  searchCourses: (search: string) => {
    dispatch(setCommonPlainSearch("Course", search));
    dispatch(getCommonPlainRecords("Course", 0, "code,name,currentlyOffered,isShownOnWeb", true, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearCoursesSearch: (loading?: boolean) => dispatch(clearCommonPlainRecords("Course", loading))
});

export default connect<any, any, Props>(mapStateToProps, mapDispatchToProps)(RelationsCommon);
