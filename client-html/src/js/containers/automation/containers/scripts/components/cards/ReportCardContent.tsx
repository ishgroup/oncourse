/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import { Dispatch } from "redux";
import { useSelector } from "react-redux";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import PdfService from "../../../pdf-reports/services/PdfService";
import { ScriptComponent } from "../../../../../../model/scripts";
import { State } from "../../../../../../reducers/state";
import instantFetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { renderAutomationItems } from "../../../../utils";

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

  const pdfReports = useSelector<State, any>(state => state.automation.pdfReport.pdfReports);
  const pdfBackgrounds = useSelector<State, any>(state => state.automation.pdfBackground.pdfBackgrounds);

  const pdfReportsItems = useMemo(
    () => (pdfReports
      ? pdfReports.filter(t => t.keyCode).map(t => ({
         value: t.keyCode, hasIcon: t.hasIcon, label: t.name, id: t.id
        }))
      : []), [pdfReports]
  );

  const pdfBackgroundsItems = useMemo(
    () => (pdfBackgrounds
      ? pdfBackgrounds.filter(t => t.name).map(t => ({ value: t.name, label: t.name }))
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
    <Grid container className="mt-2">
      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.fileName`}
          label="File Name"
          disabled={disabled}
          required
        />

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

        {field.reportEntity && renderVariables(field.reportEntity.variables, name)}

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
