/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import clsx from "clsx";
import { useDropzone } from "react-dropzone";
import { createStyles, withStyles } from "@mui/styles";
import { darken } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Dialog from "@mui/material/Dialog";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import Button from "@mui/material/Button";
import { AppTheme } from "../../../model/common/Theme";

const styles = createStyles((theme: AppTheme) => ({
  fileContainer: {
    border: `2px dotted ${theme.palette.primary.main}`,
    borderRadius: 6,
    transition: theme.transitions.create("background-color", {
      duration: theme.transitions.duration.shorter,
      easing: theme.transitions.easing.easeInOut
    })
  },
  dragActive: {
    backgroundColor: darken(theme.palette.background.default, 0.03),
    boxShadow: theme.shadows[2]
  },
  fullScreenFileDropContainer: {
    zIndex: 1300,
    left: 0,
    top: 0,
    position: "fixed"
  },
  boxDropContainer: {
    zIndex: 1400
  },
  dialogCloseButton: {
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[100]
  }
}));

interface Props {
  fileRef?: any;
  classes?: any;
  opened?: boolean;
  backdropEnabled?: boolean;
  dialog?: boolean;
  onChange?: (acceptedfiles: any[], fileRejections: any[]) => void;
  disabled?: boolean;
  accept?: string | string[];
  onClose?: () => void;
  isBackdropDragging?: boolean;
}

const FileUploaderDialog: React.FC<Props> = (props => {
  const {
    fileRef, accept, disabled, classes, dialog, onChange, onClose, opened, backdropEnabled, isBackdropDragging
  } = props;

  const onFileDrop = (acceptedfiles, fileRejections) => {
    if (onChange) onChange(acceptedfiles, fileRejections);
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: onFileDrop,
    multiple: false,
    disabled,
    accept
  } as any);

  const acceptText = () => {
    const text = [];
    if (Array.isArray(accept)) {
      accept.forEach(type => {
        const typePart = type.split('/');
        if (typePart.length > 1) text.push(`*.${typePart[1]}`);
      });
    } else {
      const typePart = accept.split('/');
      if (typePart.length > 1) text.push(`*.${typePart[1]}`);
    }
    return text.join(", ");
  };
  const backdropUploaderContent = (
    <div {...getRootProps()}  className={clsx("w-100 h-100", classes.fullScreenFileDropContainer)}>
      <div className="h-100 outline-none overflow-hidden cursor-pointer">
        <div className="h-100 p-1">
          <input {...getInputProps()} />
        </div>
      </div>
    </div>
  );

  const uploadContent = (
    <div {...getRootProps()}  className={clsx("w-100 overflow-y-auto relative", classes.boxDropContainer, { "zIndex2": isBackdropDragging })}>
      <div className="h-100 outline-none overflow-hidden cursor-pointer">
        <div className="h-100 p-1">
          <input {...getInputProps()} />
          <div
            className={clsx("w-100 h-100 flex-column align-items-center justify-content-center text-center p-2 pb-3",
              classes.fileContainer,
              { [classes.dragActive]: isDragActive })}
          >
            <Typography variant="subtitle1" gutterBottom className="mb-0">Drop files here</Typography>
            <Typography variant="caption" className="mb-1">or</Typography>
            <Button
              variant="contained"
              size="small"
              color="primary"
              className="mb-1 fontWeight-normal"
            >
              Browse 
            </Button>
            {accept && (
              <Typography variant="caption" color="error">
                Please upload only:
                {" "}
                {acceptText()}
              </Typography>
            )}
          </div>
        </div>
      </div>
    </div>
  );

  return dialog ? (
    <Dialog
      ref={fileRef}
      open={opened}
      disableEscapeKeyDown
      onClose={onClose}
      classes={{
        paper: "w-100"
      }}
    >
      <div>
        {onClose && (
          <IconButton className={clsx("fixed zIndex1401 p-1", classes.dialogCloseButton)} onClick={onClose}>
            <CloseIcon fontSize="small" />
          </IconButton>
        )}
        {uploadContent}
        {isBackdropDragging && backdropEnabled && backdropUploaderContent}
      </div>
    </Dialog>
  ) : backdropEnabled ? backdropUploaderContent : uploadContent;
});

export default withStyles(styles)(FileUploaderDialog);
