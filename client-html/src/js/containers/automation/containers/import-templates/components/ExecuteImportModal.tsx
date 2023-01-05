import {
  Binding,
  ExecuteImportRequest,
  ImportModel
} from "@api/model";
import Grid from "@mui/material/Grid";
import React, {
  useCallback, useEffect, useMemo
} from "react";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Typography from "@mui/material/Typography";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  destroy, Field, FieldArray, getFormValues, initialize, InjectedFormProps, reduxForm
} from "redux-form";
import Button from "@mui/material/Button";
import LoadingButton from "@mui/lab/LoadingButton";
import { interruptProcess } from "../../../../../common/actions";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import DataTypeRenderer from "../../../../../common/components/form/DataTypeRenderer";
import { ProcessState } from "../../../../../common/reducers/processReducer";
import { YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import { usePrevious } from "../../../../../common/utils/hooks";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { State } from "../../../../../reducers/state";
import { getEntityItemById } from "../../../../entities/common/entityItemsService";
import { runImport } from "../actions";

const FORM = "ExecuteImportForm";

interface Props {
  opened?: boolean;
  onClose?: any;
  onSave?: any;
  executeImport?: any;
  entity?: string;
  importId?: number;
  resetForm?: () => void;
  initializeForm?: any;
  dispatch?: Dispatch;
  values?: any;
  classes?: any;
  submitting?: boolean;
  interruptProcess?: (processId: string) => void;
  process?: ProcessState;
}

const templatesRenderer: React.FC<any> = React.memo<any>(({ fields }) => fields.map((f, index) => {
  const item: Binding = fields.get(index);

  const fieldProps: any = useMemo(() => {
    const props = {};

    if (item.type === "Checkbox") {
      props["stringValue"] = true;
    }

    if (item.type === "Date") {
      props["formatValue"] = YYYY_MM_DD_MINUSED;
    }

    return props;
  }, [item, item.type]);

  return (
    <Grid item xs={6} key={`${item.name}_${item.value}`}>
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

const ExecuteImportModal = React.memo<Props & InjectedFormProps>(props => {
  const {
    executeImport,
    handleSubmit,
    opened,
    onClose,
    invalid,
    importId,
    resetForm,
    initializeForm,
    dispatch,
    values,
    submitting,
    interruptProcess,
    process
  } = props;

  const prevProcessId = usePrevious(process.processId);

  const onDialogClose = useCallback(() => {
    if (process.processId) {
      interruptProcess(process.processId);
    }
    onClose();
  }, [process.processId]);

  const executing = Boolean(process.processId);

  useEffect(() => {
    if (prevProcessId && !process.processId) {
      onClose();
    }
  }, [process.processId]);

  useEffect(() => {
    if (importId) {
      getEntityItemById("Import", importId)
        .then(i => {
          const initialValues = { ...i };

          if (Array.isArray(i.variables) && i.variables.length) {
            initialValues.variables.forEach(v => {
              if (v.type === "Checkbox") {
                v.value = "false";
              }
            });
          }
          initializeForm(initialValues);
        })
        .catch(res => instantFetchErrorHandler(dispatch, res, `Failed to get import template ${importId || ""}`));
    } else {
      resetForm();
    }
  }, [importId]);

  useEffect(() => () => resetForm(), []);

  const handleRunImport = values => {
    const modelVariables = values.variables;
    const variables = modelVariables ? modelVariables.reduce((prev: any, cur) => {
      prev[cur.name] = cur.type === "File" ? cur.value && cur.value.name : cur.value;
      return prev;
    }, {}) : {};

    const files = modelVariables
      .filter(v => v.type === "File")
      .map(v => v.value);

    const executeImportRequest = {
      importScriptId: importId,
      variables
    };

    return executeImport(executeImportRequest, files);
  };

  return values ? (
    <Dialog open={opened} onClose={onDialogClose} maxWidth="md" fullWidth scroll="body">
      <form autoComplete="off" onSubmit={handleSubmit(handleRunImport)}>
        <DialogTitle className="heading pl-3 mb-1">
          {values.name ? `${values.name}` : "Execute import"}
        </DialogTitle>

        <DialogContent>
          <Grid container columnSpacing={3} rowSpacing={2}>
            {values.description && (
              <Grid item xs={12} className="mb-2">
                <Typography variant="body2" color="textSecondary" className="pb-2">
                  {`${values.description}`}
                </Typography>
              </Grid>
            )}
            <FieldArray name="variables" component={templatesRenderer} />
          </Grid>
        </DialogContent>

        <DialogActions className="p-3">
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
            Run import
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  ) : null;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(FORM)(state),
  submitting: state.fetch.pending,
  process: state.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  executeImport: (executeImportRequest: ExecuteImportRequest, files) =>
      dispatch(runImport(executeImportRequest, files)),
  initializeForm: (initialValues: ImportModel) => dispatch(initialize(FORM, initialValues)),
  resetForm: () => dispatch(destroy(FORM)),
  interruptProcess: (processId: string) => dispatch(interruptProcess(processId)),
  dispatch
});

export default reduxForm<any, Props>({
  form: FORM
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ExecuteImportModal));
