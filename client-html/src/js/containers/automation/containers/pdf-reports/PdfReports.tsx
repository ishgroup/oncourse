/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import {
 getFormInitialValues, getFormValues, initialize, reduxForm 
} from "redux-form";
import { withRouter } from "react-router";
import { Dispatch } from "redux";
import { ExportTemplate, Report } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../reducers/state";
import { setNextLocation, showConfirm } from "../../../../common/actions";
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
  variables: []
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
  pdfBackgrounds: state.automation.pdfBackground.pdfBackgrounds,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: report => dispatch(createAutomationPdfReport(report)),
  onUpdate: report => dispatch(updateAutomationPdfReport(report)),
  onDelete: (id: number) => dispatch(removeAutomationPdfReport(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage)),
  getPdfReport: (id: number) => dispatch(getAutomationPdfReport(id)),
  onUpdateInternal: report => dispatch(updateInternalAutomationPdfReport(report)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
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
