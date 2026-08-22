import { CheckBox, CheckBoxOutlineBlank, IndeterminateCheckBox } from '@mui/icons-material';
import Delete from '@mui/icons-material/Delete';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import $t from '@t';
import clsx from 'clsx';
import * as React from 'react';
import { withStyles } from 'tss-react/mui';
import styles from './FilterComponentStyles';

const FilterItem = props => {
  const {
    classes, label, checked, onChange, index, onDelete, id, isPrivate, deletable, rootEntity, expression, customLabel
  } = props;

  const renderedLabel = customLabel ? customLabel() : label;

  return (
    <div className={classes.root}>
      <FormControlLabel
        classes={{
          root: clsx("flex-fill overflow-hidden", classes.labelRoot),
          label: clsx("overflow-hidden text-nowrap", classes.checkboxLabel),
        }}
        control={(
          <Checkbox
            checked={checked}
            onChange={(e, v) => onChange(index, v)}
            className={classes.checkbox}
            color="secondary"
            icon={<CheckBoxOutlineBlank className={classes.checkboxFontSize} />}
            checkedIcon={<CheckBox className={classes.checkboxFontSize} />}
            indeterminateIcon={<IndeterminateCheckBox className={classes.checkboxFontSize} />}
          />
        )}
        label={
          expression ? (
            <Tooltip title={expression} placement="right">
              <div className="text-truncate">{renderedLabel}</div>
            </Tooltip>
          ) : (
            renderedLabel
          )
        }
      />

      {deletable && (
        <Tooltip title={$t('delete_filter')} placement="right">
          <IconButton className={classes.deleteButton} onClick={() => onDelete(id, rootEntity, checked, isPrivate)}>
            <Delete fontSize="inherit" color="secondary" />
          </IconButton>
        </Tooltip>
      )}
    </div>
  );
};

export default withStyles(FilterItem, styles);