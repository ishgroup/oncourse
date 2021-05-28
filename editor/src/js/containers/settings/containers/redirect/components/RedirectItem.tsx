import React from 'react';
import {createStyles, Grid, IconButton, makeStyles} from "@material-ui/core";
import {Delete} from "@material-ui/icons";
import clsx from "clsx";
import {RedirectItem as RedirectItemModel} from "../../../../../model";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

const useStyles = makeStyles(theme =>
  createStyles({
    deleteButton: {
      "&:hover $deleteIcon": {
        fill: "#b1b1b1!important",
      }
    },
    deleteIcon: {
      fontSize: "20px",
      fill: "#e0e0e0!important",
    },
  }),
);

interface RedirectItemState extends RedirectItemModel {
  index?: number;
  submitted?: boolean;
}

interface Props {
  data: {
    items: RedirectItemState[];
    index: number;
    onChange: (e, index, key) => any;
    onRemove: (index) => any;
  };
  index: number;
  style: any;
}

const RedirectItem = (
  {
    style,
    data,
    index,
  }) => {

  const {items, onChange, onRemove} = data;

  const item = items[index];

  const classes = useStyles();

  return (
    <div style={style}>
      <Grid container className="centeredFlex">
        <Grid item xs={5}>
          <EditInPlaceField
            label="From"
            name={`from-${item.index}`}
            id={`from-${item.index}`}
            meta={{
              invalid: (item.submitted && item.to && !item.from) || item.error,
              error: item.error,
            }}
            input={{
              onChange: e => onChange(e, item.index, 'from'),
              onBlur: stubFunction,
              onFocus: stubFunction,
              value: item.from,
            }}
            fullWidth
            noWrap
          />
        </Grid>
        <Grid item xs={5}>
          <EditInPlaceField
            label="To"
            name={`to-${item.index}`}
            id={`to-${item.index}`}
            meta={{
              invalid: (item.submitted && !item.to && item.from) || item.error,
              error: item.error,
            }}
            input={{
              onChange: e => onChange(e, item.index, 'to'),
              onBlur: stubFunction,
              onFocus: stubFunction,
              value: item.to,
            }}
            fullWidth
            noWrap
          />
        </Grid>
        <Grid item xs={2}>
          <IconButton
            className={clsx("ml-2", classes.deleteButton, {
              "invisible": !parent,
              "dndActionIconButton": true
            })}
            onClick={() => onRemove(item.index)}
          >
            <Delete className={classes.deleteIcon} />
          </IconButton>
        </Grid>
      </Grid>
    </div>
  );
};

export default RedirectItem;

