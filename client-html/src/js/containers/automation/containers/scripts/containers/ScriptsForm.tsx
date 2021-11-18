/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import FileCopy from "@mui/icons-material/FileCopy";
import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import Grid from "@mui/material/Grid";
import { withStyles } from "@mui/styles";
import {
  arrayInsert, change, FieldArray, Form, initialize
} from "redux-form";
import Typography from "@mui/material/Typography";
import { OutputType, Script, TriggerType } from "@api/model";
import createStyles from "@mui/styles/createStyles";
import DeleteForever from "@mui/icons-material/DeleteForever";
import ViewAgendaIcon from '@mui/icons-material/ViewAgenda';
import CodeIcon from '@mui/icons-material/Code';
import FormField from "../../../../../common/components/form/formFields/FormField";
import { mapSelectItems } from "../../../../../common/utils/common";
import { usePrevious } from "../../../../../common/utils/hooks";
import { CommonListItem } from "../../../../../model/common/sidebar";
import Bindings from "../../../components/Bindings";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import { validateKeycode } from "../../../utils";
import ScriptCard from "../components/cards/CardBase";
import { formatRelativeDate } from "../../../../../common/utils/dates/formatRelative";
import ImportCardContent from "../components/cards/ImportCardContent";
import TriggerCardContent from "../components/cards/TriggerCardContent";
import ScriptAddMenu from "../components/ScriptAddMenu";
import { setScriptComponents } from "../actions";
import { ScriptComponentType, ScriptViewMode } from "../../../../../model/scripts";
import CardsRenderer from "../components/cards/CardsRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import {
  getMessageComponent, getQueryComponent, getReportComponent, getScriptComponent
} from "../constants";
import { DD_MMM_YYYY_AT_HH_MM_AAAA_SPECIAL } from "../../../../../common/utils/dates/format";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { ApiMethods } from "../../../../../model/common/apiHandlers";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("scripts");
const getAuditsUrl = (id: number) => `audit?search=~"Script" and entityId == ${id}`;

const styles = theme =>
  createStyles({
    root: {
      padding: theme.spacing(0, 9),
    },
    divider: {
      margin: "20px 0",
    },
    checkbox: {
      height: "50px",
      display: "flex",
    },
    cardsBox: {
      padding: theme.spacing(0, 3, 3, 0),
    },
    deleteButtonContainer: {
      display: "flex",
    },
    deleteButton: {
      right: "-8px",
      top: "-8px",
    },
    cardContent: {
      height: "120px",
    },
    getBackIcon: {
      color: "#fff",
      transform: "rotate(180deg)",
    },
    dragging: {
      borderRadius: "4px",
      width: "55px",
      paddingLeft: "4px",
      height: "25px",
      background: "#fff",
    },
    dialogContent: {
      width: "450px",
      padding: theme.spacing(0, 3),
    },
    infoContainer: {
      background: theme.palette.background.default,
      borderRadius: "4px",
      padding: theme.spacing(1, 0, 1, 2),
      margin: theme.spacing(1, 0, 3, 0),
    },
    queryField: {},
  });

const entityNameTypes = Object.keys(TriggerType).slice(0, 6).filter(t => t !== "Schedule");

const TriggerTypeItems = Object.keys(TriggerType).map(mapSelectItems);

type TriggerToRecordObjType = {
  [K in TriggerType]?: string
};

const TriggerToRecordTypeMap: TriggerToRecordObjType = {
  "Enrolment successful": "Enrolment",
  "Enrolment cancelled": "Enrolment",
  "Class cancelled": "CourseClass",
  "Class published on web": "CourseClass",
};

interface Props {
  isNew: boolean;
  values: any;
  initialValues;
  dispatch: any;
  ScheduleTypeItems: any;
  hasUpdateAccess: boolean;
  history: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
  classes?: any;
  dirty?: boolean;
  created?: Date;
  modified?: Date;
  initialized?: boolean;
  invalid?: boolean;
  form?: string;
  openConfirm?: ShowConfirmCaller;
  handleSubmit?: any;
  onSave?: (id: number, script: Script, method: ApiMethods, viewMode: ScriptViewMode) => void;
  onCreate?: (script: Script, viewMode: ScriptViewMode) => void;
  onDelete?: any;
  formsState?: any;
  emailTemplates?: CommonListItem[];
  timeZone?: string;
  syncErrors?: any;
}

const getInitComponentBody = (componentName: ScriptComponentType) => {
  switch (componentName) {
    case "Query": {
      return getQueryComponent("");
    }
    case "Script": {
      return getScriptComponent("");
    }
    case "Message": {
      return getMessageComponent("");
    }
    case "Report": {
      return getReportComponent("");
    }
    default:
      return null;
  }
};

const outputTypes = [...Object.keys(OutputType).map(mapSelectItems), { label: "no output", value: "" }];

