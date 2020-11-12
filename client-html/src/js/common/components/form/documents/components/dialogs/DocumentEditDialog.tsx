/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Avatar from "@material-ui/core/Avatar";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import {
  Directions, Language, Link
} from "@material-ui/icons";
import { AlertTitle } from "@material-ui/lab";
import Alert from "@material-ui/lab/Alert";
import Dialog from "@material-ui/core/Dialog";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";
import DialogActions from "@material-ui/core/DialogActions";
import { Document, Tag } from "@api/model";
import { change, Field } from "redux-form";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Tooltip from "@material-ui/core/Tooltip";
import ButtonBase from "@material-ui/core/ButtonBase";
import Grid from "@material-ui/core/Grid";
import LinearProgress from "@material-ui/core/LinearProgress";
import Collapse from "@material-ui/core/Collapse";
import { StyledCheckbox } from "../../../form-fields/CheckboxField";
import FormField from "../../../form-fields/FormField";
import { Switch } from "../../../form-fields/Switch";
import DocumentIconsChooser from "../items/DocumentIconsChooser";
import FormRadioButtons from "../../../form-fields/FormRadioButtons";
import { formatRelativeDate } from "../../../../../utils/dates/formatRelative";
import DocumentTags from "../items/DocumentTags";
import { dialogStyles } from "./dialogStyles";
import Button from "../../../../buttons/Button";
import { DD_MMM_YYYY_AT_HH_MM_A_SPECIAL } from "../../../../../utils/dates/format";
import { getAvailableOptions, getDocumentShareSummary, getDocumentVersion } from "../utils";

