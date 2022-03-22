/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import { FieldArray, Form, initialize, InjectedFormProps } from "redux-form";
import DeleteForever from "@mui/icons-material/DeleteForever";
import FileCopy from "@mui/icons-material/FileCopy";
import Grid from "@mui/material/Grid";
import { ExportTemplate, OutputType } from "@api/model";
import { Dispatch } from "redux";
import Grow from "@mui/material/Grow";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import FormField from "../../../../../common/components/form/formFields/FormField";
import ScriptCard from "../../scripts/components/cards/CardBase";
import Bindings, { BindingsRenderer } from "../../../components/Bindings";
import AvailableFrom, { mapAvailableFrom } from "../../../components/AvailableFrom";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import { usePrevious } from "../../../../../common/utils/hooks";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { validateKeycode } from "../../../utils";
import { mapSelectItems } from "../../../../../common/utils/common";
import { EntityItems, EntityName } from "../../../../../model/entities/common";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { CatalogItemType } from "../../../../../model/common/Catalog";
import InfoPill from "../../../../../common/components/layout/InfoPill";

const manualUrl = getManualLink("advancedSetup_Export");
const getAuditsUrl = (id: number) => `audit?search=~"ExportTemplate" and entityId == ${id}`;

const outputTypes = Object.keys(OutputType).map(mapSelectItems);

interface Props extends InjectedFormProps {
  isNew: boolean;
  values: ExportTemplate;
  dispatch: Dispatch;
  onCreate: (template: ExportTemplate) => void;
  onUpdateInternal: (template: ExportTemplate) => void;
  onUpdate: (template: ExportTemplate) => void;
  onDelete: NumberArgFunction;
  history: any,
  syncErrors: any,
  nextLocation: string,
  setNextLocation: (nextLocation: string) => void,
  emailTemplates?: CatalogItemType[]
}

const ExportTemplatesForm = React.memo<Props>(
  ({
    dirty, form, handleSubmit, isNew, invalid, values, syncErrors, emailTemplates,
     dispatch, onCreate, onUpdate, onUpdateInternal, onDelete, history, nextLocation, setNextLocation
  }) => {
    const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);

    const [modalOpened, setModalOpened] = useState<boolean>(false);

    const onDialogClose = useCallback(() => setModalOpened(false), []);

    const onInternalSaveClick = useCallback(() => {
      dispatch(initialize("SaveAsNewAutomationForm", {}));
      setModalOpened(true);
    }, []);

    const onDialogSave = useCallback(
      ({ keyCode }) => {
        setDisableRouteConfirm(true);
        onCreate({ ...values, id: null, keyCode });
        onDialogClose();
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
        { name: "records", type: values.entity },
        { name: "context", type: "Context" },
        { name: "preferences", type: "Preferences" },
        { name: "result", type: "output stream" },
        ...(!values.outputType || ["txt", "ics"].includes(values.outputType)
          ? []
          : [{ name: values.outputType, type: `${(values.outputType as any).capitalize()} builder` }])
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

    return (
      <>
        <SaveAsNewAutomationModal opened={modalOpened} onClose={onDialogClose} onSave={onDialogSave} />

        <Form onSubmit={handleSubmit(handleSave)}>
          {(dirty || isNew) && <RouteChangeConfirm form={form} when={(dirty || isNew) && !disableRouteConfirm} />}

          <AppBarContainer
            values={values}
            manualUrl={manualUrl}
            getAuditsUrl={getAuditsUrl}
            disabled={!dirty}
            invalid={invalid}
            title={(
              <div className="centeredFlex">
                {isNew && (!values.name || values.name.trim().length === 0) ? "New" : values.name.trim()}
                {[...values.automationTags?.split(",") || [],
                  ...isInternal ? [] : ["custom"]
                ].map(t => <InfoPill key={t} label={t} />)}
              </div>
            )}
            disableInteraction={isInternal}
            opened={isNew || Object.keys(syncErrors).includes("name")}
            fields={(
              <Grid item xs={12}>
                <FormField
                  name="name"
                  label="Name"
                  margin="none"
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
                        tooltip: "Delete export template",
                        confirmText: "Export template will be deleted permanently",
                        confirmButtonText: "DELETE"
                      }
                    ]}
                  />
                )}

                {isInternal && (
                  <Grow in={isInternal}>
                    <Tooltip title="Save as new export template">
                      <IconButton onClick={onInternalSaveClick} color="inherit">
                        <FileCopy color="primary" />
                      </IconButton>
                    </Tooltip>
                  </Grow>
                )}
              </>
            )}
          >
            <Grid container columnSpacing={3}>
              <Grid item xs={9} className="pr-3">
                <Grid container columnSpacing={3} rowSpacing={2}>
                  <Grid item xs={6}>
                    <div className="heading">Type</div>
                    <FormField
                      type="select"
                      name="entity"
                      items={EntityItems}
                      disabled={isInternal}
                      required
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <FormField
                      type="select"
                      label="Output"
                      name="outputType"
                      items={outputTypes}
                      disabled={isInternal}
                      required
                    />
                  </Grid>
                  <FieldArray
                    name="options"
                    itemsType="component"
                    component={BindingsRenderer}
                    emailTemplates={emailTemplates}
                    rerenderOnEveryChange
                  />
                </Grid>

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
                  <AvailableFrom items={mapAvailableFrom(values.entity as EntityName)} />
                </div>
              </Grid>
            </Grid>
          </AppBarContainer>
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <ExportTemplatesForm {...props} /> : null);
