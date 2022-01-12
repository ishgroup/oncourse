/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { Checkbox, FormControlLabel, IconButton } from '@material-ui/core';
import clsx from 'clsx';
import { withStyles } from '@material-ui/core/styles';
import { grey } from '@material-ui/core/colors';
import DeleteIcon from '@material-ui/icons/Delete';
import MenuIcon from '@material-ui/icons/Menu';
import PageService from '../../../../../services/PageService';
import { addContentMarker } from '../../../utils';
import { PageState } from '../reducers/State';
import CustomButton from '../../../../../common/components/CustomButton';
import EditInPlaceField from '../../../../../common/components/form/form-fields/EditInPlaceField';
import { stubFunction } from '../../../../../common/utils/Components';
import { AppTheme } from '../../../../../styles/themeInterface';
import { validateLink } from '../../../../../common/utils/validation';
import { PageLink, Theme } from '../../../../../../../build/generated-sources/api';

const styles: any = (theme: AppTheme) => ({
  navWrapper: {
    display: 'flex',
    paddingLeft: '8px',
  },
  linkBack: {
    textTransform: 'capitalize',
    color: 'rgba(0, 0, 0, 0.87)',
    fontSize: '15px',
    display: 'block',
    padding: '15px 20px',
    '&:hover': {
      backgroundColor: 'rgba(0, 0, 0, 0.1)',
      color: theme.palette.text.primary,
    },
  },
  linkTitle: {
    transition: 'color .15s',
    textOverflow: 'ellipsis',
    overflow: 'hidden',
    fontFamily: theme.typography.fontFamily,
    fontSize: '14px',
    lineHeight: 1.2,
    padding: theme.spacing(.375, 1.875, .375, 0),
    color: theme.palette.text.secondary,
    fontWeight: 300,
    cursor: 'pointer',
  },
  linkDefault: {
    cursor: 'default'
  },
  removeButton: {
    marginRight: theme.spacing(2),
  },
  removeIcon: {
    color: 'rgba(0, 0, 0, 0.2)',
    fontSize: '1rem',
  },
  sideBarSetting: {
    padding: '10px 20px',
  },
  actionsGroup: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '30px',
    paddingTop: '20px',
    borderTop: '1px solid #bbbbbb',
  },
  inputWrapper: {
    marginBottom: theme.spacing(2),
  },
  linkWrapper: {
    paddingRight: theme.spacing(3.5),
    '&::after': {
      display: 'block',
      visibility: 'hidden',
      opacity: 0,
      pointerEvents: 'none',
      padding: '2px 6px',
      fontSize: 10,
      borderRadius: 15,
      position: 'absolute',
      content: "'Default'",
      fontFamily: theme.typography.fontFamily,
      right: -12,
      color: theme.palette.text.secondary,
      background: theme.palette.divider,
      marginLeft: '10px',
      whiteSpace: 'nowrap',
      transition: 'opacity .25s',
      top: '3px',
      lineHeight: '14px',
    },
    '&:hover': {
      '& $iconButton': {
        display: 'flex',
      },
      '&::after': {
        visibility: 'visible',
        opacity: 0.5,
      },
    },
    '&$linkDefault::after,&$linkDefault:hover::after': {
      visibility: 'visible',
      opacity: 1,
    }
  },
  iconButton: {
    display: 'none',
    position: 'relative',
    left: "-5px"
  },
  newLinkInlineContainer: {
    marginTop: theme.spacing(0.5),
    marginLeft: 0,
  },
});

interface Props {
  classes: any;
  page: PageState;
  pages: PageState[];
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => any;
  showError?: (title) => any;
  hideNavigation?: () => void;
  showNavigation?: () => void;
  themes?: Theme[];
  history?: any;
}

interface State {
  title: string,
  urls: PageLink[],
  visible: boolean,
  themeId: number,
  newLink: string,
  suppressOnSitemap: boolean,
  urlError: string
}

class PageSettings extends React.PureComponent<Props, State> {
  constructor(props) {
    super(props);

    this.state = {
      title: props.page.title,
      urls: props.page.urls,
      visible: props.page.visible,
      themeId: props.page.themeId,
      newLink: '',
      suppressOnSitemap: props.page.suppressOnSitemap,
      urlError: null
    };
  }

  componentWillReceiveProps(props) {
    if (props.page.id !== this.props.page.id) {
      this.setState({
        title: props.page.title,
        urls: props.page.urls,
        visible: props.page.visible,
        themeId: props.page.themeId,
        newLink: '',
        suppressOnSitemap: props.page.suppressOnSitemap,
        urlError: null
      });
    }
  }

  validateUrlHandler = (value) => {
    this.setState({
      urlError: validateLink(value)
    });
  };

  onSave = () => {
    const { onEdit, page } = this.props;

    onEdit({
      title: this.state.title,
      urls: this.state.urls,
      visible: this.state.visible,
      suppressOnSitemap: this.state.suppressOnSitemap,
      content: addContentMarker(page.content, page.contentMode),
    });
  };

  onChange = (event, key) => {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    this.setState({
      [key]: value,
    } as any, () => (key === 'suppressOnSitemap' || key === 'visible') && this.onSave());
  };

  onSetDefaultUrl = (url) => {
    const urls = this.state.urls
      .map((item) => (item.link === url.link ? { ...item, isDefault: true } : { ...item, isDefault: false }));

    this.setState({ urls }, () => this.onSave());
  };

