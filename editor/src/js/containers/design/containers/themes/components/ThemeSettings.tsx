import React from 'react';
import CloseIcon from '@material-ui/icons/Close';
import AddIcon from '@material-ui/icons/Add';
import {withStyles} from "@material-ui/core/styles";
import {Theme, Layout} from "../../../../../model";
import IconBack from "../../../../../common/components/IconBack";
import CustomButton from "../../../../../common/components/CustomButton";
import {Grid, Select, TextField} from "@material-ui/core";
import clsx from "clsx";

const styles: any = theme => ({
  linkBack: {
    textTransform: "capitalize",
    color: "rgba(0, 0, 0, 0.87)",
    fontSize: "15px",
    display: "block",
    padding: "15px 20px",
  },
  removeButton: {
    marginRight: theme.spacing(2),
  },
  removeIcon: {
    color: theme.palette.error.main,
    fontSize: "1rem",
  },
  addIcon: {
    color: theme.statistics.enrolmentText.color,
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
  input: {
    height: "20px",
    fontSize: "12px",
    width: "144px",
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
});

interface Props {
  classes: any,
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
  }
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

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  formatLink(link) {
    return (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');
  }

  onAddNewUrl() {
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
      exactMatch: this.state.exactMatch
    });

    this.setState({
      urls,
      newLink: '',
      exactMatch: false,
    });
  }

  onUpdatePath(event, path) {
    const updatedUrls = this.state.urls.map(elem => {
      if (elem.path === path) {
        elem.exactMatch = !!event.target.value;
      }

      return elem;
    });

    this.setState({
      urls: updatedUrls
    })
  }

  onChange(event, key) {
    this.setState({
      [key]: event.target.value,
    });
  }

  onDeleteUrl(url) {
    const urls = this.state.urls.filter(item => item.path !== url);
    this.setState({urls});
  }

  onSave() {
    const {onEdit} = this.props;

    onEdit({
      title: this.state.title,
      layoutId: this.state.layoutId,
      paths: this.state.urls,
    });
  }

  onClickDelete(e) {
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
            <a href="javascript:void(0)" className={classes.linkBack} onClick={e => this.clickBack(e)}>
              <IconBack text={theme.title}/>
            </a>
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
          <form>
            <Grid>
              <label htmlFor="themeTitle">Title</label>
              <TextField
                type="text"
                name="themeTitle"
                id="themeTitle"
                placeholder="Theme title"
                className={classes.input}
                value={title}
                onChange={e => this.onChange(e, 'title')}
              />
            </Grid>

            <Grid className={clsx("flex-column", "mt-2", "mb-2")}>
              <label htmlFor="themeLayout">Layout</label>
              <Select
                name="themeLayout"
                id="themeLayout"
                placeholder="Theme layout"
                value={layoutId}
                onChange={e => this.onChange(e, 'layoutId')}
              >
                {layouts.map(layout => (
                  <option key={layout.id} value={layout.id}>{layout.title}</option>
                ))}
              </Select>
            </Grid>

            <Grid>
              <label htmlFor="pageUrl">Pages</label>
              <div className="links">

                {urls.map((url, index) => (
                  <div className="centeredFlex path-item" key={index}>
                    <div>
                      <div
                        title={url.path}
                        className={classes.linkTitle}
                      >
                        {url.path}
                      </div>

                      <div>
                        <Select
                          name="exactMatch"
                          id="exactMatch"
                          placeholder="Exact matching"
                          className={clsx(classes.input, "mb-1")}
                          value={url.exactMatch.toString()}
                          onChange={e => this.onUpdatePath(e, url.path)}
                        >
                          {urlsOptions.map(option => (
                            <option key={option.title} value={option.value.toString()}>{option.title}</option>
                          ))}
                        </Select>
                      </div>
                    </div>
                    <CloseIcon
                      onClick={() => this.onDeleteUrl(url.path)}
                      className={classes.removeIcon}
                    />
                  </div>
                ))}
              </div>

              <div className={clsx("centeredFlex", "mt-2", "mb-2")}>
                <TextField
                  type="text"
                  name="newLink"
                  id="newLink"
                  placeholder="New Page Url"
                  value={newLink}
                  onChange={e => this.onChange(e, 'newLink')}
                  onKeyDown={e => e.key === 'Enter' && this.onAddNewUrl()}
                />
                <AddIcon
                  onClick={() => this.onAddNewUrl()}
                  className={classes.addIcon}
                />
              </div>
            </Grid>

            <Grid className={classes.actionsGroup}>
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
            </Grid>
          </form>
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(ThemeSettings));
