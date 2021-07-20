/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { memo, useCallback, useMemo } from "react";
import { connect } from "react-redux";
import MenuItem from "@material-ui/core/MenuItem";
import { State } from "../../../../reducers/state";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";

const LeadCogWheel = memo<any>(props => {
  const {
    selection,
    menuItemClass,
    hasQePermissions
  } = props;

  const noSelectedOrNew = useMemo(() => selection.length !== 1 || selection[0] === "NEW", [selection]);

  const onQuickEnrolment = useCallback(() => {
    window.open(`/checkout?leadId=${selection[0]}`, "_self");
  }, [selection]);

  return (
    <>
      <BulkEditCogwheelOption {...props} />
      {noSelectedOrNew ? null : (
        <MenuItem className={menuItemClass} onClick={onQuickEnrolment} disabled={!hasQePermissions}>
          Enrol
          {' '}
          {selection.length}
          {' '}
          highlighted contact
        </MenuItem>
      )}
    </>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.list.searchQuery,
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

export default connect(mapStateToProps)(LeadCogWheel);

