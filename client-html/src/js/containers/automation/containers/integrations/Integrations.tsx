import clsx from "clsx";
import React, { useCallback, useMemo } from "react";
import Grid from "@mui/material/Grid";
import { Card, CardActions, CardContent } from "@mui/material";
import { withStyles } from "@mui/styles";
import { withRouter } from "react-router-dom";
import useMediaQuery from '@mui/material/useMediaQuery';
import Button from "@mui/material/Button";
import IntegrationDescription from "./components/IntegrationDescription";

import * as IntegrationTypes from "../../../../model/automation/integrations/IntegrationTypes";
import IntegrationImages from "./IntegrationImages";
import AppBar from "../../../../common/components/layout/AppBar";
import { getByType } from "./utils";

const styles = theme => ({
  content: {
    maxWidth: 400
  },
  imageWrapper: {
    minHeight: "200px"
  },
  image: {
    padding: theme.spacing(1, 0, 1, 2),
    "align-self": "center"
  }
});

const Integrations = React.memo<any>(({
 classes, history, sidebarWidth
}) => {
  const handleClick = useCallback(
    int => {
      history.push(`/automation/integrations/new/${int.type}`);
    },
    [history]
  );

  const types = useMemo(() => Object.keys(IntegrationTypes).map(item => IntegrationTypes[item]), []);

  return (
    <div className="appBarContainer p-3">
      <AppBar title="Integrations" withDrawer sidebarWidth={sidebarWidth} />
      <Grid container columnSpacing={3} spacing={5}>
        {types.map((int, index: number) => (
          <Grid item xs={12} lg={6} key={int.type}>
            <Grid
              container
              className="h-100"
            >
              <Card className="flex-row">
                <div className={clsx("d-flex", classes.imageWrapper)}>
                  <img src={getByType(int.type, IntegrationImages)} width={200} alt="integration" className={classes.image} />
                </div>
                <div className="flex-column">
                  <CardContent className={clsx("flex-fill", classes.content)}>
                    <IntegrationDescription item={int} />
                  </CardContent>
                  <CardActions className="justify-content-end">
                    <Button
                      onClick={() => handleClick(int)}
                      variant="contained"
                      size="small"
                      color="primary"
                      className="integrationsButton"
                    >
                      Add
                    </Button>
                  </CardActions>
                </div>
              </Card>
            </Grid>
          </Grid>
        ))}
      </Grid>
    </div>
  );
});

export default (withStyles(styles)(withRouter(Integrations)));
