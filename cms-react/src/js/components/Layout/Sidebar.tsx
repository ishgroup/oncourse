import React from 'react';
import {Container, Row, Col, Form, Button} from 'reactstrap';
import {NavLink} from 'react-router-dom';
import classnames from 'classnames';
import {Route, routes} from '../../routes';
import {User} from "../../model";

interface Props {
  slim?: boolean;
  user: User;
  activeRoute: Route;
  onLogout: () => void;
}

const firstChar = (str: string) => (
  str.substring(0, 1)
);

export const Sidebar = (props: Props) => {
  const {slim, user, onLogout, activeRoute} = props;
  const userName = slim
    ? `${firstChar(user.firstName)}${firstChar(user.lastName)}`
    : `${user.firstName} ${user.lastName}`;

  console.log(props);
  return (
    <div className={classnames("sidebar", {"sidebar--slim": slim})}>
      <div className="sidebar__content">
        {activeRoute && activeRoute.sidebar &&
          activeRoute.sidebar()
        }

        {!activeRoute || !activeRoute.sidebar &&
          <ul>
            {
              routes.filter(route => !route.isPublic).map((route: Route, index) => (
                <li key={index}>
                  <NavLink
                    exact={route.exact}
                    to={route.path}
                    activeClassName="active"
                  >
                    <span>{slim ? <span className={route.icon || ''}/> : route.title}</span>
                  </NavLink>
                </li>
              ))
            }
          </ul>
        }

        <div className="sidebar__footer">
          <Row className="center">
            <Col md="6"><Button size="sm" color="primary">Publish</Button></Col>
            <Col md="6"><span className="edits">25 Edits</span></Col>
          </Row>

          <Row className="center">
            <Col md="6"><span className="user">{userName}</span></Col>
            <Col md="6"><Button size="sm" color="secondary" onClick={onLogout}>Log out</Button></Col>
          </Row>

        </div>
      </div>

    </div>
  );
};
