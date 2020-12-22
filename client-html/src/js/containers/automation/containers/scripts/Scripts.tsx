/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import {
 getFormInitialValues, getFormValues, initialize, reduxForm
} from "redux-form";
import { ScheduleType, Script, TriggerType } from "@api/model";
import { Dispatch } from "redux";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../reducers/state";
import ScriptsForm from "./containers/ScriptsForm";
import {
 createScriptItem, deleteScriptItem, getScriptItem, saveScriptItem
} from "./actions";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "./constants";
import { ApiMethods } from "../../../../model/common/apiHandlers";
import { mapSelectItems } from "../../../../common/utils/common";
import { showConfirm } from "../../../../common/actions";

const TriggerTypeItems = Object.keys(TriggerType).map(mapSelectItems);

const ScheduleTypeItems = Object.keys(ScheduleType).map(mapSelectItems);

const Initial: Script = { enabled: false, content: "", keyCode: null };

const ScriptsBase = React.memo<any>(props => {
  const {
    getScriptItem,
    dispatch,
    history,
    scripts,
    emailTemplates,
    pdfReports,
    pdfBackgrounds,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const [isNew, setIsNew] = useState(false);

  useEffect(() => {
    const newId = id === "new";

    if (!id && scripts.length) {
      history.push(`/automation/script/${scripts[0].id}`);
      return;
    }

    if (newId && !isNew) {
      setIsNew(true);
      dispatch(initialize(SCRIPT_EDIT_VIEW_FORM_NAME, Initial));
    }
    if (!newId && id) {
      getScriptItem(id);
      if (isNew) {
        setIsNew(false);
      }
    }
  }, [id, isNew, scripts]);

  return (
    <ScriptsForm
      TriggerTypeItems={TriggerTypeItems}
      ScheduleTypeItems={ScheduleTypeItems}
      dispatch={dispatch}
      isNew={isNew}
      form={SCRIPT_EDIT_VIEW_FORM_NAME}
      emailTemplates={emailTemplates}
      pdfReports={pdfReports}
      pdfBackgrounds={pdfBackgrounds}
      {...rest}
    />
  );
});

const mapStateToProps = (state: State) => ({
  hasUpdateAccess: true,
  formsState: state.form,
  values: getFormValues(SCRIPT_EDIT_VIEW_FORM_NAME)(state),
  initialValues: getFormInitialValues(SCRIPT_EDIT_VIEW_FORM_NAME)(state),
  scripts: state.automation.script.scripts,
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  pdfReports: state.automation.pdfReport.pdfReports,
  pdfBackgrounds: state.automation.pdfBackground.pdfBackgrounds
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getScriptItem: (id: number) => dispatch(getScriptItem(id)),
  onSave: (id: number, script: Script, method?: ApiMethods) => dispatch(saveScriptItem(id, script, method)),
  onCreate: (script: Script) => dispatch(createScriptItem(script)),
  onDelete: (id: number) => dispatch(deleteScriptItem(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage))
});

export default reduxForm({
  form: SCRIPT_EDIT_VIEW_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ScriptsBase));
