/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useState } from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  ImageList,
  ImageListItem,
  makeStyles,
  TextField
} from '@material-ui/core';
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
    link: 'https://template-a.oncourse.cc/courses/creative',
    value: 'a',
  },
  {
    img: b,
    title: 'template-b',
    link: 'https://template-b.oncourse.cc/courses/creative',
    value: 'b',
  },
  {
    img: c,
    title: 'template-c',
    link: 'https://template-c.oncourse.cc/courses/creative',
    value: 'c',
  },
  {
    img: d,
    title: 'template-d',
    link: 'https://template-d.oncourse.cc/courses/creative',
    value: 'd',
  },
  {
    img: e,
    title: 'template-e',
    link: 'https://template-e.oncourse.cc/courses/creative',
    value: 'e',
  },
  {
    img: f,
    title: 'template-f',
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
    width: 1000,
  },
  selectedImage: {},
  image: {
    border: '2px solid transparent',
    width: '300px',
    height: '230px',
    left: 0,
    transform: 'none',
    '&:hover': {
      cursor: 'pointer',
    },
    '&$selectedImage': {
      borderColor: theme.statistics.coloredHeaderText.color
    },
  },
  link: {
    color: theme.statistics.coloredHeaderText.color,
    display: 'inline-block',
    textDecoration: 'none',
    '&:hover': {
      color: 'rgb(172, 103, 20)',
    }
  }
}));

export const TemplateChoser = ({ value, onChange }) => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <ImageList cellHeight={281} className={classes.gridList} cols={3}>
        {imgData.map((tile) => (
          <ImageListItem key={tile.img} cols={1}>
            <img
              src={tile.img}
              alt={tile.title}
              className={clsx(classes.image, tile.value === value ? classes.selectedImage : null)}
              onClick={() => onChange(tile.value)}
              loading="lazy"
            />
            <a href={tile.link} target="_blank" className={classes.link} rel="noreferrer">
              live preview...
            </a>
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
        value={value}
        onClick={rest.disabled ? null : () => setOpen(true)}
      />
    </>
  );
};
