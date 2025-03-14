/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ExportTemplate, OutputType } from '@api/model';
import DeleteForever from '@mui/icons-material/DeleteForever';
import FileCopy from '@mui/icons-material/FileCopy';
import Grid from '@mui/material/Grid';
import Grow from '@mui/material/Grow';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { InfoPill, mapSelectItems, NumberArgFunction, usePrevious } from 'ish-ui';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Dispatch } from 'redux';
import { FieldArray, Form, initialize, InjectedFormProps } from 'redux-form';
import { IAction } from '../../../../../common/actions/IshAction';
import AppBarActions from '../../../../../common/components/appBar/AppBarActions';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { CatalogItemType } from '../../../../../model/common/Catalog';
import { EntityItems, EntityName } from '../../../../../model/entities/common';
import AvailableFrom, { mapAvailableFrom } from '../../../components/AvailableFrom';
import Bindings, { BindingsRenderer } from '../../../components/Bindings';
import getConfigActions from '../../../components/ImportExportConfig';
import SaveAsNewAutomationModal from '../../../components/SaveAsNewAutomationModal';
import { validateKeycode, validateNameForQuotes } from '../../../utils';
import ScriptCard from '../../scripts/components/cards/CardBase';

const manualUrl = getManualLink("export-templates");
const getAuditsUrl = (id: number) => `audit?search=~"ExportTemplate" and entityId == ${id}`;

const outputTypes = Object.keys(OutputType).map(mapSelectItems);

interface Props extends InjectedFormProps {
  isNew: boolean;
  values: ExportTemplate;
  dispatch: Dispatch<IAction>
  onCreate: (template: ExportTemplate) => void;
  onUpdateInternal: (template: ExportTemplate) => void;
  onUpdate: (template: ExportTemplate) => void;
  onDelete: NumberArgFunction;
  history: any,
  syncErrors: any,
  nextLocation: string,
  emailTemplates?: CatalogItemType[],
  exportTemplates?: CatalogItemType[]
}

const ExportTemplatesForm = React.memo<Props>(
  ({
    dirty, form, handleSubmit, isNew, invalid, values, syncErrors, emailTemplates, exportTemplates,
     dispatch, onCreate, onUpdate, onUpdateInternal, onDelete, history, nextLocation
  }) => {
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
        onCreate({ ...values, id: null, keyCode, name });
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
      }
    }, [nextLocation, dirty]);

    const importExportActions = useMemo(() => getConfigActions("ExportTemplate", values.name, values.id), [values.id]);

    const validateTemplateCopyName = useCallback(name => {
      if (exportTemplates.find(e => e.title.trim() === name.trim())) {
        return "Template name should be unique";
      }
      return validateNameForQuotes(name);
    }, [exportTemplates, values.id]);

    const validateTemplateName = useCallback(name => {
      if (exportTemplates.find(e => e.id !== values.id && e.title.trim() === name.trim())) {
        return "Template name should be unique";
      }
      return validateNameForQuotes(name);
    }, [exportTemplates, values.id]);

    return (
      <>
        <SaveAsNewAutomationModal
          opened={modalOpened}
          onClose={onDialogClose}
          onSave={onDialogSave}
          validateNameField={validateTemplateCopyName}
        />

        <Form onSubmit={handleSubmit(handleSave)}>
          {!disableRouteConfirm && <RouteChangeConfirm form={form} when={dirty || isNew}/>}

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
                  type="text"
                  name="name"
                  label={$t('name')}
                  validate={validateTemplateName}
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
                      ...importExportActions,
                      {
                        action: handleDelete,
                        icon: <DeleteForever/>,
                        tooltip: "Delete export template",
                        confirmText: "Export template will be deleted permanently",
                        confirmButtonText: "DELETE"
                      }
                    ]}
                  />
                )}

                {isInternal && (
                  <Grow in={isInternal}>
                    <Tooltip title={$t('save_as_new_export_template')}>
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
              <Grid item xs={12} sm={9}>
                <FormField
                  type="multilineText"
                  name="shortDescription"
                  disabled={isInternal}
                  className="overflow-hidden mb-1"
                  placeholder={$t('short_description')}
                />
                <Typography variant="caption" fontSize="13px">
                  <FormField
                    type="multilineText"
                    name="description"
                    disabled={isInternal}
                    className="overflow-hidden mb-1"
                    placeholder={$t('description')}
                    fieldClasses={{
                      text: "fw300 fsInherit"
                    }}
                  />
                </Typography>
              </Grid>
              <Grid item xs={9} className="pr-3">
                <Grid container columnSpacing={3} rowSpacing={2}>
                  <Grid item xs={6}>
                    <div className="heading">{$t('type')}</div>
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
                      label={$t('output')}
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
                  label={$t('key_code2')}
                  name="keyCode"
                  validate={isNew || !isInternal ? validateKeycode : undefined}
                  disabled={!isNew}
                  className="mb-2"
                  required
                />
              </Grid>
              <Grid item xs={3}>
                <div>
                  <FormField
                    label={$t('enabled')}
                    type="switch"
                    name="status"
                    color="primary"
                    format={v => v === "Enabled"}
                    parse={v => v ? "Enabled" : "Installed but Disabled"}
                    debounced={false}
                  />
                </div>
                <div className="mt-3 pt-1">
                  <Bindings
                    defaultVariables={defaultVariables}
                    dispatch={dispatch}
                    form={form}
                    name="variables"
                    label={$t('variables')}
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
                    label={$t('options')}
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