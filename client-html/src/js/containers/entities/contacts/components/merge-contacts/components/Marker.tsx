import Typography from '@mui/material/Typography';
import { AppTheme } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';

interface Props {
  classes?: any;
  className?: string;
  letter: string;
}

const styles = ((theme: AppTheme) => ({
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

export default withStyles(Marker, styles);
