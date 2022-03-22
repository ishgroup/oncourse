/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import PlayArrow from "@mui/icons-material/PlayArrow";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { FieldArray, Form, initialize, InjectedFormProps } from "redux-form";
import DeleteForever from "@mui/icons-material/DeleteForever";
import FileCopy from "@mui/icons-material/FileCopy";
import Grid from "@mui/material/Grid";
import Grow from "@mui/material/Grow";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import { ImportModel } from "@api/model";
import Typography from "@mui/material/Typography";
import FormField from "../../../../../common/components/form/formFields/FormField";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import ScriptCard from "../../scripts/components/cards/CardBase";
import Bindings, { BindingsRenderer } from "../../../components/Bindings";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import { usePrevious } from "../../../../../common/utils/hooks";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { validateKeycode } from "../../../utils";
import { formatRelativeDate } from "../../../../../common/utils/dates/formatRelative";
import { DD_MMM_YYYY_AT_HH_MM_AAAA_SPECIAL } from "../../../../../common/utils/dates/format";
import ExecuteImportModal from "../components/ExecuteImportModal";
import { State } from "../../../../../reducers/state";
import { setNextLocation } from "../../../../../common/actions";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { CatalogItemType } from "../../../../../model/common/Catalog";

const manualUrl = getManualLink("advancedSetup_Import");
const getAuditsUrl = (id: number) => `audit?search=~"ImportTemplate" and entityId == ${id}`;

interface Props extends InjectedFormProps {
  isNew: boolean;
  values: any;
  history: any;
  syncErrors: any;
  dispatch: Dispatch;
  onCreate: (template: ImportModel) => void;
  onUpdateInternal: (template: ImportModel) => void;
  onUpdate: (template: ImportModel) => void;
  onDelete: NumberArgFunction;
  nextLocation?: string,
  setNextLocation?: (nextLocation: string) => void,
  emailTemplates?: CatalogItemType[]
}

