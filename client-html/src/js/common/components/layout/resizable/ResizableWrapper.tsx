import * as React from "react";
import { Resizable } from "re-resizable";
import { withStyles, createStyles } from "@mui/styles";
import useMediaQuery from '@mui/material/useMediaQuery';
import clsx from "clsx";
import { LIST_SIDE_BAR_DEFAULT_WIDTH } from "../../../../constants/Config";

const styles = theme =>
  createStyles({
    sideBar: {
      borderRight: `1px solid ${theme.palette.divider}`,
      display: "flex",
      paddingRight: "1px",
      "&.d-none": {
        display: "none"
      }
    },
    sideBarWrapper: {
      overflowX: "hidden",
      overflowY: "auto",
      flex: 1
    },
    line: {
      height: "100%",
      width: "2px",
      transition: "background 120ms ease-in-out"
    },
    resizeLine: {
      zIndex: 1,
      display: "flex",
      justifyContent: "center",
      "&:active $line, &:hover $line": {
        background: `${theme.palette.secondary.main}`,
        overflow: "visible"
      }
    }
  });

interface Props {
  sidebarWidth: number;
  minWidth?: number | string;
  maxWidth?: number | string;
  onResizeStop?: any;
  onResize?: any;
  classes?: any;
  className?: any;
  ignoreScreenWidth?: boolean;
  children?: any;
}

const ResizableWrapper = (props: Props) => {
  const {
    classes,
    onResizeStop,
    onResize,
    sidebarWidth,
    minWidth,
    maxWidth,
    children,
    className,
    ignoreScreenWidth
  } = props;

    return useMediaQuery('(min-width:992px)') || ignoreScreenWidth ? (
      <Resizable
        size={{ width: sidebarWidth, height: "100%" }}
        minWidth={minWidth || LIST_SIDE_BAR_DEFAULT_WIDTH}
        maxWidth={maxWidth}
        onResizeStop={onResizeStop}
        onResize={onResize}
        enable={{ right: true }}
        className={clsx(classes.sideBar, className)}
        handleClasses={{ right: classes.resizeLine }}
        handleComponent={{ right: <div className={props.classes.line} /> }}
      >
        <div className={classes.sideBarWrapper}>{children}</div>
      </Resizable>
    ) : (
      children
    );
};

const wrapped: any = (withStyles(styles)(ResizableWrapper) as any);

export default wrapped as React.FC<Props>;
