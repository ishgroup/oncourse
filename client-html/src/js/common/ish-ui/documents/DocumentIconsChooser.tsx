/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import { FileTypeIcon } from "./utils";

const styles = theme =>
  createStyles({
    preview: {
      backgroundSize: "cover",
      width: "36px",
      height: "48px",
      borderRadius: "4px",
      marginRight: "16px",
      color: "rgba(0,0,0,.2)",
      transition: "color 200ms ease-in-out",
      backgroundPosition: "center"
    },
    previewNormal: {
      width: "60px",
      height: "calc(100% - 16px)"
    },
    previewHoverIcon: {
      "&:hover": {
        color: theme.palette.primary.main
      }
    },
    previewHoverImage: {
      "&:hover": {
        boxShadow: `0 0 1px 1px ${theme.palette.primary.main}`
      }
    }
  });

const DocumentIconsChooser = props => {
  const {
    type, thumbnail, classes, hovered = true, isHeader
  } = props;

  return thumbnail ? (
    <div
      style={{
        backgroundImage: `url(data:image;base64,${thumbnail})`
      }}
      className={clsx(classes.preview, {
        [classes.previewHoverImage]: hovered,
        [classes.previewNormal]: isHeader
      })}
    />
  ) : (
    <div
      className={clsx(classes.preview, {
        [classes.previewHoverIcon]: hovered
      })}
    >
      <FileTypeIcon mimeType={type} />
    </div>
  );
};

export default withStyles(styles)(DocumentIconsChooser);
