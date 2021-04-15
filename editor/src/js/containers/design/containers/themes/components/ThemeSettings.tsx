import React from 'react';
import CloseIcon from '@material-ui/icons/Close';
import AddIcon from '@material-ui/icons/Add';
import {withStyles} from "@material-ui/core/styles";
import clsx from "clsx";
import {Theme, Layout} from "../../../../../model";
import IconBack from "../../../../../common/components/IconBack";
import CustomButton from "../../../../../common/components/CustomButton";
import {IconButton} from "@material-ui/core";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

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
    color: theme.palette.error.main,
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
    paddingTop: "20px",
    borderTop: "1px solid #bbbbbb",
  },
  linkTitle: {
    paddingRight: "15px",
    transition: "color .15s",
    textOverflow: "ellipsis",
    overflow: "hidden",
    fontWeight: 600,
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
}

const urlsOptions = [
  {
    value: false,
    title: "Starts with",
  },
  {
    value: true,
    title: "Exact matching",
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
      text: `You are want to delete theme '${theme.title}'. Are you sure?`,
      onConfirm: () => onDelete(theme.id),
    });
  }

  render () {
    const {classes, theme, layouts} = this.props;
    const {layoutId, newLink, title, urls} = this.state;

    return (
      <div>
        <ul>
          <li>
            <a href="#" className={classes.linkBack} onClick={e => this.clickBack(e)}>
              <IconBack text={theme.title}/>
            </a>
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
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
              <label htmlFor="pageUrl" className="mt-2 mb-1">Pages</label>
              <div className={clsx("links", classes.linksWrapper)}>
                {urls.map((url, index) => (
                  <div className={"centeredFlex"} key={index}>
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
                    <IconButton size="small" onClick={() => this.onDeleteUrl(url.path)}>
                      <CloseIcon className={classes.removeIcon} />
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
              <div className="buttons-inline">
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
            </div>
          </form>
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(ThemeSettings));
