import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import {withRouter} from 'react-router-dom';
import {editPageContent, getPages} from "./actions";
import {Page as PageModel} from "../../model";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  editPageContent: (content) => any;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match, pages} = this.props;

    return (
      <div>
        {match.params.id &&
        <Page
          page={pages.find(page => page.id == match.params.id)}
        />
        }
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
    editPageContent: content => dispatch(editPageContent(content)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);


const Page = props => {
  const {page} = props;

  return (
    <div className="content-white">
      <div dangerouslySetInnerHTML={{__html: page.html}}/>
    </div>
  );
};
