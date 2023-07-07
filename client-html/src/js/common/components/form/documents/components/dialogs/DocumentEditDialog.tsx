/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Dialog from "@mui/material/Dialog";
import withStyles from "@mui/styles/withStyles";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import DialogActions from "@mui/material/DialogActions";
import { Document, Tag } from "@api/model";
import Tooltip from "@mui/material/Tooltip";
import ButtonBase from "@mui/material/ButtonBase";
import Grid from "@mui/material/Grid";
import LinearProgress from "@mui/material/LinearProgress";
import FormField from "../../../formFields/FormField";
import DocumentIconsChooser from "../items/DocumentIconsChooser";
import { dialogStyles } from "./dialogStyles";

import { getLatestDocumentItem } from "../utils";
import DocumentShare from "../items/DocumentShare";
import Button from "@mui/material/Button";

export type DocumentDialogType = "edit" | "create" | "view";

interface Props {
  opened: boolean;
  onClose: () => void;
  onAdd: () => void;
  onSave: () => void;
  onCancelEdit: () => void;
  onUnlink: (index: number) => void;
  item: Document;
  type: DocumentDialogType;
  index: number;
  isNew: boolean;
  dirty: boolean;
  itemPath: string;
  form: string;
  dispatch: any;
  tags: Tag[];
  classes?: any;
  entity?: string;
}

class DocumentEditDialog extends React.PureComponent<Props, any> {
  state = {
    loading: false
  };

  componentDidUpdate() {
    if (!this.props.opened && this.state.loading) {
      this.toggleLoading();
    }
  }

  renderType(lastVersion, validUrl, type) {
    const {
      classes, item, itemPath, tags, onClose, dispatch, form, onSave
    } = this.props;

    const readOnly = ["view", "edit"].includes(type);

    return (
      <div>
        <Grid container rowSpacing={2} className="mt-0 mb-2 centeredFlex">
          <Grid item xs={12} className="d-flex">
            <Tooltip title="Open Document URL" disableHoverListener={!validUrl}>
              <div>
                <ButtonBase disabled={!validUrl} onClick={(e: any) => this.openDocumentURL(e, validUrl)}>
                  <DocumentIconsChooser
                    hovered={Boolean(validUrl)}
                    type={lastVersion.mimeType}
                    thumbnail={item.thumbnail}
                  />
                </ButtonBase>
              </div>
            </Tooltip>
            <div className="flex-fill">
              <FormField
                type="text"
                name={`${itemPath}.name`}
                label="Name"
                required
                disabled={readOnly}
              />
            </div>
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="tags"
              name={`${itemPath}.tags`}
              tags={tags}
              disabled={readOnly}
              validateEntity="Document"
            />
          </Grid>
        </Grid>

        <DocumentShare
          validUrl={validUrl}
          readOnly={readOnly}
          documentSource={item}
          dispatch={dispatch}
          form={form}
          itemPath={itemPath}
          noPaper
        />

        <FormField
          type="text"
          name={`${itemPath}.description`}
          label="Description"
          multiline
                    disabled={readOnly}
        />

        <DialogActions classes={{ root: classes.actions }}>
          <Button color="primary" onClick={onClose}>
            {type === "view" ? "Close" : "Cancel"}
          </Button>
          {type !== "view" && (
            <Button
              variant="contained"
              disabled={!item.name}
              className="documentsSubmitButton"
              onClick={type === "create" ? this.onAdd : onSave}
            >
              Add
            </Button>
          )}
        </DialogActions>
      </div>
    );
  }

  toggleLoading = () => {
    this.setState(prevState => ({
      loading: !prevState.loading
    }));
  };

  openDocumentURL = (e: React.MouseEvent<any>, url: string) => {
    e.stopPropagation();
    window.open(url);
  };

  onAdd = () => {
    this.toggleLoading();
    this.props.onAdd();
  };

  render() {
    const {
      opened, onClose, classes, type, item, onCancelEdit, isNew
    } = this.props;

    const { loading } = this.state;

    const lastVersion = item && getLatestDocumentItem(item.versions);

    const validUrl = item && item.urlWithoutVersionId;

    return (
      <Dialog
        open={opened}
        onClose={type === "create" || isNew ? onClose : onCancelEdit}
        classes={{ paper: classes.paper }}
      >
        <div className={classes.container}>
          {item && (
            <>
              <div className={clsx(classes.contentWrapper, "relative overflow-hidden paperBackgroundColor mb-1")}>
                <div className="centeredFlex">
                  <Typography variant="subtitle1" className="flex-fill text-truncate">
                    {item.name}
                  </Typography>
                  <Typography
                    variant="caption"
                    color="textSecondary"
                    className="text-center flex-fill text-truncate"
                  >
                    {lastVersion.fileName}
                  </Typography>
                  <Typography
                    variant="caption"
                    color="textSecondary"
                    className="flex-fill text-end text-truncate"
                  >
                    {lastVersion.size}
                  </Typography>
                </div>

                {loading && <LinearProgress className={classes.documentLoading} />}
              </div>

              {!loading && (
                <div className={clsx(classes.contentWrapper, "relative overflow-hidden paperBackgroundColor")}>
                  {this.renderType(lastVersion, validUrl, type)}
                </div>
              )}
            </>
          )}
        </div>
      </Dialog>
    );
  }
}

export default withStyles(dialogStyles)(DocumentEditDialog) as React.ComponentClass<Props>;
