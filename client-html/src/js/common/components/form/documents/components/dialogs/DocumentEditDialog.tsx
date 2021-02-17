/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Dialog from "@material-ui/core/Dialog";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";
import DialogActions from "@material-ui/core/DialogActions";
import { Document, Tag } from "@api/model";
import Tooltip from "@material-ui/core/Tooltip";
import ButtonBase from "@material-ui/core/ButtonBase";
import Grid from "@material-ui/core/Grid";
import LinearProgress from "@material-ui/core/LinearProgress";
import FormField from "../../../form-fields/FormField";
import DocumentIconsChooser from "../items/DocumentIconsChooser";
import { formatRelativeDate } from "../../../../../utils/dates/formatRelative";
import { dialogStyles } from "./dialogStyles";
import Button from "../../../../buttons/Button";
import { DD_MMM_YYYY_AT_HH_MM_A_SPECIAL } from "../../../../../utils/dates/format";
import { getDocumentVersion } from "../utils";
import DocumentShare from "../items/DocumentShare";

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
  showConfirm: any;
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

  unlink = () => {
    const { index, onUnlink } = this.props;
    onUnlink(index);
  };

  renderCreateType(lastVersion, validUrl, readOnly: boolean = false) {
    const {
      classes, item, itemPath, tags, onClose, dispatch, form
    } = this.props;

    return (
      <div>
        <div className="mt-1 mb-2 centeredFlex">
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
          <div className="flex-fill ml-3">
            <FormField
              type="text"
              name={`${itemPath}.name`}
              label="Name"
              required
              disabled={readOnly}
            />
          </div>
        </div>

        <DocumentShare
          validUrl={validUrl}
          readOnly={readOnly}
          documentSource={item}
          dispatch={dispatch}
          form={form}
          itemPath={itemPath}
          noPaper
        />

        <div className="mt-1 centeredFlex">
          <FormField
            type="tags"
            name={`${itemPath}.tags`}
            tags={tags}
            rerenderOnEveryChange
            disabled={readOnly}
          />
        </div>

        <Grid container className="mt-1 centeredFlex">
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${itemPath}.description`}
              label="Description"
              multiline
              fullWidth
              disabled={readOnly}
            />
          </Grid>
        </Grid>

        <DialogActions classes={{ root: classes.actions }}>
          <Button variant="text" color="primary" onClick={onClose}>
            {readOnly ? "Close" : "Cancel"}
          </Button>
          {!readOnly && (
            <Button variant="text" disabled={!item.name} className="documentsSubmitButton" onClick={this.onAdd}>
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

  getVersions = () => {
    const { item } = this.props;

    return item.versions.map(v => ({
      value: String(v.id),
      label: (
        <div className="centeredFlex">
          <Typography>{v.createdBy}</Typography>
          <Typography className="ml-1" color="textSecondary">
            {formatRelativeDate(new Date(v.added), new Date(), DD_MMM_YYYY_AT_HH_MM_A_SPECIAL)}
          </Typography>
        </div>
      )
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

    const lastVersion = item && getDocumentVersion(item);

    const validUrl = item
      && (item.versionId
        ? item.versions.find(v => v.id === item.versionId).url
        : item.versions[item.versions.length - 1].url);

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
                  {type === "create" && this.renderCreateType(lastVersion, validUrl)}
                  {(type === "view" || type === "edit") && this.renderCreateType(lastVersion, validUrl, true)}
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