const ImportTemplatesForm = React.memo<Props>(
  ({
    dirty, form, handleSubmit, isNew, invalid, values, dispatch, syncErrors, emailTemplates,
     onCreate, onUpdate, onUpdateInternal, onDelete, nextLocation, history, setNextLocation
  }) => {
    const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);
    const [modalOpened, setModalOpened] = useState<boolean>(false);
    const [execMenuOpened, setExecMenuOpened] = useState(false);
    const [importIdSelected, setImportIdSelected] = useState(null);

    const onDialodClose = useCallback(() => setModalOpened(false), []);

    const onInternalSaveClick = useCallback(() => {
      dispatch(initialize("SaveAsNewAutomationForm", {}));
      setModalOpened(true);
    }, []);

    const onDialogSave = useCallback(
      ({ keyCode }) => {
        setDisableRouteConfirm(true);
        onCreate({ ...values, id: null, keyCode });
        onDialodClose();
      },
      [values]
    );

    const isInternal = useMemo(() => values.keyCode && values.keyCode.startsWith("ish."), [values.keyCode]);

    const prevId = usePrevious(values.id);

    const handleDelete = useCallback(() => {
      setDisableRouteConfirm(true);
      onDelete(values.id);
    }, [values.id]);

    const handleSave = useCallback(
      val => {
        setDisableRouteConfirm(true);
        if (isNew) {
          onCreate(val);
          return;
        }
        if (isInternal) {
          onUpdateInternal(val);
          return;
        }
        onUpdate(val);
      },
      [isNew, isInternal]
    );

    const defaultVariables = useMemo(
      () => [
        { name: "context", type: "Context" }
      ],
      [values]
    );

    useEffect(() => {
      if (disableRouteConfirm && values.id !== prevId) {
        setDisableRouteConfirm(false);
      }
    }, [values.id, prevId, disableRouteConfirm]);

    useEffect(() => {
      if (!dirty && nextLocation) {
        history.push(nextLocation);
        setNextLocation('');
      }
    }, [nextLocation, dirty]);

    const handleRun = () => {
      setImportIdSelected(values.id);
      setExecMenuOpened(true);
    };

    return (
      <>
        <SaveAsNewAutomationModal opened={modalOpened} onClose={onDialodClose} onSave={onDialogSave} />
        <ExecuteImportModal
          opened={execMenuOpened}
          onClose={() => {
            setExecMenuOpened(false);
            setImportIdSelected(null);
          }}
          importId={importIdSelected}
        />

        <Form onSubmit={handleSubmit(handleSave)}>
          {(dirty || isNew) && <RouteChangeConfirm form={form} when={(dirty || isNew) && !disableRouteConfirm} />}

          <AppBarContainer
            values={values}
            manualUrl={manualUrl}
            getAuditsUrl={getAuditsUrl}
            disabled={!dirty}
            invalid={invalid}
            title={isNew && (!values.name || values.name.trim().length === 0) ? "New" : values.name.trim()}
            disableInteraction={isInternal}
            opened={isNew || Object.keys(syncErrors).includes("name")}
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
            actions={(
              <>
                {!isNew && !isInternal && (
                  <AppBarActions
                    actions={[
                      {
                        action: handleDelete,
                        icon: <DeleteForever />,
                        confirm: true,
                        tooltip: "Delete import template",
                        confirmText: "Import template will be deleted permanently",
                        confirmButtonText: "DELETE"
                      },
                      {
                        action: handleRun,
                        icon: <PlayArrow />,
                        tooltip: dirty ? "Save changes before run" : "Run import",
                        tooltipError: dirty,
                        disabled: dirty
                      }
                    ]}
                  />
                )}

                {isInternal && (
                  <>
                    <AppBarActions
                      actions={[
                        {
                          action: handleRun,
                          icon: <PlayArrow />,
                          tooltip: dirty ? "Save changes before run" : "Run import",
                          tooltipError: dirty,
                          disabled: dirty
                        }
                      ]}
                    />
                    <Grow in={isInternal}>
                      <Tooltip title="Save as new import template">
                        <IconButton onClick={onInternalSaveClick} color="inherit">
                          <FileCopy color="primary" />
                        </IconButton>
                      </Tooltip>
                    </Grow>
                  </>
                )}
              </>
            )}
          >
            <Grid container columnSpacing={3} rowSpacing={2}>
              <FieldArray
                name="options"
                itemsType="component"
                component={BindingsRenderer}
                emailTemplates={emailTemplates}
                rerenderOnEveryChange
              />
              <Grid item xs={9} className="pr-3">
                <ScriptCard
                  heading="Script"
                  className="mb-3"
                  onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                  expanded
                  noPadding
                >
                  <FormField
                    type="code"
                    name="body"
                    className="mt-3"
                    disabled={isInternal}
                    required
                  />
                </ScriptCard>

                <FormField
                  type="text"
                  label="Key Code"
                  name="keyCode"
                  validate={isNew || !isInternal ? validateKeycode : undefined}
                  disabled={!isNew}
                  className="mb-2"
                  required
                />

                <FormField
                  type="text"
                  label="Description"
                  name="description"
                  disabled={isInternal}
                  fullWidth
                  multiline
                />
              </Grid>
              <Grid item xs={3}>
                <div>
                  <FormField
                    label="Enabled"
                    type="switch"
                    name="status"
                    color="primary"
                    format={v => v === "Enabled"}
                    parse={v => v ? "Enabled" : "Installed but Disabled"}
                  />
                </div>
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
                  />
                </div>

                <div className="mt-3">
                  <FormField
                    type="text"
                    name="description"
                    label="Description"
                    className="overflow-hidden"
                    multiline
                    fullWidth
                  />

                  <Grid container columnSpacing={3}>
                    <Grid item xs className="d-flex">
                      <div className="flex-fill">
                        <Typography variant="caption" color="textSecondary">
                          Last run
                        </Typography>

                        {values.lastRun && values.lastRun.length ? (
                          values.lastRun.map((runDate, index) => (
                            <Typography variant="body1" key={index}>
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
                </div>
              </Grid>
            </Grid>
          </AppBarContainer>
        </Form>
      </>
    );
  }
);

const mapStateToProps = (state: State) => ({
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)((props:Props) => (props.values
  ? <ImportTemplatesForm {...props} /> : null));
