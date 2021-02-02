/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import CircularProgress from "@material-ui/core/CircularProgress";
import React, { useCallback, useMemo, useRef } from "react";
import clsx from "clsx";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faFile,
  faFileAlt,
  faFileArchive,
  faFileExcel,
  faFileImage,
  faFilePdf,
  faFilePowerpoint,
  faFileWord,
  faCog
} from "@fortawesome/free-solid-svg-icons";
import { connect } from "react-redux";
import { arrayInsert, change, Field, } from "redux-form";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import Collapse from "@material-ui/core/Collapse";
import {
  ExpandMore,
  OpenWith
} from "@material-ui/icons";
import Button from "@material-ui/core/Button";
import { addDays, format, isSunday } from "date-fns";
import { Document, DocumentVisibility, DocumentVersion } from "@api/model";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import FormField from "../../../../common/components/form/form-fields/FormField";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import SimpleTagList from "../../../../common/components/form/simpleTagListComponent/SimpleTagList";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import { III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL } from "../../../../common/utils/dates/format";
import { getDocumentVersion, iconSwitcher } from "../../../../common/components/form/documents/components/utils";
import { mapSelectItems } from "../../../../common/utils/common";
import { EditViewProps } from "../../../../model/common/ListView";
import { AppTheme } from "../../../../model/common/Theme";
import { State } from "../../../../reducers/state";
import DocumentShare from "../../../../common/components/form/documents/components/items/DocumentShare";
import { showMessage } from "../../../../common/actions";

library.add(faFileImage, faFilePdf, faFileExcel, faFileWord, faFilePowerpoint, faFileArchive, faFileAlt, faFile, faCog);

const styles = (theme: AppTheme) => createStyles({
  previewPaper: {
    width: 200,
    height: 200,
    "&:hover": {
      "& $viewDocument": {
        display: "flex"
      }
    }
  },
  previewPaperExpand: {
    width: 350,
    height: 350
  },
  preview: {
    backgroundSize: "contain",
    backgroundRepeat: "no-repeat",
    backgroundPosition: "center"
  },
  previewIcon: {
    padding: 25
  },
  previewHoverImage: {
    "&:hover": {
      boxShadow: `0 0 1px 1px ${theme.palette.primary.main}`
    }
  },
  wh100: {
    height: "30px !important",
    width: "auto !important"
  },
  documentTitle: {
    margin: "12px 0 0"
  },
  documentTitleExpandMore: {
    width: 20,
    height: 20
  },
  rotateExpandMore: {
    transform: "rotate(180deg)"
  },
  viewDocument: {
    background: "rgba(0,0,0, 0.5)",
    display: "none",
    top: 0,
    left: 0,
    color: "#ddd"
  },
  rootPanel: {
    gridTemplateColumns: "1fr auto",
    gridColumnGap: "8px"
  },
  settingsWrapper: {},
  divider: {
    margin: theme.spacing(3, -3)
  },
  whiteTextColor: {
    fontSize: 12
  },
  removedSwitch: {
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center",
    marginBottom: theme.spacing(2)
  }
});

interface DocumentGeneralProps extends EditViewProps<Document> {
  tags?: any;
  form?: string;
  classes?: any;
  hovered?: boolean;
}

const documentVisibility = () => Object.keys(DocumentVisibility).filter(val => isNaN(Number(val))).map(mapSelectItems);

const validateTagList = (value, allValues, props) => {
  const { tags } = props;
  return validateTagsList(tags && tags.length > 0 ? tags : [], value, allValues, props);
};

const openDocumentURL = (e: React.MouseEvent<any>, url: string) => {
  e.stopPropagation();
  window.open(url);
};

