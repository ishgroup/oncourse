import * as React from "react";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import clsx from "clsx";
import { iconSwitcher } from "../utils";

const styles = theme =>
  createStyles({
    preview: {
      backgroundSize: "cover",
      width: "36px",
      height: "48px",
      borderRadius: "4px",
      marginRight: "16px",
      color: "rgba(0,0,0,.2)",
      transition: "color 200ms ease-in-out"
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
    },
    wh100: {
      height: "100% !important",
      width: "100% !important"
    }
  });

const DocumentIconsChooser = props => {
  const {
    type, thumbnail, classes, hovered = true, isHeader
  } = props;
  const currentIcon = iconSwitcher(type);
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
      {currentIcon({ classes })}
    </div>
  );
};

export default withStyles(styles)(DocumentIconsChooser);
