import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import {NavLink} from 'react-router-dom';
import {getHistoryInstance} from "../../../history";


export class PagesSidebar extends React.Component<any, any> {

  goBack() {
    getHistoryInstance().push('/');
  }

  render() {
    const {pages} = this.props;

    return (
      <ul>
        <li>
          <a href="#" onClick={() => this.goBack()}>
            <span className="icon-arrow_back"/>
          </a>
        </li>
        {pages.map(page => (
          <li key={page.id}>
            <NavLink
              exact={false}
              to={`/pages/${page.id}`}
              activeClassName="active"
            >
              <span>{page.title}</span>
            </NavLink>
          </li>
        ))}
      </ul>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.pages,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {};
};

export default connect(mapStateToProps, mapDispatchToProps)(PagesSidebar);
