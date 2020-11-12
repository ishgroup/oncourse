import * as React from "react";
import { Resizable } from "re-resizable";
import { withStyles, createStyles } from "@material-ui/core/styles";
import withWidth, { isWidthUp } from "@material-ui/core/withWidth";
import { Breakpoint } from "@material-ui/core/styles/createBreakpoints";
import clsx from "clsx";
import { ListSideBarDefaultWidth } from "../../list-view/ListView";

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
  width?: Breakpoint;
  onResizeStop?: any;
  onResize?: any;
  classes?: any;
  className?: any;
  ignoreScreenWidth?: boolean;
}

class ResizableWrapper extends React.PureComponent<Props, any> {
  line = () => <div className={this.props.classes.line} />;

  render() {
    const {
      classes,
      onResizeStop,
      onResize,
      sidebarWidth,
      width,
      minWidth,
      maxWidth,
      children,
      className,
      ignoreScreenWidth
    } = this.props;

    return isWidthUp("md", width) || ignoreScreenWidth ? (
      <Resizable
        size={{ width: sidebarWidth, height: "100%" }}
        minWidth={minWidth || ListSideBarDefaultWidth}
        maxWidth={maxWidth}
        onResizeStop={onResizeStop}
        onResize={onResize}
        enable={{ right: true }}
        className={clsx(classes.sideBar, className)}
        handleClasses={{ right: classes.resizeLine }}
        handleComponent={{ right: <this.line /> }}
      >
        <div className={classes.sideBarWrapper}>{children}</div>
      </Resizable>
    ) : (
      children
    );
  }
}

const wrapped: any = withWidth()(withStyles(styles)(ResizableWrapper) as any);

export default wrapped as React.ComponentClass<Props>;
