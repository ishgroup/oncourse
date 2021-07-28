/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect } from "react";
import debounce from "lodash.debounce";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import EditInPlaceSearchSelect from "./EditInPlaceSearchSelect";
import { AnyArgFunction, NumberArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../actions/CommonPlainRecordsActions";
import { EntityName } from "../../../../model/entities/common";

interface Props {
  onSearchChange: StringArgFunction;
  onLoadMoreRows: NumberArgFunction;
  onClearRows: AnyArgFunction;
  rowHeight?: number;
  items: any[];
  loading?: boolean;
  entity: EntityName;
  aqlFilter?: string;
  aqlColumns?: string;
}

const EditInPlaceRemoteDataSearchSelect: React.FC<Props> = (
  {
    onLoadMoreRows,
    onSearchChange,
    entity,
    aqlFilter,
    aqlColumns,
    ...rest
  }
) => {
  useEffect(() => {
    onSearchChange("");
    return () => onSearchChange("");
  }, []);

  const onInputChange = useCallback(debounce((input: string) => {
    onSearchChange(input);
    if (input) {
      onLoadMoreRows(0);
    }
  }, 800), [aqlFilter, aqlColumns, entity]);

  const onLoadMoreRowsOwn = startIndex => {
    if (!rest.loading) {
      onLoadMoreRows(startIndex);
    }
  };

  return (
    <EditInPlaceSearchSelect {...rest as any} onInputChange={onInputChange} loadMoreRows={onLoadMoreRowsOwn} remoteData />
  );
};

const mapStateToProps = (state: State, ownProps) => ({
  items: state.plainSearchRecords[ownProps.entity]?.items,
  loading: state.plainSearchRecords[ownProps.entity]?.loading,
  remoteRowCount: state.plainSearchRecords[ownProps.entity]?.rowsCount,
});

const getDefaultColumns = entity => {
  switch (entity) {
    case "CourseClass":
      return "course.name,course.code,code,feeIncGst";
    case "Contact":
      return "firstName,lastName,email,birthDate,street,suburb,state,postcode,invoiceTerms,taxOverride.id";
    case "Site":
      return "name,localTimezone";
    case "Qualification":
      return "nationalCode,title,level,fieldOfEducation,isOffered";
    case "Course":
      return "code,name,currentlyOffered,isShownOnWeb";
    case "Assessment":
      return "code,name";
    case "Module":
      return "nationalCode,title";
    case "Lead":
      return "id,customer";
  }
  return "";
};

const mapDispatchToProps = (dispatch: Dispatch<any>, ownProps) => {
  const getSearch = search => (search ? `~"${search}"${ownProps.aqlFilter ? ` and ${ownProps.aqlFilter}` : ""}` : "");
  return {
    onLoadMoreRows: (offset?: number) => dispatch(
      getCommonPlainRecords(
        ownProps.entity,
        offset,
        ownProps.aqlColumns || getDefaultColumns(ownProps.entity),
        true
      )
    ),
    onSearchChange: (search: string) => dispatch(setCommonPlainSearch(ownProps.entity, getSearch(search))),
    onClearRows: () => dispatch(clearCommonPlainRecords(ownProps.entity))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EditInPlaceRemoteDataSearchSelect);
