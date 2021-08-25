/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useState } from 'react';
import {
  alpha,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  ImageList,
  ImageListItem,
  makeStyles,
  TextField
} from '@material-ui/core';
import IconButton from "@material-ui/core/IconButton";
import CheckIcon from "@material-ui/icons/Check";
import OpenInNew from "@material-ui/icons/OpenInNew";
import CheckCircleRoundedIcon from "@material-ui/icons/CheckCircleRounded";
import clsx from 'clsx';
import { TextFieldProps } from '@material-ui/core/TextField/TextField';
import { AppTheme } from '../../models/Theme';
import a from '../../../images/a.png';
import b from '../../../images/b.png';
import c from '../../../images/c.png';
import d from '../../../images/d.png';
import e from '../../../images/e.png';
import f from '../../../images/f.png';

const imgData = [
  {
    img: a,
    title: 'template-a',
    host: 'https://template-a.oncourse.cc',
    link: 'https://template-a.oncourse.cc/courses/creative',
    value: 'a',
  },
  {
    img: b,
    title: 'template-b',
    host: 'https://template-b.oncourse.cc',
    link: 'https://template-b.oncourse.cc/courses/creative',
    value: 'b',
  },
  {
    img: c,
    title: 'template-c',
    host: 'https://template-c.oncourse.cc',
    link: 'https://template-c.oncourse.cc/courses/creative',
    value: 'c',
  },
  {
    img: d,
    title: 'template-d',
    host: 'https://template-d.oncourse.cc',
    link: 'https://template-d.oncourse.cc/courses/creative',
    value: 'd',
  },
  {
    img: e,
    title: 'template-e',
    host: 'https://template-e.oncourse.cc',
    link: 'https://template-e.oncourse.cc/courses/creative',
    value: 'e',
  },
  {
    img: f,
    title: 'template-f',
    host: 'https://template-f.oncourse.cc',
    link: 'https://template-f.oncourse.cc/courses/creative',
    value: 'f',
  },
];

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-around',
    overflow: 'hidden',
    backgroundColor: theme.palette.background.paper,
  },
  gridList: {
    // width: 1000,
  },
  listItem: {
    position: "relative",
    overflow: "hidden",
    height: "100%",
    border: '3px solid #e5e5e5',
    borderRadius: 4,
    transition: "0.5s all cubic-bezier(0.46, 0.03, 0.52, 0.96)",
    "&:hover, &$listItemSelected": {
      borderColor: "#434343",
      "& $listItemOverlay": {
        transform: "translateX(0)",
      }
    },
    "&$listItemSelected": {
      borderColor: theme.palette.primary.main,
      borderWidth: 5,
      "& $listItemOverlay": {
        backgroundColor: alpha(theme.palette.primary.main, 0.6),
        color: theme.palette.primary.contrastText,
      },
    },
  },
  listItemSelected: {},
  selectedImage: {},
  image: {
    width: '100%',
    height: '100%',
    left: 0,
    top: 0,
    transform: 'none',
    transition: "0.5s all cubic-bezier(0.46, 0.03, 0.52, 0.96)",
    '&:hover': {
      cursor: 'pointer',
    },
    '&$selectedImage': {
      borderColor: theme.statistics.coloredHeaderText.color
    },
  },
  listItemOverlay: {
    position: "absolute",
    backgroundColor: alpha('#000', 0.5),
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    height: "100%",
    width: "100%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
    transform: "translateX(-100%)",
    transition: "0.5s all cubic-bezier(0.46, 0.03, 0.52, 0.96)",
  },
  link: {
    color: theme.statistics.coloredHeaderText.color,
    display: 'inline-block',
    textDecoration: 'none',
    '&:hover': {
      color: 'rgb(172, 103, 20)',
    }
  },
  actionButton: {
    fontSize: 16,
    padding: "8px 24px",
    textTransform: "initial",
  },
  actionButtonLink: {
    marginTop: 16,
    backgroundColor: theme.palette.primary.contrastText,
    color: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: theme.palette.primary.main,
      color: theme.palette.primary.contrastText,
    },
  },
  selectedItemCheck: {
    color: theme.palette.primary.contrastText,
  },
}));

export const TemplateChoser = ({ value, onChange }) => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <ImageList cellHeight={250} className={classes.gridList} cols={2} gap={20}>
        {imgData.map((tile) => (
          <ImageListItem key={tile.img} cols={1}>
            <div className={clsx(classes.listItem, tile.value === value && classes.listItemSelected)}>
              <img
                src={tile.img}
                alt={tile.title}
                className={clsx(classes.image, tile.value === value ? classes.selectedImage : null)}
                loading="lazy"
              />
              <div className={classes.listItemOverlay}>
                <div>
                  {tile.value === value ? (
                      <IconButton aria-label="unlink-template" onClick={() => onChange(tile.value)}>
                        <CheckCircleRoundedIcon fontSize="large" className={classes.selectedItemCheck}/>
                      </IconButton>
                  ) : (
                      <div>
                        <Button
                            variant="contained"
                            color="primary"
                            endIcon={<CheckIcon />}
                            classes={{ root: classes.actionButton }}
                            disableElevation
                            onClick={() => onChange(tile.value)}
                        >
                          Select this design
                        </Button>
                        <Button
                            variant="contained"
                            color="primary"
                            endIcon={<OpenInNew />}
                            href={tile.link}
                            target="_blank"
                            rel="noreferrer"
                            className={classes.actionButtonLink}
                            classes={{ root: classes.actionButton }}
                            disableElevation
                        >
                          View Live Preview
                        </Button>
                      </div>
                  )}
                </div>
              </div>
            </div>
          </ImageListItem>
        ))}
      </ImageList>
    </div>
  );
};

export const TemplateField = ({ onChange, value, ...rest }: TextFieldProps) => {
  const [open, setOpen] = useState(false);

  const onClose = () => setOpen(false);

  return (
    <>
      <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
        <DialogContent>
          <TemplateChoser value={value} onChange={onChange} />
        </DialogContent>
        <DialogActions>
          <Button variant="contained" color="primary" onClick={onClose}>
            Close
          </Button>
        </DialogActions>
      </Dialog>
      <TextField
        {...rest}
        value={imgData.find(img => img.value === value)?.host}
        onClick={rest.disabled ? null : () => setOpen(true)}
      />
    </>
  );
};
