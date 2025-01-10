/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ScheduleType, Script } from "@api/model";
import { mapSelectItems } from "ish-ui";
import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import { Dispatch } from "redux";
import { getFormInitialValues, getFormSyncErrors, getFormValues, initialize, reduxForm } from "redux-form";
import { showConfirm } from "../../../../common/actions";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { State } from "../../../../reducers/state";
import { createScriptItem, deleteScriptItem, getScriptItem, saveScriptItem } from "./actions";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "./constants";
import ScriptsForm from "./containers/ScriptsForm";

const ScheduleTypeItems = Object.keys(ScheduleType).map(mapSelectItems);

const Initial: Script = { status: "Installed but Disabled", content: "", keyCode: null, trigger: { cron: {} } };

const ScriptsBase = React.memo<any>(props => {
  const {
    getScriptItem,
    dispatch,
    history,
    scripts,
    emailTemplates,
    pdfReports,
    pdfBackgrounds,
    timeZone,
    syncErrors,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const [isNew, setIsNew] = useState(false);

  useEffect(() => {
    if (!id && scripts.length) {
      history.push(`/automation/script/${scripts[0].id}`);
      return;
    }

    if (id === "new" && !isNew) {
      setIsNew(true);
      dispatch(initialize(SCRIPT_EDIT_VIEW_FORM_NAME, Initial));
    }
    if (id && !Number.isNaN(Number(id))) {
      getScriptItem(id);
      if (isNew) {
        setIsNew(false);
      }
    }
  }, [id, isNew, scripts]);

  return (
    <ScriptsForm
      ScheduleTypeItems={ScheduleTypeItems}
      dispatch={dispatch}
      isNew={isNew}
      form={SCRIPT_EDIT_VIEW_FORM_NAME}
      emailTemplates={emailTemplates}
      pdfReports={pdfReports}
      pdfBackgrounds={pdfBackgrounds}
      history={history}
      timeZone={timeZone}
      syncErrors={syncErrors}
      scripts={scripts}
      {...rest}
    />
  );
});

const mapStateToProps = (state: State) => ({
  hasUpdateAccess: true,
  formsState: state.form,
  values: getFormValues(SCRIPT_EDIT_VIEW_FORM_NAME)(state),
  initialValues: getFormInitialValues(SCRIPT_EDIT_VIEW_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(SCRIPT_EDIT_VIEW_FORM_NAME)(state),
  scripts: state.automation.script.scripts,
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  nextLocation: state.nextLocation,
  timeZone: state.automation.timeZone,
  checklists: state.tags.allChecklists
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getScriptItem: (id: number) => dispatch(getScriptItem(id)),
  onSave: (id, script, method, viewMode) => dispatch(saveScriptItem(id, script, method, viewMode)),
  onCreate: (script, viewMode) => dispatch(createScriptItem(script, viewMode)),
  onDelete: (id: number) => dispatch(deleteScriptItem(id)),
  openConfirm: props => dispatch(showConfirm(props))
});

export default reduxForm({
  form: SCRIPT_EDIT_VIEW_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(ScriptsBase)));
