/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import { getFormSyncErrors, getFormValues, initialize, reduxForm } from "redux-form";
import { Dispatch } from "redux";
import { ExportTemplate } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { State } from "../../../../reducers/state";

import ImportTemplatesForm from "./containers/ImportTemplatesForm";
import {
  createImportTemplate,
  getImportTemplate,
  updateImportTemplate,
  updateInternalImportTemplate,
  removeImportTemplate
} from "./actions";
import { usePrevious } from "../../../../common/utils/hooks";

export const IMPORT_TEMPLATES_FORM_NAME = "ImportTemplatesForm";

const initialDefault: ExportTemplate = {
  name: null,
  keyCode: null,
  entity: null,
  body: null,
  status: "Installed but Disabled",
  variables: [],
  options: []
};

const ImportTemplates = React.memo<any>(props => {
  const {
    dispatch,
    getImportTemplate,
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
        dispatch(initialize(IMPORT_TEMPLATES_FORM_NAME, initialDefault));
      } else {
        getImportTemplate(id);
      }
    }
  }, [id, prevId]);

  return <ImportTemplatesForm dispatch={dispatch} isNew={isNew} {...rest} />;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(IMPORT_TEMPLATES_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(IMPORT_TEMPLATES_FORM_NAME)(state),
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  importTemplates: state.automation.importTemplate.importTemplates,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (template: ExportTemplate) => dispatch(createImportTemplate(template)),
  onUpdate: (template: ExportTemplate) => dispatch(updateImportTemplate(template)),
  onUpdateInternal: (template: ExportTemplate) => dispatch(updateInternalImportTemplate(template)),
  onDelete: (id: number) => dispatch(removeImportTemplate(id)),
  getImportTemplate: (id: number) => dispatch(getImportTemplate(id))
});

export default reduxForm({
  form: IMPORT_TEMPLATES_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(ImportTemplates)));
