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
import { setNextLocation, showConfirm } from "../../../../common/actions";
import PdfReportsForm from "./containers/PdfBackgroundsForm";
import { usePrevious } from "../../../../common/utils/hooks";
import {
  createAutomationPdfBackground,
  getAutomationPdfBackground,
  removeAutomationPdfBackground,
  updateAutomationPdfBackground
} from "./actions";

export const PDF_BACKGROUND_FORM_NAME = "PdfBackgroundsForm";

const initialDefault: ExportTemplate = {
  variables: []
};

const PdfBackgrounds = React.memo<any>(props => {
  const {
    dispatch,
    getPdfBackground,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const prevId = usePrevious(id);

  const isNew = useMemo(() => id === "new", [id]);

  useEffect(() => {
    if (id && prevId !== id) {
      isNew ? dispatch(initialize(PDF_BACKGROUND_FORM_NAME, initialDefault)) : getPdfBackground(id);
    }
  }, [id, prevId]);

  return <PdfReportsForm dispatch={dispatch} isNew={isNew} {...rest} />;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(PDF_BACKGROUND_FORM_NAME)(state),
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (fileName: string, overlay: File) => dispatch(createAutomationPdfBackground(fileName, overlay)),
  onUpdate: (fileName: string, id: number, overlay: File) =>
    dispatch(updateAutomationPdfBackground(fileName, id, overlay)),
  onDelete: (id: number) => dispatch(removeAutomationPdfBackground(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage)),
  getPdfBackground: (id: number) => dispatch(getAutomationPdfBackground(id)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default reduxForm({
  form: PDF_BACKGROUND_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(PdfBackgrounds)));
