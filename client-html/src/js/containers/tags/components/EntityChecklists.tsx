/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import {
  Card,
  IconButton 
} from "@mui/material";
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { Tag } from "@api/model";
import { makeAppStyles } from "../../../common/styles/makeStyles";
import TagsService from "../services/TagsService";
import { useAppDispatch } from "../../../common/utils/hooks";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import StaticProgress from "../../../common/components/progress/StaticProgress";
import { ColoredCheckBox } from "../../../common/components/form/ColoredCheckBox";

const useStyles = makeAppStyles(theme => ({

}));

interface ChecklistItemProps {
  item: Tag
}

const ChecklistItem = ({ item }: ChecklistItemProps) => (
  <div>
    <div className="centeredFlex">
      <div className="heading centeredFlex flex-fill">
        {item.name}
        <IconButton size="small" color="inherit">
          <KeyboardArrowDownIcon color="inherit" />
        </IconButton>
      </div>

      <div className="centeredFlex">
        <StaticProgress color={item.color} value={70} className="pt-0-5 pr-0-5" />
        <IconButton size="small">
          <MoreVertIcon />
        </IconButton>
      </div>
    </div>

    {item.childTags.map(ct => <ColoredCheckBox label={ct.name} color={ct.color} input={{} as any} meta={{} as any} key={ct.id} />)}

  </div>
);

interface EntityChecklistsProps {
  entity: string;
  entityId: number;
}

export const EntityChecklists = ({
 entity, entityId
}: EntityChecklistsProps) => {
  const [checklists, setChecklists] = useState<Tag[]>([]);
  
  const dispatch = useAppDispatch();
  
  const classes = useStyles();
  
  useEffect(() => {
    TagsService.getChecklists(entity, entityId)
      .then(res => setChecklists(res.allowedChecklists))
      .catch(res => instantFetchErrorHandler(dispatch, res));
  }, [entity, entityId]);
  
  return checklists.length ? (
    <Card className="cardBorders p-3" elevation={0}>
      {checklists.map(c => <ChecklistItem item={c} key={c.id} />)}
    </Card>
  ) : null;
};