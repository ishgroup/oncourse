import React, { useCallback } from "react";
import { withRouter } from "react-router-dom";
import Grid from "@mui/material/Grid";
import {
 Card, CardActions, CardContent, CardMedia 
} from "@mui/material";
import { withStyles } from "@mui/styles";
import Button from "@mui/material/Button";
import IntegrationTypes from "./IntegrationTypes";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";
import IntegrationDescription from "./components/IntegrationDescription";

const styles = theme => ({
  image: {
    maxWidth: "200px",
    minWidth: 0,
    padding: theme.spacing(1, 0, 1, 2),
    backgroundSize: "contain",
    "object-fit": "contain"
  }
});

const Integrations = React.memo<any>(({
   classes, history
  }) => {
  const handleClick = useCallback(
    type => {
      history.push(`/automation/integration/${type}/new`);
    },
    [history]
  );

  return (
    <AppBarContainer
      disableInteraction
      hideSubmitButton
      hideHelpMenu
      noDrawer
      title="Integrations"
    >
      <Grid container spacing={5}>
        {Object.keys(IntegrationTypes).map(key => (
          <Grid item xs={12} lg={6} key={key}>
            <Card className="flex-row h-100">
              <CardMedia
                component="img"
                className={classes.image}
                image={IntegrationTypes[key].image}
                alt={IntegrationTypes[key].name}
              />
              <div className="flex-column justify-content-end flex-fill">
                <CardContent className="flex-fill">
                  <IntegrationDescription item={IntegrationTypes[key]} />
                </CardContent>
                <CardActions className="justify-content-end">
                  <Button
                    onClick={() => handleClick(key)}
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
        ))}
      </Grid>
    </AppBarContainer>
  );
});

export default (withStyles(styles)(withRouter(Integrations)));
