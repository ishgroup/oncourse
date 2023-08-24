/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import MenuItem from "@mui/material/MenuItem";
import React, { memo, useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import EntityService from "../../../../common/services/EntityService";
import { useAppDispatch } from "../../../../common/utils/hooks";
import { State } from "../../../../reducers/state";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";

const LeadCogWheel = memo<any>(props => {
  const {
    selection,
    menuItemClass,
    hasQePermissions
  } = props;
  
  const dispatch = useAppDispatch();
  
  const [hasProductRelations, setHasProductRelations] = useState(false);
  const [hasCourseRelations, setHasCourseRelations] = useState(false);

  const noSelectedOrNew = useMemo(() => selection.length !== 1 || selection[0] === "NEW", [selection]);

  const onQuickEnrolment = useCallback(() => {
    window.open(`/checkout?leadId=${selection[0]}`, "_self");
  }, [selection]);

  const onQuickApplication = useCallback(() => {
    window.open(`/application/new?leadId=${selection[0]}`, "_self");
  }, [selection]);

  useEffect(
    () => {
      if (selection.length === 1) {
        EntityService.getPlainRecords(
          "Lead",
          "items.course.id,items.product.id",
          `id is ${selection[0]}`
        )
        .then(lead => {
          const leadCourseIds = JSON.parse(lead.rows[0].values[0]);
          const productIds = JSON.parse(lead.rows[0].values[1]);
          setHasCourseRelations(Boolean(leadCourseIds.length));
          setHasProductRelations(Boolean(productIds.length));
        })
        .catch(e => instantFetchErrorHandler(dispatch, e));
      }
    },
    [selection]
  );

  return (
    <>
      <BulkEditCogwheelOption {...props} />
      <MenuItem className={menuItemClass} onClick={onQuickApplication} disabled={noSelectedOrNew || !hasCourseRelations}>
        Convert lead to application...
      </MenuItem>
      <MenuItem className={menuItemClass} onClick={onQuickEnrolment} disabled={!hasQePermissions || noSelectedOrNew || !(hasProductRelations || hasCourseRelations)}>
        Convert lead to sale...
      </MenuItem>
    </>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.list.searchQuery,
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

export default connect(mapStateToProps)(LeadCogWheel);

