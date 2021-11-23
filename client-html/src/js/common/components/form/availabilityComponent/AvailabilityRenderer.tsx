/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { createStyles } from "@mui/material";
import Grid, { GridSize } from "@mui/material/Grid";
import withStyles from "@mui/styles/withStyles";
import { Holiday } from "@api/model";
import AvailabilityItem from "./AvailabilityItem";

const styles = theme =>
  createStyles({
    hintContainer: {
      background: theme.palette.background.default,
      padding: "5px",
      borderRadius: "2px",
      minHeight: "86px",
      marginTop: "20px"
    },
    threeColumnCard: {
      marginBottom: "20px",
      padding: "20px"
    }
  });

const getLayoutArray = (threeColumn: boolean): { [key: string]: GridSize }[] =>
  (threeColumn
    ? [
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 }
      ]
    : [
        { xs: 12 },
        { xs: 9 },
        { xs: 12 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 3 }
      ]);

const AvailabilityRenderer = ({
 fields, classes, onDelete, threeColumn, dispatch, meta, timezone
}) => {
  const availabilityLayout = getLayoutArray(threeColumn);

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={availabilityLayout[0].xs}>
        {fields.map((item, index) => {
          const field: Holiday = fields.get(index);

          return (
            <AvailabilityItem
              key={field.id || item}
              index={index}
              item={item}
              onDelete={onDelete}
              field={field}
              fieldName={fields.name}
              formName={meta.form}
              threeColumn={threeColumn}
              classes={classes}
              dispatch={dispatch}
              availabilityLayout={availabilityLayout}
              timezone={timezone}
            />
          );
        })}
      </Grid>
    </Grid>
  );
};

export default withStyles(styles)(AvailabilityRenderer) as any;
