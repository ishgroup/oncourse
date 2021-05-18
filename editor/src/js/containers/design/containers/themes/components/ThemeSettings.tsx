import React from 'react';
import AddIcon from '@material-ui/icons/Add';
import {withStyles} from "@material-ui/core/styles";
import DeleteIcon from '@material-ui/icons/Delete';
import {IconButton} from "@material-ui/core";
import clsx from "clsx";
import {Theme, Layout} from "../../../../../model";
import CustomButton from "../../../../../common/components/CustomButton";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import MenuIcon from "@material-ui/icons/Menu";

const styles: any = theme => ({
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
  removeButton: {
    marginRight: theme.spacing(2),
  },
  removeIcon: {
    color: "rgba(0, 0, 0, 0.2)",
    fontSize: "1.2rem",
  },
  addIconButton: {
    position: "relative",
    bottom: "-5px",
  },
  addIcon: {
    color: theme.statistics.enrolmentText.color,
    fontSize: "1.2rem",
  },
  sideBarSetting: {
    padding: "10px 20px",
  },
  actionsGroup: {
    display: "flex",
    justifyContent: "space-between",
    paddingTop: "20px",
    borderTop: "1px solid #bbbbbb",
  },
  linkTitle: {
    paddingRight: "15px",
    transition: "color .15s",
    textOverflow: "ellipsis",
    overflow: "hidden",
    fontWeight: 300,
    "&:hover": {
      cursor: "pointer",
    },
  },
  urlsWrapper: {
    maxHeight: "calc(100vh - 410px)",
    overflow: "auto",
    marginRight: "-20px",
  },
  linksWrapper: {
    paddingRight: "20px",
  },
  linkWrapper: {
    "&:hover": {
      "& $iconButton": {
        display: "flex",
      }
    }
  },
  iconButton: {
    display: "none",
  }
});

interface Props {
  classes: any;
  theme: Theme;
  themes: Theme[];
  layouts: Layout[];
  onBack: () => void;
  onEdit?: (settings) => void;
  showError?: (title) => any;
  onDelete?: (title) => void;
  showModal?: (props) => void;
  showNavigation?: () => void;
}

const urlsOptions = [
  {
    value: false,
    title: "Starts with",
  },
  {
    value: true,
    title: "Exact match",
  },
];

class ThemeSettings extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      layoutId: props.theme.layoutId,
      newLink: '',
      title: props.theme.title,
      urls: [],
      exactMatch: false,
    };
  }

  componentDidMount() {
    const {theme} = this.props;
    if (theme.paths) this.setState({urls: theme.paths});
  }

  componentDidUpdate(prevProps: Readonly<Props>, prevState: Readonly<any>, snapshot?: any) {
    if (this.props.theme.id !== prevProps.theme.id) {
      const {theme} = this.props;

      this.setState({
        urls: theme.paths,
        title: theme.title,
        layoutId: theme.layoutId,
      });
    }
  }

  clickBack = (e) => {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  formatLink = (link) => {
    return (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');
  }

  onAddNewUrl = () => {
    const newLink = this.formatLink(this.state.newLink);
    // const {showError, themes} = this.props;

    if (!this.state.newLink) return;

    // const isUsedUrl = themes.some((elem: Theme) => elem.paths
    //   && elem.paths.some((elem: ThemePath) => elem === newLink))
    //   || this.state.urls.some((elem: string) => elem === newLink);
    //
    // if (isUsedUrl) {
    //   showError('This url already exist');
    //   return;
    // }

    const urls = this.state.urls.concat({
      path: newLink,
      exactMatch: this.state.exactMatch,
    });

    this.setState({
      urls,
      newLink: '',
      exactMatch: false,
    });
  }

  onUpdatePath = (event, path) => {
    const updatedUrls = this.state.urls.map(elem => {
      if (elem.path === path) {
        elem.exactMatch = !!event;
      }

      return elem;
    });

    this.setState({
      urls: updatedUrls,
    });
  }

  onChange = (event, key) => {
    this.setState({
      [key]: event.target && event.target.value || event,
    });
  }

  onDeleteUrl = (url) => {
    const urls = this.state.urls.filter(item => item.path !== url);
    this.setState({urls});
  }

  onSave = () => {
    const {onEdit} = this.props;

    onEdit({
      title: this.state.title,
      layoutId: this.state.layoutId,
      paths: this.state.urls,
    });
  }

  onClickDelete = (e) => {
    e.preventDefault();
    const {onDelete, theme, showModal} = this.props;

    showModal({
      text: `You are want to delete theme '${theme.title}'.`,
      onConfirm: () => onDelete(theme.id),
    });
  }

  render () {
    const {classes, layouts, showNavigation} = this.props;
    const {layoutId, newLink, title, urls} = this.state;

    return (
      <div>
        <ul>
          <li className={"pl-1"}>
            <IconButton onClick={showNavigation}>
              <MenuIcon/>
            </IconButton>
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
          <div className="heading mb-2">Themes</div>

          <form>
            <EditInPlaceField
              label="Theme title"
              name="themeTitle"
              id="pageTitle"
              meta={{}}
              input={{
                onChange: e => this.onChange(e, 'title'),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: title,
              }}
            />

            <EditInPlaceField
              select
              label="Theme layout"
              name="themeLayout"
              id="themeLayout"
              selectValueMark="id"
              selectLabelMark="title"
              meta={{}}
              input={{
                onChange: e => this.onChange(e, 'layoutId'),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: layoutId,
              }}
              items={layouts}
            />

            <div className={classes.urlsWrapper}>
              <label htmlFor="pageUrl" className="mt-2 mb-1 secondaryHeading">Apply this theme to urls</label>
              <div className={clsx("links", classes.linksWrapper)}>
                {urls.map((url, index) => (
                  <div className={clsx(classes.linkWrapper, "centeredFlex")} key={index}>
                    <EditInPlaceField
                      select
                      label={url.path}
                      name="exactMatch"
                      id="exactMatch"
                      selectLabelMark="title"
                      className="w-100"
                      meta={{}}
                      input={{
                        onChange: e => this.onUpdatePath(e, url.path),
                        onFocus: stubFunction,
                        onBlur: stubFunction,
                        value: url.exactMatch,
                      }}
                      fieldClasses={{
                        label: classes.linkTitle,
                      }}
                      items={urlsOptions}
                    />
                    <IconButton size="small" onClick={() => this.onDeleteUrl(url.path)} className={classes.iconButton}>
                      <DeleteIcon className={classes.removeIcon} />
                    </IconButton>
                  </div>
                ))}
              </div>
            </div>

            <div className={"centeredFlex pt-1"}>
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
                className="w-100"
              />
              <IconButton size="small" onClick={this.onAddNewUrl} className={classes.addIconButton}>
                <AddIcon className={classes.addIcon} />
              </IconButton>
            </div>

            <div className={classes.actionsGroup}>
              <CustomButton
                styleType="delete"
                onClick={e => this.onClickDelete(e)}
                styles={classes.removeButton}
              >
                Remove
              </CustomButton>
              <CustomButton
                styleType="submit"
                onClick={e => this.onSave()}
              >
                Save
              </CustomButton>
            </div>
          </form>
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(ThemeSettings));
