/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { getFormSyncErrors, getFormValues, initialize, reduxForm } from "redux-form";
import { Dispatch } from "redux";
import { withRouter } from "react-router";
import { ExportTemplate } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { State } from "../../../../reducers/state";
import ExportTemplatesForm from "./containers/ExportTemplatesForm";
import {
  createExportTemplate,
  getExportTemplate,
  updateExportTemplate,
  updateInternalExportTemplate,
  removeExportTemplate
} from "./actions";
import { usePrevious } from "ish-ui";

export const EXPORT_TEMPLATES_FORM_NAME = "ExportTemplatesForm";

const initialDefault: ExportTemplate = {
  name: null,
  keyCode: null,
  entity: null,
  body: null,
  status: "Installed but Disabled",
  variables: [],
  options: [],
  outputType: null
};

const ExportTemplates = React.memo<any>(props => {
  const {
    dispatch,
    getExportTemplate,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const prevId = usePrevious(id);

  const isNew = useMemo(() => id === "new", [id]);

  useEffect(() => {
    if (id && prevId !== id) {
      if (isNew) {
        dispatch(initialize(EXPORT_TEMPLATES_FORM_NAME, initialDefault));
      } else {
        getExportTemplate(id);
      }
    }
  }, [id, prevId]);

  return <ExportTemplatesForm dispatch={dispatch} isNew={isNew} {...rest} />;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(EXPORT_TEMPLATES_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(EXPORT_TEMPLATES_FORM_NAME)(state),
  exportTemplates: state.automation.exportTemplate.exportTemplates,
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (template: ExportTemplate) => dispatch(createExportTemplate(template)),
  onUpdate: (template: ExportTemplate) => dispatch(updateExportTemplate(template)),
  onUpdateInternal: (template: ExportTemplate) => dispatch(updateInternalExportTemplate(template)),
  onDelete: (id: number) => dispatch(removeExportTemplate(id)),
  getExportTemplate: (id: number) => dispatch(getExportTemplate(id))
});

export default reduxForm({
  form: EXPORT_TEMPLATES_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(ExportTemplates)));