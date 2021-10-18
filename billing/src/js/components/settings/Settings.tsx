/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import {
  Switch,
  Route,
  NavLink, useHistory
} from 'react-router-dom';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import { makeAppStyles } from '../../styles/makeStyles';
import LeftMenu from '../common/LeftMenu';
import arrowRight from '../../../images/icon-arrow-right.svg';
import { useAppSelector } from '../../redux/hooks/redux';
import Billing from '../Billing';
import { SitesPage } from '../sites/SitesPage';

export const useStyles = makeAppStyles()((theme, prop, createRef) => {
  const navItem = {
    ref: createRef(),
    position: 'relative',
    color: theme.palette.text.secondary,
    left: 0,
    transition: 'all ease-out 0.2s',
    '&:before': {
      content: "''",
      position: 'absolute',
      width: '16px',
      height: '12px',
      top: '4px',
      left: '-22px',
      opacity: 0,
      backgroundSize: 'contain',
      backgroundImage: `url(${arrowRight})`
    }
  } as const;

  return {
    navItem,
    nav: {
      paddingLeft: 20,
      listStyle: 'none',
      '& > li': {
        marginTop: theme.spacing(1)
      },
      '& a': {
        textDecoration: 'none'
      }
    },
    activeNav: {
      [`& .${navItem.ref}`]: {
        left: '22px',
        color: theme.palette.primary.main,
      },
      [`& .${navItem.ref}:before`]: {
        opacity: 1,
      }
    },
    plusIcon: {
      position: 'absolute',
      top: -6
    },
    formItem: {
      backgroundColor: '#fff',
      padding: '48px',
    }
  };
});

const Settings = () => {
  const { classes } = useStyles();

  const sites = useAppSelector((state) => state.sites);

  const appHistory = useHistory();

  const onAddSite = () => {
    appHistory.push('/websites/new');
  };

  return (
    <div className="root">
      <LeftMenu>
        <nav>
          <ul className={classes.nav}>
            <li>
              <NavLink
                exact
                strict
                to="/"
                activeClassName={classes.activeNav}
              >
                <Typography variant="body2" className={classes.navItem}>
                  Billing
                </Typography>
              </NavLink>
            </li>
            <li>
              <Typography variant="body2" color="textSecondary" className="relative">
                Websites
                <IconButton size="small" className={classes.plusIcon} onClick={onAddSite}>
                  <AddCircleOutlineIcon color="primary" />
                </IconButton>
              </Typography>
              <ul className={classes.nav}>
                {sites.map((s) => (
                  <li key={s.id}>
                    <NavLink
                      exact
                      strict
                      to={`/websites/${s.id}`}
                      activeClassName={classes.activeNav}
                    >
                      <Typography variant="body2" className={classes.navItem}>
                        {s.key}
                      </Typography>
                    </NavLink>
                  </li>
                ))}
              </ul>
            </li>
          </ul>
        </nav>
      </LeftMenu>

      <div className="formWrapper">
        <div className="content">
          <div className="contentInner">
            <div className={classes.formItem}>
              <Switch>
                <Route exact path="/">
                  <Billing />
                </Route>
                <Route path="/websites/:id">
                  <SitesPage />
                </Route>
              </Switch>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Settings;
