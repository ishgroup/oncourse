import React from 'react';
import {Checkbox, FormControlLabel, Grid, IconButton} from '@material-ui/core';
import clsx from "clsx";
import CloseIcon from '@material-ui/icons/Close';
import {withStyles} from "@material-ui/core/styles";
import AddIcon from '@material-ui/icons/Add';
import IconBack from "../../../../../common/components/IconBack";
import PageService from "../../../../../services/PageService";
import {addContentMarker} from "../../../utils";
import {PageState} from "../reducers/State";
import CustomButton from "../../../../../common/components/CustomButton";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import {stubFunction} from "../../../../../common/utils/Components";
import {AppTheme} from "../../../../../styles/themeInterface";

const styles: any = (theme: AppTheme) => ({
  links: {
    marginBottom: "10px",
  },
  linksItem: {
    position: "relative",
    color: theme.palette.text.primary,
    cursor: "pointer",
    fontFamily: theme.typography.fontFamily,
    padding: "4px 0",
    fontSize: "13px",
    lineHeight: "1em",
  },
  linkDefault: {
    color: theme.statistics.enrolmentText.color,
    cursor: "default",
    "&:hover": {
      "&:after": {
        display: "none",
      },
    },
  },
  linkBack: {
    textTransform: "capitalize",
    color: "rgba(0, 0, 0, 0.87)",
    fontSize: "15px",
    display: "block",
    padding: "15px 20px",
    "&:hover": {
      backgroundColor: "rgba(0, 0, 0, 0.1)",
      color: theme.palette.text.primary,
    },
  },
  linkTitle: {
    paddingRight: "15px",
    transition: "color .15s",
    textOverflow: "ellipsis",
    overflow: "hidden",
    fontWeight: 600,
    "&:after": {
      display: "block",
      // visibility: "hidden",
      opacity: 0,
      pointerEvents: "none",
      padding: "3px",
      fontSize: "12px",
      fontWeight: 100,
      borderRadius: "2px",
      position: "absolute",
      content: "Make Default",
      fontFamily: theme.typography.fontFamily,
      top: "1px",
      left: "100%",
      color: theme.share.color.headerText,
      background: "#fbf9f0",
      marginLeft: "10px",
      whiteSpace: "nowrap",
      transition: "opacity .25s",
    },
    "&:hover": {
      cursor: "pointer",
      "&:after": {
        visibility: "visible",
        opacity: .85,
      },
    },
  },
  removeButton: {
    marginRight: theme.spacing(2),
  },
  removeIcon: {
    color: theme.palette.error.main,
    fontSize: "1rem",
  },
  addIconButton: {
    position: "relative",
    bottom: "-5px",
  },
  addIcon: {
    color: theme.palette.divider,
    fontSize: "1.2rem",
  },
  sideBarSetting: {
    padding: "10px 20px",
  },
  actionsGroup: {
    marginTop: "30px",
    paddingTop: "20px",
    borderTop: "1px solid #bbbbbb",
  },
  inputWrapper: {
    marginBottom: theme.spacing(2),
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
  themes?: any;
}

class PageSettings extends React.PureComponent<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      title: props.page.title,
      urls: props.page.urls,
      visible: props.page.visible,
      themeId: props.page.themeId,
      newLink: '',
      suppressOnSitemap: props.page.suppressOnSitemap,
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
      });
    }
  }

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  onSave = () => {
    const {onEdit, page} = this.props;

    onEdit({
      title: this.state.title,
      urls: this.state.urls,
      visible: this.state.visible,
      themeId: this.state.themeId,
      suppressOnSitemap: this.state.suppressOnSitemap,
      content: addContentMarker(page.content, page.contentMode),
    });
  }

  onChange(event, key) {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    this.setState({
      [key]: value,
    });
  }

  onSetDefaultUrl(url) {
    const urls = this.state.urls
      .map(item => item.link === url.link ? {...item, isDefault: true} : {...item, isDefault: false});

    this.setState({urls});
  }

  onDeleteUrl(url) {
    const urls = this.state.urls.filter(item => item.link !== url.link);
    this.setState({urls});
  }

  onClickDelete = e => {
    e.preventDefault();
    const {onDelete, page, showModal} = this.props;

    showModal({
      text: `You are want to delete page '${page.title}'. Are you sure?`,
      onConfirm: () => onDelete(page.id),
    });
  }

  onAddNewUrl() {
    const newLink = this.formatLink(this.state.newLink);
    const {pages, page, showError} = this.props;
    const actualPages = pages.map(p => p.id === page.id ? {...p, urls: this.state.urls} : p);

    if (!this.state.newLink) return;
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
    });
  }

  formatLink(link) {
    return (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');
  }

  render () {
    const {classes, page} = this.props;
    const {title, visible, urls, newLink, suppressOnSitemap} = this.state;
    const defaultPageUrl = PageService.generateBasetUrl(page);

    return (
      <div>
        <ul>
          <li>
            <a href="#" className={classes.linkBack} onClick={e => this.clickBack(e)}>
              <IconBack text={page.title || 'New Page'}/>
            </a>
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
          <form>
            <Grid>
              <EditInPlaceField
                label="Title"
                name="pageTitle"
                id="pageTitle"
                className={classes.inputWrapper}
                meta={{}}
                input={{
                  onChange: e => this.onChange(e, 'title'),
                  onFocus: stubFunction,
                  onBlur: stubFunction,
                  value: title,
                }}
              />
            </Grid>

            <Grid>
              <label htmlFor="pageUrl">Page Links (URLs)</label>

              <div className={classes.links}>

                <div className={classes.linksItem}>
                  <div
                    onClick={() => urls.find(url => url.isDefault) && this.onSetDefaultUrl(defaultPageUrl)}
                    className={clsx(classes.linkTitle, !urls.find(url => url.isDefault) && classes.linkDefault)}
                    title={defaultPageUrl.link}
                  >
                    {defaultPageUrl.link}
                  </div>
                </div>


                {urls.map((url, index) => (
                  <div className="centeredFlex justify-content-space-between" key={index}>
                    <div
                      onClick={() => !url.isDefault && this.onSetDefaultUrl(url)}
                      className={clsx(classes.linkTitle, url.isDefault && classes.linkDefault)}
                      title={url.link}
                    >
                      {url.link}
                    </div>

                    {!url.isDefault &&
                      <CloseIcon
                        onClick={() => !url.isDefault && this.onDeleteUrl(url)}
                        className={classes.removeIcon}
                      />
                    }
                  </div>
                ))}
              </div>

              <div className="centeredFlex">
                <div>
                  <EditInPlaceField
                    label="New Page Url"
                    type="text"
                    name="newLink"
                    id="newLink"
                    value={newLink}
                    meta={{}}
                    onKeyDown={e => e.key === 'Enter' && this.onAddNewUrl()}
                    input={{
                      onChange: e => this.onChange(e, 'newLink'),
                      onFocus: stubFunction,
                      onBlur: stubFunction,
                      value: newLink,
                    }}
                  />
                </div>
                <IconButton size="small" onClick={this.onAddNewUrl} className={classes.addIconButton}>
                  <AddIcon className={classes.addIcon} />
                </IconButton>
              </div>
            </Grid>

            <FormControlLabel
              control={
                <Checkbox
                  checked={visible}
                  onChange={e => {this.onChange(e, 'visible'); }}
                  name="visible"
                  color="primary"
                />
              }
              label="Visible"
            />

            <FormControlLabel
              control={
                <Checkbox
                  checked={suppressOnSitemap}
                  onChange={e => {this.onChange(e, 'suppressOnSitemap'); }}
                  name="suppressOnSitemap"
                  color="primary"
                />
              }
              label="Hide from sitemap"
            />

            <Grid className={classes.actionsGroup}>
              <div className="buttons-inline">
                <CustomButton
                  styleType="delete"
                  onClick={this.onClickDelete}
                  styles={classes.removeButton}
                >
                  Remove
                </CustomButton>

                <CustomButton
                  styleType="submit"
                  onClick={this.onSave}
                >
                  Save
                </CustomButton>
              </div>
            </Grid>
          </form>
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(PageSettings));
