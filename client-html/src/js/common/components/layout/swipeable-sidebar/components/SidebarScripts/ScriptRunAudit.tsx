import { Check, Clear } from '@mui/icons-material';
import Link from '@mui/material/Link';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import { III_DD_MMM_YYYY_HH_MM } from 'ish-ui';
import React, { useMemo } from 'react';
import { withStyles } from 'tss-react/mui';

const styles = theme => ({
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

const ScriptRunAudit = React.memo<{
  lastRun, selectedScriptAudits, scriptIdSelected, classes?
}>(props => {
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
            <span>{$t('more2')}</span>
          </Link>
        </Typography>
      </div>
    </div>
  );
});

export default withStyles(ScriptRunAudit, styles);