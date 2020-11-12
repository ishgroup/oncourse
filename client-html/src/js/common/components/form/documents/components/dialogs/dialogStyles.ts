import createStyles from "@material-ui/core/styles/createStyles";

export const dialogStyles = theme =>
  createStyles({
    paper: {
      background: "transparent",
      boxShadow: "none",
      padding: theme.spacing(5),
      margin: "unset"
    },
    contentWrapper: {
      padding: theme.spacing(1, 2),
      background: theme.palette.background.paper,
      boxShadow: `${theme.shadows[20]}`,
      borderRadius: `${theme.shape.borderRadius}px`
    },
    container: {
      width: "500px"
    },
    actions: {
      margin: "6px 0",
      padding: 0
    },
    documentLoading: {
      left: 0,
      bottom: 0,
      width: "100%",
      position: "absolute",
      borderBottomRightRadius: `${theme.shape.borderRadius}px`,
      borderBottomLeftRadius: `${theme.shape.borderRadius}px`
    },
    selectContainer: {
      minWidth: "500px",
      position: "relative",
      zIndex: 1400
    },
    closeAndClearButton: {
      position: "relative",
      zIndex: 1400
    }
  });
