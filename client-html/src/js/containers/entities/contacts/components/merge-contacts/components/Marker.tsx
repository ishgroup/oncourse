import React from "react";
import { createStyles, withStyles } from "@material-ui/core/styles";
import { Theme } from "@material-ui/core";
import Typography from "@material-ui/core/Typography";

interface Props {
  classes?: any;
  className?: string;
  letter: string;
}

const styles = createStyles(({ palette, spacing }: Theme) => ({
  marker: {
    borderRadius: spacing(1),
    backgroundColor: palette.text.primary,
    width: spacing(2),
    height: spacing(2),
    fontSize: "12px",
    color: palette.getContrastText(palette.text.hint)
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
