import { AppTheme } from 'ish-ui';

const styles = (theme: AppTheme, p, classes) =>
  ({
    checkbox: {
      height: "1em",
      width: "1em",
      marginRight: theme.spacing(0.5),
    },
    checkboxFontSize: {
      fontSize: "18px"
    },
    labelRoot: {
      [`& .${classes.checkboxLabel}`]: {
        fontSize: "12px",
      }
    },
    checkboxLabel: {},
    root: {
      display: "flex",
      alignItems: "center",
      [`&:hover .${classes.deleteButton}`]: {
        visibility: "visible"
      },
      height: theme.spacing(3),
      maxHeight: theme.spacing(3),
      marginLeft: '27px'
    },
    deleteButton: {
      visibility: "hidden",
      fontSize: "20px",
      height: "30px",
      width: "30px",
      padding: `${theme.spacing(0.5)}`
    }
  });

export default styles;