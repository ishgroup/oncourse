import React from "react";
import { createStyles, withStyles } from "@mui/styles";
import Typography from "@mui/material/Typography";
import { AppTheme } from "../../../../../../../ish-ui/model/Theme";

interface Props {
  classes?: any;
  className?: string;
  letter: string;
}

const styles = createStyles((theme: AppTheme) => ({
  marker: {
    borderRadius: theme.spacing(1),
    backgroundColor: theme.palette.text.primary,
    width: theme.spacing(2),
    height: theme.spacing(2),
    fontSize: "12px",
    color: theme.palette.getContrastText(theme.palette.text.disabled)
  }
}));

const Marker = React.memo(({ classes, className, letter }: Props) => (
  <Typography
    className={`d-inline-flex-center justify-content-center text-uppercase text-bold ${
      className ? `${classes.marker} ${className}` : classes.marker
    }`}
    display="block"
  >
    {letter}
  </Typography>
));

export default withStyles(styles)(Marker);