const ScriptsForm = React.memo<Props>(props => {
  const {
    classes,
    dirty,
    dispatch,
    form,
    values,
    initialValues,
    invalid,
    ScheduleTypeItems,
    openConfirm,
    hasUpdateAccess,
    handleSubmit,
    onSave,
    onCreate,
    onDelete,
    isNew,
    formsState,
    emailTemplates,
    history,
    nextLocation,
    setNextLocation,
    timeZone,
    syncErrors
  } = props;

  const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);
  const [viewMode, setViewMode] = useState<ScriptViewMode>("Cards");

  const isInternal = useMemo(() => values && values.keyCode && values.keyCode.startsWith("ish."), [values && values.keyCode]);
  const isOriginallyInternal = useMemo(
    () => initialValues && initialValues.keyCode && initialValues.keyCode.startsWith("ish."),
    [initialValues && initialValues.keyCode],
  );
  const prevId = usePrevious(values && values.id);

  const [modalOpened, setModalOpened] = useState<boolean>(false);

  const onDialogClose = useCallback(() => setModalOpened(false), []);

  const onInternalSaveClick = useCallback(() => {
    dispatch(initialize("SaveAsNewAutomationForm", {}));
    setModalOpened(true);
  }, []);

  const onDialogSave = useCallback(
    ({ keyCode, name }) => {
      setDisableRouteConfirm(true);
      onCreate({
        ...values,
        id: null,
        keyCode,
        name,
      }, viewMode);
      onDialogClose();
    },
    [values, viewMode],
  );

  const addComponent = async (componentName: ScriptComponentType) => {
    dispatch(arrayInsert(form, "components", 0, await getInitComponentBody(componentName)));
  };

  const addImport = e => {
    e.stopPropagation();
    dispatch(arrayInsert(form, "imports", 0, ""));
  };

  const removeImports = e => {
    e.stopPropagation();
    openConfirm({
      onConfirm: () => dispatch(change(form, "imports", null)),
      confirmMessage: "Script component will be deleted permanently"
    });
  };

  const handleDelete = () => {
    setDisableRouteConfirm(true);
    onDelete(values.id);
  };

  useEffect(() => () => {
    dispatch(setScriptComponents([]));
  }, []);

  useEffect(() => {
    if (disableRouteConfirm && values && values.id !== prevId) {
      setDisableRouteConfirm(false);
    }
  }, [values && values.id, prevId, disableRouteConfirm]);

  useEffect(() => {
    if (!dirty && nextLocation) {
      history.push(nextLocation);
      setNextLocation('');
    }
  }, [nextLocation, dirty]);

  const isScheduleTrigger = useMemo(() => Boolean(
    values
    && values.trigger
    && values.trigger.type === "Schedule",
  ), [
    values && values.trigger,
    values && values.trigger && values.trigger.type,
  ]);

  const isSystemTrigger = useMemo(() => Boolean(
    values
    && values.trigger
    && (isScheduleTrigger || TriggerToRecordTypeMap[values.trigger.type]),
  ), [
    values && values.trigger,
    values && values.trigger && values.trigger.type,
    isScheduleTrigger,
  ]);

  const handleSave = useCallback(
    (valuesToSave: Script) => {
      setDisableRouteConfirm(true);

      valuesToSave.entity = valuesToSave?.trigger?.entityName;

      if (isNew) {
        onCreate(valuesToSave, viewMode);
        return;
      }

      if (isInternal) {
        onSave(valuesToSave.id, valuesToSave, "PATCH", viewMode);
      } else {
        onSave(valuesToSave.id, valuesToSave, "PUT", viewMode);
      }
    },
    [formsState, isNew, isSystemTrigger, values, viewMode],
  );

  const toogleViewMode = () => {
    if (viewMode === "Cards") {
      setViewMode("Code");
    } else {
      setViewMode("Cards");
    }
  };

  const defaultVariables = useMemo(
    () => [
      { name: "context", type: "Context" },
      ...(
        values && values.entity && !isSystemTrigger
          ? [{ name: "record", type: values.entity }]
          : []
      ),
      ...(
        isSystemTrigger && !isScheduleTrigger
          ? [{ name: "record", type: TriggerToRecordTypeMap[values.trigger.type] }]
          : []
      ),
    ],
    [values, values && values.trigger, values && values.trigger && values.trigger.type, isSystemTrigger, isScheduleTrigger],
  );

  const enableEntityNameField = entityNameTypes.indexOf(values && values.trigger && values.trigger.type) > -1;

  return (
    <>
      <SaveAsNewAutomationModal opened={modalOpened} onClose={onDialogClose} onSave={onDialogSave} hasNameField />

      <Form onSubmit={handleSubmit(handleSave)}>
        {(dirty || isNew) && <RouteChangeConfirm form={form} when={!disableRouteConfirm && (dirty || isNew)} />}

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl={getAuditsUrl}
          disabled={!dirty}
          invalid={invalid}
          title={isNew && (!values.name || values.name.trim().length === 0) ? "New" : values.name.trim()}
          disableInteraction={isInternal}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          noDrawer
          fields={(
            <Grid item xs={12}>
              <FormField
                name="name"
                label="Name"
                disabled={isInternal}
                required
              />
            </Grid>
          )}
          actions={!isNew && (
            <AppBarActions
              actions={[
                isInternal
                  ? {
                    action: onInternalSaveClick,
                    icon: <FileCopy />,
                    tooltip: "Save as new script"
                  }
                  : {
                    action: handleDelete,
                    icon: <DeleteForever />,
                    tooltip: "Delete script",
                    confirmText: "Script component will be deleted permanently",
                    confirmButtonText: "DELETE",
                  },
                viewMode === "Cards"
                  ? {
                    action: toogleViewMode,
                    icon: <CodeIcon />,
                    tooltip: "Switch to code view"
                  }
                  : {
                    action: toogleViewMode,
                    icon: <ViewAgendaIcon />,
                    tooltip: "Switch to cards view"
                  }
              ]}
            />
          )}
          customAddMenu={!isInternal && viewMode !== "Code" && (
            <ScriptAddMenu
              addComponent={addComponent}
              form={form}
              dispatch={dispatch}
              values={values}
              hasUpdateAccess={hasUpdateAccess}
            />
          )}
        >
          {values && (
            <Grid container className={classes.root}>
              <Grid item xs={9} className={classes.cardsBox}>
                <div>
                  <ScriptCard heading="Trigger" disableExpandedBottomMargin expanded>
                    <TriggerCardContent
                      classes={classes}
                      dispatch={dispatch}
                      TriggerTypeItems={TriggerTypeItems}
                      ScheduleTypeItems={ScheduleTypeItems}
                      enableEntityNameField={enableEntityNameField}
                      values={values}
                      isInternal={isInternal}
                      timeZone={timeZone}
                    />
                  </ScriptCard>
                </div>

                {viewMode === "Code" ? (
                  <ScriptCard
                    heading="Script"
                    className="mb-3 mt-3"
                    onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                    expanded
                    noPadding
                  >
                    <FormField
                      type="code"
                      name="content"
                      disabled={isInternal}
                      required
                    />
                  </ScriptCard>
                ) : (
                  <>
                    {values.imports && (
                      <div>
                        <ScriptCard
                          heading="Import"
                          className="mt-3"
                          onDelete={hasUpdateAccess && !isInternal ? removeImports : null}
                          onAddItem={hasUpdateAccess && !isInternal ? addImport : null}
                          disableExpandedBottomMargin
                          expanded
                          onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                        >
                          <ImportCardContent classes={classes} hasUpdateAccess={hasUpdateAccess} isInternal={isInternal} />
                        </ScriptCard>
                      </div>
                    )}

                    <FieldArray
                      name="components"
                      component={CardsRenderer}
                      hasUpdateAccess={hasUpdateAccess}
                      dispatch={dispatch}
                      showConfirm={openConfirm}
                      classes={classes}
                      rerenderOnEveryChange
                      isInternal={isInternal}
                      onInternalSaveClick={onInternalSaveClick}
                      emailTemplates={emailTemplates}
                    />
                  </>
                )}

                <FormField
                  type="text"
                  label="Key Code"
                  name="keyCode"
                  validate={isNew || !isInternal ? validateKeycode : undefined}
                  disabled={isOriginallyInternal}
                  required
                />
              </Grid>

              <Grid item xs={3}>
                <div>
                  <FormField
                    type="switch"
                    name="enabled"
                    label="Enabled"
                    color="primary"
                    fullWidth
                  />
                </div>
                <FormField
                  label="Output"
                  name="outputType"
                  type="select"
                  items={outputTypes}
                  disabled={isInternal}
                  placeholder="no output"
                />
                <div className="mt-3 pt-1">
                  <Bindings
                    defaultVariables={defaultVariables}
                    dispatch={dispatch}
                    form={form}
                    name="variables"
                    label="Variables"
                    itemsType="label"
                    disabled={isInternal}
                  />
                </div>
                <div className="mt-3">
                  <Bindings
                    dispatch={dispatch}
                    form={form}
                    itemsType="component"
                    name="options"
                    label="Options"
                    disabled={isInternal}
                    emailTemplates={emailTemplates}
                  />
                </div>

                <FormField
                  type="multilineText"
                  name="description"
                  label="Description"
                  disabled={isInternal}
                  className="overflow-hidden"
                  fullWidth
                />

                <Grid container columnSpacing={3} >
                  <Grid item xs className="d-flex">
                    <div className="flex-fill">
                      <Typography variant="caption" color="textSecondary">
                        Last run
                      </Typography>

                      {values.lastRun && values.lastRun.length ? (
                        values.lastRun.map((runDate, index) => (
                          <Typography variant="body1" key={runDate + index}>
                            {formatRelativeDate(new Date(runDate), new Date(), DD_MMM_YYYY_AT_HH_MM_AAAA_SPECIAL)}
                          </Typography>
                        ))
                      ) : (
                        <Typography variant="subtitle1" color="textSecondary">
                          Never
                        </Typography>
                      )}
                    </div>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          )}
        </AppBarContainer>
      </Form>
    </>
  );
});

const StyledScriptsForm = withStyles(styles)(ScriptsForm);

export default props => (props.values ? <StyledScriptsForm {...props} /> : null);
