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
}

const firstChar = (str: string) => (
  str.substring(0, 1)
);

export class Sidebar extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {activeUrl: 'site'};
  }

  onClickMenu(url) {
    this.setState({
      activeUrl: url,
    });
  }

  onClickHistory() {
    getHistoryInstance().push('/history');
  }

  render() {
    const {slim, user, onLogout} = this.props;
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
              <span>{slim ? <span className={route.icon || ''}/> : route.title}</span>
            </NavLink>
          </li>,
          getSubRoutes(route.url),
        ]))}

      </ul>
    );

    return (
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
              <Col md="6"><Button size="sm" color="primary">Publish</Button></Col>
              <Col md="6">
                <span onClick={() => this.onClickHistory()}>
                  25 Edits
               </span>
              </Col>
            </Row>

            <Row className="center">
              <Col md="6"><span className="user">{userName}</span></Col>
              <Col md="6"><Button size="sm" color="secondary" onClick={onLogout}>Log out</Button></Col>
            </Row>

          </div>
        </div>

      </div>
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

