/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Avatar from '@mui/material/Avatar';
import { Directions, Language, Link, MoreVert } from '@mui/icons-material';
import { AlertTitle } from '@mui/lab';
import Alert from '@mui/lab/Alert';
import clsx from 'clsx';
import { AppTheme, DocumentIconsChooser, formatRelativeDate, III_DD_MMM_YYYY_HH_MM_SPECIAL } from "ish-ui";
import React, { MouseEvent } from 'react';
import withStyles from '@mui/styles/withStyles';
import createStyles from '@mui/styles/createStyles';
import { Grid, Popover } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { library } from '@fortawesome/fontawesome-svg-core';
import {
  faFile,
  faFileAlt,
  faFileArchive,
  faFileExcel,
  faFileImage,
  faFilePdf,
  faFilePowerpoint,
  faFileWord
} from '@fortawesome/free-solid-svg-icons';
import Tooltip from '@mui/material/Tooltip';
import ButtonBase from '@mui/material/ButtonBase';
import { Document } from '@api/model';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { getDocumentShareSummary, getLatestDocumentItem } from "../../utils/documents";


library.add(faFileImage, faFilePdf, faFileExcel, faFileWord, faFilePowerpoint, faFileArchive, faFileAlt, faFile);

const styles = (theme: AppTheme) =>
  createStyles({
    closeIcon: {
      fontSize: 20,
      width: '24px',
      height: '24px',
      margin: 0,
      padding: 0,
      position: 'absolute',
      top: theme.spacing(0.5),
      right: theme.spacing(0.5)
    },
    miniGrayText: {
      fontSize: '10px',
      color: theme.palette.grey[500]
    },
    infoName: {
      fontSize: '14px',
      display: '-webkit-box',
      WebkitBoxOrient: 'vertical',
      WebkitLineClamp: 2,
      maxHeight: '3em'
    },
    avatar: {
      width: '25px',
      height: '25px',
      margin: '2px'
    },
    share: {
      display: 'flex',
      alignItems: 'center',
      position: 'absolute',
      bottom: theme.spacing(0.5),
      right: theme.spacing(0.5)
    },
    documentChooserButton: {
      position: 'absolute',
      left: 4,
      top: 0,
      height: '100%',
      width: 66
    }
  });

const DocumentInfo = props => {
  const {classes} = props;
  return (
    <div className="flex-column flex-fill overflow-hidden pr-1">
      <Typography className={clsx('text-truncate word-break-all', classes.infoName)}>
        {props.name}
      </Typography>
      <Typography className={classes.miniGrayText}>
        {props.size}
        {' '}
        {formatRelativeDate(new Date(props.date), new Date(), III_DD_MMM_YYYY_HH_MM_SPECIAL)}
      </Typography>
    </div>
  );
};

interface Props {
  entity: string;
  index: number;
  unlink: any;
  classes: any;
  item: Document;
  editItem: () => void;
  viewItem: () => void;
}

class DocumentHeader extends React.PureComponent<Props, any> {
  state = {
    popoverAnchor: null,
    openMoreMenu: null
  }

  unlinkItem = e => {
    e.stopPropagation();
    const {index, unlink} = this.props;
    unlink(index);
    this.onCloseMoreMenu(e);
  };

  openDocumentURL = (e: MouseEvent<any>, url: string) => {
    e.stopPropagation();
    window.open(url);
  };

  handlePopoverClose = () => {
    this.setState({
      popoverAnchor: null
    });
  }

  handlePopoverOpen = event => {
    this.setState({
      popoverAnchor: event.currentTarget
    });
  };

  onOpenMoreMenu = event => {
    event.stopPropagation();
    this.setState({
      openMoreMenu: event.currentTarget
    });
  };

  onCloseMoreMenu = event => {
    event.stopPropagation();
    this.setState({
      openMoreMenu: null
    });
  };

  openDocumentView = e => {
    const {viewItem} = this.props;
    viewItem();
    this.onCloseMoreMenu(e);
  };

  render() {
    const {classes, item, entity} = this.props;
    const {popoverAnchor, openMoreMenu} = this.state;

    const latestItem = item && getLatestDocumentItem(item.versions);
    const validUrl = latestItem && latestItem.url;

    return (
      <Grid container columnSpacing={3} className="mb-1">
        <div className="d-flex overflow-hidden">
          <Tooltip title="Open Document URL" disableHoverListener={!validUrl}>
            <ButtonBase
              disabled={!validUrl}
              onClick={(e: any) => this.openDocumentURL(e, validUrl)}
              className={classes.documentChooserButton}
            >
              <DocumentIconsChooser
                type={getLatestDocumentItem(item.versions).mimeType}
                thumbnail={item.thumbnail || (item.versions && item.versions[0] && item.versions[0].thumbnail)}
                isHeader
              />
            </ButtonBase>
          </Tooltip>

          <DocumentInfo
            name={item.name}
            date={item.added || (item.versions && item.versions[0] && item.versions[0].added)}
            size={getLatestDocumentItem(item.versions).size}
            classes={classes}
          />

          <div
            className={classes.share}
            onMouseEnter={this.handlePopoverOpen}
            onMouseLeave={this.handlePopoverClose}
          >
            {((item.attachmentRelations.length === 1
                && item.attachmentRelations[0].entity === 'Course'
                && item.access === 'Public') || (!item.attachmentRelations.length && entity === 'Course' && item.access === 'Public'))
              && (
                <Avatar className={clsx('activeAvatar', classes.avatar)}>
                  <Language fontSize="small"/>
                </Avatar>
              )}
            {
              ['Link', 'Public'].includes(item.access)
              && (
                <Avatar className={clsx('activeAvatar', classes.avatar)}>
                  <Link fontSize="small"/>
                </Avatar>
              )
            }
            {
              ['Tutors and enrolled students', 'Tutors only'].includes(item.access)
              && (
                <Avatar className={clsx('activeAvatar', classes.avatar)}>
                  <Directions fontSize="small"/>
                </Avatar>
              )
            }
          </div>

          <Popover
            className="pointer-events-none"
            open={Boolean(popoverAnchor)}
            anchorEl={popoverAnchor}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'left',
            }}
            transformOrigin={{
              vertical: 'top',
              horizontal: 'left',
            }}
            disableRestoreFocus
          >
            <Alert severity="info">
              <AlertTitle>Who can view this document</AlertTitle>
              {getDocumentShareSummary(item.access, item.attachmentRelations)}
            </Alert>
          </Popover>

          <IconButton
            aria-label="more"
            aria-controls="document-more-menu"
            aria-haspopup="true"
            onClick={this.onOpenMoreMenu}
            className={classes.closeIcon}
          >
            <MoreVert fontSize="inherit" color="inherit"/>
          </IconButton>

          <Menu
            open={Boolean(openMoreMenu)}
            anchorEl={openMoreMenu}
            onClose={this.onCloseMoreMenu}
          >
            <MenuItem onClick={this.openDocumentView}>
              Document info
            </MenuItem>
            <MenuItem onClick={this.openDocumentView}>
              Permissions
            </MenuItem>
            <MenuItem onClick={this.unlinkItem} className="errorColor">
              Unlink
            </MenuItem>
          </Menu>
        </div>
      </Grid>
    );
  }
}

export default withStyles(styles)(DocumentHeader);
