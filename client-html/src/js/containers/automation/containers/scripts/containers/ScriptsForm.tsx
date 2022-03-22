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
import clsx from "clsx";
import { OutputType, Script, TriggerType } from "@api/model";
import Typography from "@mui/material/Typography";
import createStyles from "@mui/styles/createStyles";
import DeleteForever from "@mui/icons-material/DeleteForever";
import ViewAgendaIcon from '@mui/icons-material/ViewAgenda';
import CodeIcon from '@mui/icons-material/Code';
import Divider from "@mui/material/Divider";
import Accordion from "@mui/material/Accordion";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import CheckIcon from "@mui/icons-material/Check";
import ClearIcon from "@mui/icons-material/Clear";
import UploadIcon from "@mui/icons-material/Upload";
import IconButton from "@mui/material/IconButton";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { mapSelectItems } from "../../../../../common/utils/common";
import { usePrevious } from "../../../../../common/utils/hooks";
import Bindings, { BindingsRenderer } from "../../../components/Bindings";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import { validateKeycode } from "../../../utils";
import ScriptCard from "../components/cards/CardBase";
import { formatRelativeDate } from "../../../../../common/utils/dates/formatRelative";
import ImportCardContent from "../components/cards/ImportCardContent";
import TriggerCardContent from "../components/cards/TriggerCardContent";
import { setScriptComponents } from "../actions";
import {
 ScriptComponent, ScriptComponentType, ScriptExtended, ScriptViewMode 
} from "../../../../../model/scripts";
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
import AddScriptAction from "../components/AddScriptAction";
import BoltIcon from "../../../../../../images/icon-bolt.svg";
import ScriptIcon from "../../../../../../images/icon-script.svg";
import InfoPill from "../../../../../common/components/layout/InfoPill";
import { AppTheme } from "../../../../../model/common/Theme";
import { CatalogItemType } from "../../../../../model/common/Catalog";

const manualUrl = getManualLink("scripts");
const getAuditsUrl = (id: number) => `audit?search=~"Script" and entityId == ${id}`;

const styles = (theme: AppTheme) =>
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
    cardsItem: {
      position: "relative",
      "&::before": {
        content: `" "`,
        position: "absolute",
        height: `calc(100% - ${theme.spacing(3)})`,
        left: -50,
        width: 2,
        backgroundColor: theme.palette.divider
      }
    },
    cardCodeView: {
      "&::after": {
        bottom: 0,
      }
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
    queryIcon: {
      backgroundColor: "#fef4e8",
      color: "#f7941d"
    },
    technicalInfoRoot: {
      boxShadow: "none",
      background: "none",
      "&::before": {
        content: "none",
      },
    },
    technicalInfoExpanded: {
      margin: "0px",
    },
    cardReaderCustomHeading: {
      maxWidth: "calc(100% - 8px)",
    },
    descriptionText: {
      fontSize: theme.spacing(1.625),
    },
    cardLeftIcon: {
      position: "absolute",
      left: -75,
      width: 50,
      height: 50,
      backgroundColor: theme.palette.mode === "light" ? "#fef4e8" : theme.palette.background.paper,
      color: theme.palette.primary.main,
      zIndex: 1,
      top: 0,
      "&:hover": {
        cursor: "inherit"
      }
    },
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
  values: ScriptExtended;
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
  emailTemplates?: CatalogItemType[];
  timeZone?: string;
  syncErrors?: any;
}

