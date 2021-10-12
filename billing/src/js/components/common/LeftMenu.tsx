/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import Grid from '@mui/material/Grid';
import { makeAppStyles } from '../../styles/makeStyles';
import onCourseLogoChristmas from '../../../images/onCourseLogoChristmas.png';
import onCourseLogoDark from '../../../images/onCourseLogoDark.png';

const useStyles = makeAppStyles()((theme, _params, createRef) => ({
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
    }
  },
  listContainerInner: {
    marginBottom: theme.spacing(8),
    paddingTop: '70%',
    paddingLeft: 20,
    textAlign: 'left'
  }
}));

const LeftMenu = ((
  {
    children
  }
) => {
  const isChristmas = localStorage.getItem('theme') === 'christmas';

  const { classes, cx } = useStyles();

  return (
    <Grid container className={classes.root}>
      <div className={cx('relative',
        classes.listContainer,
        localStorage.getItem('theme') === 'christmas' && 'christmasHeader')}
      >
        {isChristmas ? (
          <img src={onCourseLogoChristmas} alt="Logo" />
        ) : (
          <img
            src={onCourseLogoDark}
            alt="Logo"
          />
        )}
        <div className={classes.listContainerInner}>
          {children}
        </div>
      </div>
    </Grid>
  );
});

export default LeftMenu;
