/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import { useDropzone } from "react-dropzone";
import { createStyles, darken, withStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import Dialog from "@material-ui/core/Dialog";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import { AppTheme } from "../../../model/common/Theme";
import Button from "../buttons/Button";

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

  const onFileDrop = React.useCallback((acceptedfiles, fileRejections) => {
    if (onChange) onChange(acceptedfiles, fileRejections);
  }, []);

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
  const backdropUploaderContent = () => (
    <div className={clsx("w-100 h-100", classes.fullScreenFileDropContainer)}>
      <div {...getRootProps()} className="h-100 outline-none overflow-hidden cursor-pointer">
        <div className="h-100 p-1">
          <input {...getInputProps()} />
        </div>
      </div>
    </div>
  );

  const uploadContent = () => (
    <div className={clsx("w-100 overflow-y-auto relative", classes.boxDropContainer, { "zIndex2": isBackdropDragging })}>
      <div {...getRootProps()} className="h-100 outline-none overflow-hidden cursor-pointer">
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
              size="small"
              text="Browse"
              color="primary"
              className="mb-1 fontWeight-normal"
            />
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
        {uploadContent()}
        {isBackdropDragging && backdropEnabled && backdropUploaderContent()}
      </div>
    </Dialog>
  ) : backdropEnabled ? backdropUploaderContent() : uploadContent();
});

export default withStyles(styles)(FileUploaderDialog);
