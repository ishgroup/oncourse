/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  Binding, ExecuteScriptRequest, OutputType, Script, SearchQuery
} from "@api/model";
import Grid from "@mui/material/Grid";
import { format } from "date-fns";
import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Typography from "@mui/material/Typography";
import LoadingButton from "@mui/lab/LoadingButton";
import Button from "@mui/material/Button";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  destroy, Field, FieldArray, getFormValues, initialize, InjectedFormProps, reduxForm
} from "redux-form";
import { interruptProcess } from "../../../../../common/actions";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import DataTypeRenderer from "../../../../../common/components/form/DataTypeRenderer";
import ScriptRunAudit from "../../../../../common/components/layout/swipeable-sidebar/components/SidebarScripts/ScriptRunAudit";
import { getExpression } from "../../../../../common/components/list-view/utils/listFiltersUtils";
import { ProcessState } from "../../../../../common/reducers/processReducer";
import EntityService from "../../../../../common/services/EntityService";
import { III_DD_MMM_YYYY_HH_MM, YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import { usePrevious } from "../../../../../common/utils/hooks";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { State } from "../../../../../reducers/state";
import RecipientsSelectionSwitcher from "../../../../entities/messages/components/RecipientsSelectionSwitcher";
import { runScript } from "../actions";
import ScriptsService from "../services/ScriptsService";
import { LICENSE_SCRIPTING_KEY } from "../../../../../constants/Config";
import { getCookie } from "../../../../../common/utils/Cookie";

const FORM = "ExecuteScriptForm";

interface Props {
  opened?: boolean;
  onClose?: any;
  onSave?: any;
  selection?: string[];
  executeScript?: any;
  entity?: string;
  scriptId?: number;
  resetForm?: () => void;
  initializeForm?: any;
  dispatch?: Dispatch;
  values?: Script;
  classes?: any;
  filteredCount?: number;
  submitting?: boolean;
  listSearchQuery?: SearchQuery;
  interruptProcess?: (processId: string) => void;
  process?: ProcessState;
  updateAudits?: any;
  hasScriptingLicense?: boolean;
  filteredSelection?: string[];
}

const templatesRenderer: React.FC<any> = React.memo<any>(({ fields }) => fields.map((f, index) => {
  const item: Binding = fields.get(index);

  const fieldProps: any = useMemo(() => {
    const props = {};

    if (["Checkbox", "Money"].includes(item.type)) {
      props["stringValue"] = true;
    }

    if (item.type === "Date") {
      props["formatValue"] = YYYY_MM_DD_MINUSED;
    }

    return props;
  }, [item, item.type]);

  return (
    <Grid item xs={6} key={f}>
      <Field
        label={item.label}
        name={`${f}.value`}
        type={item.type}
        component={DataTypeRenderer}
        validate={validateSingleMandatoryField}
                {...fieldProps}
      />
    </Grid>
  );
}));

const ExecuteScriptModal = React.memo<Props & InjectedFormProps>(props => {
  const {
    executeScript,
    handleSubmit,
    opened,
    onClose,
    invalid,
    scriptId,
    resetForm,
    initializeForm,
    dispatch,
    values,
    selection,
    filteredCount,
    submitting,
    interruptProcess,
    hasScriptingLicense,
    listSearchQuery,
    filteredSelection,
    process
  } = props;

  const [selectAll, setSelectAll] = useState(false);
  const [selectedScriptAudits, setSelectedScriptAudits] = useState([]);

  const prevScriptId = usePrevious(scriptId);
  const prevProcessId = usePrevious(process.processId);

  const onDialogClose = useCallback(() => {
    if (process.processId) {
      interruptProcess(process.processId);
    }
    onClose();
  }, [process.processId]);

  const executing = Boolean(process.processId);

  const updateAudits = () => {
    EntityService.getPlainRecords(
      "Audit",
      "created,action",
      `entityIdentifier is "Script" and entityId is ${scriptId} and ( action is SCRIPT_FAILED or action is SCRIPT_EXECUTED)`,
      7,
      0,
      'created',
      false
    )
      .then(response => {
        const audits = response.rows.map(r => ({
          runDate: r.values[0],
          action: r.values[1]
        }));

        setSelectedScriptAudits(audits);
      })
      .catch(res => instantFetchErrorHandler(dispatch, res, `Failed to get audits fo script ${scriptId || ""}`));
  };

  useEffect(() => {
    if (prevProcessId && !process.processId) {
      onClose();

      if (typeof updateAudits === "function" && scriptId) {
        updateAudits();
      }
    }
  }, [process.processId]);

  useEffect(() => {
    if (scriptId) {
      updateAudits();
      ScriptsService.getScriptItem(scriptId)
        .then(s => {
          const initialValues = { ...s };

          if (Array.isArray(s.variables) && s.variables.length) {
            initialValues.variables.forEach(v => {
              if (v.type === "Checkbox") {
                v.value = "false";
              }
            });
          }

          initializeForm(initialValues);
        })
        .catch(res => instantFetchErrorHandler(dispatch, res, `Failed to get script ${scriptId || ""}`));
    } else if (prevScriptId) {
      resetForm();
    }
  }, [scriptId]);

  const handleRunScript = values => {
    const modelVariables = values.variables;
    const variables = modelVariables ? modelVariables.reduce((prev: any, cur) => {
        prev[cur.name] = cur.value;
        return prev;
      }, {}) : {};

    let executeScriptRequest;

    if ((values.trigger && values.trigger.entityName) || values.entity) {
      const searchQuery = { ...listSearchQuery };

      if (!selectAll) {
        searchQuery.search = getExpression(filteredSelection || selection);
      }

      executeScriptRequest = {
        scriptId,
        variables,
        searchQuery
      };
    } else {
      executeScriptRequest = {
        scriptId,
        variables
      };
    }

    return executeScript(executeScriptRequest, values.outputType, values.name);
  };

  const lastRun = selectedScriptAudits.length
    ? format(new Date(selectedScriptAudits[0].runDate), III_DD_MMM_YYYY_HH_MM)
    : "never";
  
  if (opened && !hasScriptingLicense && !values?.keyCode?.startsWith("ish.")) {
    return (
      <Dialog open onClose={onDialogClose}>
        <DialogTitle>
          Script execution disabled
        </DialogTitle>

        <DialogContent>
          Custom scripts execution disabled due to
          {' '}
          <a href={`https://provisioning.ish.com.au?token=${getCookie("JSESSIONID")}`}>required license</a>
        </DialogContent>

        <DialogActions>
          <Button color="primary" onClick={onDialogClose}>
            Close
          </Button>
        </DialogActions>
      </Dialog>
    );
  }

  return values ? (
    <Dialog open={opened} onClose={onClose} maxWidth="md" fullWidth scroll="body">
      <form autoComplete="off" onSubmit={handleSubmit(handleRunScript)}>
        <DialogTitle className="heading pl-3 mb-1">
          {values.name ? `${values.name}` : "Execute script"}
        </DialogTitle>

        <DialogContent>
          <Grid container columnSpacing={3}>
            {values.description && (
              <Grid item xs={12} className="mb-2">
                <Typography variant="body2" color="textSecondary" className="pb-2">
                  {`${values.description}`}
                </Typography>
              </Grid>
            )}
            {(values.trigger.entityName || values.entity) && (
              <Grid item xs={12} className="centeredFlex mb-2">
                <RecipientsSelectionSwitcher
                  selectedRecords={filteredSelection?.length || selection.length}
                  allRecords={filteredCount}
                  selectAll={selectAll}
                  setSelectAll={setSelectAll}
                  disabled={submitting || executing}
                />
              </Grid>
            )}

            <FieldArray name="variables" component={templatesRenderer} />
          </Grid>
        </DialogContent>

        <DialogActions className="p-3">
          <ScriptRunAudit lastRun={lastRun} selectedScriptAudits={selectedScriptAudits} scriptIdSelected={scriptId} />
          <Button color="primary" onClick={onDialogClose}>
            Cancel
          </Button>
          <LoadingButton
            variant="contained"
            color="primary"
            type="submit"
            disabled={invalid || submitting || executing}
            loading={submitting || executing}
          >
            Run script
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  ) : null;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(FORM)(state),
  hasScriptingLicense: state.userPreferences[LICENSE_SCRIPTING_KEY] && state.userPreferences[LICENSE_SCRIPTING_KEY] === "true",
  submitting: state.fetch.pending,
  listSearchQuery: state.list.searchQuery,
  process: state.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  executeScript: (executeScriptRequest: ExecuteScriptRequest, outputType: OutputType, name: string) =>
    dispatch(runScript(executeScriptRequest, outputType, name)),
  initializeForm: (initialValues: Script) => dispatch(initialize(FORM, initialValues)),
  resetForm: () => dispatch(destroy(FORM)),
  interruptProcess: (processId: string) => dispatch(interruptProcess(processId)),
  dispatch
});

export default reduxForm<any, Props>({
  form: FORM
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ExecuteScriptModal));