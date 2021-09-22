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
  TextField
} from '@mui/material';
import IconButton from '@mui/material/IconButton';
import CheckIcon from '@mui/icons-material/Check';
import OpenInNew from '@mui/icons-material/OpenInNew';
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import { TextFieldProps } from '@mui/material/TextField/TextField';

import { makeAppStyles } from '../../styles/makeStyles';
import a from '../../../images/a.png';
import b from '../../../images/b.png';
import c from '../../../images/c.png';
import d from '../../../images/d.png';
import e from '../../../images/e.png';
import f from '../../../images/f.png';
import g from '../../../images/g.png';
import h from '../../../images/h.png';
import i from '../../../images/i.png';

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
  {
    img: g,
    title: 'template-g',
    host: 'https://template-g.oncourse.cc',
    link: 'https://template-g.oncourse.cc/courses/creative',
    value: 'g',
  },
  {
    img: h,
    title: 'template-h',
    host: 'https://template-h.oncourse.cc',
    link: 'https://template-h.oncourse.cc/courses/creative',
    value: 'h',
  },
  {
    img: i,
    title: 'template-i',
    host: 'https://template-i.oncourse.cc',
    link: 'https://template-i.oncourse.cc/courses/creative',
    value: 'i',
  },
];

const useStyles = makeAppStyles()((theme, _params, createRef) => {
  const listItemSelected = {
    ref: createRef()
  };
  const selectedImage = {
    ref: createRef()
  };

  const listItemOverlay = {
    ref: createRef(),
    position: 'absolute',
    backgroundColor: alpha('#000', 0.5),
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    height: '100%',
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    textAlign: 'center',
    transform: 'translateX(-100%)',
    transition: '0.5s all cubic-bezier(0.46, 0.03, 0.52, 0.96)',
  } as const;

  return {
    root: {
      display: 'flex',
      flexWrap: 'wrap',
      justifyContent: 'space-around',
      overflow: 'hidden',
      backgroundColor: theme.palette.background.paper,
    },
    listItem: {
      position: 'relative',
      overflow: 'hidden',
      height: '100%',
      border: '3px solid #e5e5e5',
      borderRadius: 4,
      transition: '0.5s all cubic-bezier(0.46, 0.03, 0.52, 0.96)',
      minWidth: 301,
      [`&:hover, &.${listItemSelected.ref}`]: {
        borderColor: '#434343',
        [`& .${listItemOverlay.ref}`]: {
          transform: 'translateX(0)',
        }
      },
      [`&.${listItemSelected.ref}`]: {
        borderColor: theme.palette.primary.main,
        borderWidth: 5,
        [`& .${listItemOverlay.ref}`]: {
          backgroundColor: alpha(theme.palette.primary.main, 0.6),
          color: theme.palette.primary.contrastText,
        },
      },
    },
    image: {
      width: '100%',
      height: '100%',
      left: 0,
      top: 0,
      transform: 'none',
      transition: '0.5s all cubic-bezier(0.46, 0.03, 0.52, 0.96)',
      '&:hover': {
        cursor: 'pointer',
      },
      [`&.${selectedImage.ref}`]: {
        borderColor: theme.statistics.coloredHeaderText.color
      },
    },
    responsiveImage: {
      position: 'absolute',
      width: '100%',
      height: '100%',
      overflow: 'hidden',
      '& > img': {
        position: 'relative',
        left: '50%',
        top: 0,
        transform: 'translate(-50%, 0%)',
        width: 'auto',
        height: 'auto',
        maxWidth: 450,
        minWidth: '100%',
      },
    },
    link: {
      color: theme.statistics.coloredHeaderText.color,
      display: 'inline-block',
      textDecoration: 'none',
      '&:hover': {
        color: 'rgb(172, 103, 20)',
      }
    },
    actionContainer: {
      display: 'flex',
      flexDirection: 'column',
    },
    actionButton: {
      fontSize: 16,
      padding: '8px 24px',
      textTransform: 'initial',
    },
    actionButtonLink: {
      marginTop: 16,
      backgroundColor: theme.palette.primary.contrastText,
      color: theme.palette.primary.main,
      '&:hover': {
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.primary.contrastText,
      },
    },
    selectedItemCheck: {
      color: theme.palette.primary.contrastText,
    },
    listItemOverlay,
    listItemSelected,
    selectedImage,
  };
});

export const TemplateChoser = ({ value, onChange }) => {
  const { classes, cx } = useStyles();

  return (
    <div className={classes.root}>
      <ImageList rowHeight={250} cols={2} gap={20} className="w-100 mt-0">
        {imgData.map((tile) => (
          <ImageListItem key={tile.img} cols={1}>
            <div className={cx(classes.listItem, tile.value === value && classes.listItemSelected)}>
              <span className={classes.responsiveImage}>
                <img
                  src={tile.img}
                  alt={tile.title}
                  className={cx(classes.image, tile.value === value ? classes.selectedImage : null)}
                  loading="lazy"
                />
              </span>
              <div className={classes.listItemOverlay}>
                <div>
                  {tile.value === value ? (
                    <IconButton
                      aria-label="unlink-template"
                      onClick={() => onChange(tile.value)}
                      size="large"
                    >
                      <CheckCircleRoundedIcon fontSize="large" className={classes.selectedItemCheck} />
                    </IconButton>
                  ) : (
                    <div className={classes.actionContainer}>
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
        value={imgData.find((img) => img.value === value)?.host}
        onClick={rest.disabled ? null : () => setOpen(true)}
      />
    </>
  );
};
