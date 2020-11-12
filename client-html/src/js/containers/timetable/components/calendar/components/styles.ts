import { Theme } from "@material-ui/core";
import createStyles from "@material-ui/styles/createStyles";

const styles = (theme: Theme) =>
  createStyles({
    gapDayWrapper: {
      "& > $gapPeriodOffsetTop:first-child": {
        marginTop: 0
      }
    },
    gapDayOffsetTop: {
      paddingTop: "3px"
    },
    gapPeriodOffsetTop: {
      marginTop: theme.spacing(2)
    },
    stickyIcon: {
      position: "sticky",
      top: 35,
      "& path": {
        fill: `${theme.palette.text.primary}`
      }
    },
    root: {
      display: "grid"
    },
    sessions: {
      display: "grid",
      marginLeft: "32px",
      marginTop: "8px",
      gridRowGap: "8px"
    },
    codeLine: {
      display: "grid",
      gridAutoFlow: "column",
      gridColumnGap: "16px",
      justifyContent: "start",
      alignItems: "center"
    },
    expandButton: {
      padding: 0
    },
    expandIcon: {
      transition: "transform 200ms ease-in-out"
    },
    rotateIcon: {
      transform: "rotate(180deg)"
    }
  });

export default styles;
