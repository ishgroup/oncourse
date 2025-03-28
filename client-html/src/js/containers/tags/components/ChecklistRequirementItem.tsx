/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { TagRequirement } from '@api/model';
import Delete from '@mui/icons-material/Delete';
import { Collapse, FormControlLabel } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { makeAppStyles, Switch, useHoverShowStyles } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { IAction } from '../../../common/actions/IshAction';
import FormField from '../../../common/components/form/formFields/FormField';
import EntityService from '../../../common/services/EntityService';
import GetTagRequirementDisplayName from '../utils/GetTagRequirementDisplayName';

const useStyles = makeAppStyles()(theme => ({
  deleteIcon: {
    fontSize: "20px"
  },
  root: {
    display: "grid",
    gridTemplateColumns: `0.5fr 1fr ${theme.spacing(4.5)}`,
    alignItems: "center",
  },
  collapse: {
    gridColumn: "1/4"
  }
}));

interface Props {
  disabled: boolean;
  item: TagRequirement;
  onDelete: any;
  index: number;
  parent: string;
  form: string;
  dispatch: Dispatch<IAction>
}

const ChecklistRequirementItem = (props: Props) => {
  const {
    disabled, item, onDelete, index, parent, form, dispatch
  } = props;
  
  const [showDisplayRule, setShowDisplayRule] = useState(Boolean(item.displayRule));
  const [isValidQuery, setIsValidQuery] = useState(true);

  const { classes } = useStyles();
  const { classes: hoverClasses } = useHoverShowStyles();

  const header = useMemo(() => GetTagRequirementDisplayName(item.type), [item.type]);
  
  const debounseSearch = useCallback<any>(
    debounce((entity, query) => {
      EntityService.getPlainRecords(
        entity,
        "id",
        query,
        1
      )
        .then(() => {
          setIsValidQuery(true);
        })
        .catch(() => {
          setIsValidQuery(false);
        });
    }, 600),
    []
  );

  useEffect(() => {
    debounseSearch(item.type, item.displayRule);
  }, [item.displayRule, item.type]);

  const validateExpression = useCallback(() => (isValidQuery ? undefined : "Expression is invalid"), [isValidQuery]);
  
  const onSwitchDisplayRule = (e, v) => {
    setShowDisplayRule(v);
    if (!v) {
      dispatch(change(form, `${parent}.displayRule`, null));
    }
  };

  return (
    <div className={clsx(classes.root, hoverClasses.container)}>
      <Typography variant="h5" className="flex-fill" fontSize="1.3rem">
        {header}
      </Typography>

      <FormControlLabel
        className="justify-content-end"
        control={<Switch checked={showDisplayRule} onChange={onSwitchDisplayRule} />}
        label={$t('add_display_rule')}
        labelPlacement="start"
      />

      <IconButton
        className={clsx("dndActionIconButton", hoverClasses.target, {
          "invisible": disabled
        })}
        onClick={() => onDelete(index)}
      >
        <Delete className={clsx(classes.deleteIcon, "dndActionIcon")} />
      </IconButton>
      
      <Collapse in={showDisplayRule} className={classes.collapse}>
        <FormField
          type="aql"
          name={`${parent}.displayRule`}
          label={$t('display_when')}
          rootEntity={item.type}
          validate={validateExpression}
          className="mt-2 mb-2"
        />
      </Collapse>
    </div>
  );
};

export default ChecklistRequirementItem;