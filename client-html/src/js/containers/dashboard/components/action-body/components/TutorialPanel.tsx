/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import clsx from "clsx";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded';
import { AppTheme } from "../../../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
  root: {
    padding: theme.spacing(6, 8),
  },
  title: {
    fontSize: theme.spacing(4.875),
  },
  manualLink: {
    borderBottom: `2px solid ${theme.palette.text.primary}`,
    color: theme.palette.text.primary,
  },
  content: {
    margin: theme.spacing(4, 0),
  },
  contentText: {
    color: theme.palette.text.grey,
  },
  skipButton: {
    color: theme.palette.text.primary,
  },
  letsDoItIcon: {
    transform: "rotate(45deg)",
  },
});

interface Props {
  classes?: any;
  tutorial: any;
}

const TutorialPanel: React.FC<Props> = props => {
  const {
    classes,
    tutorial,
  } = props;

  return (
    <div className={classes.root}>
      <Box sx={{ width: '100%', bgcolor: 'background.paper' }} className="p-4">
        <div className="d-flex align-items-center">
          <Typography variant="h3" component="div" className={clsx("text-truncate text-nowrap pr-2", classes.title)}>
            Create your first course
          </Typography>
          <div className="flex-fill" />
          <Typography
            variant="body2"
            component="div"
            className={clsx("cursor-pointer fontWeight600", classes.manualLink)}
            onClick={() => {}}
          >
            Read documentation
          </Typography>
        </div>
        <div className={clsx("d-flex", classes.content)}>
          {tutorial.video && (
            <iframe
              allow="fullscreen"
              width="220px"
              height="150"
              src={`https://www.youtube.com/embed/${tutorial.video}`}
              title="video"
              className="mw-100 mt-2 mb-2 mr-4"
            />
          )}
          <Typography variant="body2" component="div" className={classes.contentText}>
            <Box component="div" display="block" dangerouslySetInnerHTML={{ __html: tutorial.content }} />
          </Typography>
        </div>
        <Divider className="mt-3 mb-3" />
        <Stack spacing={2} direction="row">
          {tutorial.canSkip && (
            <Button variant="text" className={classes.skipButton}>Skip</Button>
          )}
          <div className="flex-fill" />
          <Button variant="text" endIcon={<ArrowUpwardRoundedIcon className={classes.letsDoItIcon} />} color="primary">
            Let’s do it
          </Button>
        </Stack>
      </Box>
    </div>
  );
};

export default withStyles(styles)(TutorialPanel);