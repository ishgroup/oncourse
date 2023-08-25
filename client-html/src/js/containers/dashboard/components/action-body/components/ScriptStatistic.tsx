/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Check, Clear } from "@mui/icons-material";
import { Grid, Link, List, ListItem, Tooltip, Typography } from "@mui/material";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import { differenceInHours, differenceInMinutes, format } from "date-fns";
import { III_DD_MMM_YYYY_HH_MM, openInternalLink } from "ish-ui";
import React, { createRef, useEffect, useState } from "react";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import AnimateList from "../../../../../common/utils/animation/AnimateList";

const styles = theme => createStyles({
  icon: {
    top: "0.3rem",
    marginRight: theme.spacing(1),
    fontSize: "1.2rem",
  },
  smallScriptGroup: {
    display: "flex",
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

const ScriptStatistic = ({ dispatch, classes }) => {
  const [scripts, setScripts] = useState([]);

  const getScriptsData = async () => {
    const today = new Date();
    
    try {
      const scriptRes = await EntityService.getPlainRecords(
        "Script",
        "name",
        'automationStatus == ENABLED',
        null,
        null,
        "name",
        true
      );

      if (!Array.isArray(scriptRes?.rows)) return;

      const resultForRender = [];

      for (const scriptRow of scriptRes.rows) {
        const auditRes = await EntityService.getPlainRecords(
          "Audit",
          "entityId,created,action",
          `entityIdentifier is "Script" and entityId is ${scriptRow.id}
            and ( action is SCRIPT_FAILED or action is SCRIPT_EXECUTED) `,
          7,
          0,
          'created',
          false
        );

        if (auditRes.rows.length && auditRes.rows.some(r => differenceInHours(today, new Date(r.values[1])) <= 24)) {
          const result = []; 
          auditRes.rows.forEach(row => {
            if (row.values[1]) {
              result.push({
                id: row.values[0],
                date: row.values[1],
                status: row.values[2],
              });
            }
          });
          resultForRender.push({ name: scriptRow.values[0], result });
        }
      }

      resultForRender.sort((a, b) => (new Date(a.result[0].date) < new Date(b.result[0].date) ? 1 : -1));
      setScripts(resultForRender);
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
    const currentDate = new Date();

    const minutes = differenceInMinutes(currentDate, new Date(date));

    if (minutes < 60) {
      return minutes + 'm ago';
    }
    return Math.floor(minutes / 60) + 'h ago';
  };

  return (
    <List dense disablePadding>
      <AnimateList>
        {scripts.map(script => (
          <ListItem ref={createRef() as any} key={script.name} className={classes.smallScriptGroup} dense disableGutters>
            <Grid container columnSpacing={3} wrap="nowrap">
              <Grid item xs className="overflow-hidden">
                <Typography
                  onClick={() => openInternalLink(`/automation/script/${script.result[0].id}`)}
                  className={clsx(classes.smallText, "linkDecoration", classes.leftColumn, classes.smallScriptText)}
                  noWrap
                >
                  {script.name}
                </Typography>
              </Grid>
              <Grid item className={classes.smallScriptGroup} xs={6}>
                <Typography className={classes.lastRunText} color="textSecondary">
                  {getTime(script.result[0].date)}
                </Typography>
                {script.result.map((elem, index) => (
                  elem.status === "SCRIPT_EXECUTED"
                    ? (
                      <Tooltip key={script.name + index} title={`Succeeded at ${format(new Date(elem.date), III_DD_MMM_YYYY_HH_MM)}`}>
                        <Check className={clsx(classes.icon, "successColor")} />
                      </Tooltip>
                    )
                    : (
                      <Tooltip key={script.name + index} title={`Failed at ${format(new Date(elem.date), III_DD_MMM_YYYY_HH_MM)}`}>
                        <Clear className={clsx(classes.icon, "errorColor")} />
                      </Tooltip>
                    )
                ))}
                <Link
                  href={`${window.location.origin}/audit?search=entityId is ${script.result[0].id} and entityIdentifier is "Script"`}
                  target="_blank"
                  color="textSecondary"
                  underline="none"
                >
                  <Tooltip title="more...">
                    <span> ...</span>
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

export default (withStyles(styles)(ScriptStatistic));
