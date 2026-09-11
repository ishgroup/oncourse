/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ButtonBase, Collapse } from '@mui/material';
import { alpha } from '@mui/material/styles';
import useEventCallback from '@mui/utils/useEventCallback';
import $t from '@t';
import clsx from 'clsx';
import { ColoredCheckBox, makeAppStyles, stubFunction } from 'ish-ui';
import React, { memo, useMemo, useState } from 'react';
import { FormMenuTag } from '../../../../../../model/tags';
import { useAppSelector } from '../../../../../utils/hooks';
import { getActiveTags } from '../../../utils/listFiltersUtils';
import ListTagGroup from './ListTagGroup';

const useStyles = makeAppStyles()(theme => ({
  expandable: {
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-start",
    width: "100%",
    padding: theme.spacing(1),
    marginTop: theme.spacing(1),
    borderRadius: `${theme.shape.borderRadius}px`,
    backgroundColor: alpha(theme.palette.text.primary, 0.025),
    "& span": {
      textDecoration: "none",
      color: theme.palette.text.primary
    }
  },
  expandIcon: {
    transition: theme.transitions.create("transform"),
  },
  expandedIcon: {
    transform: "rotate(180deg)"
  }
}));

interface Props {
  updateChecked: any;
  updateUnChecked: any;
}

const CHECKED_INPUT = { onChange: stubFunction, value: true } as any;
const UNCHECKED_INPUT = { onChange: stubFunction, value: false } as any;
const EMPTY_META = {} as any;

const groupKey = (group: FormMenuTag) => group.prefix + group.tagBody.id.toString();

// each checklist keeps its own selection - a shared list would tick a task in every checklist
// that happens to hold a tag of the same id
const getActiveTagsByGroup = (checklists: FormMenuTag[]) => new Map(checklists.map(t => [
  groupKey(t),
  getActiveTags(t.children).map(c => c.tagBody.id.toString())
]));

const renderGroups = (
  checklists: FormMenuTag[],
  activeTagsByGroup: Map<string, string[]>,
  updateActive: (updated: FormMenuTag) => void
) => checklists.map((t, index) => {
  if (!t.children.length) {
    return null;
  }
  return (
    <ListTagGroup
      activeTags={activeTagsByGroup.get(groupKey(t))}
      key={groupKey(t)}
      dndKey={index}
      rootTag={t}
      updateActive={updateActive}
      showColoredDots={false}
      dndEnabled={false}
    />
  );
});

const ChecklistsFilters = memo<Props>(({ updateChecked, updateUnChecked }) => {
  const [expanded, setExpanded] = useState(null);
  const { classes } = useStyles();

  const checkedChecklists = useAppSelector(state => state.list.checkedChecklists);
  const uncheckedChecklists = useAppSelector(state => state.list.uncheckedChecklists);

  const activeCheckedChecklists = useMemo(() => getActiveTagsByGroup(checkedChecklists), [checkedChecklists]);

  const activeUncheckedChecklists = useMemo(() => getActiveTagsByGroup(uncheckedChecklists), [uncheckedChecklists]);

  // untouched groups keep their identity so the memoized `ListTagGroup`s below can skip re-rendering
  const onUpdateChecked = useEventCallback((active: FormMenuTag) => {
    updateChecked(checkedChecklists.map(cl => (cl.tagBody.id === active.tagBody.id ? active : cl)));
  });

  const onUpdateUnChecked = useEventCallback((active: FormMenuTag) => {
    updateUnChecked(uncheckedChecklists.map(cl => (cl.tagBody.id === active.tagBody.id ? active : cl)));
  });

  const toggleChecked = useEventCallback(() => setExpanded(expanded === "1" ? null : "1"));
  const toggleUnChecked = useEventCallback(() => setExpanded(expanded === "2" ? null : "2"));

  return (
    <div className="pr-2">
      <div className="heading mt-3 mb-2">
        {$t('select_task_status')}
      </div>

      <ButtonBase className={classes.expandable} onClick={toggleChecked}>
        <ColoredCheckBox
          className="flex-fill"
          label={$t('completed2')}
          color="#43a047"
          input={CHECKED_INPUT}
          meta={EMPTY_META}
        />
        <ExpandMoreIcon color="secondary" className={clsx(classes.expandIcon, expanded === "1" && classes.expandedIcon)} />
      </ButtonBase>

      <Collapse in={expanded === "1"} unmountOnExit>
        {renderGroups(checkedChecklists, activeCheckedChecklists, onUpdateChecked)}
      </Collapse>

      <ButtonBase className={classes.expandable} onClick={toggleUnChecked}>
        <ColoredCheckBox
          className="flex-fill"
          label={$t('incomplete')}
          color="#43a047"
          input={UNCHECKED_INPUT}
          meta={EMPTY_META}
        />
        <ExpandMoreIcon color="secondary" className={clsx(classes.expandIcon, expanded === "2" && classes.expandedIcon)} />
      </ButtonBase>

      <Collapse in={expanded === "2"} unmountOnExit>
        {renderGroups(uncheckedChecklists, activeUncheckedChecklists, onUpdateUnChecked)}
      </Collapse>
    </div>
);
});

export default ChecklistsFilters;
