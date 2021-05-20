/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EmailTemplate, MessageType } from "@api/model";
import { Grow } from "@material-ui/core";
import Grid from "@material-ui/core/Grid/Grid";
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import { FileCopy } from "@material-ui/icons";
import DeleteForever from "@material-ui/icons/DeleteForever";
import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import { Dispatch } from "redux";
import { Form, initialize, InjectedFormProps } from "redux-form";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import { mapSelectItems } from "../../../../../common/utils/common";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { usePrevious } from "../../../../../common/utils/hooks";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { NumberArgFunction, StringArgFunction } from "../../../../../model/common/CommonFunctions";
import AvailableFrom, { mapMessageAvailableFrom } from "../../../components/AvailableFrom";
import Bindings from "../../../components/Bindings";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import { MessageTemplateEntityItems, MessageTemplateEntityName } from "../../../constants";
import { validateKeycode } from "../../../utils";
import ScriptCard from "../../scripts/components/cards/CardBase";

const manualUrl = getManualLink("emailTemplates");
const getAuditsUrl = (id: number) => `audit?search=~"EmailTemplate" and entityId == ${id}`;

const messageTypes = Object.keys(MessageType).map(mapSelectItems).filter(t => t.value !== "Post");

interface Props extends InjectedFormProps {
  isNew: boolean;
  values: EmailTemplate;
  dispatch: Dispatch;
  onCreate: (template: EmailTemplate) => void;
  onUpdateInternal: (template: EmailTemplate) => void;
  onUpdate: (template: EmailTemplate) => void;
  onDelete: NumberArgFunction;
  validateTemplateCopyName: StringArgFunction;
  validateNewTemplateName: StringArgFunction;
  history: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
}

const validatePlainBody = (v, allValues) => ((allValues.type !== 'Email' || !allValues.body) ? validateSingleMandatoryField(v) : undefined);

const validateBody = (v, allValues) => (!allValues.plainBody ? validateSingleMandatoryField(v) : undefined);

const validateSubject = (v, allValues) => (allValues.entity ? validateSingleMandatoryField(v) : undefined);

const EmailTemplatesForm: React.FC<Props> = props => {
  const {
    dirty,
    form,
    handleSubmit,
    isNew,
    invalid,
    values,
    dispatch,
    onCreate,
    onUpdate,
    onUpdateInternal,
    onDelete,
    validateTemplateCopyName,
    validateNewTemplateName,
    history,
    nextLocation,
    setNextLocation,
  } = props;

  const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);

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
        name
      });
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
      { name: "record", type: values.entity },
      { name: "to", type: "Contact" }
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
      <SaveAsNewAutomationModal
        opened={modalOpened}
        onClose={onDialogClose}
        onSave={onDialogSave}
        validateNameField={validateTemplateCopyName}
        hasNameField
      />

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
            validate={validateNewTemplateName}
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
                  tooltip: "Delete message template",
                  confirmText: "Message template will be deleted permanently",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}

          {isInternal && (
          <Grow in={isInternal}>
            <Tooltip title="Save as new email template">
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
                  items={MessageTemplateEntityItems}
                  disabled={isInternal}
                />
              </Grid>
              <Grid item xs={6}>
                <FormField
                  type="select"
                  label="Message type"
                  name="type"
                  items={messageTypes}
                  disabled={isInternal}
                  required
                  select
                />
              </Grid>
            </Grid>

            {values.type === 'Email' && (
              <Grid container>
                <Grid item xs={6}>
                  <div className="heading">Subject</div>
                  <FormField
                    type="text"
                    name="subject"
                    validate={validateSubject}
                    disabled={!isNew && isInternal}
                  />
                </Grid>
              </Grid>
            )}

            <ScriptCard
              heading="Text Template"
              className="mb-3"
              onDetailsClick={isInternal ? onInternalSaveClick : undefined}
              expanded
              noPadding
            >
              <FormField
                type="code"
                name="plainBody"
                className="mt-3"
                validate={validatePlainBody}
                disabled={isInternal}
              />
            </ScriptCard>

            {values.type === 'Email' && (
              <ScriptCard
                heading="HTML template"
                className="mb-3"
                onDetailsClick={isInternal ? onInternalSaveClick : undefined}
                expanded
                noPadding
              >
                <FormField
                  type="code"
                  name="body"
                  className="mt-3"
                  validate={validateBody}
                  disabled={isInternal}
                />
              </ScriptCard>
            )}

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
              <AvailableFrom items={mapMessageAvailableFrom(values.entity as MessageTemplateEntityName)} />
            </div>
          </Grid>
        </Grid>
      </Form>
    </>
  );
};

export default props => (props.values ? <EmailTemplatesForm {...props} /> : null);
