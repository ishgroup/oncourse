/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { DASHBOARD_ACTIVITY_STORAGE_NAME } from "../../../../../constants/Config";
import { getEntityDisplayName } from "../../../../utils/getEntityDisplayName";
import ListLinksGroup from "./searchResults/ListLinksGroup";
import { LSGetItem } from "../../../../utils/storage";
import { AppTheme } from "../../../../../../ish-ui/model/Theme";

const styles = (theme: AppTheme) => createStyles({
  activityStatistic: {
    marginTop: 4
  },
  divider: {
    margin: theme.spacing(2, 0, 1.25),
  }
});

const SidebarLatestActivity: React.FC<any> = props => {
  const { classes, checkSelectedResult } = props;
  const [activities, setActivities] = React.useState(JSON.parse(LSGetItem(DASHBOARD_ACTIVITY_STORAGE_NAME) || "null"));

  const updateActivity = React.useCallback(() => {
    const activity = JSON.parse(LSGetItem(DASHBOARD_ACTIVITY_STORAGE_NAME) || "null");

    if (activity) {
      setActivities(activity);
    }
  }, []);

  React.useEffect(() => {
    const interval = setInterval(updateActivity, 10000);
    return () => {
      clearInterval(interval);
    };
  }, []);

  return (
    <Grid container columnSpacing={3} className="p-2">
      <Grid item>
        <Typography className="heading">Latest activity</Typography>
      </Grid>
      <Grid item xs={12} className={classes.activityStatistic}>
        {activities
          && activities.data.map((v, i) => (
            <div key={i}>
              <ListLinksGroup
                entityDisplayName={getEntityDisplayName(v.entity)}
                entity={v.entity}
                items={v.items}
                checkSelectedResult={checkSelectedResult}
              />
            </div>
          ))}
      </Grid>
    </Grid>
  );
};

export default withStyles(styles)(SidebarLatestActivity);
