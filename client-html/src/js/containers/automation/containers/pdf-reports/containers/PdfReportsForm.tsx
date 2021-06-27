/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { change, Form, initialize, InjectedFormProps } from "redux-form";
import DeleteForever from "@material-ui/icons/DeleteForever";
import FileCopy from "@material-ui/icons/FileCopy";
import Grid from "@material-ui/core/Grid/Grid";
import { Report } from "@api/model";
import { Dispatch } from "redux";
import Typography from "@material-ui/core/Typography";
import Grow from "@material-ui/core/Grow";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import Button from "../../../../../common/components/buttons/Button";
import Bindings from "../../../components/Bindings";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import { usePrevious } from "../../../../../common/utils/hooks";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { validateKeycode } from "../../../utils";
import { CommonListItem } from "../../../../../model/common/sidebar";
import { createAndDownloadFile } from "../../../../../common/utils/common";
import FilePreview from "../../../../../common/components/form/FilePreview";
import SaveAsNewAutomationModal from "../../../components/SaveAsNewAutomationModal";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { EntityItems } from "../../../../../model/entities/common";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";

const manualUrl = getManualLink("reports");
const getAuditsUrl = (id: number) => `audit?search=~"Report" and entityId == ${id}`;

interface Props extends InjectedFormProps<Report> {
  isNew: boolean;
  values: Report;
  dispatch: Dispatch;
  onCreate: (report: Report) => void;
  onUpdateInternal: (report: Report) => void;
  onUpdate: (report: Report) => void;
  onDelete: NumberArgFunction;
  pdfBackgrounds: CommonListItem[];
  openConfirm: ShowConfirmCaller;
  history: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
}

const reader = new FileReader();
const parser = new DOMParser();

const fillAttributes = (
  names: string[],
  formFields: string[],
  document: Document,
  dispatch: Dispatch,
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
    setNextLocation
  }) => {
    const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);
    const [modalOpened, setModalOpened] = useState<boolean>(false);
    const [chosenFileName, setChosenFileName] = useState(null);

    const fileRef = useRef<any>();

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
      }
    );
    }, [form]);

    const onBackgroundIdChange = useCallback(
      id => {
        if (id !== initialValues.backgroundId) {
          dispatch(change(form, "preview", null));
        }
      },
      [form, initialValues.backgroundId]
    );

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
        setNextLocation('');
      }
    }, [nextLocation, dirty]);

    return (
      <>
        <Form onSubmit={handleSubmit(handleSave)}>
          <input type="file" ref={fileRef} className="d-none" onChange={handleUpload} />
          <FormField type="stub" name="body" />

          <SaveAsNewAutomationModal opened={modalOpened} onClose={onDialodClose} onSave={onDialodSave} />

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
                    tooltip: "Delete PDF template",
                    confirmText: "PDF template will be deleted permanently",
                    confirmButtonText: "DELETE"
                  }
                ]}
              />
            )}

            {isInternal && (
              <Grow in={isInternal}>
                <Tooltip title="Save as new PDF report">
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
              disabled={!isNew && !dirty}
              invalid={invalid}
            />
          </CustomAppBar>

          <Grid container className="p-3 appBarContainer">
            <Grid item xs={7} className="pr-3">
              <div className="heading">Type</div>
              <FormField
                name="entity"
                type="select"
                items={EntityItems}
                disabled={isInternal}
                required
              />

              <FormField label="Sort On" name="sortOn" type="text" disabled={isInternal} />

              <FormField
                type="text"
                label="Description"
                name="description"
                disabled={isInternal}
                fullWidth
                multiline
              />

              <FormField
                type="select"
                label="PDF background"
                name="backgroundId"
                selectValueMark="id"
                selectLabelMark="name"
                items={pdfBackgrounds}
                onChange={onBackgroundIdChange}
                allowEmpty
              />

              <FormField
                type="text"
                label="Keycode"
                name="keyCode"
                validate={isNew || !isInternal ? validateKeycode : undefined}
                disabled={!isNew}
                required
              />

              {!isNew && (
                <div className="pt-2">
                  <Button variant="outlined" color="secondary" onClick={handleEdit} disabled={isInternal}>
                    Edit
                  </Button>
                </div>
              )}

              <div className="pt-2">
                <Button variant="outlined" color="secondary" onClick={handleUploadClick} disabled={isInternal}>
                  Upload New Version
                </Button>
              </div>

              {chosenFileName && <Uneditable value={chosenFileName} label="Chosen file" className="mt-1" />}

              {isNew && !values.body && (
                <Typography id="body" variant="caption" color="error" className="mt-1 shakingError" paragraph>
                  Report body is required. Press &quot;Upload New Version&quot; to attach xml
                </Typography>
              )}
            </Grid>
            <Grid item xs={5}>
              <div>
                <FormField type="switch" name="enabled" label="Enabled" color="primary" fullWidth />
              </div>
              <div className="mt-3 pt-1 pb-2">
                <Bindings
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
                {!isNew && (
                  <FilePreview
                    label="Preview"
                    actions={[{ actionLabel: "Clear preview", onAction: handleClearPreview }]}
                    data={values.preview}
                  />
                )}
              </div>
            </Grid>
          </Grid>
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <PdfReportsForm {...props} /> : null);
