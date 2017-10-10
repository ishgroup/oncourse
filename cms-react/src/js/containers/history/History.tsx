import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Table, Button} from 'reactstrap';
import TimeAgo from 'react-timeago';
import {getHistory, publish, setVersion} from "./actions";
import {Version} from "../../model/History";

interface Props {
  versions: Version[];
  onInit: () => any;
  onPublish: () => any;
  onRevert: (id) => any;
}

class History extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  onPublish() {
    this.props.onPublish();
  }

  onRevert(id) {
    this.props.onRevert(id);
  }

  render() {
    const {versions} = this.props;

    return (
      <div>

        <Table className="table table--row-center">
          <thead>
            <tr>
              <th>Published</th>
              <th>By</th>
              <th>Changes</th>
              <th/>
            </tr>
          </thead>

          <tbody>
          {versions &&
            versions.map(version => (
              <tr key={version.id}>
                <td>
                  {!version.published && 'Draft'}
                  {version.published && version.date && <TimeAgo date={version.date}/>}
                </td>
                <td>{version.author}</td>
                <td>{version.changes}</td>
                <td>
                  {!version.published &&
                    <Button color="primary" onClick={() => this.onPublish()}>Publish</Button>
                  }
                  {version.published &&
                    <Button color="secondary" onClick={() => this.onRevert(version.id)}>Revert</Button>
                  }
                </td>
              </tr>
            ))
          }
          </tbody>
        </Table>

      </div>
    );
  }
}

const mapStateToProps = state => ({
  versions: state.history.versions,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getHistory()),
    onPublish: () => dispatch(publish()),
    onRevert: id => dispatch(setVersion(id)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(History);
