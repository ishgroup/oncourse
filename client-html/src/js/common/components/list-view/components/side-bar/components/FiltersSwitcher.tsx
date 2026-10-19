/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import useEventCallback from '@mui/utils/useEventCallback';
import $t from '@t';
import { makeAppStyles } from 'ish-ui';
import React, { memo, useMemo } from 'react';

const useStyles = makeAppStyles()(theme => ({
  root: {
    minHeight: "unset",
    marginRight: theme.spacing(1)
  },
  tab: {
    textTransform: "none",
    padding: theme.spacing(0.5),
    minHeight: "unset",
    fontSize: "13px"
  }
}));

const FiltersSwitcher = memo<{ setValue: (value: number) => void; value: number }>(({ setValue, value }) => {
  const { classes } = useStyles();

  const tabsClasses = useMemo(() => ({ root: classes.root }), [classes]);
  const tabClasses = useMemo(() => ({ root: classes.tab }), [classes]);

  const handleChange = useEventCallback((event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  });

  return (
    <div className="w100 centeredFlex mt-2">
      <Typography variant="caption" className="flex-fill">
        {$t('filter_by')}
      </Typography>
      <Tabs classes={tabsClasses} value={value} onChange={handleChange}>
        <Tab classes={tabClasses} label={$t('filters_tags')} />
        <Tab classes={tabClasses} label={$t('checklists')} />
      </Tabs>
    </div>
  );
});

export default FiltersSwitcher;