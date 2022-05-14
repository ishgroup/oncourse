/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import clsx from "clsx";
import { 
  Box, 
  Card, 
  CircularProgress, 
  circularProgressClasses, 
  CircularProgressProps, 
  IconButton 
} from "@mui/material";
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { Checklists, Tag } from "@api/model";
import { makeAppStyles } from "../../../common/styles/makeStyles";
import TagsService from "../services/TagsService";
import { useAppDispatch } from "../../../common/utils/hooks";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";

const useStyles = makeAppStyles(theme => ({

}));

interface ChecklistItemProps {
 item: Tag 
}

const CheckedProgress = (props: CircularProgressProps) => (
  <Box sx={{ position: 'relative' }}>
    <CircularProgress
      variant="determinate"
      sx={{
          color: theme =>
            theme.palette.grey[theme.palette.mode === 'light' ? 200 : 800],
      }}
      size={40}
      thickness={4}
      {...props}
      value={100}
    />
    <CircularProgress
      variant="indeterminate"
      disableShrink
      sx={{
          color: theme => (theme.palette.mode === 'light' ? '#1a90ff' : '#308fe8'),
          position: 'absolute',
          left: 0,
          [`& .${circularProgressClasses.circle}`]: {
            strokeLinecap: 'round',
          },
      }}
      size={40}
      thickness={4}
      value={70}
      {...props}
    />
  </Box>
  );

const ChecklistItem = ({ item }: ChecklistItemProps) => (
  <div>
    <div className="centeredFlex">
      <div className="heading centeredFlex flex-fill">
        {item.name}
        <IconButton size="small" color="inherit">
          <KeyboardArrowDownIcon color="inherit" />
        </IconButton>
      </div>

      <div>
        <CheckedProgress />
        <IconButton size="small">
          <MoreVertIcon />
        </IconButton>
      </div>
    </div>
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
  
  return (
    <Card className="cardBorders" elevation={0}>
      {checklists.map(c => <ChecklistItem item={c} key={c.id} />)}
    </Card>
);
};