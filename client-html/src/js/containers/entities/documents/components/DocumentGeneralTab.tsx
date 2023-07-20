/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useRef } from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import { arrayRemove, change, } from "redux-form";
import { createStyles, withStyles } from "@mui/styles";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Collapse from "@mui/material/Collapse";
import { Delete, ExpandMore, OpenWith } from "@mui/icons-material";
import Button from "@mui/material/Button";
import { addDays, format } from "date-fns";
import { Document, DocumentVersion } from "@api/model";
import FormField from "../../../../common/components/form/formFields/FormField";
import { D_MMM_YYYY, III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL } from "../../../../common/utils/dates/format";
import {
  FileTypeIcon, getDocumentContent,
  getLatestDocumentItem
} from "../../../../../ish-ui/documents/utils";
import { EditViewProps } from "../../../../model/common/ListView";
import { AppTheme } from "../../../../../ish-ui/model/Theme";
import { State } from "../../../../reducers/state";
import DocumentShare from "../../../../common/components/form/documents/DocumentShare";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { EntityChecklists } from "../../../tags/components/EntityChecklists";
import IconButton from "@mui/material/IconButton";
import { useHoverShowStyles } from "../../../../../ish-ui/styles/hooks";
import { useAppTheme } from "../../../../../ish-ui/themes/ishTheme";

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

const openDocumentURL = (e: React.MouseEvent<any>, url: string) => {
  e.stopPropagation();
  window.open(url);
};

const DocumentVersion = ({
  classes,
  version,
  index,
  dispatch, 
  form, 
  showConfirm,
  hasOneVersion,
  onCurrentChange
 }) => {
  
  const onDelete = () => {
    showConfirm({
      onConfirm: () => dispatch(arrayRemove(form, "versions", index)),
      confirmMessage: "Version will be deleted permanently after save",
      confirmButtonText: "Delete"
    });
  };

  const hoverShowClasses = useHoverShowStyles();

  const theme = useAppTheme();

  return (
    <div className={hoverShowClasses.container}>
      <div className={clsx("d-grid mb-2", classes.rootPanel)}>
        <div className="text-truncate">
          <Typography variant="body2" noWrap>
            {version.fileName}
          </Typography>
          <Typography variant="caption" noWrap>
            {version.createdBy}
            {Boolean(version.createdBy) && <>&nbsp;&nbsp;</>}
            {format(new Date(version.added), III_DD_MMM_YYYY_HH_MM_AAAA_SPECIAL).replace(/\./g, "")}
          </Typography>
        </div>
        <div className="flex-fill">
          <div>
            <IconButton className={version.current ? null : hoverShowClasses.target} disabled={hasOneVersion}>
              <FormField
                type="coloredCheckbox"
                name={`versions[${index}].current`}
                color={theme.palette.secondary.main}
                onChange={onCurrentChange}
              />
            </IconButton>
            <IconButton className={hoverShowClasses.target} color="secondary" onClick={(e: any) => openDocumentURL(e, version.url)}>
              <OpenWith />
            </IconButton>
            <IconButton className={hoverShowClasses.target} color="secondary" onClick={onDelete} disabled={hasOneVersion || version.current}>
              <Delete />
            </IconButton>
          </div>
        </div>
      </div>
    </div>
  );
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
    isNew,
    syncErrors,
    showConfirm
  } = props;

  const fileRef = useRef<any>();

  const [moreDetailcollapsed, setMoreDetailcollapsed] = React.useState(false);

  const showMoreDetails = () => {
    setMoreDetailcollapsed(!moreDetailcollapsed);
  };

  const restoreDocument = () => {
    dispatch(change(form, "removed", false));
  };

  const documentVersion = getLatestDocumentItem(values.versions);

  const validUrl = documentVersion?.url;

  const thumbnail = documentVersion?.thumbnail;

  const handleFileSelect = () => {
    const content:File = fileRef.current.files[0];
    if (content) {
      getDocumentContent(content).then( _content => {
        const newVersion: DocumentVersion = {
          id: null,
          added: new Date().toISOString(),
          createdBy: null,
          fileName: content.name,
          mimeType: content.type,
          size: null,
          url: null,
          thumbnail: null,
          current: true,
          content: _content
        };
        dispatch(change(form, "versions", [newVersion, ...values.versions.map(v => ({ ...v, current: false }))]));
      });
    }
  };

  const onUploadClick = useCallback(() => {
    fileRef.current.click();
  }, []);

  const hasOneVersion = values.versions.length === 1;

  const onCurrentChange = (e, value, index) => {
    const currentIndex = values.versions.findIndex(v => v.current);

    if (!value || hasOneVersion || currentIndex === index) {
      e.preventDefault();
      return;
    }

    dispatch(change(form, "versions", values.versions.map((v, i) => ({ ...v, current: index === i }))));
  };

  return (
    <div className={twoColumn ? "" : "h-100"}>
      <Grid container columnSpacing={3} rowSpacing={2} className="p-3 ">
        <Grid item container xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).includes("name")}
            twoColumn={twoColumn}
            title={<span>{values && values.name}</span>}
            fields={(
              <Grid item xs={twoColumn ? 6 : 12}>
                <FormField
                  type="text"
                  name="name"
                  label="Name"
                  required
                />
              </Grid>
            )}
          />
        </Grid>
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
                  <FileTypeIcon mimeType={documentVersion.mimeType} />
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
                  startIcon={<OpenWith />}
                >
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
        <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 4 : 12} alignContent="flex-start">
          <Grid item xs={12}>
            <FormField
              type="tags"
              name="tags"
              tags={tags}
            />
          </Grid>
          <Grid item xs={12}>
            <FormField type="multilineText" name="description" label="Description" />
          </Grid>
          {Boolean(values.removed) && (
          <Grid item xs={12} className="pb-2">
            <Typography variant="body2" className={clsx("d-flex align-items-baseline mb-2", classes.textInfo)}>
              <span>
                This document will be permanently deleted after
                { ' ' }
                { format(addDays(new Date(values.modifiedOn), 30), D_MMM_YYYY) }
              </span>
            </Typography>
            <Button variant="outlined" size="medium" color="secondary" onClick={restoreDocument}>
              RESTORE
            </Button>
          </Grid>
          )}
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12} className="mb-3">
          <EntityChecklists
            className="mb-3"
            entity="Document"
            form={form}
            entityId={values.id}
            checked={values.tags}
          />

          <div className="heading mb-2">
            History
          </div>
          <div>
            {Boolean(values.versions)
              && values.versions.map((version, index) => (
                <DocumentVersion
                  key={"key_" + version.id || `new${index}`}
                  classes={classes}
                  version={version}
                  index={index}
                  dispatch={dispatch}
                  form={form}
                  showConfirm={showConfirm}
                  onCurrentChange={(e, v) => onCurrentChange(e, v, index)}
                  hasOneVersion={hasOneVersion}
                />
            ))}
          </div>
          <input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />
          <Button variant="outlined" size="medium" color="secondary" onClick={onUploadClick}>
            UPLOAD NEW VERSION
          </Button>
        </Grid>

        <Grid item xs={twoColumn ? 8 : 12} className="pt-2 pb-2 saveButtonTableOffset">
          <DocumentShare
            validUrl={validUrl}
            dispatch={dispatch}
            documentSource={values}
            form={form}
          />
        </Grid>
      </Grid>
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Document"]
});

export default connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(DocumentGeneralTab));