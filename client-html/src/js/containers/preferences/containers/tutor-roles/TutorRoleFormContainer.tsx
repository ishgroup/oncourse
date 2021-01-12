/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo, useEffect, useCallback, useState } from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormValues, initialize, isDirty } from "redux-form";
import { DefinedTutorRole } from "@api/model";
import { State } from "../../../../reducers/state";
import { usePrevious } from "../../../../common/utils/hooks";
import {
 createTutorRole, getTutorRole, removeTutorRole, updateTutorRole 
} from "../../actions";
import TutorRolesForm from "./components/TutorRolesForm";
import { setNextLocation, showConfirm } from "../../../../common/actions";

export const TUTOR_ROLES_FORM_NAME: string = "TutorRolesForm";

const initialValues: DefinedTutorRole = {
  name: null,
  description: null,
  active: true,
  payRates: [],
};

const TutorRoleFormContainer = React.memo<any>(props => {
  const {
    dispatch,
    getTutorRole,
    match: {
      params: { id }
    },
    onCreate,
    onUpdate,
    onDelete,
    tutorRoles,
    nextLocation,
    setNextLocation,
    history,
    isDirty,
    ...rest
  } = props;

  const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);
  const prevId = usePrevious(id);
  const isNew = useMemo(() => id === "new", [id]);

  useEffect(() => {
    if (id && prevId !== id) {
      isNew ? dispatch(initialize(TUTOR_ROLES_FORM_NAME, initialValues)) : getTutorRole(id);
    }

    if (disableRouteConfirm && id !== prevId) {
      setDisableRouteConfirm(false);
    }

    if (nextLocation && !isDirty) {
      history.push(nextLocation);
      setNextLocation('');
    }
  }, [id, prevId, nextLocation, isDirty]);

  const handleDelete = useCallback(() => {
    setDisableRouteConfirm(true);
    onDelete(id, tutorRoles);
  }, [id]);

  const handleSave = useCallback(
    val => {
      setDisableRouteConfirm(true);
      if (isNew) {
        onCreate(val);
        return;
      }
      onUpdate(val);
    },
    [isNew]
  );
  
  return (
    <TutorRolesForm
      dispatch={dispatch}
      isNew={isNew}
      onSubmit={handleSave}
      onDelete={onDelete}
      handleDelete={handleDelete}
      disableRouteConfirm={disableRouteConfirm}
      formName={TUTOR_ROLES_FORM_NAME}
      {...rest}
    />);
});

const mapStateToProps = (state: State) => ({
  value: getFormValues(TUTOR_ROLES_FORM_NAME)(state),
  isDirty: isDirty(TUTOR_ROLES_FORM_NAME)(state),
  tutorRoles: state.preferences.tutorRoles,
  fetch: state.fetch,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    onUpdate: (role: DefinedTutorRole) => dispatch(updateTutorRole(role)),
    onDelete: (id: string, tutorRoles: DefinedTutorRole[]) => dispatch(removeTutorRole(id, tutorRoles)),
    onCreate: (role: DefinedTutorRole) => dispatch(createTutorRole(role)),
    getTutorRole: (id: string) => dispatch(getTutorRole(id)),
    showConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage)),
    setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(TutorRoleFormContainer));
