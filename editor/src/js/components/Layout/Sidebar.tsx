import React from 'react';
import {Route as DomRoute, Redirect} from 'react-router-dom';
import {NavLink} from 'react-router-dom';
import clsx from "clsx";
import {withStyles} from "@material-ui/core/styles";
import {Route, routes} from '../../routes';
import {User} from "../../model";
import {getHistoryInstance} from "../../history";
import CustomButton from "../../common/components/CustomButton";
import {Grid} from "@material-ui/core";

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
  activeLink: {
    color: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: "transparent",
      color: theme.palette.primary.main,
    },
  },
  subLink: {
    padding: "10px 20px 10px 50px",
  },
  logoutLink: {
    padding: "5px 0",
    textAlign: "center",
    fontSize: "12px",
    display: "inline-block",
    marginTop: "10px",
    "&:hover": {
      textDecoration: "underline",
      backgroundColor: "transparent",
    }
  },
  slimPublishButton: {
    fontSize: "10px",
    padding: "3px 3px",
    minWidth: "59px",
  },
  listItem: {
    borderBottom: "1px solid #bbb",
  }
})

interface Props {
  classes: any;
  slim?: boolean;
  user: User;
  onLogout: () => void;
  onPublish: () => void;
  showModal: (props) => any;
}

const firstChar = (str: string) => (
  str && str.substring(0, 1)
);

class Sidebar extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {activeUrl: '/'};
  }

  componentWillReceiveProps(props) {
    const history = getHistoryInstance();
    if (history.location.state && history.location.state.updateActiveUrl) {
      this.setState({activeUrl: history.location.pathname});
    }
  }

  onClickPublish() {
    const {showModal, onPublish} = this.props;

    showModal({
      text: `You are about to push your changes onto the live site. Are you sure?`,
      onConfirm: () => onPublish(),
    });
  }

  onClickMenu(url) {
    this.setState({
      activeUrl: url,
    });
  }

  onClickHistory() {
    return false;
    // getHistoryInstance().push('/history');
  }

  onClickLogout(e) {
    e.preventDefault();
    const {onLogout} = this.props;

    onLogout();
  }

  render() {
    const {classes, slim, user} = this.props;
    const userName = slim
      ? `${firstChar(user.firstName) || ''}${firstChar(user.lastName) || ''}`
      : `${user.firstName || ''} ${user.lastName || ''}`;

    const getSubRoutes = url => (
      routes.filter(route => !route.isPublic && route.parent === url).map((route: Route, index) => (
        <li key={index} className={clsx((this.state.activeUrl !== url || slim) && "d-none")}>
          {/*{console.log("this.state.activeUrl", this.state.activeUrl)}*/}
          {/*{console.log("route.url", route.url)}*/}
          <NavLink
            exact={route.exact}
            className={clsx(classes.subLink, classes.link, this.state.activeUrl === route.url && classes.activeLink)}
            to={route.url}
          >
            <span>{route.title}</span>
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
              className={clsx(classes.link, this.state.activeUrl === route.url && classes.activeLink)}
              onClick={e => this.onClickMenu(route.url)}
            >
              <span className="d-flex">
                {route.icon && <span className="centeredFlex mr-0-5">{route.icon}</span>}
                {!slim && <span> {route.title}</span>}
              </span>
            </NavLink>
          </li>,
          getSubRoutes(route.url),
        ]))}

      </ul>
    );

    return (
      <Grid item xs={2} className={clsx(classes.sidebarWrapper, slim && classes.sidebarWrapperSlim)}>
        <div className={clsx(classes.sidebar, slim && classes.sidebarSlim)}>
          <div className={classes.sidebarContent}>

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
              <Grid container className="center">
                <Grid item xs={6}>
                  <CustomButton
                    styleType="submit"
                    size="small"
                    styles={slim ? classes.slimPublishButton : null}
                    onClick={() => this.onClickPublish()}
                  >
                    Publish
                  </CustomButton>
                </Grid>
                <Grid item xs={6}>

                </Grid>
              </Grid>

              <Grid container className="center">
                <Grid item xs={12}>
                  <a href="javascript:void(0)" className={classes.logoutLink} onClick={e => this.onClickLogout(e)}>
                    <span className="user">{userName} {userName && ':'} logout</span>
                  </a>
                </Grid>
              </Grid>
            </div>
          </div>
        </div>
      </Grid>
    );
  }
}

const RouteWrapper = ({component: Component, ...rest}) => {
  return (
    <DomRoute {...rest} render={props => (
      <Component {...props}/>
    )}/>
  );
};

export default (withStyles(styles)(Sidebar))

