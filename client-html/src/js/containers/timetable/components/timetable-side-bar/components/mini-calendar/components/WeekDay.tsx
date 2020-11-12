import React from "react";
import { Button, Typography } from "@material-ui/core";
import { createStyles, withStyles } from "@material-ui/styles";
import clsx from "clsx";

interface Props {
  name: string;
  selected: boolean;
  click: (id) => void;
  id: number;
  classes?: any;
}

const styles = theme =>
  createStyles({
    weekButton: {
      padding: theme.spacing(0.5),
      minWidth: "initial",
      textTransform: "capitalize"
    },
    selectedWeekDay: {
      boxShadow: "none",
      "&:active": {
        boxShadow: "none"
      }
    },
    weekDayText: {
      color: theme.palette.common.white
    }
  });

const WeekDay = ({ classes, name, selected, click, id }: Props) => {
  return (
    <Button
      className={clsx(classes.weekButton, {
        [classes.selectedWeekDay]: selected
      })}
      onClick={() => click(id)}
      variant={selected ? "contained" : "text"}
      color={selected ? "primary" : "default"}
    >
      <Typography variant="caption" align={"center"} className={selected ? classes.weekDayText : ""}>
        {name}
      </Typography>
    </Button>
  );
};

export default withStyles(styles)(WeekDay);
