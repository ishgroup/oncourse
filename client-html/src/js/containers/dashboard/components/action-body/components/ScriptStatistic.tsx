/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import { DataResponse } from "@api/model";
import {
 Grid, Link, List, ListItem, Typography, Tooltip
} from "@material-ui/core";
import clsx from "clsx";
import {
  differenceInHours, differenceInMinutes, differenceInSeconds, format, parseISO
} from "date-fns";
import { Check, Clear } from "@material-ui/icons";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import EntityService from "../../../../../common/services/EntityService";
import { III_DD_MMM_YYYY_HH_MM } from "../../../../../common/utils/dates/format";
import { openInternalLink } from "../../../../../common/utils/links";

const styles = theme => createStyles({
  statisticGroup: {
    padding: "0 8px 0"
  },
  containerStatisticGroup: {
    width: "calc(100% + 16px)",
    margin: "-8px"
  },
  displayBlock: {
    display: "block",
  },
  doneIcon: {
    position: "relative",
    top: "0.3rem",
    marginRight: theme.spacing(1),
    fontSize: "1.2rem",
    color: "green"
  },
  failedIcon: {
    position: "relative",
    top: "0.3rem",
    marginRight: theme.spacing(1),
    fontSize: "1.2rem",
    color: "red"
  },
  smallScriptGroup: {
    display: "flex",
    alignItems: "baseline",
    padding: "0",
    height: "18px",
  },
  smallScriptText: {
    fontSize: "12px",
    marginRight: "12px",
    minWidth: "50%",
  },
  lastRunText: {
    width: "70px",
    minWidth: "70px",
    fontSize: "12px",
    marginLeft: theme.spacing(1),
  }
});

const ScriptStatistic = (props: any) => {
  const [scripts, setScripts] = useState({});

  const { classes } = props;

  const getScriptsData = () => {
    EntityService.getPlainRecords(
      "Script",
      "name",
      'enabled is true',
      null,
      null,
      "name",
      true
    )
      .then((response: DataResponse) => {
        const scripts = [...response.rows];
        const enabledScripts = response.rows.map(elem => elem.id);

        EntityService.getPlainRecords(
          "Audit",
          "entityId,created,action",
          `entityIdentifier is "Script" and entityId in (${enabledScripts.toString()}) 
            and ( action is SCRIPT_FAILED or action is SCRIPT_EXECUTED)`,
          null,
          0,
          'created',
          false
        ).then((response: DataResponse) => {
          const result = {};

          response.rows.forEach(elem => {
            const arrayWithValues = elem.values;

            const elemForArray = {
              id: arrayWithValues[0],
              date: arrayWithValues[1],
              status: arrayWithValues[2],
            };

            result[arrayWithValues[0]] = result[arrayWithValues[0]] ? [...result[arrayWithValues[0]], elemForArray] : [elemForArray];
          });

          const scriptsWithName = {};

          Object.keys(result).forEach((key: string) => {
            const script = scripts.find((elem: any) => key === elem.id);
            scriptsWithName[script.values[0]] = [...result[key].slice(0, 7)];
          });

          const sortable = [];
          const currentDate = new Date();
          for (const script in scriptsWithName) {
            if (differenceInHours(currentDate, parseISO(scriptsWithName[script][0].date)) <= 24) {
              sortable.push([script, scriptsWithName[script][0].date]);
            }
          }

          sortable.sort((a, b) => differenceInSeconds(
            parseISO(a[1]),
            parseISO(b[1]),
          ));

          const resultForRender = sortable.reverse().reduce((result, elem) => (
            { ...result, [elem[0]]: [...scriptsWithName[elem[0]]] }), {});

          setScripts(resultForRender);
        });
      });
  };

  useEffect(() => {
    getScriptsData();

    const interval = setInterval(getScriptsData, 60000);

    return () => clearInterval(interval);
  }, []);

  const getTime = (date: any) => {
    const currentDate = new Date();

    const minutes = differenceInMinutes(currentDate, parseISO(date));

    if (minutes < 60) {
      return minutes + 'm ago';
    }
      return Math.floor(minutes / 60) + 'h ago';
  };

  return (
    <List dense disablePadding>
      <Grid container className={clsx(classes.containerStatisticGroup, classes.displayBlock)}>
        {Object.keys(scripts).map((key: string) => (
          <Grid item className={classes.statisticGroup}>
            <ListItem className={classes.smallScriptGroup} dense disableGutters>
              <Grid xs={6}>
                <Typography
                  onClick={() => openInternalLink(`/automation/script/${scripts[key][0].id}`)}
                  className={clsx(classes.smallText, "linkDecoration", classes.leftColumn, classes.smallScriptText)}
                  noWrap
                >
                  {key}
                </Typography>
              </Grid>
              <Grid className={classes.smallScriptGroup} xs={6}>
                <Typography className={classes.lastRunText}>
                  {getTime(scripts[key][0].date)}
                </Typography>
                {scripts[key] && [...scripts[key]].reverse().map((elem: any) => (
                  elem.status === "SCRIPT_EXECUTED"
                  ? (
                    <Tooltip title={`Succeeded at ${format(new Date(elem.date), III_DD_MMM_YYYY_HH_MM)}`}>
                      <Check className={classes.doneIcon} />
                    </Tooltip>
                  )
                  : (
                    <Tooltip title={`Failed at ${format(new Date(elem.date), III_DD_MMM_YYYY_HH_MM)}`}>
                      <Clear className={classes.failedIcon} />
                    </Tooltip>
                  )
              ))}
                <Link
                  href={`${window.location.origin}/audit?search=entityId is ${scripts[key][0].id} and entityIdentifier is "Script"`}
                  target="_blank"
                  color="textSecondary"
                  underline="none"
                >
                  <Tooltip title="more...">
                    <span> ...</span>
                  </Tooltip>
                </Link>
              </Grid>
            </ListItem>
          </Grid>
        ))}
      </Grid>
    </List>
  );
};

export default (withStyles(styles)(ScriptStatistic));