/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Note } from '@api/model';
import DeleteIcon from '@mui/icons-material/Delete';
import { Grid, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import Tooltip from '@mui/material/Tooltip';
import $t from '@t';
import clsx from 'clsx';
import { DD_MMM_YYYY_AT_HH_MM_A_SPECIAL, formatRelativeDate } from 'ish-ui';
import React from 'react';
import FormField from '../../formFields/FormField';

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

  return (
    <Grid item xs={twoColumn ? 1 : 12} md={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
      <Paper className="p-1 h-100">
        <Grid container className="h-100" alignContent="space-between">
          <Grid item xs={12}>
            <FormField
              type="text"
              name={messageName}
              disabled={item.readonly}
              required
              multiline
            />
          </Grid>
          <Grid container justifyContent="space-between">
            <Grid item>
              {item.created && (
                <Typography className={classes.dateInfo}>
                  {$t('Created')}
                  {' '}
                  {formatRelativeDate(new Date(item.created), new Date(), DD_MMM_YYYY_AT_HH_MM_A_SPECIAL)}
                </Typography>
              )}
              {item.createdBy && (
                <Typography className={classes.dateInfo}>
                  {$t('by')}
                  {" "}
                  {item.createdBy}
                </Typography>
              )}
              {item.modified && (
                <Typography className={clsx(classes.dateInfo, "mt-1")}>
                  {$t('modified', [formatRelativeDate(new Date(item.modified), new Date(), DD_MMM_YYYY_AT_HH_MM_A_SPECIAL)])}
                </Typography>
              )}
              {item.modifiedBy && (
                <Typography className={classes.dateInfo}>
                  {$t('by')}
                  {" "}
                  {item.modifiedBy}
                </Typography>
              )}
            </Grid>
            <Grid item className="d-flex" alignItems="flex-end">
              <Tooltip title={$t('remove_note')}>
                <IconButton
                  className="lightGrayIconButton"
                  color="secondary"
                  onClick={() => onDelete(index)}
                  disabled={item.readonly}
                >
                  <DeleteIcon fontSize="inherit" color="inherit"/>
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
