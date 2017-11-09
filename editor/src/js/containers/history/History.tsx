import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Table, Button} from 'reactstrap';
import classnames from "classnames";
import TimeAgo from 'react-timeago';
import {getHistory, publish, setVersion} from "./actions";
import {Version} from "../../model/History";
import {showModal} from "../../common/containers/modal/actions";

interface Props {
  versions: Version[];
  onInit: () => any;
  onPublish: () => any;
  onRevert: (id) => any;
  showModal: (props) => any;
  fetching: boolean;
}

class History extends React.Component<Props, any> {

  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.props.onInit();
  }

  onPublish() {
    this.props.showModal({
      text: 'You are about to push your changes onto the live site. Are you sure?',
      onConfirm: () => this.props.onPublish(),
    });
  }

  onRevert(id) {
    this.props.showModal({
      text: `You are about to revert draft version to this revision. Are you sure?`,
      onConfirm: () => this.props.onRevert(id),
    });

  }

  render() {
    const {versions, fetching} = this.props;

    return (
      <div className={classnames({fetching})}>

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
  fetching: state.history.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getHistory()),
    onPublish: () => dispatch(publish()),
    onRevert: id => dispatch(setVersion(id)),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(History);
