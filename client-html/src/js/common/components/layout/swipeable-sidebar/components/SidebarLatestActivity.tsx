/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { DASHBOARD_ACTIVITY_STORAGE_NAME } from "../../../../../constants/Config";
import { getEntityDisplayName } from "../../../../utils/getEntityDisplayName";
import ListLinksGroup from "./searchResults/ListLinksGroup";
import { LSGetItem } from "../../../../utils/storage";

const styles = () => createStyles({
  activityStatistic: {
    display: "grid",
    gridTemplateColumns: "min-content 1fr",
    marginTop: "4px"
  }
});

const SidebarLatestActivity: React.FC<any> = props => {
  const { showConfirm, classes, checkSelectedResult } = props;
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
    <Grid container justify="space-between" className="p-2">
      <Grid item>
        <Typography className="heading">Latest activity</Typography>
      </Grid>
      <Grid item xs={12} className={classes.activityStatistic}>
        {activities
          && activities.data.map((v, i) => (
            <ListLinksGroup
              key={i}
              entityDisplayName={getEntityDisplayName(v.entity)}
              entity={v.entity}
              items={v.items}
              showConfirm={showConfirm}
              checkSelectedResult={checkSelectedResult}
            />
          ))}
      </Grid>
    </Grid>
  );
};

export default withStyles(styles)(SidebarLatestActivity);
