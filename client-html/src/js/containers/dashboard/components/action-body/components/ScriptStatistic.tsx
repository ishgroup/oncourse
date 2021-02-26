/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { createRef, useEffect, useState } from "react";
import { DataResponse } from "@api/model";
import {
 Grid, Link, List, ListItem, Typography, Tooltip
} from "@material-ui/core";
import clsx from "clsx";
import {
  differenceInHours,
  differenceInMinutes,
  format
} from "date-fns";
import { Check, Clear } from "@material-ui/icons";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import EntityService from "../../../../../common/services/EntityService";
import { III_DD_MMM_YYYY_HH_MM } from "../../../../../common/utils/dates/format";
import { openInternalLink } from "../../../../../common/utils/links";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
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

  const getScriptsData = () => {
    const today = new Date();

    EntityService.getPlainRecords(
      "Script",
      "name",
      'enabled is true',
      null,
      null,
      "name",
      true
    )
      .then(async (scriptRes: DataResponse) => {
        const resultForRender = [];

        await scriptRes.rows.map(scriptRow => () =>
          EntityService.getPlainRecords(
            "Audit",
            "entityId,created,action",
            `entityIdentifier is "Script" and entityId is ${scriptRow.id}
            and ( action is SCRIPT_FAILED or action is SCRIPT_EXECUTED) `,
            7,
            0,
            'created',
            false
          ).then(auditRes => {
            if (!auditRes.rows.length || !auditRes.rows.some(r => differenceInHours(today, new Date(r.values[1])) <= 24)) {
              return;
            }
            const result = auditRes.rows.map(row => ({
              id: row.values[0],
              date: row.values[1],
              status: row.values[2],
            }));
            resultForRender.push({ name: scriptRow.values[0], result });
          })
          .catch(err => instantFetchErrorHandler(dispatch, err))).reduce(async (a, b) => {
          await a;
          await b();
        }, Promise.resolve());

        resultForRender.sort((a, b) => (new Date(a.result[0].date) < new Date(b.result[0].date) ? 1 : -1));
        setScripts(resultForRender);
      })
      .catch(err => instantFetchErrorHandler(dispatch, err));
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
            <Grid container wrap="nowrap">
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
