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
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../reducers/state";
import { setNextLocation } from "../../../../common/actions";
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
  syncErrors: getFormSyncErrors(PDF_BACKGROUND_FORM_NAME)(state),
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (fileName: string, overlay: File) => dispatch(createAutomationPdfBackground(fileName, overlay)),
  onUpdate: (fileName: string, id: number, overlay: File) =>
    dispatch(updateAutomationPdfBackground(fileName, id, overlay)),
  onDelete: (id: number) => dispatch(removeAutomationPdfBackground(id)),
  getPdfBackground: (id: number) => dispatch(getAutomationPdfBackground(id)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default reduxForm({
  form: PDF_BACKGROUND_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(PdfBackgrounds)));
