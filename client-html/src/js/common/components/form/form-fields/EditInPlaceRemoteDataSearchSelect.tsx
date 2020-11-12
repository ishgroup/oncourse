/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import debounce from "lodash.debounce";
import EditInPlaceSearchSelect from "./EditInPlaceSearchSelect";
import { AnyArgFunction, NumberArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";

interface Props {
  onSearchChange: StringArgFunction;
  onLoadMoreRows: NumberArgFunction;
  onClearRows: AnyArgFunction;
  rowHeight?: number;
  items: any[];
  loading?: boolean;
}

const EditInPlaceRemoteDataSearchSelect: React.FC<Props> = ({ onLoadMoreRows, onSearchChange, ...rest }) => {
  const onInputChange = useCallback(debounce((input: string) => {
    onSearchChange(input);
    if (input) {
      onLoadMoreRows(0);
    }
  }, 800), []);

  const onLoadMoreRowsOwn = startIndex => {
    if (!rest.loading) {
      onLoadMoreRows(startIndex);
    }
  };

  return (
    <EditInPlaceSearchSelect {...rest as any} onInputChange={onInputChange} loadMoreRows={onLoadMoreRowsOwn} remoteData />
  );
};

export default EditInPlaceRemoteDataSearchSelect as any;
