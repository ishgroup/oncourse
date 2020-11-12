/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useState } from "react";
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import DeleteIcon from "@material-ui/icons/Delete";
import { Note } from "@api/model";
import Paper from "@material-ui/core/Paper";
import Tooltip from "@material-ui/core/Tooltip";
import FormField from "../../form-fields/FormField";
import { formatRelativeDate } from "../../../../utils/dates/formatRelative";
import { DD_MMM_YYYY_AT_HH_MM_A_SPECIAL } from "../../../../utils/dates/format";

interface Props {
  item: Note;
  index: number;
  classes: any;
  messageName: string;
  onDelete: (index: number) => void;
  twoColumn?: any;
}

const NoteItem = (props: Props) => {
  const {
    item, classes, messageName, onDelete, index, twoColumn
  } = props;

  const [checked, setChecked] = useState(false);
  const [iconHovered, setIconHovered] = useState(false);

  return (
    <Grid item xs={twoColumn ? 1 : 12} md={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
      <Paper className="p-1 h-100">
        <Grid container className="h-100" alignContent="space-between">
          <Grid item xs={12}>
            <FormField
              type="text"
              name={messageName}
              onBlur={() => setChecked(!!item.message)}
              required
              fullWidth
              multiline
            />
          </Grid>
          <Grid container justify="space-between" alignItems="flex-end">
            <Grid item>
              {item.created && (
                <Typography className={classes.dateInfo}>
                  Created
                  {' '}
                  {formatRelativeDate(new Date(item.created), new Date(), DD_MMM_YYYY_AT_HH_MM_A_SPECIAL)}
                </Typography>
              )}
              {item.createdBy && (
              <Typography className={classes.dateInfo}>
                by
                {" "}
                {item.createdBy}
              </Typography>
              )}
              {item.modified && (
                <Typography className={clsx(classes.dateInfo, "mt-1")}>
                  Modified
                  {' '}
                  {formatRelativeDate(new Date(item.modified), new Date(), DD_MMM_YYYY_AT_HH_MM_A_SPECIAL)}
                </Typography>
              )}
              {item.modifiedBy && (
              <Typography className={classes.dateInfo}>
                by
                {" "}
                {item.modifiedBy}
              </Typography>
              )}
            </Grid>
            <Grid item>
              {item.message && !item.id && (
                <Tooltip title={checked ? "Note added" : "Add note"}>
                  <span>
                    <IconButton
                      className="lightGrayIconButton"
                      color="secondary"
                      onMouseEnter={() => setIconHovered(true)}
                      onMouseLeave={() => setIconHovered(false)}
                    >
                      <FontAwesomeIcon fixedWidth icon="check" className={clsx("inherit", { "successColor": checked || iconHovered })} />
                    </IconButton>
                  </span>
                </Tooltip>
              )}
              <Tooltip title="Remove Note">
                <IconButton
                  className="lightGrayIconButton"
                  color="secondary"
                  onClick={() => onDelete(index)}
                >
                  <DeleteIcon fontSize="inherit" color="inherit" />
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>
        </Grid>
      </Paper>
    </Grid>
  );
};

export default NoteItem;
