import React from 'react';
import {Container, Row, Col, Form, Button} from 'reactstrap';
import {Route as DomRoute, Redirect} from 'react-router-dom';
import {NavLink} from 'react-router-dom';
import classnames from 'classnames';
import {Route, routes} from '../../routes';
import {User} from "../../model";
import {getHistoryInstance} from "../../history";

interface Props {
  slim?: boolean;
  user: User;
  onLogout: () => void;
  onPublish: () => void;
  showModal: (props) => any;
}

const firstChar = (str: string) => (
  str.substring(0, 1)
);

export class Sidebar extends React.Component<Props, any> {
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
    getHistoryInstance().push('/history');
  }

  onClickLogout(e) {
    e.preventDefault();
    const {onLogout} = this.props;

    onLogout();
  }

  render() {
    const {slim, user} = this.props;
    const userName = slim
      ? `${firstChar(user.firstName)}${firstChar(user.lastName)}`
      : `${user.firstName} ${user.lastName}`;

    const getSubRoutes = url => (
      routes.filter(route => !route.isPublic && route.parent === url).map((route: Route, index) => (
        <li key={index} className={classnames('sub', {hidden: this.state.activeUrl !== url || slim})}>
          <NavLink
            exact={route.exact}
            to={route.url}
            activeClassName="active"
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
          <li key={index}>
            <NavLink
              exact={route.exact}
              to={route.url}
              activeClassName="active"
              onClick={e => this.onClickMenu(route.url)}
            >
              <span>
                <span className={route.icon || ''}/>
                {!slim && <span> {route.title}</span>}
              </span>
            </NavLink>
          </li>,
          getSubRoutes(route.url),
        ]))}

      </ul>
    );

    return (
      <Col md="2" className={classnames("sidebar-wrapper", {"sidebar-wrapper--slim": slim})}>
        <div className={classnames("sidebar", {"sidebar--slim": slim})}>
          <div className="sidebar__content">

            {routes.map((route, index) => (
              <RouteWrapper
                key={index}
                path={route.path}
                exact={route.exact}
                isPublic={route.isPublic}
                component={route.sidebar || mainSidebar}
              />
            ))}

            <div className="sidebar__footer">
              <Row className="center">
                <Col md="6">
                  <Button size="sm" color="primary" onClick={() => this.onClickPublish()}>Publish</Button>
                </Col>
                <Col md="6">
                  <Button color="link" onClick={() => this.onClickHistory()}>
                    25 Edits
                 </Button>
                </Col>
              </Row>

              <Row className="center">
                <Col md="12">
                  <a href="#" className="logout-link" onClick={e => this.onClickLogout(e)}>
                    <span className="user">{userName}: logout</span>
                  </a>
                </Col>
              </Row>

            </div>
          </div>

        </div>
      </Col>
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

