/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useRef, useState
} from "react";
import { Form, InjectedFormProps } from "redux-form";
import DeleteForever from "@mui/icons-material/DeleteForever";
import Grid from "@mui/material/Grid";
import { ReportOverlay } from "@api/model";
import { Dispatch } from "redux";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import FormField from "../../../../../common/components/form/formFields/FormField";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { usePrevious } from "../../../../../common/utils/hooks";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import FilePreview from "../../../../../common/components/form/FilePreview";
import Uneditable from "../../../../../common/components/form/Uneditable";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("reports_background");

interface Props extends InjectedFormProps {
  isNew: boolean;
  values: ReportOverlay;
  dispatch: Dispatch;
  onCreate: (fileName: string, overlay: File) => void;
  onUpdate: (fileName: string, id: number, overlay: File) => void;
  onDelete: (id: number) => void;
  history: any;
  syncErrors: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
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
     setNextLocation,
     syncErrors
    }) => {
    const [disableRouteConfirm, setDisableRouteConfirm] = useState<boolean>(false);
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
      setDisableRouteConfirm(true);
      onDelete(values.id);
      discardFileInput();
    }, [values.id]);

    const handleSave = useCallback(
      (val: ReportOverlay) => {
        setDisableRouteConfirm(true);
        if (isNew) {
          onCreate(val.name, fileRef.current.files[0]);
          discardFileInput();
          return;
        }
        onUpdate(val.name, val.id, fileIsChosen ? fileRef.current.files[0] : null);
        discardFileInput();
      },
      [isNew, fileIsChosen]
    );

    const handleUploadClick = useCallback(() => fileRef.current.click(), []);

    useEffect(() => {
      if (disableRouteConfirm && values.id !== prevId) {
        setDisableRouteConfirm(false);
      }
    }, [values.id, prevId, disableRouteConfirm]);

    useEffect(() => {
      if (values.id !== prevId) {
        discardFileInput();
      }
    }, [values.id, prevId]);

    useEffect(() => {
      if (!dirty && nextLocation) {
        history.push(nextLocation);
        setNextLocation('');
      }
    }, [nextLocation, dirty]);

    const handleFileSelect = () => {
      const file = fileRef.current.files[0];
      if (file) {
        setFileIsChosen(true);
        setChosenFileName(file.name);
      }
    };

    return (
      <>
        <Form onSubmit={handleSubmit(handleSave)}>
          <input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />
          {(dirty || isNew || fileIsChosen) && (
            <RouteChangeConfirm form={form} when={(dirty || isNew || fileIsChosen) && !disableRouteConfirm} />
          )}

          <AppBarContainer
            values={values}
            manualUrl={manualUrl}
            getAuditsUrl='audit?search=~"ReportOverlay"'
            disabled={(isNew && !fileIsChosen)
            || (!isNew && !dirty && !fileIsChosen)
            || (!isNew && !values.preview && !fileIsChosen)}
            invalid={invalid}
            title={(isNew && (!values.name || values.name.trim().length === 0)) ? "New" : values.name.trim()}
            opened={Object.keys(syncErrors).includes("name")}
            fields={(
              <Grid item xs={12}>
                <FormField
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
                        confirm: true,
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
            <Grid container columnSpacing={3}>
              <Grid item xs={12}>
                <FilePreview data={values.preview} label="Preview" />

                <Button variant="outlined" color="secondary" className="mt-2" onClick={handleUploadClick}>
                  Upload New Version
                </Button>

                {chosenFileName && <Uneditable value={chosenFileName} label="Chosen file" className="mt-1" />}

                {!chosenFileName && !values.preview ? (
                  <Typography color="error" variant="body2" className="mt-1" paragraph>
                    File must be added
                  </Typography>
                ) : null}
              </Grid>
            </Grid>
          </AppBarContainer>
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <PdfBackgroundsForm {...props} /> : null);
