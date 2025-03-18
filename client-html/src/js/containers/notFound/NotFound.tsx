/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import Link from '@mui/material/Link';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { AppTheme } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import NotFoundBgImage from '../../../images/bg-404.png';

const styles = (theme: AppTheme) => ({
  container: {
    height: `calc(100vh - ${theme.spacing(14)})`
  },
  bgImage: {
    position: "fixed",
    right: 0,
    bottom: 0,
    maxWidth: 493.5,
    maxHeight: 227,
    opacity: ".7",
    zIndex: -1,
    "& img": {
      width: "100%"
    }
  },
  backLink: {
    position: "relative",
    borderBottom: `2px solid ${theme.palette.secondary.main}`,
    outline: "none",
    textDecoration: "none !important",
    "&::after": {
      content: `""`,
      position: "absolute",
      left: 0,
      right: 0,
      bottom: -5,
      height: 2,
      background: theme.palette.secondary.main
    }
  }
});

const NotFound: React.FC<any> = props => {
  const { classes } = props;

  return (
    <div className={`centeredFlex text-center ${classes.container}`}>
      <div className="w-100">
        <Typography variant="h4" className="mb-3">
          {$t('you_have_gone_offcourse')}
        </Typography>
        <Typography variant="h5">
          {$t('return_to')}
          {" "}
          <Link href="/" color="inherit" className={classes.backLink}>{$t('dashboard')}</Link>
          .
        </Typography>
        <div className={classes.bgImage}>
          <img src={NotFoundBgImage} alt="404-bg-image" />
        </div>
      </div>
    </div>
  );
};

export default withStyles(NotFound, styles);