  onDeleteUrl = (url) => {
    const urls = this.state.urls.filter((item) => item.link !== url.link);
    this.setState({ urls }, () => this.onSave());
  };

  onClickDelete = (e) => {
    e.preventDefault();
    const { onDelete, page, showModal } = this.props;

    showModal({
      text: `You are want to delete page '${page.title}'.`,
      onConfirm: () => onDelete(page.id),
    });
  };

  onAddNewUrl = () => {
    if (!this.state.newLink || this.state.urlError) return;

    const newLink = this.formatLink(this.state.newLink);
    const { pages, page, showError } = this.props;
    const actualPages = pages.map((p) => (p.id === page.id ? { ...p, urls: this.state.urls } : p));

    if (!PageService.isValidPageUrl(newLink, actualPages)) {
      showError('This url already exist');
      return;
    }

    const newUrl = {
      link: newLink,
      isDefault: false,
    };

    const urls = this.state.urls.concat(newUrl);
    this.setState({
      urls,
      newLink: '',
    }, () => this.onSave());
  };

  formatLink = (link) => (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');

  render() {
    const {
      classes, page, showNavigation, history, themes
    } = this.props;

    const {
      title, visible, urls, newLink, suppressOnSitemap, urlError, themeId
    } = this.state;

    const defaultPageUrl = PageService.generateBasetUrl(page);

    const themeName = themes?.find((t) => t.id === themeId)?.title || '';

    return (
      <div>
        <ul>
          <li className={classes.navWrapper}>
            <IconButton onClick={showNavigation}>
              <MenuIcon />
            </IconButton>
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
          <div className="heading mb-2">Page</div>

          <form>
            <EditInPlaceField
              label="Title"
              name="pageTitle"
              id="pageTitle"
              className={classes.inputWrapper}
              meta={{}}
              input={{
                onChange: (e) => this.onChange(e, 'title'),
                onFocus: stubFunction,
                onBlur: this.onSave,
                value: title,
              }}
            />

            <label htmlFor="pageUrl" className="pb-1 secondaryHeading">Page Links (URLs)</label>

            <div>
              <div className={clsx(
                classes.linkWrapper,
                'centeredFlex justify-content-space-between relative',
                !urls.find((url) => url.isDefault) && classes.linkDefault
              )}>
                <div
                  onClick={() => urls.find((url) => url.isDefault) && this.onSetDefaultUrl(defaultPageUrl)}
                  className={clsx('centeredFlex', classes.linkTitle)}
                  title={defaultPageUrl.link}
                >
                  {defaultPageUrl.link}
                </div>
              </div>

              {urls.map((url, index) => (
                <div className={clsx(
                  classes.linkWrapper,
                  url.isDefault && classes.linkDefault,
                  'centeredFlex justify-content-space-between relative'
                )} key={index}>
                  <div
                    onClick={() => !url.isDefault && this.onSetDefaultUrl(url)}
                    className={clsx('centeredFlex', classes.linkTitle)}
                  >
                    {url.link}
                  </div>

                  {!url.isDefault
                      && (
                      <IconButton size="small" className={classes.iconButton} onClick={() => !url.isDefault && this.onDeleteUrl(url)}>
                        <DeleteIcon
                          className={classes.removeIcon}
                        />
                      </IconButton>
                      )}
                </div>
              ))}
            </div>

            <div className="centeredFlex w-100 mb-2">
              <EditInPlaceField
                hidePlaceholderInEditMode
                disableInputOffsets
                placeholder="New link"
                formatting="inline"
                hideLabel
                type="text"
                name="newLink"
                id="newLink"
                value={newLink}
                meta={{
                  error: urlError,
                  invalid: Boolean(urlError)
                }}
                onKeyDown={(e) => e.key === 'Enter' && newLink && !urlError && this.onAddNewUrl()}
                input={{
                  onChange: (e) => {
                    this.validateUrlHandler(e.target.value);
                    this.onChange(e, 'newLink');
                  },
                  onFocus: stubFunction,
                  onBlur: this.onAddNewUrl,
                  value: newLink,
                }}
                className="w-100"
                classes={{ inlineContainer: classes.newLinkInlineContainer }}
              />
            </div>

            <FormControlLabel
              control={(
                <Checkbox
                  checked={visible}
                  onChange={(e) => { this.onChange(e, 'visible'); }}
                  name="visible"
                  color="primary"
                />
              )}
              label="Visible"
            />

            <FormControlLabel
              control={(
                <Checkbox
                  checked={suppressOnSitemap}
                  onChange={(e) => { this.onChange(e, 'suppressOnSitemap'); }}
                  name="suppressOnSitemap"
                  color="primary"
                />
              )}
              label="Hide from sitemap"
            />

            <div className="centeredFlex w-100 mt-3">
              <EditInPlaceField
                label="Using theme"
                type="text"
                name="usedTheme"
                input={{
                  value: themeName
                }}
                meta={{}}
                className="w-100"
                disabled
              />
              <CustomButton
                styleType="outline"
                onClick={() => history.push(`/themes/${themeId}`)}
              >
                Change
              </CustomButton>
            </div>

            <div className={classes.actionsGroup}>
              <CustomButton
                styleType="delete"
                onClick={this.onClickDelete}
                styles={classes.removeButton}
              >
                Remove
              </CustomButton>
            </div>
          </form>
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(PageSettings));
