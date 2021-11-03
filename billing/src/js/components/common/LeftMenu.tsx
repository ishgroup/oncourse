/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import Grid from '@mui/material/Grid';
import { makeAppStyles } from '../../styles/makeStyles';
import onCourseLogoDark from '../../../images/onCourseLogoDark.png';

const useStyles = makeAppStyles()((theme) => ({
  root: {
    width: '250px',
    height: '100vh',
    padding: theme.spacing(4),
    backgroundColor: theme.tabList.listContainer.backgroundColor,
    position: 'fixed',
    left: 0,
    top: 0,
    bottom: 0,
  },
  listContainer: {
    flexDirection: 'column',
    flex: 1,
    textAlign: 'center',
    '& > img': {
      maxWidth: 160,
      position: 'relative',
      left: -5
    },
    display: 'flex',
    height: '100%'
  }
}));

const LeftMenu = ((
  {
    children
  }
) => {
  const { classes, cx } = useStyles();

  return (
    <Grid container className={classes.root}>
      <div className={cx('relative',
        classes.listContainer)}
      >
        <img
          src={onCourseLogoDark}
          alt="Logo"
        />
        {children}
      </div>
    </Grid>
  );
});

export default LeftMenu;
