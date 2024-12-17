/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Delete from "@mui/icons-material/Delete";
import RestoreFromTrash from "@mui/icons-material/RestoreFromTrash";
import MenuItem from "@mui/material/MenuItem";
import { NoArgFunction } from "ish-ui";
import React, { memo, useEffect, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { IAction } from '../../../../common/actions/IshAction';
import { getPluralSuffix } from "../../../../common/utils/strings";
import { IS_JEST } from "../../../../constants/EnvironmentConstants";
import { ListState } from "../../../../model/common/ListView";
import { removeDocument, restoreDocument } from "../actions";

interface Props {
  selection?: ListState["selection"];
  records?: ListState["records"];
  onDelete?: (ids: number[]) => void;
  onRestore?: (ids: number[]) => void;
  closeMenu?: NoArgFunction;
}

const BinCogwheel = memo<Props>(props => {
  const {
    selection,
    records,
    onDelete,
    onRestore,
    closeMenu
  } = props;

  const [selected, setSelected] = useState([]);
  const [selectedDeleted, setSelectedDeleted] = useState([]);

  useEffect(() => {
    const selectedVal = [];
    const selectedDeletedVal = [];
    const activeColumnIndex = records.columns
      .filter(c => c.visible || c.system)
      .findIndex(c => c.attribute === "active");

    selection.forEach(id => {
      const selectedRecord = records.rows.find(r => Number(r.id) === Number(id));
      if (!selectedRecord) {
        return;
      }
      if (selectedRecord.values[activeColumnIndex] === "true") selectedVal.push(id);
      else selectedDeletedVal.push(id);
    });
    setSelected(selectedVal);
    setSelectedDeleted(selectedDeletedVal);
  }, [selection, records.columns]);

  const deleteActionName = React.useMemo(() => {
    if (selected.length && selectedDeleted.length) {
      return `Move ${selected.length} record${getPluralSuffix(selected.length)} to bin 
      and restore ${selectedDeleted.length} record${getPluralSuffix(selectedDeleted.length)} from bin`;
    }
    return selected.length ? "Move to bin" : "Restore from Bin";
  }, [selected, selectedDeleted]);

  const onClick = () => {
    if (selected.length) {
      onDelete(selected);
    }
    if (selectedDeleted.length) {
      onRestore(selectedDeleted);
    }
    closeMenu();
  };

  const menuItemProps = IS_JEST ? {
    "data-testid": "delete-action-item"
  } : {};

  return selection.length ? (
    <MenuItem {...menuItemProps} onClick={onClick}>
      <span className={deleteActionName === "Move to bin" ? "errorColor" : null}>
        {deleteActionName}
      </span>
      {deleteActionName === "Move to bin" && <Delete color="error" className="ml-1" />}
      {deleteActionName === "Restore from Bin" && <RestoreFromTrash className="ml-1" />}
    </MenuItem>
  ) : null;
});

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  onDelete: ids => dispatch(removeDocument({
    ids,
    diff: {
      isRemoved: "true"
    }
  })),
  onRestore: ids => dispatch(restoreDocument({
    ids,
    diff: {
      isRemoved: "false"
    }
  })),
});

export default connect(null, mapDispatchToProps)(BinCogwheel);
