/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {useEffect, useMemo, useState} from "react";
import Grid from "@material-ui/core/Grid";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import {change} from "redux-form";
import {SCRIPT_EDIT_VIEW_FORM_NAME} from "../../constants";
import PdfService from "../../../pdf-reports/services/PdfService";

const ReportCardContent = props => {
  const {
    dispatch, field, name, pdfReports, pdfBackgrounds, renderVariables
  } = props;

  const [pdfReportsVariables, setPdfReportsVariables] = useState([]);

  const pdfReportsItems = useMemo(
    () => (pdfReports
      ? pdfReports.filter(t => t.keyCode).map(t => ({ value: t.keyCode, label: t.name, id: t.id }))
      : []), [pdfReports]);

  const pdfBackgroundsItems = useMemo(
    () => (pdfBackgrounds
      ? pdfBackgrounds.filter(t => t.name).map(t => ({ value: t.name, label: t.name }))
      : []), [pdfBackgrounds]);

  const getPdfReport = async (id: number) => {
    let pdfReport;

    try {
      pdfReport = await PdfService.getReport(id);
      pdfReport && setPdfReportsVariables(pdfReport.variables);
    } catch (e) {
      console.warn(e)
    }

    return pdfReport;
  }

  const changePdfReport = async (item) => {
    const pdfReport = await getPdfReport(item.id)

    pdfReport && pdfReport.variables && pdfReport.variables.forEach(e => {
      if (e.type === "Checkbox") {
        dispatch(change(SCRIPT_EDIT_VIEW_FORM_NAME, `${name}.${e.name}`, false));
      }
    })
  }

  useEffect(() => {
    if (field && field.report) {
      const currentPdfReport = pdfReports.filter(t => t.keyCode === field.report);
      currentPdfReport.length && getPdfReport(currentPdfReport[0].id);
    }
  }, [field]);

  return (
    <Grid container>
      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.fileName`}
          label="File Name"
          required
        />

        <FormField
          type="select"
          name={`${name}.keycode`}
          label="Report"
          items={pdfReportsItems}
          className="d-flex mt-2"
          onInnerValueChange={changePdfReport}
          required
        />

        {renderVariables(pdfReportsVariables, name)}

        <FormField
          type="select"
          name={`${name}.background`}
          label="Background"
          items={pdfBackgroundsItems}
          className="d-flex mt-2"
          required
        />

      </Grid>
    </Grid>
  );
};

export default ReportCardContent;
