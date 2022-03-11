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
import { makeAppStyles } from "../../../../../common/styles/makeStyles";
import IconDots from "../../../../../../images/icon-dots.png";
import { openInternalLink } from "../../../../../common/utils/links";

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
  dotsBackground: {
    background: `transparent url(${IconDots}) 0 0`,
    backgroundSize: 25,
  },
}));

interface Props {
  tutorial?: any;
}

const TutorialPanel = ({ tutorial }: Props) => {
  const classes = useStyles();

  return tutorial ? (
    <div className={classes.root}>
      <div className="d-flex align-items-center">
        <Typography variant="h3" component="div" className={clsx("text-truncate text-nowrap pr-2", classes.title)}>
          Create your first course
        </Typography>
        <div className="flex-fill" />
        <Typography
          variant="body2"
          component="div"
          className={clsx("cursor-pointer fontWeight600", classes.manualLink)}
          onClick={() => openInternalLink(tutorial.documentation)}
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
          <div dangerouslySetInnerHTML={{ __html: tutorial.content }} />
        </Typography>
      </div>
      <Divider className="mt-3 mb-3" />
      <Stack spacing={2} direction="row">
        {tutorial.canSkip && (
          <Button variant="text" className={classes.skipButton}>Skip</Button>
        )}
        <div className="flex-fill" />
        <Button onClick={() => openInternalLink(tutorial.link)} variant="text" endIcon={<ArrowUpwardRoundedIcon className={classes.letsDoItIcon} />} color="primary">
          Let’s do it
        </Button>
      </Stack>
    </div>
  ) : null;
};

export default TutorialPanel;