import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import {getPages} from "./actions";
import {Page} from "../../model/Page";

interface Props {
  pages: Page[];
  onInit: () => any;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {pages} = this.props;

    return (
      <div>
        <p>Pages list (test)</p>
        <ul>
          {pages && pages.map(page => (
            <li key={page.id}>
              <p>{page.title}</p>
              <p>{page.url}</p>
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.pages,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getPages()),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);
