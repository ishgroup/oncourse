import Check from '@mui/icons-material/Check';
import { Typography } from '@mui/material';
import { green } from '@mui/material/colors';
import clsx from 'clsx';
import { AppTheme } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import Marker from './Marker';

interface Props {
  classes?: any;
  name: string;
  selected?: boolean;
  letter: string;
  onClick?: () => void;
  className?: string;
}

const styles = (({ palette, spacing }: AppTheme) => ({
  pill: {
    borderRadius: 16,
    color: palette.text.secondary,
    fontSize: "0.7rem",
    padding: spacing(0.25, 0.5),
  },
  active: {
    backgroundColor: palette.divider
  },
  check: {
    color: green[500]
  }
}));

const RadioPill = React.memo(({
 classes, name, selected = false, letter, onClick, className
}: Props) => (
  <Typography
    className={clsx(className, "d-inline-flex-center cursor-pointer", classes.pill, { [classes.active]: selected })}
    onClick={onClick}
    component="div"
  >
    <Marker letter={letter} className="mr-0-5" />
    <Typography noWrap variant="inherit" color="inherit">
      {name}
    </Typography>
    {selected && <Check fontSize="inherit" className={`ml-0-5 ${classes.check}`} />}
  </Typography>
));

export default withStyles(RadioPill, styles);