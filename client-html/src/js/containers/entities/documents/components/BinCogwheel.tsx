/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { memo, useEffect, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import MenuItem from "@material-ui/core/MenuItem";
import Delete from "@material-ui/icons/Delete";
import RestoreFromTrash from "@material-ui/icons/RestoreFromTrash";
import { State } from "../../../../reducers/state";
import { removeDocument, restoreDocument } from "../actions";
import { ListState } from "../../../../model/common/ListView";
import { NoArgFunction } from "../../../../model/common/CommonFunctions";

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
      const selectedRecord = records.rows.find(r => r.id === id);
      if (!selectedRecord) {
        return;
      }
      selectedRecord.values[activeColumnIndex] === "true" ? selectedVal.push(id) : selectedDeletedVal.push(id);
    });
    setSelected(selectedVal);
    setSelectedDeleted(selectedDeletedVal);
  }, [selection, records.columns]);

  const deleteActionName = React.useMemo(() => {
    if (selected.length && selectedDeleted.length) {
      return `Move ${selected.length} record${selected.length === 1 ? "" : "s"} to bin 
      and restore ${selectedDeleted.length} record${selectedDeleted.length === 1 ? "" : "s"} from bin`;
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

  return selection.length ? (
    <MenuItem onClick={onClick}>
      <span className={deleteActionName === "Move to bin" ? "errorColor" : null}>
        {deleteActionName}
      </span>
      {deleteActionName === "Move to bin" && <Delete color="error" className="ml-1" />}
      {deleteActionName === "Restore from Bin" && <RestoreFromTrash className="ml-1" />}
    </MenuItem>
  ) : null;
});

const mapStateToProps = (state: State) => ({
  selection: state.list.selection,
  records: state.list.records
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
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

export default connect(mapStateToProps, mapDispatchToProps)(BinCogwheel);
