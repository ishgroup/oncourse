import React from "react";
import SwipeableDrawer from "@material-ui/core/SwipeableDrawer";
import {withStyles} from "@material-ui/core/styles";
import clsx from "clsx";
import {Grid, IconButton} from "@material-ui/core";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {NavLink} from "react-router-dom";
import CloseIcon from '@material-ui/icons/Close';
import {Route, routes} from "../../../routes";
import {RouteWrapper} from "../../../components/Layout/Sidebar";
import PageService from "../../../services/PageService";
import {State} from "../../../reducers/state";
import {setActiveUrl} from "./actions";

const styles = (theme) => ({
  sidebarWrapper: {
    pointerEvents: "all",
    backgroundColor: "#fff",
    color: "rgba(0, 0, 0, 0.87)",
    boxShadow: "rgba(0, 0, 0, 0.16) 0px 3px 10px, rgba(0, 0, 0, 0.23) 0px 3px 10px",
    height: "100vh",
    position: "relative",
    // position: "fixed",
    width: "230px",
    zIndex: 1300,
  },
  sidebar: {
    position: "fixed",
    height: "100vh",
    zIndex: 1100,
    width: "230px",
  },
  sidebarContent: {
    height: "100%",
    position: "relative",
  },
  link: {
    color: theme.palette.text.secondary,
    fontSize: "15px",
    display: "block",
    padding: "15px 20px",
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
      }
    }
  },
  subLink: {
    padding: "10px 20px 10px 50px",
  },
  listItem: {
    borderBottom: "1px solid #bbb",
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
});

const includedSubMenus = ["Blocks", "Pages", "Themes"];

interface Props {
  blocks: any[];
  classes: any;
  hideNavigation: () => void;
  navigation: any;
  open: boolean;
  pages: any[];
  setActiveUrl: (url: string) => void;
  themes: any[];
}

const SwipeableNavigation = (props: Props) => {
  const {classes, hideNavigation, navigation, open, setActiveUrl} = props;

  const renderSubMenu = (title) => {
    const {blocks, pages, themes} = props;

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

  const getSubRoutes = url => (
    routes.filter(route => !route.isPublic && route.parent === url).map((route: Route, index) => (
      <li key={index} className={clsx((navigation.activeUrl !== url) && "d-none", classes.subLinkWrapper)}>
        {includedSubMenus.includes(route.title) && (
          <div className={classes.subMenuWrapper}>
            {renderSubMenu(route.title)}
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
        </NavLink>
      </li>
    ))
  );

  const setNewUrl = (url: string) => {
    const filter = routes.filter(route => !route.isPublic && route.parent === url);

    if (!filter.length) {
      hideNavigation();
    }

    setActiveUrl(url);
  }

  const mainSidebar = () => (
    <ul>
      {routes.filter(route => !route.isPublic && route.root).map((route, index) => ([
        <li key={index}>
          <NavLink
            exact={route.exact}
            to={route.url}
            className={clsx(classes.link)}
            onClick={() => setNewUrl(route.url)}
            activeClassName={classes.activeLink}
          >
            <span className="centeredFlex">
              {route.icon}
              <span className="ml-0-5"> {route.title}</span>
            </span>
          </NavLink>
        </li>,
        getSubRoutes(route.url),
      ]))}

    </ul>
  );

  return (
    <SwipeableDrawer
      open={open}
      onOpen={()=>{}}
      onClose={hideNavigation}
    >
      <div className={classes.sidebarWrapper}>
        <IconButton onClick={hideNavigation}>
          <CloseIcon/>
        </IconButton>

        <div className={classes.sidebar}>
          <div className={classes.sidebarContent}>

            {routes.map((route, index) => (
              <RouteWrapper
                key={index}
                path={route.path}
                exact={route.exact}
                isPublic={route.isPublic}
                component={mainSidebar}
              />
            ))}
          </div>
        </div>
      </div>
    </SwipeableDrawer>
  )
}

const mapStateToProps = (state: State) => ({
  blocks: state.block.items,
  navigation: state.navigation,
  pages: state.page.items,
  themes: state.theme.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    setActiveUrl: (url) => dispatch(setActiveUrl(url)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles as any)(SwipeableNavigation));