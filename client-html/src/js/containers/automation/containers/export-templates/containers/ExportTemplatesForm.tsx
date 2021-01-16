/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import { Form, initialize, InjectedFormProps } from "redux-form";
import DeleteForever from "@material-ui/icons/DeleteForever";
import FileCopy from "@material-ui/icons/FileCopy";
import Grid from "@material-ui/core/Grid/Grid";
import { ExportTemplate, OutputType } from "@api/model";
import { Dispatch } from "redux";
import Grow from "@material-ui/core/Grow/Grow";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import IconButton from "@material-ui/core/IconButton/IconButton";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import ScriptCard from "../../scripts/components/cards/CardBase";
import Bindings from "../../../components/Bindings";
import AvailableFrom, { mapAvailableFrom } from "../../../components/AvailableFrom";
import { EntityItems, EntityName } from "../../../constants";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import { usePrevious } from "../../../../../common/utils/hooks";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { validateKeycode } from "../../../utils";
import { mapSelectItems } from "../../../../../common/utils/common";

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
  nextLocation: string,
  setNextLocation: (nextLocation: string) => void,
}

const ExportTemplatesForm = React.memo<Props>(
  ({
    dirty, form, handleSubmit, isNew, invalid, values,
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

          <CustomAppBar>
            <FormField
              type="headerText"
              name="name"
              placeholder="Name"
              margin="none"
              className="pl-1"
              listSpacing={false}
              disabled={isInternal}
              required
            />

            <div className="flex-fill" />

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
                    <FileCopy color="inherit" />
                  </IconButton>
                </Tooltip>
              </Grow>
            )}

            <AppBarHelpMenu
              created={values.createdOn ? new Date(values.createdOn) : null}
              modified={values.modifiedOn ? new Date(values.modifiedOn) : null}
              manualUrl={manualUrl}
              auditsUrl={getAuditsUrl(values.id)}
            />

            <FormSubmitButton
              disabled={!dirty}
              invalid={invalid}
            />
          </CustomAppBar>

          <Grid container className="p-3 appBarContainer">
            <Grid item xs={9} className="pr-3">
              <Grid container>
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
                  type="switch"
                  name="enabled"
                  label="Enabled"
                  color="primary"
                  fullWidth
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
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <ExportTemplatesForm {...props} /> : null);
