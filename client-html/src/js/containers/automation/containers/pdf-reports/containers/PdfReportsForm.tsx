/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Report } from '@api/model';
import DeleteForever from '@mui/icons-material/DeleteForever';
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import FileCopy from '@mui/icons-material/FileCopy';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Grow from '@mui/material/Grow';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { FilePreview, InfoPill, NumberArgFunction, ShowConfirmCaller, usePrevious } from 'ish-ui';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Dispatch } from 'redux';
import { change, FieldArray, Form, initialize, InjectedFormProps } from 'redux-form';
import { IAction } from '../../../../../common/actions/IshAction';
import AppBarActions from '../../../../../common/components/appBar/AppBarActions';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../../common/components/form/formFields/Uneditable';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { createAndDownloadFile } from '../../../../../common/utils/common';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { CatalogItemType } from '../../../../../model/common/Catalog';
import { CommonListItem } from '../../../../../model/common/sidebar';
import { EntityItems } from '../../../../../model/entities/common';
import Bindings, { BindingsRenderer } from '../../../components/Bindings';
import getConfigActions from '../../../components/ImportExportConfig';
import SaveAsNewAutomationModal from '../../../components/SaveAsNewAutomationModal';
import { validateKeycode, validateNameForQuotes } from '../../../utils';
import { reportFullScreenPreview } from '../actions';

const manualUrl = getManualLink("using-reports");
const getAuditsUrl = (id: number) => `audit?search=~"Report" and entityId == ${id}`;

interface Props extends InjectedFormProps<Report> {
  isNew: boolean;
  values: Report;
  dispatch: Dispatch<IAction>
  onCreate: (report: Report) => void;
  onUpdateInternal: (report: Report) => void;
  onUpdate: (report: Report) => void;
  onDelete: NumberArgFunction;
  pdfBackgrounds: CommonListItem[];
  openConfirm: ShowConfirmCaller;
  history: any;
  syncErrors: any;
  nextLocation: string;
  emailTemplates?: CatalogItemType[];
  pdfReports?: CatalogItemType[];
}

const reader = new FileReader();
const parser = new DOMParser();

const fillAttributes = (
  names: string[],
  formFields: string[],
  document: Document,
  dispatch: Dispatch<IAction>,
  form: string
) => {
  names.forEach((n, index) => {
    const tag = document.querySelector(`[name='${n}' i]`);

    if (tag) {
      dispatch(change(form, formFields[index], tag.getAttribute("value")));
    }
  });
};