const getInitComponentBody = (componentName: ScriptComponentType): ScriptComponent | Promise<ScriptComponent> => {
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
  const [expandInfo, setExpandInfo] = useState<boolean>(isNew);
  const [triggerExpand, setTriggerExpand] = useState<boolean>(false);
  const [isCardDragging, setCardDragging] = useState<boolean>(false);
  const [expanded, setExpand] = useState([]);
  
  const onExpand = id => setExpand(prev => {
    const index = prev.indexOf(id);
    const updated = [...prev];
    index === -1 ? updated.push(id) : updated.splice(index, 1);
    return updated;
  });

  const isInternal = useMemo(() => values && values.keyCode && values.keyCode.startsWith("ish."), [values && values.keyCode]);
  const isOriginallyInternal = useMemo(
    () => initialValues && initialValues.keyCode && initialValues.keyCode.startsWith("ish."),
    [initialValues && initialValues.keyCode],
  );
  const prevId = usePrevious(values && values.id);

  const [modalOpened, setModalOpened] = useState<boolean>(false);

  const onDialogClose = () => setModalOpened(false);

  const onInternalSaveClick = () => {
    dispatch(initialize("SaveAsNewAutomationForm", {}));
    setModalOpened(true);
  };

  const onDialogSave = ({ keyCode, name }) => {
      setDisableRouteConfirm(true);
      onCreate({
        ...values,
        id: null,
        keyCode,
        name,
      }, viewMode);
      onDialogClose();
    };

  const addComponent = async (componentName: ScriptComponentType, index) => {
    const component = await getInitComponentBody(componentName);
    dispatch(arrayInsert(form, "components", index, component));
    onExpand(component.id);
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

  const isTriggerExpanded = useMemo(() => triggerExpand
      || !values?.trigger?.type
      || (enableEntityNameField && values?.trigger?.type !== "On demand" && !values?.trigger?.entityName)
      || (values?.trigger?.type === "Schedule" && !values?.trigger?.cron?.scheduleType)
      || (values?.trigger?.cron?.scheduleType === "Custom" && !values?.trigger?.cron?.custom), [
    triggerExpand,
    values,
    values && values.trigger,
    values && values.trigger && values.trigger.type,
    values && values.trigger && values.trigger.entityName,
    values?.trigger?.cron?.scheduleType,
    values?.trigger?.cron?.custom
  ]);

  const customTriggerHeading = useMemo(() => {
    let detail;

    if (values?.trigger?.type === "On demand") {
      detail = values?.trigger?.entityName;
    } else if (values?.trigger?.type === "Schedule") {
      detail = values?.trigger?.cron?.scheduleType;
      if (values?.trigger?.cron?.scheduleType === "Custom" && values?.trigger?.cron?.custom) {
        detail += ` (${values?.trigger?.cron?.custom})`;
      }
    } else if (enableEntityNameField && values?.trigger?.entityName) {
      detail = values?.trigger?.entityName;
      if (values?.trigger?.type === "On edit" && values?.trigger?.entityAttribute) {
        detail += ` (${values?.trigger?.entityAttribute})`;
      }
    } else {
      detail = values?.trigger?.type;
    }

    return (
      <div className="w-100 centeredFlex text-nowrap text-truncate">
        <Typography className="heading mr-5" component="div">
          Trigger
        </Typography>
        {detail && !triggerExpand && !isTriggerExpanded && (
          <div className="text-nowrap text-truncate pr-1">
            <Typography variant="caption" color="textSecondary">
              {detail}
            </Typography>
          </div>
        )}
      </div>
    );
  }, [
    triggerExpand,
    isTriggerExpanded,
    values,
    values && values.trigger,
    values && values.trigger && values.trigger.type,
    values && values.trigger && values.trigger.entityName,
    values && values.trigger && values.trigger.entityAttribute,
  ]);

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
          title={(
            <div className="centeredFlex">
              {values.name}
              {[...values.automationTags?.split(",") || [],
                ...values.keyCode.startsWith("ish.") ? [] : ["custom"]
              ].map(t => <InfoPill key={t} label={t} />)}
            </div>
          )}
          disableInteraction={isInternal}
          opened={!values.name || Object.keys(syncErrors).includes("name")}
          noDrawer
          fields={(
            <Grid item xs={12}>
              <FormField
                name="name"
                label="Name"
                disabled={isInternal}
                required
                placeholder={` `}
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
        >
          {values && (
            <>
              <Grid container className="mb-4" rowSpacing={2} columnSpacing={3}>
                <Grid item xs={12} sm={9}>
                  <FormField
                    type="multilineText"
                    name="shortDescription"
                    disabled={isInternal}
                    className="overflow-hidden mb-1"
                    hideLabel
                    placeholder="Short description"
                  />
                  <Typography variant="caption">
                    <FormField
                      type="multilineText"
                      name="description"
                      disabled={isInternal}
                      className="overflow-hidden mb-1"
                      hideLabel
                      placeholder="Description"
                      fieldClasses={{
                        text: clsx("fw300", classes.descriptionText)
                      }}
                    />
                  </Typography>
                </Grid>
                <FieldArray
                  name="options"
                  itemsType="component"
                  component={BindingsRenderer}
                  emailTemplates={emailTemplates}
                  rerenderOnEveryChange
                />
              </Grid>
              <Divider className="mb-5" />
              <Grid container className={classes.root}>
                <Grid item xs={9} className={classes.cardsBox}>
                  <div
                    className={clsx(classes.cardsItem,
                      { [classes.cardCodeView]: (viewMode === "Code" || isInternal) })}
                  >
                    <div className={clsx("relative", (viewMode === "Code" || isInternal) ? "mb-5" : "")}>
                      <IconButton size="large" className={classes.cardLeftIcon} disableRipple>
                        <img src={BoltIcon} alt="icon-bolt" />
                      </IconButton>
                      <ScriptCard
                        heading={customTriggerHeading}
                        disableExpandedBottomMargin
                        customButtons={(
                          <FormField
                            type="switch"
                            name="status"
                            color="primary"
                            format={v => v === "Enabled"}
                            parse={v => (v ? "Enabled" : "Installed but Disabled")}
                            onClick={e => e.stopPropagation()}
                          />
                        )}
                        onExpand={() => setTriggerExpand(!triggerExpand)}
                        expanded={isTriggerExpanded}
                        customHeading
                      >
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
                      <div className={clsx("mb-5 relative")}>
                        <IconButton size="large" className={classes.cardLeftIcon} disableRipple>
                          <img src={ScriptIcon} alt="icon-script" />
                        </IconButton>
                        <ScriptCard
                          heading="Script"
                          onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                          noPadding
                        >
                          <FormField
                            type="code"
                            name="content"
                            disabled={isInternal}
                            required
                          />
                        </ScriptCard>
                      </div>
                    ) : (
                      <>
                        {values.imports && (
                          <div className={clsx("mt-5 relative", { "mb-5": isInternal })}>
                            <IconButton size="large" className={classes.cardLeftIcon} disableRipple>
                              <UploadIcon />
                            </IconButton>
                            <ScriptCard
                              heading="Import"
                              onDelete={hasUpdateAccess && !isInternal ? removeImports : null}
                              onAddItem={hasUpdateAccess && !isInternal ? addImport : null}
                              expanded={expanded.includes("Import")}
                              onExpand={() => onExpand("Import")}
                              onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                              disableExpandedBottomMargin
                            >
                              <ImportCardContent classes={classes} hasUpdateAccess={hasUpdateAccess} isInternal={isInternal} />
                            </ScriptCard>
                          </div>
                        )}

                        {!isInternal && (
                          <AddScriptAction
                            index={0}
                            addComponent={addComponent}
                            form={form}
                            dispatch={dispatch}
                            values={values}
                            hasUpdateAccess={hasUpdateAccess}
                            active={!values.components}
                            disabled={isCardDragging}
                          />
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
                          addComponent={addComponent}
                          values={values}
                          setDragging={setCardDragging}
                          isDragging={isCardDragging}
                          syncErrors={syncErrors}
                          onExpand={onExpand}
                          expanded={expanded}
                        />
                      </>
                    )}
                  </div>
                </Grid>

                <Grid item xs={3}>
                  <Grid container columnSpacing={3} className="mb-5">
                    <Grid item xs className="d-flex">
                      <div className="flex-fill">
                        <Typography variant="caption" color="textSecondary">
                          Last run
                        </Typography>

                        {values.lastRun && values.lastRun.length ? (
                          values.lastRun.map((lastRun, index) => (
                            <div key={index} className="centeredFlex mb-0-5">
                              {lastRun.status === "Script executed"
                                ? <CheckIcon className="mr-0-5" color="success" />
                                : <ClearIcon className="mr-0-5" color="error" />}
                              <Typography variant="body2">
                                {formatRelativeDate(new Date(lastRun.date), new Date(), DD_MMM_YYYY_AT_HH_MM_AAAA_SPECIAL)}
                              </Typography>
                            </div>
                          ))
                        ) : (
                          <Typography variant="subtitle1" color="textSecondary">
                            Never
                          </Typography>
                        )}
                      </div>
                    </Grid>
                  </Grid>
                  <Accordion
                    defaultExpanded={expandInfo}
                    onChange={() => setExpandInfo(!expandInfo)}
                    classes={{ root: classes.technicalInfoRoot, expanded: classes.technicalInfoExpanded }}
                  >
                    <AccordionSummary classes={{ root: "p-0" }} expandIcon={<ExpandMoreIcon />}>
                      <Typography className="heading" component="div">
                        Technical info
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails classes={{ root: "p-0" }}>
                      <div className="mb-2">
                        <Bindings
                          defaultVariables={defaultVariables}
                          dispatch={dispatch}
                          form={form}
                          itemsType="component"
                          name="options"
                          label="Configuration variables"
                          disabled={isInternal}
                        />
                      </div>
                      {values?.trigger?.type === "On demand" && (
                        <div className="mt-3 mb-4">
                          <Bindings
                            dispatch={dispatch}
                            form={form}
                            name="variables"
                            label="Runtime variables"
                            itemsType="label"
                            disabled={isInternal}
                          />
                        </div>
                      )}
                      <FormField
                        label="Output"
                        name="outputType"
                        type="select"
                        items={outputTypes}
                        disabled={isInternal}
                        placeholder="no output"
                        className="mb-2"
                      />
                      <FormField
                        type="text"
                        label="Code"
                        name="keyCode"
                        validate={isNew || !isInternal ? validateKeycode : undefined}
                        disabled={isOriginallyInternal}
                        required
                      />
                    </AccordionDetails>
                  </Accordion>
                </Grid>
              </Grid>
            </>
          )}
        </AppBarContainer>
      </Form>
    </>
  );
});

const StyledScriptsForm = withStyles(styles)(ScriptsForm);

export default props => (props.values ? <StyledScriptsForm {...props} /> : null);
