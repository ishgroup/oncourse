/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ScriptStatistic } from '@api/model';
import { Check, Clear } from '@mui/icons-material';
import { Grid, Link, List, ListItem, Tooltip, Typography } from '@mui/material';
import $t from '@t';
import { format, formatDistanceStrict } from 'date-fns';
import { III_DD_MMM_YYYY_HH_MM, makeAppStyles } from 'ish-ui';
import React, { createRef, useEffect, useState } from 'react';
import instantFetchErrorHandler from '../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import AnimateList from '../../../../../common/utils/animation/AnimateList';
import ScriptsService from '../../../../automation/containers/scripts/services/ScriptsService';

const styles = theme => ({
  icon: {
    top: "0.3rem",
    marginRight: theme.spacing(1),
    fontSize: "1.2rem",
  },
  smallScriptGroup: {
    display: "flex",
    padding: "0",
    height: "18px",
    fontSize: '12px'
  }
});

const useStyles = makeAppStyles()(styles);

const ScriptStatistic = ({ dispatch }) => {
  const [scripts, setScripts] = useState<ScriptStatistic[]>([]);
  const { classes, cx } = useStyles();
  const today = new Date();

  const getScriptsData = async () => {
    try {
      const statistics = await ScriptsService.getLastRunsStatistic();
      statistics.sort((a, b) => (new Date(a.lastRuns[0]?.date) < new Date(b.lastRuns[0]?.date) ? 1 : -1));
      if (statistics) setScripts(statistics);
    } catch (e) {
      instantFetchErrorHandler(dispatch, e, "Failed to get automation status");
    }
  };

  useEffect(() => {
    getScriptsData();

    const interval = setInterval(getScriptsData, 60000);

    return () => clearInterval(interval);
  }, []);

  const getTime = (date: any) => {
    return formatDistanceStrict(today, new Date(date)) + ' ago ';
  };

  return (
    <List dense disablePadding>
      <AnimateList>
        {scripts.map(script => (
          <ListItem ref={createRef() as any} key={script.name} className={classes.smallScriptGroup} dense disableGutters>
            <Grid container columnSpacing={3} className={classes.smallScriptGroup} wrap="nowrap">
              <Grid item xs={6} className="overflow-hidden">
                <Link
                  href={`/automation/script/${script.id}`}
                  target="_blank"
                  underline='hover'
                  fontSize="inherit"
                  color="textPrimary"
                  className="linkDecoration d-block"
                  noWrap
                >
                  {script.name}
                </Link>
              </Grid>
              <Grid item className={cx(classes.smallScriptGroup, "overflow-hidden")} xs={2}>
                <Typography noWrap color="textSecondary" fontSize="inherit">
                  {getTime(script.lastRuns[0]?.date)}
                </Typography>
              </Grid>
              <Grid item xs={4} display='flex' flexWrap='nowrap'>
                <span className="overflow-hidden">
                  {script.lastRuns.map((elem, index) => (
                    elem.status === "Script executed"
                      ? (
                        <Tooltip key={script.name + index} title={`Succeeded at ${format(new Date(elem.date), III_DD_MMM_YYYY_HH_MM)}`}>
                          <Check className={cx(classes.icon, "successColor")} />
                        </Tooltip>
                      )
                      : (
                        <Tooltip key={script.name + index} title={`Failed at ${format(new Date(elem.date), III_DD_MMM_YYYY_HH_MM)}`}>
                          <Clear className={cx(classes.icon, "errorColor")} />
                        </Tooltip>
                      )
                  ))}
                </span>
                <Link
                  href={`${window.location.origin}/audit?search=entityId is ${script.id} and entityIdentifier is "Script"`}
                  target="_blank"
                  color="textSecondary"
                  underline="none"
                >
                  <Tooltip title={$t('more2')}>
                    <strong> ...</strong>
                  </Tooltip>
                </Link>
              </Grid>
            </Grid>
          </ListItem>
        ))}
      </AnimateList>
    </List>
  );
};

export default ScriptStatistic;
