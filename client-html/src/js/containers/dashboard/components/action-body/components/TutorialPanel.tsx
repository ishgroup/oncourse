/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import clsx from "clsx";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded';
import { alpha } from "@mui/material/styles";
import { Grid } from "@mui/material";
import { makeAppStyles } from "../../../../../../ish-ui/styles/makeStyles";
import { openInternalLink } from "../../../../../common/utils/links";
import { useAppDispatch } from "../../../../../common/utils/hooks";
import { setUserPreference } from "../../../../../common/actions";
import { SYSTEM_USER_TUTORIAL_SKIP } from "../../../../../constants/Config";

const useStyles = makeAppStyles(theme => ({
  root: {
    margin: theme.spacing(3),
    padding: theme.spacing(4),
    background: theme.palette.background.paper,
    position: "relative",
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: alpha(theme.palette.text.disabled, 0.1),
    "&:not(:last-child)": {
      marginBottom: theme.spacing(2),
    },
  },
  title: {
    fontSize: theme.spacing(4.875),
  },
  manualLink: {
    borderBottom: `2px solid ${theme.palette.text.primary}`,
    color: theme.palette.text.primary,
  },
  content: {
    display: "flex",
    margin: theme.spacing(4, 0),
  },
  description: {
    [theme.breakpoints.up('lg')]: {
      paddingLeft: theme.spacing(2),
    },
  },
  skipButton: {
    color: theme.palette.text.primary,
  },
  letsDoItIcon: {
    transform: "rotate(45deg)",
  }
}));

interface Props {
  tutorial?: any;
  customLink?: string;
}

const TutorialPanel = ({ tutorial, customLink }: Props) => {
  const dispatch = useAppDispatch();
  
  const classes = useStyles();
  
  const onSkip = () => dispatch(setUserPreference({
    key: SYSTEM_USER_TUTORIAL_SKIP,
    value: new Date().toString()
  }));

  return tutorial ? (
    <div className={classes.root}>
      <div className="d-flex align-items-center">
        <Typography variant="h3" component="div" className={clsx("text-truncate text-nowrap pr-2", classes.title)}>
          {tutorial.title}
        </Typography>
        <div className="flex-fill" />
        <Typography
          variant="body2"
          component="div"
          className={clsx("cursor-pointer fontWeight600", classes.manualLink)}
          onClick={() => openInternalLink(`https://www.ish.com.au/onCourse/doc${tutorial.documentation}`)}
        >
          Read documentation
        </Typography>
      </div>
      <div className={clsx("d-flex", classes.content)}>
        <Grid container>
          {tutorial.video && (
            <Grid item xs={12} lg={4}>
              <iframe
                width="100%"
                allow="fullscreen"
                src={`https://www.youtube.com/embed/${tutorial.video}`}
                title="video"
                className="mw-100 mt-2 mb-2 mr-4"
              />
            </Grid>
          )}
          <Grid item xs={12} lg={true} className={classes.description}>
            <Typography variant="body2" color="textSecondary" dangerouslySetInnerHTML={{ __html: tutorial.content }} />
          </Grid>
        </Grid>
      </div>
      <Divider className="mt-3 mb-3" />
      <Stack spacing={2} direction="row">
        {tutorial.canSkip && (
          <Button variant="text" className={classes.skipButton} onClick={onSkip}>Skip</Button>
        )}
        <div className="flex-fill" />
        <Button 
          onClick={() => openInternalLink(customLink || tutorial.link)}
          color="primary"
          variant="text" 
          endIcon={(
            <ArrowUpwardRoundedIcon 
              className={classes.letsDoItIcon}
            />
          )}
        >
          Let’s do it
        </Button>
      </Stack>
    </div>
  ) : null;
};

export default TutorialPanel;