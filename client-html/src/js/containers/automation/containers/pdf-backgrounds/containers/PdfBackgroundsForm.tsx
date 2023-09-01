/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ReportOverlay } from "@api/model";
import DeleteForever from "@mui/icons-material/DeleteForever";
import LoadingButton from "@mui/lab/LoadingButton";
import Button from "@mui/material/Button";
import Grid from "@mui/material/Grid";
import { FilePreview, usePrevious } from "ish-ui";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { Dispatch } from "redux";
import { change, Form, initialize, InjectedFormProps } from "redux-form";
import { showMessage } from "../../../../../common/actions";
import AppBarActions from "../../../../../common/components/appBar/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import FormField from "../../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../../common/components/form/formFields/Uneditable";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PDF_BACKGROUND_FORM_NAME } from "../PdfBackgrounds";

const manualUrl = getManualLink("reports_background");

interface Props extends InjectedFormProps {
  isNew: boolean;
  loading: boolean;
  values: ReportOverlay;
  dispatch: Dispatch;
  onCreate: (fileName: string, overlay: File) => void;
  onUpdate: (fileName: string, id: number, overlay: File) => void;
  onDelete: (id: number) => void;
  getPdfBackgroundCopy: (id: number, name: string) => void;
  history: any;
  syncErrors: any;
  nextLocation: string;
}

const PdfBackgroundsForm = React.memo<Props>(
  ({
     dirty,
     handleSubmit,
     isNew,
     invalid,
     values,
     onCreate,
     onUpdate,
     onDelete,
     form,
     history,
     nextLocation,
     syncErrors,
     getPdfBackgroundCopy,
     loading,
     dispatch
    }) => {
    const [fileIsChosen, setFileIsChosen] = useState(false);
    const [chosenFileName, setChosenFileName] = useState(null);

    const fileRef = useRef<any>();

    const prevId = usePrevious(values.id);

    const discardFileInput = () => {
      fileRef.current.value = null;
      setFileIsChosen(false);
      setChosenFileName(null);
    };

    const handleDelete = useCallback(() => {
      dispatch(initialize(PDF_BACKGROUND_FORM_NAME, values));
      onDelete(values.id);
      discardFileInput();
    }, [values.id]);

    const handleSave = useCallback(
      (val: ReportOverlay) => {
        if (isNew) {
          onCreate(val.name, fileRef.current.files[0]);
          return;
        }
        onUpdate(val.name, val.id, fileIsChosen ? fileRef.current.files[0] : null);
      },
      [isNew, fileIsChosen]
    );

    const handleUploadClick = useCallback(() => fileRef.current.click(), []);

    const handleDownloadClick = () => getPdfBackgroundCopy(values.id, values.name);

    useEffect(() => {
      if (values.id !== prevId) {
        discardFileInput();
      }
    }, [values.id, prevId]);

    useEffect(() => {
      if (!dirty && nextLocation) {
        history.push(nextLocation);
      }
    }, [nextLocation, dirty]);

    const handleFileSelect = () => {
      const file = fileRef.current.files[0];
      if (file) {

        // Set file size limit to 5mb
        if ((file.size / 1024 / 1024) > 5 ) {
          dispatch(showMessage({ message: "File size exceeds 5 MiB" }));
          return;
        }

        setFileIsChosen(true);
        setChosenFileName(file.name);
        
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          const image = new Image();
          image.src = reader.result as string;
          image.onload = () => {
            const canvas = document.createElement("canvas");
            const ctx = canvas.getContext("2d");

            const isPortrait = image.height >= image.width;

            canvas.width = isPortrait ? 165 : 240;
            canvas.height = isPortrait ? 240 : 165;
            ctx.drawImage(image,
              0, 0, image.width, image.height,
              0, 0, canvas.width, canvas.height
            );

            dispatch(change(form, "preview", (canvas.toDataURL() as string).replace("data:image/png;base64,", "")));
          };
        };
        reader.onerror = e => {
          console.error(e);
          dispatch(showMessage({ message: e as any }));
        };
      }
    };

    return (
      <>
        <Form onSubmit={handleSubmit(handleSave)}>
          <input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />
          <RouteChangeConfirm form={form} when={dirty} />

          <AppBarContainer
            values={values}
            manualUrl={manualUrl}
            getAuditsUrl='audit?search=~"ReportOverlay"'
            disabled={(isNew && !fileIsChosen)
            || (!isNew && !dirty && !fileIsChosen)
            || (!isNew && !values.preview && !fileIsChosen)}
            invalid={invalid}
            title={(isNew && (!values.name || values.name.trim().length === 0)) ? "New" : values.name.trim()}
            opened={isNew || Object.keys(syncErrors).includes("name")}
            fields={(
              <Grid item xs={12}>
                <FormField
                  type="text"
                  name="name"
                  label="Name"
                  required
                />
              </Grid>
            )}
            actions={(
              <>
                {!isNew && (
                  <AppBarActions
                    actions={[
                      {
                        action: handleDelete,
                        icon: <DeleteForever />,
                        tooltip: "Delete PDF background",
                        confirmText: "PDF background will be deleted permanently",
                        confirmButtonText: "DELETE"
                      }
                    ]}
                  />
                )}
              </>
            )}
          >
            <Grid container columnSpacing={3} rowSpacing={2}>
              <Grid item xs={12}>
                <FilePreview data={values.preview} label="Preview" />
              </Grid>
              {(isNew || chosenFileName) && (
                <Grid item xs={12}>
                  <Uneditable value={chosenFileName} error={!chosenFileName && "File must be added"} label="Chosen file" className="mt-1" />
                </Grid>
              )}
              
              <Grid item xs={12}>
                <Button
                  variant="outlined"
                  color="secondary"
                  onClick={handleUploadClick}
                >
                  Upload New Version
                </Button>
              </Grid>
              {Boolean(values.preview) && (
                <Grid item xs={12}>
                  <LoadingButton
                    loading={loading}
                    variant="outlined"
                    color="secondary"
                    onClick={handleDownloadClick}
                  >
                    Download copy
                  </LoadingButton>
                </Grid>
              )}
            </Grid>
          </AppBarContainer>
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <PdfBackgroundsForm {...props} /> : null);
