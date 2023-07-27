import Link from "@mui/material/Link";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Tooltip from "@mui/material/Tooltip";
import { format } from "date-fns";
import React, { useMemo } from "react";
import { Typography } from "@mui/material";
import { Check, Clear } from "@mui/icons-material";
import clsx from "clsx";
import { III_DD_MMM_YYYY_HH_MM } from  "ish-ui";
import Typography from "@mui/material/Typography";

const styles = theme => createStyles({
  icon: {
    position: "relative",
    top: "0.3rem",
    marginRight: theme.spacing(1),
    fontSize: "1.2rem"
  },
  auditWrapper: {
    flex: "auto"
  }
});

const ScriptRunAudit = React.memo<any>(props => {
  const {
    lastRun, selectedScriptAudits, scriptIdSelected, classes
  } = props;

  const lastScriptAudits = useMemo(
    () => [...selectedScriptAudits]
      .reverse()
      .map((a, ind) => {
        const runTime = format(new Date(a.runDate), III_DD_MMM_YYYY_HH_MM);
        switch (a.action) {
          case "SCRIPT_EXECUTED": {
            return <Tooltip title={`Succeeded at ${runTime}`}><span><Check
              className={clsx(classes.icon, "successColor")} key={ind}/></span></Tooltip>;
          }
          case "SCRIPT_FAILED": {
            return <Tooltip title={`Failed at ${runTime}`}><span><Clear className={clsx(classes.icon, "errorColor")}
                                                                        key={ind}/></span></Tooltip>;
          }
          default: {
            return null;
          }
        }
      }), [selectedScriptAudits]
  );

  return (
    <div className={classes.auditWrapper}>
      <Typography variant="caption">{`Last run ${lastRun}`}</Typography>
      <div>
        <Typography variant="caption">
          {lastScriptAudits}
          {' '}
          <Link
            href={`${window.location.origin}/audit?search=entityId is ${scriptIdSelected} and entityIdentifier is "Script"`}
            target="_blank"
            color="textSecondary"
            underline="none"
          >
            <span>more...</span>
          </Link>
        </Typography>
      </div>
    </div>
  );
});

export default withStyles(styles)(ScriptRunAudit);
