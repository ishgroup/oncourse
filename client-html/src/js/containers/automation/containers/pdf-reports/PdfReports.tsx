/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import {
  getFormInitialValues, getFormSyncErrors, getFormValues, initialize, reduxForm
} from "redux-form";
import { withRouter } from "react-router";
import { Dispatch } from "redux";
import { ExportTemplate, Report } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { State } from "../../../../reducers/state";
import { showConfirm } from "../../../../common/actions";
import PdfReportsForm from "./containers/PdfReportsForm";
import { usePrevious } from "../../../../common/utils/hooks";
import {
  createAutomationPdfReport,
  getAutomationPdfReport,
  updateAutomationPdfReport,
  updateInternalAutomationPdfReport,
  removeAutomationPdfReport
} from "./actions";

export const PDF_REPORT_FORM_NAME = "PdfReportForm";

const initialDefault: ExportTemplate = {
  variables: [],
  status: "Installed but Disabled"
};

const PdfReports = React.memo<any>(props => {
  const {
    dispatch,
    getPdfReport,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const prevId = usePrevious(id);

  const isNew = useMemo(() => id === "new", [id]);

  useEffect(() => {
    if (id && prevId !== id) {
      isNew ? dispatch(initialize(PDF_REPORT_FORM_NAME, initialDefault)) : getPdfReport(id);
    }
  }, [id, prevId]);

  return <PdfReportsForm dispatch={dispatch} isNew={isNew} {...rest} />;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(PDF_REPORT_FORM_NAME)(state),
  initialValues: getFormInitialValues(PDF_REPORT_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(PDF_REPORT_FORM_NAME)(state),
  pdfBackgrounds: state.automation.pdfBackground.pdfBackgrounds,
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: report => dispatch(createAutomationPdfReport(report)),
  onUpdate: report => dispatch(updateAutomationPdfReport(report)),
  onDelete: (id: number) => dispatch(removeAutomationPdfReport(id)),
  openConfirm: props => dispatch(showConfirm(props)),
  getPdfReport: (id: number) => dispatch(getAutomationPdfReport(id)),
  onUpdateInternal: report => dispatch(updateInternalAutomationPdfReport(report)),
});

const validatePdfReportBody = (values: Report) => {
  const errors = {};

  if (values && !values.body) {
    errors["body"] = "body is required";
  }

  return errors;
};

export default reduxForm({
  form: PDF_REPORT_FORM_NAME,
  validate: validatePdfReportBody,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(PdfReports)));
