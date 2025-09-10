/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Button, FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { makeAppStyles, NumberArgFunction } from 'ish-ui';
import React from 'react';
import FormField from '../../../common/components/form/formFields/FormField';
import { validateUniqueNamesInArray } from '../../../common/utils/validation';
import { cardsFormStyles } from '../styles/formCommonStyles';

interface SpecialTagTypeProps {
  index: number;
  onDelete: NumberArgFunction;
  disabled?: boolean;
}

const useStyles = makeAppStyles()(cardsFormStyles as any);

function SpecialTagType({ index, onDelete, disabled }: SpecialTagTypeProps) {
  const { classes }: any = useStyles();
  return (
    <Grid container columnSpacing={3} id={`special-tag-type-${index}`} className="relative">
      <Grid item xs={6}>
        <FormField
          type="text"
          name={`types.${index}.name`}
          label={$t('name')}
          className={classes.field}
          disabled={disabled}
          validate={validateUniqueNamesInArray}
          required
        />
      </Grid>

      <Grid item xs={6}>
        <div className="d-flex">
          <FormControlLabel
            className={classes.checkbox}
            disabled={disabled}
            control={<FormField
              type="switch"
              name={`types.${index}.status`}
              format={v => v === "Show on website"}
              parse={v => (v ? "Show on website" : "Private")}
              debounced={false}
            />}
            label={$t('visible_on_web')}
            labelPlacement="start"
          />
          <div>
            <Button
              size="small"
              color="secondary"
              onClick={() => onDelete(index)}
              className={classes.deleteButton}
              disabled={disabled}
            >
              {$t('delete2')}
            </Button>
          </div>
        </div>
      </Grid>
    </Grid>
  );
}

export default SpecialTagType;