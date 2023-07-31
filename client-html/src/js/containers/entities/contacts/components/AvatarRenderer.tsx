/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useRef } from "react";
import { change } from "redux-form";
import FormHelperText from "@mui/material/FormHelperText";
import Avatar from "@mui/material/Avatar";
import DeleteIcon from '@mui/icons-material/Delete';
import UploadIcon from '@mui/icons-material/Upload';
import Gravatar from "react-awesome-gravatar";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import Grid from "@mui/material/Grid";
import clsx from "clsx";
import noAvatarImg from "../../../../../images/no_pic.png";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { FilePreview } from "ish-ui";
import { createAvatarDocument } from "../../../../common/components/form/documents/actions";
import { showMessage } from "../../../../common/actions";
import { makeAppStyles } from  "ish-ui";
import { getInitialDocument } from "../../../../common/utils/documents";

const validateImageFormat = (imageFile: File) =>
  (["image/jpeg", "image/png"].includes(imageFile.type) ? undefined : "Avatar must be of image type");

const useStyles = makeAppStyles(theme => ({
    avatarWrapper: {
      "&, & img": {
        transition: theme.transitions.create("all", {
          duration: theme.transitions.duration.standard,
          easing: theme.transitions.easing.easeInOut
        }),
      },
    },
    profileThumbnail: {
      "&:hover $avatarBackdrop": {
        opacity: 1,
      },
    },
    avatarBackdrop: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      background: theme.palette.action.active,
      position: "absolute",
      height: "100%",
      width: "100%",
      opacity: 0,
      transition: theme.transitions.create("opacity", {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
      borderRadius: "100%",
      color: "#fff",
    },
    avatarRoot: {
      transition: theme.transitions.create("all", {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
    },
  }));

const AvatarRenderer: React.FC<any> = props => {
  const {
    meta: { invalid, error },
    email,
    input,
    form,
    dispatch,
    disabled,
    avatarSize,
    twoColumn,
  } = props;

  const classes = useStyles();

  const fileRef = useRef<any>();

  const handleGravatarError = useCallback(e => {
    const img = e.currentTarget;
    img.setAttribute("src", noAvatarImg);
    img.onerror = null;
  }, []);

  const unlink = useCallback(() => {
    dispatch(change(form, input.name, null));
  }, []);

  const upload = useCallback(() => {
    fileRef?.current?.click();
  }, []);

  const handleFileSelect = () => {
    const file = fileRef?.current?.files[0];

    if (file) {
      const error = validateImageFormat(file);

      if (error) {
        dispatch(showMessage({ message: error }));
        fileRef.current.value = null;
        return;
      }

      DocumentsService.searchDocument(file).then(res => {
        if (res) {
          dispatch(change(form, input.name, res));
        } else {
          getInitialDocument(file).then(document => {
            dispatch(createAvatarDocument(document, form, input.name));
          });
        }
      });
    }
  };

  const size = avatarSize || 90;

  return (
    <Grid item className="mr-3">
      {!disabled && (<input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />)}
      <div className={`centeredFlex justify-content-start ${!twoColumn && "mb-2"} ${classes.avatarWrapper}`}>
        {input.value && input.value.thumbnail ? (
          <FilePreview
            actions={[
                  { actionLabel: "Unlink", onAction: unlink, icon: <DeleteIcon fontSize="small" /> }
                ]}
            data={input.value.thumbnail}
            iconPlacementRow
            avatarSize={size}
            disabled={disabled}
          />
            ) : (
              <div onClick={upload} className={clsx(classes.profileThumbnail, "relative", !disabled && "cursor-pointer")}>
                {!disabled && (
                  <div className={classes.avatarBackdrop}>
                    <div className="centeredFlex">
                      <Tooltip title="Upload" placement="top">
                        <IconButton color="inherit" size="medium">
                          <UploadIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </div>
                  </div>
                )}
                <Gravatar email={email || ""} options={{ size, default: 'mp' }}>
                  {url => (
                    <Avatar
                      alt="Profile picture"
                      src={url}
                      sx={{ width: size, height: size }}
                      onError={handleGravatarError}
                      classes={{ root: classes.avatarRoot }}
                    />
                  )}
                </Gravatar>
              </div>
            )}
      </div>

      {!disabled && error && (
        <FormHelperText error={invalid}>
          <div style={{ maxWidth: "120px", padding: "0 10px 10px 0" }}>{typeof error === "string" ? error : ""}</div>
        </FormHelperText>
          )}
    </Grid>
  );
};

export default AvatarRenderer;
