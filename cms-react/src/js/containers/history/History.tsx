import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Table, Button} from 'reactstrap';

class History extends React.Component<any, any> {

  componentDidMount() {

  }

  render() {

    return (
      <div>

        <Table>
          <thead>
          <tr>
            <th>Published</th>
            <th>By</th>
            <th>Changes</th>
            <th/>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>Draft</td>
            <td>Mark</td>
            <td>20</td>
            <td><Button color="primary">Publish</Button></td>
          </tr>
          <tr>
            <td>27 minutes ago</td>
            <td>John</td>
            <td>52</td>
            <td><Button color="primary">Revert</Button></td>
          </tr>
          </tbody>
        </Table>

      </div>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {

  };
};

export default connect(mapStateToProps, mapDispatchToProps)(History);