const DocumentGeneralTab: React.FC<DocumentGeneralProps> = props => {
  const {
    twoColumn,
    values,
    classes,
    tags,
    hovered = true,
    form,
    dispatch,
    rootEntity,
    manualLink,
    onCloseClick,
    isNew,
    dirty,
    invalid
  } = props;

  const fileRef = useRef<any>();

  const [moreDetailcollapsed, setMoreDetailcollapsed] = React.useState(false);
  const [loadingDocVersion, setLoadingDocVersion] = React.useState(false);
  // const [versionMenu, setVersionMenu] = React.useState(null);

  const showMoreDetails = () => {
    setMoreDetailcollapsed(!moreDetailcollapsed);
  };

  const restoreDocument = () => {
    dispatch(change(form, "removed", false));
  };

  // const versionMenuOpen = e => {
  //   setVersionMenu(e.currentTarget);
  // };
  //
  // const versionMenuClose = () => {
  //   setVersionMenu(null);
  // };

  const documentVersion = getDocumentVersion(values);

  const currentIcon = iconSwitcher(documentVersion.mimeType);

  const validUrl = values && Array.isArray(values.versions) && ( values.versions[0].url);

  const thumbnail = values && Array.isArray(values.versions) && ( values.versions[0].thumbnail);

  const handleFileSelect = useCallback(() => {
    const file = fileRef.current.files[0];

    if (file) {
      setLoadingDocVersion(true);
      DocumentsService.createDocumentVersion(values.id, file.name, file).then(docVersion => {
        dispatch(arrayInsert(form, "versions", 0, docVersion));
        setLoadingDocVersion(false);
      }).catch(error => {
        setLoadingDocVersion(false);
        dispatch(showMessage({ message: error.data.errorMessage, success: false }));
      });
    }
  }, [form]);

  const onUploadClick = useCallback(() => {
    fileRef.current.click();
  }, []);

  const headerField = (
    <FormField
      name="name"
      label="Name"
      type={twoColumn ? "headerText" : "text"}
      required
      fullWidth
    />
  );

  return (
    loadingDocVersion
      ? (
        <div className="centeredFlex w-100 h-100 justify-content-center">
          <CircularProgress size={64} thickness={5} className={classes.buttonProgress} />
        </div>
      )
      : (
        <div className={twoColumn ? "appBarContainer" : "h-100"}>
          {twoColumn && (
            <CustomAppBar>
              <Grid container className="flex-fill">
                <Grid item xs={6} className="pr-2">
                  {headerField}
                </Grid>
              </Grid>
              <div>
                {manualLink && (
                  <AppBarHelpMenu
                    created={values ? new Date(values.createdOn) : null}
                    modified={values ? new Date(values.modifiedOn) : null}
                    auditsUrl={`audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                    manualUrl={manualLink}
                  />
                )}

                <Button onClick={onCloseClick} className="closeAppBarButton">
                  Close
                </Button>
                <FormSubmitButton
                  disabled={(!isNew && !dirty)}
                  invalid={invalid}
                />
              </div>
            </CustomAppBar>
          )}

          <Grid container className="p-3 relative">
            <Grid item xs={twoColumn ? 4 : 12}>
              {Boolean(values.removed) && (
              <div className={clsx("backgroundText errorColorFade-0-2", twoColumn ? "fs10" : "fs8")}>PENDING DELETION</div>
              )}

              <Paper className={clsx("relative cursor-pointer", classes.previewPaper)}>
                {thumbnail ? (
                  <div
                    style={{ backgroundImage: `url(data:image;base64,${thumbnail})` }}
                    className={clsx("w-100 h-100", classes.preview, { [classes.previewHoverImage]: hovered })}
                  />
                ) : (
                  <div className={clsx("centeredFlex justify-content-center h-100", classes.preview, classes.previewIcon,
                    { "coloredHover": hovered })}
                  >
                    <div className="text-center">
                      {currentIcon({ classes })}
                      <br />
                      <Typography
                        variant="caption"
                        color="textSecondary"
                        className="text-center flex-fill text-truncate"
                      >
                        Click to view
                      </Typography>
                    </div>
                  </div>
                )}
                <div className={clsx("absolute w-100 h-100 align-items-center text-center", classes.viewDocument)}>
                  <div className="flex-fill">
                    <Button
                      variant="outlined"
                      size="small"
                      color="inherit"
                      disabled={!validUrl}
                      onClick={(e: any) => openDocumentURL(e, validUrl)}
                    >
                      <OpenWith />
                      {` `}
                      View
                    </Button>
                  </div>
                </div>
              </Paper>
              <Typography
                variant="caption"
                color="textSecondary"
                className={clsx(classes.documentTitle, "d-inline-block cursor-pointer text-center flex-fill text-truncate")}
                onClick={showMoreDetails}
              >
                {documentVersion.fileName}
                {" "}
                <ExpandMore
                  className={clsx("d-inline-block vert-align-mid",
                    classes.documentTitleExpandMore, moreDetailcollapsed && classes.rotateExpandMore)}
                />
              </Typography>
              <Collapse in={moreDetailcollapsed} mountOnEnter unmountOnExit className="mb-0">
                <Typography variant="caption" color="textSecondary">
                  {documentVersion.size}
                  <br />
                  author:
                  {' '}
                  {documentVersion.createdBy}
                </Typography>
              </Collapse>
              <br />
            </Grid>
            <Grid item container xs={twoColumn ? 4 : 12} alignContent="flex-start">
              <Grid item xs={12}>
                {!twoColumn && headerField}
              </Grid>
              <Grid item xs={12}>
                <Field
                  name="tags"
                  tags={tags}
                  component={SimpleTagList}
                  validate={tags && tags.length ? validateTagList : undefined}
                />
              </Grid>
              <Grid item xs={12}>
                <Field name="description" label="Description" component={EditInPlaceField} multiline fullWidth />
              </Grid>
              {Boolean(values.removed) && (
              <Grid item xs={12} className="pb-2">
                <Typography variant="body2" className={clsx("d-flex align-items-baseline mb-2", classes.textInfo)}>
                  <span>
                    This document will be permanently deleted after
                    { ' ' }
                    { format(addDays(new Date(values.modifiedOn), 30), "d MMMM yy") }
                  </span>
                </Typography>
                <Button variant="outlined" size="medium" color="secondary" onClick={restoreDocument}>
                  RESTORE
                </Button>
              </Grid>
              )}
            </Grid>

            <Grid item xs={twoColumn ? 4 : 12} className="mb-3">
              <div className="heading mb-2">
                History
              </div>
              <div>
                {Boolean(values.versions)
                && values.versions.map((version: DocumentVersion) => (
                  <div key={"key_" + version.id || "new"}>
                    <div className={clsx("d-grid mb-2", classes.rootPanel)}>
                      <div>
                        <Typography variant="body2">
                          {format(new Date(version.added), III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL).replace(/\./g, "")}
                        </Typography>
                        <Typography variant="body2" className={clsx("d-flex align-items-baseline", classes.textInfo)}>
                          <span>{version.createdBy}</span>
                        </Typography>
                      </div>
                      <div className="flex-fill">
                        <Button
                          variant="outlined"
                          size="small"
                          color="secondary"
                          disabled={!version.url}
                          onClick={(e: any) => openDocumentURL(e, version.url)}
                        >
                          <OpenWith />
                          {` `}
                          View
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              <input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />
              <Button variant="outlined" size="medium" color="secondary" onClick={onUploadClick}>
                UPLOAD NEW VERSION
              </Button>
            </Grid>

            <Grid item xs={twoColumn ? 8 : 12} className="pt-2 pb-2">
              <DocumentShare
                validUrl={validUrl}
                dispatch={dispatch}
                documentSource={values}
                form={form}
              />
            </Grid>
          </Grid>
        </div>
      )
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Document"]
});

export default connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(DocumentGeneralTab));
