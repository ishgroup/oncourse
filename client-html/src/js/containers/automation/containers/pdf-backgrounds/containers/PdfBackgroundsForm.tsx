/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useRef, useState
} from "react";
import { Form, InjectedFormProps } from "redux-form";
import DeleteForever from "@material-ui/icons/DeleteForever";
import Grid from "@material-ui/core/Grid/Grid";
import { ReportOverlay } from "@api/model";
import { Dispatch } from "redux";
import Typography from "@material-ui/core/Typography";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import Button from "../../../../../common/components/buttons/Button";
import { usePrevious } from "../../../../../common/utils/hooks";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import FilePreview from "../../../../../common/components/form/FilePreview";
import Uneditable from "../../../../../common/components/form/Uneditable";

const manualUrl = getManualLink("reports_background");

interface Props extends InjectedFormProps {
  isNew: boolean;
  values: ReportOverlay;
  dispatch: Dispatch;
  onCreate: (fileName: string, overlay: File) => void;
  onUpdate: (fileName: string, id: number, overlay: File) => void;
  onDelete: (id: number) => void;
  openConfirm: (onConfirm: any, confirmMessage?: string) => void;
  history: any;
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
     setNextLocation
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

          <CustomAppBar>
            <FormField
              type="headerText"
              name="name"
              placeholder="Name"
              margin="none"
              className="pl-1"
              listSpacing={false}
              required
            />

            <div className="flex-fill" />

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

            <AppBarHelpMenu
              manualUrl={manualUrl}
              auditsUrl={'audit?search=~"ReportOverlay"'}
            />

            <FormSubmitButton
              disabled={(isNew && !fileIsChosen)
                || (!isNew && !dirty && !fileIsChosen)
                || (!isNew && !values.preview && !fileIsChosen)}
              invalid={invalid}
            />
          </CustomAppBar>

          <Grid container className="p-3 appBarContainer">
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
        </Form>
      </>
    );
  }
);

export default props => (props.values ? <PdfBackgroundsForm {...props} /> : null);
