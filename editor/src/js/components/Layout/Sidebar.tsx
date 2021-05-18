import React from 'react';
import {Route as DomRoute, NavLink} from 'react-router-dom';
import clsx from "clsx";
import {matchPath, withRouter} from "react-router";
import Avatar from '@material-ui/core/Avatar';
import {withStyles} from "@material-ui/core/styles";
import Popover from '@material-ui/core/Popover';
import ArrowForwardIosIcon from '@material-ui/icons/ArrowForwardIos';
import {Grid, Typography} from "@material-ui/core";
import {Route, routes} from '../../routes';
import {Page, Theme, User} from "../../model";
import {getHistoryInstance} from "../../history";
import CustomButton from "../../common/components/CustomButton";
import {BlockState} from "../../containers/content/containers/blocks/reducers/State";
import PageService from "../../services/PageService";
import ModalPublish from "../../common/components/ModalPublish";

const includedSubMenus = ["Blocks", "Pages", "Themes"];

const styles: any = theme => ({
  sidebarWrapper: {
    pointerEvents: "all",
    backgroundColor: "#fff",
    color: "rgba(0, 0, 0, 0.87)",
    boxShadow: "rgba(0, 0, 0, 0.16) 0px 3px 10px, rgba(0, 0, 0, 0.23) 0px 3px 10px",
    transition: "all .2s",
    padding: 0,
    minWidth: "70px",
    height: "100%",
  },
  sidebarWrapperSlim: {
    maxWidth: "4%",
  },
  sidebar: {
    position: "fixed",
    height: "100vh",
    width: "16.666667%",
    zIndex: 1100,
  },
  sidebarSlim: {
    width: "4%",
    minWidth: "70px",
  },
  sidebarContent: {
    height: "100%",
    position: "relative",
  },
  sidebarFooter: {
    position: "absolute",
    fontSize: "12px",
    width: "100%",
    bottom: 0,
    left: 0,
    padding: "15px",
    color: "gray",
    borderTop: "1px solid #bbbbbb",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  sidebarFooterSlim: {
    position: "absolute",
    fontSize: "10px",
    width: "100%",
    bottom: 0,
    left: 0,
    padding: "15px 5px",
    color: "gray",
    borderTop: "1px solid #bbbbbb",
    display: "flex",
    flexDirection: "column-reverse",
    alignItems: "center",
  },
  link: {
    color: theme.palette.text.secondary,
    fontSize: "15px",
    display: "block",
    padding: "15px 20px",
    transition: "all 450ms cubic-bezier(0.23, 1, 0.32, 1) 0ms",
    "&:hover": {
      backgroundColor: "rgba(0, 0, 0, 0.1)",
      color: theme.palette.text.primary,
    },
  },
  subMenuLink: {
    padding: "6px 16px",
  },
  activeLink: {
    color: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: "transparent",
      color: theme.palette.primary.main,
    },
  },
  subLinkWrapper: {
    position: "relative",
    "&:hover": {
      "& $subMenuWrapper": {
        display: "flex",
        position: "absolute",
        left: "100%",
      },
      "& $arrowIcon": {
        display: "block",
      },
    }
  },
  subLink: {
    padding: "10px 20px 10px 50px",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },
  slimPublishButton: {
    fontSize: "10px",
    padding: "3px 3px",
    minWidth: "59px",
  },
  listItem: {
    borderBottom: "1px solid #bbb",
  },
  avatar: {
    height: "27px",
    width: "27px",
    fontSize: "14px",
    textTransform: "uppercase",
    "&:hover": {
      cursor: "pointer",
    }
  },
  userNameLabel: {
    fontSize: "12px",
    color: "rgba(0, 0, 0, 0.87)",
    padding: "8px 16px 0",
    opacity: .5,
  },
  userName: {
    fontSize: "16px",
    padding: "0 16px 6px",
    color: "rgba(0, 0, 0, 0.87)",
    opacity: .5,
  },
  logout: {
    color: "rgba(0, 0, 0, 0.87)",
    opacity: .5,
    fontSize: "16px",
    padding: "6px 16px",
    "&:hover": {
      backgroundColor: "rgba(0, 0, 0, 0.09)",
      cursor: "pointer",
    },
  },
  subMenuWrapper: {
    backgroundColor: "#fff",
    boxShadow: "rgba(0, 0, 0, 0.16) 0px 3px 10px, rgba(0, 0, 0, 0.23) 0px 3px 10px",
    display: "none",
    minWidth: "200px",
    width: "130%",
    maxHeight: "100vh",
    overflowY: "auto",
    zIndex: 1100,
  },
  small: {
    // display: "block",
    color: theme.palette.text.primary,
    lineHeight: "12px",
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
  linkTitle: {
    fontWeight: 400,
    textOverflow: "ellipsis",
  },
  sidebarList: {
    overflowY: "auto",
    maxHeight: "calc(100vh - 265px)",
    width: "100%",
  },
  arrowIcon: {
    fontSize: "15px",
    display: "none",
  },
});

interface Props {
  classes: any;
  user: User;
  onLogout: () => void;
  location: any;
  match: any;
  onPublish: () => void;
  setActiveUrl: (url: string) => void;
  showModal: (props) => any;
  hideNavigation?: () => void;
  navigation?: any;
  blocks?: BlockState[];
  pages?: Page[];
  slim?: boolean;
  themes?: Theme[];
}

const firstChar = (str: string) => (
  str && str.substring(0, 1)
);

class Sidebar extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      activeUrl: '/',
      anchorEl: null,
      // showModal: false,
    };
  }

  componentDidUpdate(prevProps: Readonly<Props>, prevState: Readonly<any>, snapshot?: any) {
    if (this.props.navigation.activeUrl === '/' && this.props.location.pathname !== prevProps.location.pathname) {
      const match = matchPath(this.props.location.pathname, {
        path: "/page/:id?",
        exact: false,
        strict: false
      });

      if (match) this.props.setActiveUrl("/content");
      // if (match) this.setState({activeUrl: "/content"});
    }
  }

  componentWillReceiveProps(props) {
    const history = getHistoryInstance();
    if (history.location.state && history.location.state.updateActiveUrl) {
      this.props.setActiveUrl(history.location.pathname)
      // this.setState({activeUrl: history.location.pathname});
    }
  }

  onClickPublish() {
    const {showModal, onPublish} = this.props;

    showModal({
      text: `You are about to push your changes onto the live site. Are you sure?`,
      confirmButtonText: "Publish",
      onConfirm: () => onPublish(),
    });
  }

  modalToggle(value) {
    this.setState({showModal: value});
  }

  onClickMenu(url) {
    this.props.hideNavigation();

    this.props.setActiveUrl(url);

    // this.setState({
    //   activeUrl: url,
    // });
  }

  onClickLogout(e) {
    e.preventDefault();
    const {onLogout} = this.props;

    onLogout();
  }

  handleClick = (event: any) => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  renderSubMenu(title) {
    const {blocks, classes, hideNavigation, pages, themes} = this.props;

    const items: any[] = title === "Blocks" && blocks || title === "Pages" && pages || title === "Themes" && themes || [];
    const category: string = title === "Blocks" && "blocks" || title === "Pages" && "page" || title === "Themes" && "themes" || "";
    const subTitleKey: string = title === "Pages" ? "urls" : null;
    const idKey: string = "id";

    let subTitleFilterFunc: any = null;
    if (title === "Pages") subTitleFilterFunc = (items, page) => getDefaultLink(items, page) || null;

    const getDefaultLink = (items, page) => {
      const defaultUrl = items.find(item => item.isDefault);
      return defaultUrl ? defaultUrl.link : PageService.generateBasetUrl(page).link;
    }

    const getSubtitle = (item: any) => (
      subTitleFilterFunc ? subTitleFilterFunc(item[subTitleKey], item) : item[subTitleKey]
    );

    return (
      <ul className={classes.sidebarList}>
        {items
          .map(item => (
            <li key={item[idKey]}>
              <NavLink
                exact={false}
                className={clsx(classes.link, classes.subMenuLink)}
                to={`/${category}/${item[idKey]}`}
                activeClassName="active"
                onClick={hideNavigation}
              >
                <span>
                  <span className={clsx(classes.linkTitle, "mr-1")}>{item.title}</span>
                  {item[subTitleKey] && <small className={classes.small}>{getSubtitle(item)}</small>}
                </span>
              </NavLink>
            </li>
          ))}
      </ul>
    )
  }

  render() {
    const {classes, hideNavigation, navigation, slim, user} = this.props;
    const {anchorEl, showModal} = this.state;

    const userName = `${user.firstName || ''} ${user.lastName || ''}`;
    const userNameForAvatar = user.firstName && user.lastName
      ? user.firstName[0] + user.lastName[0]
      : userName.substring(0, 2);

    const getSubRoutes = url => (
      routes.filter(route => !route.isPublic && route.parent === url).map((route: Route, index) => (
        <li key={index} className={clsx((navigation.activeUrl !== url || slim) && "d-none", classes.subLinkWrapper)}>
          {includedSubMenus.includes(route.title) && (
            <div className={classes.subMenuWrapper}>
              {this.renderSubMenu(route.title)}
            </div>
          )}

          <NavLink
            exact={route.exact}
            className={clsx(classes.subLink, classes.link)}
            onClick={hideNavigation}
            to={route.url}
            activeClassName={classes.activeLink}
          >
            <span>{route.title}</span>
            {includedSubMenus.includes(route.title) && <ArrowForwardIosIcon className={classes.arrowIcon}/>}
          </NavLink>
        </li>
      ))
    );

    // Default main sidebar component
    const mainSidebar = () => (
      <ul>
        {routes.filter(route => !route.isPublic && route.root).map((route, index) => ([
          <li key={index} className={slim && classes.listItem}>
            <NavLink
              exact={route.exact}
              to={route.url}
              className={clsx(classes.link)}
              onClick={e => this.onClickMenu(route.url)}
              activeClassName={classes.activeLink}
            >
              <span className="centeredFlex">
                {route.icon}
                {!slim && <span className="ml-0-5"> {route.title}</span>}
              </span>
            </NavLink>
          </li>,
          getSubRoutes(route.url),
        ]))}
      </ul>
    );

    const open = Boolean(anchorEl);
    const id = open ? 'simple-popover' : null;

    return (
      <Grid item xs={2} className={clsx(classes.sidebarWrapper, slim && classes.sidebarWrapperSlim)}>
        <div className={clsx(classes.sidebar, slim && classes.sidebarSlim)}>
          <div className={classes.sidebarContent}>

            <ModalPublish show={showModal} onHide={(val: boolean) => this.modalToggle(val)}/>

            {routes.map((route, index) => (
              <RouteWrapper
                key={index}
                path={route.path}
                exact={route.exact}
                isPublic={route.isPublic}
                component={route.sidebar || mainSidebar}
              />
            ))}

            <div className={slim ? classes.sidebarFooterSlim : classes.sidebarFooter}>
              <Avatar
                aria-describedby={id}
                className={clsx(classes.avatar, slim && "mt-1")}
                onClick={(e) => this.handleClick(e)}
              >
                {userNameForAvatar}
              </Avatar>
              <Popover
                id={id}
                open={open}
                anchorEl={anchorEl}
                onClose={this.handleClose}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                transformOrigin={{
                  vertical: 'center',
                  horizontal: 'left',
                }}
              >
                <div className={"pt-1 pb-1"}>
                  <Typography className={classes.userName}>
                    {userName}
                  </Typography>
                  <Typography className={classes.logout} onClick={e => this.onClickLogout(e)}>
                    Logout
                  </Typography>
                </div>
              </Popover>

              <div className="center">
                <CustomButton
                  styleType="outline"
                  size="small"
                  styles={classes.slimPublishButton}
                  onClick={this.modalToggle.bind(this, true)}
                >
                  Publish
                </CustomButton>
              </div>
            </div>
          </div>
        </div>
      </Grid>
    );
  }
}

export const RouteWrapper = ({component: Component, ...rest}) => {
  return (
    <DomRoute {...rest} render={props => (
      <Component {...props}/>
    )}/>
  );
};

export default withRouter((withStyles(styles)(Sidebar)));

