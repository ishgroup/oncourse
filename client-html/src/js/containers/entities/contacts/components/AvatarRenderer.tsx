/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useRef } from "react";
import { change } from "redux-form";
import FormHelperText from "@mui/material/FormHelperText";
import Edit from "@mui/icons-material/Edit";
import Avatar from "@mui/material/Avatar";
import DeleteIcon from '@mui/icons-material/Delete';
import Gravatar from "react-awesome-gravatar";
import noAvatarImg from "../../../../../images/no_pic.png";
import FilePreview from "../../../../common/components/form/FilePreview";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import { getInitialDocument } from "../../../../common/components/form/documents/components/utils";
import { createAvatarDocument } from "../../../../common/components/form/documents/actions";
import { showMessage } from "../../../../common/actions";

const validateImageFormat = (imageFile: File) =>
  (["image/jpeg", "image/png"].includes(imageFile.type) ? undefined : "Avatar must be of image type");

const AvatarRenderer: React.FC<any> = props => {
  const {
    classes,
    meta: { invalid, error },
    email,
    input,
    form,
    dispatch,
    disabled,
    avatarSize,
  } = props;

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
    fileRef.current.click();
  }, []);

  const handleFileSelect = useCallback(() => {
    const file = fileRef.current.files[0];

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
  }, [form, input.name]);

  const size = avatarSize || 90;

  return (
    <div>
      {!disabled && (<input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />)}

      <div className={`centeredFlex justify-content-start mb-2 ${classes.avatarWrapper}`}>
        {input.value && input.value.thumbnail ? (
          <FilePreview
            actions={[
              { actionLabel: "unlink", onAction: unlink, icon: <DeleteIcon fontSize="small" /> }
            ]}
            data={input.value.thumbnail}
            iconPlacementRow
            avatarSize={size}
            disabled={disabled}
          />
        ) : (
          <div onClick={upload} className={`relative cursor-pointer ${classes.profileThumbnail}`}>
            <Gravatar email={email || ""} options={{ size, default: 'mp' }}>
              {url => (
                <Avatar
                  alt="Profile picture"
                  src={url}
                  sx={{ width: size, height: size }}
                  onError={handleGravatarError}
                />
              )}
            </Gravatar>
            {!disabled && (<Edit className={classes.profileEditIcon} />)}
          </div>
        )}
      </div>

      {!disabled && error && (
        <FormHelperText error={invalid} className={classes.error}>
          <div style={{ maxWidth: "120px", padding: "0 10px 10px 0" }}>{typeof error === "string" ? error : ""}</div>
        </FormHelperText>
      )}
    </div>
  );
};

export default AvatarRenderer;
