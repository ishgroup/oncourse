/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Grid from "@mui/material/Grid";
import { change } from "redux-form";
import { Dispatch } from "redux";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import PdfService from "../../../pdf-reports/services/PdfService";
import { ScriptComponent } from "../../../../../../model/scripts";
import instantFetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { renderAutomationItems } from "../../../../utils";
import { useAppSelector } from "../../../../../../common/utils/hooks";

interface Props {
  name: string;
  field: ScriptComponent;
  dispatch: Dispatch;
  form: string;
  renderVariables: any;
  disabled: boolean;
}

const ReportCardContent: React.FC<Props> = props => {
  const {
    dispatch, field, name, renderVariables, form, disabled
  } = props;

  const pdfReports = useAppSelector(state => state.automation.pdfReport.pdfReports);
  const pdfBackgrounds = useAppSelector(state => state.automation.pdfBackground.pdfBackgrounds);

  const pdfReportsItems = useMemo(
    () => (pdfReports
      ? pdfReports.filter(t => t.keyCode).map(t => ({
         value: t.keyCode, hasIcon: t.keyCode.startsWith("ish."), label: t.title, id: t.id
        }))
      : []), [pdfReports]
  );

  const pdfBackgroundsItems = useMemo(
    () => (pdfBackgrounds
      ? pdfBackgrounds.filter(t => t.title).map(t => ({ value: t.title, label: t.title }))
      : []), [pdfBackgrounds]
  );

  const getPdfReport = async (id: number) => PdfService
    .getReport(id)
    .catch(e => instantFetchErrorHandler(dispatch, e));

  const changePdfReport = async item => {
    const pdfReport = await getPdfReport(item.id);

    if (pdfReport) {
      pdfReport.variables.forEach(e => {
        if (e.type === "Checkbox") {
          dispatch(change(form, `${name}.${e.name}`, false));
        }
      });
      dispatch(change(form, `${name}.reportEntity`, pdfReport));
    }
  };

  return (
    <Grid container columnSpacing={3} rowSpacing={1} className="pt-3 pb-3">
      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.fileName`}
          label="File Name"
          disabled={disabled}
          required
        />
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${name}.keycode`}
          label="Report"
          items={pdfReportsItems}
          className="d-flex mt-2"
          onInnerValueChange={changePdfReport}
          selectLabelCondition={renderAutomationItems}
          disabled={disabled}
          required
        />
      </Grid>
      {field.reportEntity && renderVariables(field.reportEntity.variables, name)}
      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${name}.background`}
          label="Background"
          items={pdfBackgroundsItems}
          className="d-flex mt-2"
          disabled={disabled}
          allowEmpty
        />
      </Grid>
    </Grid>
  );
};

export default ReportCardContent;
