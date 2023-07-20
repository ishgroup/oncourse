/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { ShowConfirmCaller } from "../../../../../ish-ui/model/Confirm";
import React, { memo } from "react";
import { useAppDispatch } from "../../../../common/utils/hooks";
import MenuItem from "@mui/material/MenuItem";
import clsx from "clsx";
import { bulkDeleteEntityRecordsRequest } from "../../common/actions";

interface QuedMessagesBulkDeleteProps {
  menuItemClass: string;
  closeMenu: AnyArgFunction;
  selection: number[];
  showConfirm: ShowConfirmCaller;
}

const QuedMessagesBulkDelete = memo<QuedMessagesBulkDeleteProps>(({
  menuItemClass,
  closeMenu,
  selection,
  showConfirm
}) => {
  const dispatch = useAppDispatch();

  const onBulkEditClick = () => {
    showConfirm({
      onConfirm: () => {
        dispatch(bulkDeleteEntityRecordsRequest(
          "Message",
          {
            ids: [],
            search: "status is QUEUED ",
            filter: "",
            tagGroups: []
          }));
        closeMenu();
      },
      confirmMessage: "All messages with Queued status will be permanently deleted. This action can not be undone",
      confirmButtonText: "Delete anyway"
    });

  };

  return  <MenuItem className={clsx(menuItemClass, "errorColor")} onClick={onBulkEditClick}>
    Bulk delete queued messages
  </MenuItem>;
});

export default QuedMessagesBulkDelete;