export type DocumentDialogType = "edit" | "create";

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

  onWebsiteChange = (e, val) => {
    const { dispatch, form, itemPath } = this.props;
    dispatch(change(form, `${itemPath}.access`, val ? "Public" : "Private"));
  }

  onPortalSharingChange = (e, val) => {
    const { dispatch, form, itemPath } = this.props;
    dispatch(change(form, `${itemPath}.access`, val ? "Tutors and enrolled students" : "Private"));
  }

  onTutorsChange = (e, val) => {
    const { dispatch, form, itemPath } = this.props;
    dispatch(change(form, `${itemPath}.access`, val ? "Tutors only" : "Private"));
  }

  onTutorsAndStudentsChange = (e, val) => {
    const { dispatch, form, itemPath } = this.props;
    dispatch(change(form, `${itemPath}.access`, val ? "Tutors and enrolled students" : "Tutors only"));
  }

  onLinkChange = (e, val) => {
    const { dispatch, form, itemPath } = this.props;
    dispatch(change(form, `${itemPath}.access`, val ? "Link" : "Private"));
  }

  renderCreateType(lastVersion, validUrl) {
    const {
      classes, item, itemPath, tags, onClose, entity
    } = this.props;

    const isCourseOnly = entity === "Course";

    const availableOptions = getAvailableOptions({ [entity]: [] });

    const contactRelated = [
      "Contact",
      "Assessment",
      "Application",
      "Certificate",
      "Enrolment",
      "Invoice",
      "PriorLearning"].includes(entity);

    const tutorsAndStudents = ["Tutors and enrolled students", "Tutors only"].includes(item.access);

    return (
      <>
        <div className="mt-1 centeredFlex">
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
            />
          </div>
        </div>
        <div className="flex-fill">
          <Alert severity="info" className="mb-3 mt-3">
            <AlertTitle>Who can view this document</AlertTitle>
            {getDocumentShareSummary(item.access, [])}
          </Alert>
        </div>

        {availableOptions["PortalSharing"]
        && (
          <div className="flex-fill mb-2">
            <CardHeader
              classes={{
                root: "p-0",
                action: "mt-0",
                title: "heading"
              }}
              action={(
                <FormControlLabel
                  classes={{
                    root: "switchWrapper",
                    label: "switchLabel"
                  }}
                  control={(
                    <Switch
                      checked={tutorsAndStudents}
                      onChange={this.onPortalSharingChange}
                    />
                  )}
                  label="Shared in portal"
                />
              )}
              avatar={(
                <Avatar className="activeAvatar">
                  <Directions />
                </Avatar>
              )}
              title="Skills onCourse"
            />
            {!contactRelated
                && (
                <>
                  {availableOptions["Tutor&Student"]
                  && (
                    <Collapse in={tutorsAndStudents}>
                      <CardContent>
                        <FormControlLabel
                          classes={{
                            root: "checkbox",
                            label: "ml-0"
                          }}
                          control={(
                            <StyledCheckbox
                              checked={tutorsAndStudents}
                              onChange={this.onTutorsChange}
                            />
                          )}
                          label="Show to tutors"
                        />
                        <FormControlLabel
                          classes={{
                            root: "checkbox"
                          }}
                          control={(
                            <StyledCheckbox
                              checked={item.access === "Tutors and enrolled students"}
                              onChange={this.onTutorsAndStudentsChange}
                            />
                          )}
                          label="Show to students"
                        />
                      </CardContent>
                    </Collapse>
                  )}
                </>
)}
          </div>
        )}

        <div className="flex-fill mb-2">
          <CardHeader
            classes={{
              root: "p-0",
              action: "mt-0",
              title: "heading"
            }}
            action={(
              <FormControlLabel
                classes={{
                  root: "switchWrapper",
                  label: "switchLabel"
                }}
                control={(
                  <Switch
                    checked={["Link", "Public"].includes(item.access)}
                    onChange={this.onLinkChange}
                  />
                )}
                label="Shared by link"
              />
            )}
            avatar={(
              <Avatar className="activeAvatar">
                <Link />
              </Avatar>
            )}
            title="Web link"
          />
        </div>

        {isCourseOnly
        && (
          <div className="flex-fill mb-2">
            <CardHeader
              classes={{
              root: "p-0",
              action: "mt-0",
              title: "heading"
            }}
              action={(
                <FormControlLabel
                  classes={{
                  root: "switchWrapper",
                  label: "switchLabel"
                }}
                  control={(
                    <Switch
                      checked={
                      item.access === "Public"
                    }
                      onChange={this.onWebsiteChange}
                    />
                )}
                  label="Shared with website visitors"
                />
            )}
              avatar={(
                <Avatar className="activeAvatar">
                  <Language />
                </Avatar>
            )}
              title="Website"
            />
          </div>
        )}

        <div className="mt-1 centeredFlex">
          <FormField
            type="tags"
            name={`${itemPath}.tags`}
            tags={tags}
            rerenderOnEveryChange
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
            />
          </Grid>
        </Grid>

        <DialogActions classes={{ root: classes.actions }}>
          <Button variant="text" color="primary" onClick={onClose}>
            Cancel
          </Button>
          <Button variant="text" disabled={!item.name} className="documentsSubmitButton" onClick={this.onAdd}>
            Add
          </Button>
        </DialogActions>
      </>
    );
  }

  renderEditType(lastVersion, validUrl) {
    const {
      classes, item, itemPath, onCancelEdit, onSave, onClose, isNew, dirty
    } = this.props;

    return (
      <>
        <div className="mt-1 centeredFlex">
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

          <div className="flex-fill ml-3 overflow-hidden">
            <Typography variant="caption" color="textSecondary">
              Name
            </Typography>
            <Typography variant="subtitle1" className="text-truncate">
              {item.name}
            </Typography>
          </div>

          <div className="flex-fill overflow-hidden">
            <Typography variant="caption" color="textSecondary">
              Access
            </Typography>
            <Typography variant="subtitle1" className="text-truncate">
              {item.access}
            </Typography>
          </div>

          {!isNew && (
            <Button variant="text" className="errorColor" onClick={this.unlink}>
              Unlink
            </Button>
          )}
        </div>

        <div className="mt-1 centeredFlex">
          <DocumentTags tags={item.tags} bold />
        </div>

        <div className="mt-1 centeredFlex">
          <div>
            <Typography variant="caption" color="textSecondary">
              Description
            </Typography>
            {item.description ? (
              <Typography variant="body1">{item.description}</Typography>
            ) : (
              <Typography variant="body1" className="placeholderContent">
                No Value
              </Typography>
            )}
          </div>
        </div>

        {/* <div className={clsx("mt-1", "centeredFlex")}> */}
        {/*  <FormControlLabel */}
        {/*    classes={{ */}
        {/*      root: "checkbox" */}
        {/*    }} */}
        {/*    control={ */}
        {/*      <Field */}
        {/*        name={`${itemPath}.versionId`} */}
        {/*        component={CheckboxField} */}
        {/*        color="primary" */}
        {/*        parse={val => (val ? item.versions[item.versions.length - 1].id : null)} */}
        {/*        format={val => Boolean(val)} */}
        {/*      /> */}
        {/*    } */}
        {/*    label="Freeze to a specific version" */}
        {/*  /> */}
        {/* </div> */}

        <Collapse in={Boolean(item.versionId)}>
          <div className="mt-1 centeredFlex">
            <Field
              name={`${itemPath}.versionId`}
              component={FormRadioButtons}
              parse={val => Number(val)}
              format={val => String(val)}
              items={this.getVersions()}
              color="secondary"
              stringValue
            />
          </div>
        </Collapse>

        <DialogActions classes={{ root: classes.actions }}>
          <Button variant="text" color="primary" onClick={isNew ? onClose : onCancelEdit}>
            Cancel
          </Button>
          <Button variant="text" className="documentsSubmitButton" onClick={onSave} disabled={!isNew && !dirty}>
            {isNew ? "Add" : "Save"}
          </Button>
        </DialogActions>
      </>
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
                  {type === "edit" && this.renderEditType(lastVersion, validUrl)}
                  {type === "create" && this.renderCreateType(lastVersion, validUrl)}
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
