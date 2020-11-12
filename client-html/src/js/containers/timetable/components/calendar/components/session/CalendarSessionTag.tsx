import React from "react";
import { Theme } from "@material-ui/core";
import withStyles from "@material-ui/core/styles/withStyles";

const styles = (theme: Theme) => ({
  "@global": {
    ".tagColorDot": {
      isolate: false,
      width: theme.spacing(2),
      height: theme.spacing(2),
      borderRadius: "100%",
      marginRight: theme.spacing(0.5),
      transition: theme.transitions.create("transform", {
        duration: theme.transitions.duration.shorter,
        easing: theme.transitions.easing.easeInOut
      })
    }
  },
  tagName: {
    transition: theme.transitions.create("width", {
      duration: theme.transitions.duration.shorter,
      easing: theme.transitions.easing.easeInOut
    }),
    whiteSpace: "nowrap",
    overflow: "hidden",
    minWidth: "0px"
  }
});

interface SessionTagProps {
  classes?: any;
  color: string;
  tagsExpanded: boolean;
  name: string;
}

const CalendarSessionTag: React.FC<SessionTagProps> = ({
  classes, color, tagsExpanded, name
}) => (
  <span className="d-inline-flex align-items-center mr-1">
    <span className="tagColorDot" style={{ background: color }} />
    <span className={classes.tagName} style={{ width: tagsExpanded ? "calc(100% - 20px)" : "0px" }}>
      #
      {name}
    </span>
  </span>
);

export default withStyles(styles as any)(CalendarSessionTag);
