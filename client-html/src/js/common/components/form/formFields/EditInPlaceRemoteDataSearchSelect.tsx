/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EditInPlaceSearchSelect } from "ish-ui";
import debounce from "lodash.debounce";
import React, { useCallback, useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getDefaultRemoteColumns } from "../../../../containers/entities/common/entityConstants";
import { EditInPlaceRemoteDataSelectFieldProps, } from "../../../../model/common/Fields";
import { State } from "../../../../reducers/state";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../actions/CommonPlainRecordsActions";

const EditInPlaceRemoteDataSearchSelect: React.FC<EditInPlaceRemoteDataSelectFieldProps> = (
  {
    onLoadMoreRows,
    onSearchChange,
    entity,
    aqlFilter,
    aqlColumns,
    getCustomSearch,
    preloadEmpty,
    ...rest
  }
) => {
  useEffect(() => {
    onSearchChange("");
    return () => onSearchChange("");
  }, []);

  useEffect(() => {
    if (preloadEmpty) {
      onSearchChange("");
      onLoadMoreRows(0);
    }
  }, [preloadEmpty, getCustomSearch]);

  const onInputChange = useCallback(debounce((input: string) => {
    onSearchChange(input);
    if (input || preloadEmpty) {
      onLoadMoreRows(0);
    }
  }, 800), [preloadEmpty, aqlFilter, aqlColumns, getCustomSearch, entity]);

  const onLoadMoreRowsOwn = startIndex => {
    if (!rest.loading) {
      onLoadMoreRows(startIndex);
    }
  };

  return (
    <EditInPlaceSearchSelect
      {...rest as any}
      onInputChange={onInputChange}
      loadMoreRows={onLoadMoreRowsOwn}
      remoteData
    />
  );
};

const mapStateToProps = (state: State, ownProps) => ({
  items: state.plainSearchRecords[ownProps.entity]?.items,
  loading: state.plainSearchRecords[ownProps.entity]?.loading,
  remoteRowCount: state.plainSearchRecords[ownProps.entity]?.rowsCount,
});

const mapDispatchToProps = (dispatch: Dispatch<any>, ownProps) => {
  const getSearch = search => ownProps.getCustomSearch
    ? ownProps.getCustomSearch(search)
    : (search
         ? `~"${search}"${ownProps.aqlFilter ? ` and ${ownProps.aqlFilter}` : ''}`
         : `${ownProps.preloadEmpty ? ownProps.aqlFilter : ''}`
    );

  return {
    onLoadMoreRows: (offset?: number) => dispatch(
      getCommonPlainRecords(
        ownProps.entity,
        offset,
        ownProps.aqlColumns || getDefaultRemoteColumns(ownProps.entity),
        true
      )
    ),
    onSearchChange: (search: string) => dispatch(setCommonPlainSearch(ownProps.entity, getSearch(search))),
    onClearRows: () => dispatch(clearCommonPlainRecords(ownProps.entity))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EditInPlaceRemoteDataSearchSelect) as React.FC<EditInPlaceRemoteDataSelectFieldProps>;