/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo, memo, useCallback } from "react";
import { connect } from "react-redux";
import MenuItem from "@mui/material/MenuItem";
import { State } from "../../../../reducers/state";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";
import clsx from "clsx";
import { useAppDispatch, useAppSelector } from "../../../../common/utils/hooks";
import { bulkDeleteWaitingLists } from "../actions";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

interface WaitingListBulkDeleteProps {
  menuItemClass: string;
  closeMenu: AnyArgFunction;
  selection: number[];
  showConfirm: ShowConfirmCaller;
}

const WaitingListBulkDelete = memo<WaitingListBulkDeleteProps>(({
  menuItemClass, closeMenu, selection, showConfirm
}) => {
  const hasAql = useAppSelector(state => state.list.searchQuery
    && (state.list.searchQuery.search || state.list.searchQuery.filter || state.list.searchQuery.tagGroups.length));

  const { search, filter, tagGroups } = useAppSelector(state => state.list.searchQuery);

  const dispatch = useAppDispatch();

  const onBulkEditClick = () => {
    showConfirm({
      onConfirm: () => {
        dispatch(bulkDeleteWaitingLists({
          ids: selection,
          search,
          filter,
          tagGroups
        }));
        closeMenu();
      },
      confirmMessage: "Records will be permanently deleted. This action can not be undone",
      confirmButtonText: "Delete"
    })

  };

  return (
    <MenuItem className={clsx(menuItemClass, "errorColor")} onClick={onBulkEditClick} disabled={!selection.length && !hasAql}>
      Bulk delete...
    </MenuItem>
  );
});

const WaitingListCogWheel = memo<any>(props => {
  const {
    selection,
    menuItemClass,
    hasQePermissions
  } = props;

  const hoSelectedOrNew = useMemo(() => selection.length === 0 || selection[0] === "NEW", [selection]);

  const onQuickEnrolment = useCallback(() => {
    window.open(`/checkout?waitingListIds=${selection.toString()}`, "_self");
  }, [selection]);

  return (
    <>
      {hoSelectedOrNew ? null : (
        <MenuItem className={menuItemClass} onClick={onQuickEnrolment} disabled={!hasQePermissions}>
          Enrol
          {' '}
          {selection.length}
          {' '}
          highlighted student
          {selection.length > 1 && "s"}
        </MenuItem>
      )}
      <BulkEditCogwheelOption {...props} />
      <WaitingListBulkDelete {...props} />
    </>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.list.searchQuery,
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

export default connect<any, any, any>(mapStateToProps)(WaitingListCogWheel);