import React from 'react';
import {NavLink} from 'react-router-dom';
import {withStyles} from "@material-ui/core/styles";
import {IconButton, TextField, Typography} from "@material-ui/core";
import {AddCircle} from "@material-ui/icons";
import clsx from "clsx";
import MenuIcon from "@material-ui/icons/Menu";
import {Page, Block, Theme} from "../../model";


type Item = (Page | Block | Theme);

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
  link: {
    color: theme.palette.text.secondary,
    fontSize: "15px",
    display: "block",
    padding: "10px 20px",
    transition: "all 450ms cubic-bezier(0.23, 1, 0.32, 1) 0ms",
    "&:hover": {
      backgroundColor: "rgba(0, 0, 0, 0.1)",
      color: theme.palette.text.primary,
    },
  },
  addNewButton: {
    padding: "10px 20px 0",
  },
  sidebarSettings: {
    padding: "10px 20px",
  },
  sidebarList: {
    overflowY: "auto",
    height: "calc(100vh - 265px)",
  },
  small: {
    display: "block",
    color: theme.palette.text.primary,
    paddingTop: "3px",
    lineHeight: "12px",
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
  linkTitle: {
    fontWeight: 400,
  },
});

interface Props {
  classes: any;
  items: Item[];
  onBack: () => void;
  category: string;
  showNavigation: () => any;
  subTitleKey?: string;
  idKey?: string;
  subTitleFilterFunc?: (items, parent?) => any;
  onAdd?: () => any;
}

class SidebarList extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      filter: '',
    };
  }

  clickBack(e) {
    const {onBack} = this.props;

    e.preventDefault();
    onBack();
  }

  onChangeFilter(e) {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {classes, items, category, subTitleKey, subTitleFilterFunc, onAdd, idKey = 'id', showNavigation} = this.props;
    const reg = new RegExp(this.state.filter.replace('(', '\\(').replace(')', '\\)'), 'gi');

    const getSubtitle = (item: Item) => (
      subTitleFilterFunc ? subTitleFilterFunc(item[subTitleKey], item) : item[subTitleKey]
    );

    // apply filter to title and subtitle
    const applyFilter = (item: Item): boolean => (
      item.title && item.title.toLocaleLowerCase().indexOf(this.state.filter.toLocaleLowerCase()) !== -1 ||
      item[subTitleKey] && getSubtitle(item).toLocaleLowerCase().indexOf(this.state.filter.toLocaleLowerCase()) !== -1
    );

    return (
      <ul>
        <li>
          <IconButton onClick={() => showNavigation()}>
          {/*<IconButton onClick={e => this.clickBack(e)}>*/}
            <MenuIcon/>
          </IconButton>
        </li>

        <li>
          <div className={classes.sidebarSettings}>
            <TextField
              placeholder="Filter"
              name="filter"
              value={this.state.filter}
              onChange={e => this.onChangeFilter(e)}
            />
          </div>
        </li>

        {onAdd &&
        <li>
          <div className={clsx(classes.addNewButton, "centeredFlex")}>
            <Typography className="heading">Add new</Typography>
            <IconButton onClick={onAdd}>
                <AddCircle className="addButtonColor" width={20} />
            </IconButton>
          </div>
        </li>
        }

        {items &&
        <li>
          <ul className={classes.sidebarList}>
            {items
              .filter(item => applyFilter(item))
              .map(item => (
                <li key={item[idKey]}>
                  <NavLink
                    exact={false}
                    className={classes.link}
                    to={`/${category}/${item[idKey]}`}
                    activeClassName="active"
                  >
                    {this.state.filter &&
                    <span>
                      <span
                        dangerouslySetInnerHTML={{__html: item.title.replace(reg, str => (`<mark>${str}</mark>`))}
                      }/>

                      {item[subTitleKey] &&
                        <small
                          className={classes.small}
                          dangerouslySetInnerHTML={{
                            __html: getSubtitle(item).replace(reg, str => (`<mark>${str}</mark>`)),
                          }}
                        />
                      }
                      </span>
                    }

                    {!this.state.filter &&
                    <span>
                      <span className={classes.linkTitle}>{item.title}</span>
                      {item[subTitleKey] &&
                        <small className={classes.small}>{getSubtitle(item)}</small>
                      }
                    </span>
                    }
                  </NavLink>
                </li>
              ))}
          </ul>
        </li>
        }
      </ul>
    );
  }
}

export default (withStyles(styles)(SidebarList));
