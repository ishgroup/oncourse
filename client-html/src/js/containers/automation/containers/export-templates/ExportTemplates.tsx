/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { getFormValues, initialize, reduxForm } from "redux-form";
import { Dispatch } from "redux";
import { withRouter } from "react-router";
import { ExportTemplate } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../reducers/state";
import { setNextLocation } from "../../../../common/actions";
import ExportTemplatesForm from "./containers/ExportTemplatesForm";
import {
  createExportTemplate,
  getExportTemplate,
  updateExportTemplate,
  updateInternalExportTemplate,
  removeExportTemplate
} from "./actions";
import { usePrevious } from "../../../../common/utils/hooks";

export const EXPORT_TEMPLATES_FORM_NAME = "ExportTemplatesForm";

const initialDefault: ExportTemplate = {
  name: null,
  keyCode: null,
  entity: null,
  body: null,
  enabled: false,
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
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (template: ExportTemplate) => dispatch(createExportTemplate(template)),
  onUpdate: (template: ExportTemplate) => dispatch(updateExportTemplate(template)),
  onUpdateInternal: (template: ExportTemplate) => dispatch(updateInternalExportTemplate(template)),
  onDelete: (id: number) => dispatch(removeExportTemplate(id)),
  getExportTemplate: (id: number) => dispatch(getExportTemplate(id)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default reduxForm({
  form: EXPORT_TEMPLATES_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(ExportTemplates)));
