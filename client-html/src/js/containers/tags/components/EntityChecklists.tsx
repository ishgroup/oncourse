/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  Fragment,
  useCallback, useEffect, useMemo, useState
} from "react";
import {
  Card, CircularProgress, Collapse, Divider,
  IconButton, Link, Menu, MenuItem, Typography
} from "@mui/material";
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { Tag } from "@api/model";
import { change } from "redux-form";
import debounce from "lodash.debounce";
import clsx from "clsx";
import TagsService from "../services/TagsService";
import { useAppDispatch } from "../../../common/utils/hooks";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import StaticProgress from "../../../common/components/progress/StaticProgress";
import { BooleanArgFunction, openInternalLink, ColoredCheckBox } from  "ish-ui";
import { LSGetItem, LSSetItem } from "../../../common/utils/storage";
import { ENTITY_TAGS_EXPAND_SETTINGS_KEY } from "../constants";

interface ChecklistItemProps {
  item: Tag;
  onCheck: any;
  onCheckAll: BooleanArgFunction;
  checkedIds: Record<number, boolean>;
  collapsedIds: Record<number, boolean>;
}

const expandAllInit = (item: Tag) => JSON.parse(LSGetItem(ENTITY_TAGS_EXPAND_SETTINGS_KEY) || "[]").includes(item.id);

const ChecklistItem = ({
 item, onCheck, onCheckAll, checkedIds, collapsedIds 
}: ChecklistItemProps) => {
  const [menuAnchor, setMenuAnchor] = useState(null);
  const [expandItems, setExpandItems] = useState(true);
  const [expandAll, setExpandAll] = useState(() => expandAllInit(item));
  
  const checkedFraction = useMemo(() => {
    const checkedCount = item.childTags.reduce((p, c) => (checkedIds[c.id] ? ++p : p), 0);
    return (checkedCount / item.childTags.length) * 100;
  }, [checkedIds, item.childTags]);
  
  const allChecked = checkedFraction === 100;

  const onMenuClose = () => setMenuAnchor(null);

  const onShowCompleted = () => setExpandAll(prev => {
    const updated = !prev;
    
    const prevSettings = JSON.parse(LSGetItem(ENTITY_TAGS_EXPAND_SETTINGS_KEY) || "[]");
    
    LSSetItem(ENTITY_TAGS_EXPAND_SETTINGS_KEY, JSON.stringify(Array.from(new Set([...prevSettings, item.id]))));
    
    return updated;
  });
  
  const onMarkAllComplete = () => onCheckAll(!allChecked);
  
  const onEditClick = () => openInternalLink(`/tags/checklist/${item.id}`);
  
  return (
    <div className="flex-fill">
      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={onMenuClose}
        MenuListProps={{
        'aria-labelledby': 'basic-button',
        }}
      >
        <MenuItem onClick={onEditClick}>Edit checklist</MenuItem>
        <MenuItem onClick={onShowCompleted}>{expandAll ? "Hide completed tasks" : "Show completed tasks"}</MenuItem>
        <MenuItem onClick={onMarkAllComplete}>
          Mark all tasks
          {allChecked ? " in" : " "}
          complete
        </MenuItem>
      </Menu>
    
      <div className="centeredFlex mb-1">
        <div className="heading headingHover centeredFlex flex-fill" onClick={() => setExpandItems(prev => !prev)}>
          {item.name}
          <IconButton 
            size="small" 
            color="inherit"
            sx={{
              transform: expandItems ? "rotate(180deg)" : "none",
              transition: theme => theme.transitions.create(['transform'])
            }}
          >
            <KeyboardArrowDownIcon color="inherit" />
          </IconButton>
        </div>

        <div className="centeredFlex">
          <StaticProgress color={item.color} value={checkedFraction} />
          <IconButton size="small" className="text-disabled" onClick={e => setMenuAnchor(e.target)}>
            <MoreVertIcon />
          </IconButton>
        </div>
      </div>

      <Collapse in={expandItems}>
        {item.childTags.map(ct => (
          <Collapse key={ct.id} in={expandAll || !collapsedIds[ct.id]}>
            <ColoredCheckBox
              className="mb-1"
              label={ct.name}
              color={`#${item.color}`}
              input={{ onChange: e => onCheck(ct.id, e.target.checked), value: checkedIds[ct.id] } as any}
              meta={{} as any}
            />
          </Collapse>
        ))}
      </Collapse>
    </div>
);
};

interface EntityChecklistsProps {
  entity: string;
  form: string;
  entityId: number;
  checked: number[];
  className?: string;
}

const getCheckedIds = (checked: number[]) => checked.reduce((p, c) => {
  p[c] = true;
  return p;
}, {});

export const EntityChecklists = ({
 entity, className, form, entityId, checked = []
}: EntityChecklistsProps) => {
  const [checklists, setChecklists] = useState<Tag[]>([]);
  const [collapsedIds, setCollapsedIds] = useState<Record<number, boolean>>(getCheckedIds(checked));
  const [loading, setLoading] = useState<boolean>(true);

  const dispatch = useAppDispatch();
  
  const checkedIds = useMemo(() => getCheckedIds(checked), [checked]);

  const setCollapsedIdsDebounced = useCallback(debounce(setCollapsedIds, 2000), []);

  useEffect(() => {
    setCollapsedIdsDebounced(checkedIds);
  }, [checkedIds]);

  useEffect(() => {
    setCollapsedIds(getCheckedIds(checked));
  }, [entityId]);

  useEffect(() => {
    setLoading(true);
    TagsService.getChecklists(entity, entityId)
      .then(res => {
        setChecklists(res);
        setLoading(false);
      })
      .catch(res => {
        instantFetchErrorHandler(dispatch, res);
        setLoading(false);
      });
  }, [entity, entityId]);
  
  const onCheck = (id, v) => {
    dispatch(change(form, "tags", v ? [...checked, id] : checked.filter(cId => cId !== id)));
  };

  const onCheckAll = (item: Tag, v) => {
    dispatch(change(form, "tags", v ? [...checked, ...item.childTags.map(ct => ct.id)] : checked.filter(cId => !item.childTags.some(ct => ct.id === cId))));
  };
  
  return (
    <Card className={clsx("flex-column cardBorders p-3", className)} elevation={0}>
      {loading
      ? <CircularProgress size={40} thickness={4} className="ml-auto mr-auto" />
      : checklists.map((c, index) => (
        <Fragment key={c.id}>
          <ChecklistItem
            item={c}
            checkedIds={checkedIds}
            collapsedIds={collapsedIds}
            onCheck={onCheck}
            onCheckAll={v => onCheckAll(c, v)}
          />
          {index < checklists.length - 1 && <Divider light className="mt-2 mb-2" />}
        </Fragment>
      ))}

      {!loading && !checklists.length && (
        <div className="centeredFlex">
          <Typography className="flex-fill" variant="caption">
            <Link color="inherit" href={`/tags/checklist/new?entity=${entity}`} target="_blank">Create a checklist now</Link>
          </Typography>
          <StaticProgress color={null} value={0} />
        </div>
      )}
    </Card>
  );
};