const PdfReportsForm = React.memo<Props>(
  ({
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
    pdfBackgrounds,
    openConfirm,
    initialValues,
    history,
    nextLocation,
    emailTemplates,
     pdfReports,
    syncErrors
  }) => {
    const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);
    const [modalOpened, setModalOpened] = useState<boolean>(false);
    const [chosenFileName, setChosenFileName] = useState(null);

    const fileRef = useRef<any>(undefined);

    const isInternal = useMemo(() => values.keyCode && values.keyCode.startsWith("ish."), [values.keyCode]);

    const prevId = usePrevious(values.id);

    const onDialodClose = useCallback(() => setModalOpened(false), []);

    const discardFileInput = useCallback(() => {
      fileRef.current.value = null;
      setChosenFileName(null);
    }, []);

    const onDialodSave = useCallback(
      ({ keyCode }) => {
        setDisableRouteConfirm(true);
        onCreate({ ...values, id: null, keyCode });
        discardFileInput();
        onDialodClose();
      },
      [values]
    );

    const onInternalSaveClick = useCallback(() => {
      dispatch(initialize("SaveAsNewAutomationForm", {}));
      setModalOpened(true);
    }, []);

    const handleDelete = useCallback(() => {
      setDisableRouteConfirm(true);
      onDelete(values.id);
    }, [values.id]);

    const handleSave = useCallback(
      val => {
        setDisableRouteConfirm(true);
        discardFileInput();
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

    const handleEdit = useCallback(() => createAndDownloadFile(values.body, "jrxml", values.name), [
      values.body,
      values.name
    ]);

    const handleUpload = useCallback(() => {
      if (!fileRef.current || fileRef.current.files[0] === undefined) return;
      setChosenFileName(fileRef.current.files[0].name);

      reader.onload = () => {
        dispatch(change(form, "body", reader.result));

        if (isNew) {
          const xml = parser.parseFromString(reader.result as string, "text/xml");

          fillAttributes(
            ["name", "entity", "keyCode", "ish.oncourse.description", "sortOn"],
            ["name", "entity", "keyCode", "description", "sortOn"],
            xml,
            dispatch,
            form
          );
        }
      };

      reader.readAsText(fileRef.current.files[0]);
    }, [form, isNew]);

    const handleUploadClick = useCallback(() => fileRef.current.click(), []);

    const handleClearPreview = useCallback(() => {
      openConfirm({
        onConfirm: () => dispatch(change(form, "preview", null)),
        confirmMessage: "Report preview will be deleted permanently"
      });
    }, [form]);

    const onBackgroundIdChange = useCallback(
      id => {
        if (id !== initialValues.backgroundId) {
          dispatch(change(form, "preview", null));
        }
      },
      [form, initialValues.backgroundId]
    );

    const handleFullScreenPreview = () => {
      dispatch(reportFullScreenPreview(values.id));
    };

    useEffect(() => {
      if (values.id !== prevId) {
        discardFileInput();
        if (disableRouteConfirm) {
          setDisableRouteConfirm(false);
        }
      }
    }, [values.id, prevId, disableRouteConfirm]);

    useEffect(() => {
      if (!dirty && nextLocation) {
        history.push(nextLocation);
      }
    }, [nextLocation, dirty]);

    const importExportActions = useMemo(() => getConfigActions("Report", values.name, values.id), [values.id]);

    const validateReportCopyName = useCallback(name => {
      if (pdfReports.find(r => r.title.trim() === name.trim())) {
        return "Report name should be unique";
      }
      return validateNameForQuotes(name);
    }, [pdfReports, values.id]);

    const validateReportName = useCallback(name => {
      if (pdfReports.find(r => r.id !== values.id && r.title.trim() === name.trim())) {
        return "Report name should be unique";
      }
      return validateNameForQuotes(name);
    }, [pdfReports, values.id]);

    return (
      <>
        <Form onSubmit={handleSubmit(handleSave)}>
          <input type="file" ref={fileRef} className="d-none" onChange={handleUpload}/>
          <FormField type="stub" name="body"/>

          <SaveAsNewAutomationModal 
            opened={modalOpened} 
            onClose={onDialodClose} 
            onSave={onDialodSave} 
            validateNameField={validateReportCopyName}
          />

          {!disableRouteConfirm && <RouteChangeConfirm form={form} when={dirty || isNew}/>}

          <AppBarContainer
            values={values}
            manualUrl={manualUrl}
            getAuditsUrl={getAuditsUrl}
            disabled={!isNew && !dirty}
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
                  validate={validateReportName}
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
                        tooltip: "Delete PDF template",
                        confirmText: "PDF template will be deleted permanently",
                        confirmButtonText: "DELETE"
                      }
                    ]}
                  />
                )}

                {isInternal && (
                  <Grow in={isInternal}>
                    <Tooltip title={$t('save_as_new_pdf_report')}>
                      <IconButton onClick={onInternalSaveClick} color="inherit">
                        <FileCopy color="primary" />
                      </IconButton>
                    </Tooltip>
                  </Grow>
                )}
              </>
            )}
          >
            <Grid container rowSpacing={2}>
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
              <Grid item container columnSpacing={3} rowSpacing={2} xs={7} className="pr-3">
                <Grid item xs={12}>
                  <div className="heading">{$t('type')}</div>
                  <FormField
                    name="entity"
                    type="select"
                    items={EntityItems}
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

                <Grid item xs={12}>
                  <FormField label={$t('sort_on')} name="sortOn" type="text" disabled={isInternal} />
                </Grid>

                <Grid item xs={12}>
                  <FormField
                    type="select"
                    label={$t('pdf_background')}
                    name="backgroundId"
                    selectValueMark="id"
                    selectLabelMark="title"
                    items={pdfBackgrounds}
                    onChange={onBackgroundIdChange}
                    debounced={false}
                    allowEmpty
                  />
                </Grid>

                <Grid item xs={12}>
                  <FormField
                    type="text"
                    label={$t('keycode')}
                    name="keyCode"
                    validate={isNew || !isInternal ? validateKeycode : undefined}
                    disabled={!isNew}
                    required
                  />
                </Grid>

                <Grid item xs={12}>
                  {!isNew && (
                    <Button variant="outlined" color="secondary" onClick={handleEdit} disabled={isInternal}>
                      {$t('edit')}
                    </Button>
                  )}
                </Grid>
                
                <Grid item xs={12}>
                  <Button variant="outlined" color="secondary" onClick={handleUploadClick} disabled={isInternal}>
                    {$t('upload_new_version')}
                  </Button>
                </Grid>

                <Grid item xs={12}>
                  {chosenFileName && <Uneditable value={chosenFileName} label={$t('chosen_file')} />}
                </Grid>

                <Grid item xs={12}>
                  {isNew && !values.body && (
                    <Typography id="body" variant="caption" color="error" className="shakingError" paragraph>
                      {$t('report_body_is_required_press_upload_new_version_t')}
                    </Typography>
                  )}
                </Grid>
              </Grid>
              <Grid item xs={5}>
                <div>
                  <FormField
                    label={$t('enabled')}
                    type="switch"
                    name="status"
                    color="primary"
                    format={v => v === "Enabled"}
                    parse={v => (v ? "Enabled" : "Installed but Disabled")}
                    debounced={false}
                  />
                </div>
                <div className="mt-3 pt-1 pb-2">
                  <Bindings
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
                  {!isNew && (
                    <FilePreview
                      label={$t('preview')}
                      actions={[
                        {
                          actionLabel: "Clear preview",
                          onAction: handleClearPreview,
                          icon: <DeleteOutlineRoundedIcon />
                        },
                        {
                          actionLabel: "Full size preview",
                          onAction: handleFullScreenPreview,
                          icon: <FullscreenIcon />
                        }
                      ]}
                      data={values.preview}
                    />
                  )}
                </div>
              </Grid>
            </Grid>
          </AppBarContainer>
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <PdfReportsForm {...props} /> : null);