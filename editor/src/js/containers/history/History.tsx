import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Table, Button} from 'reactstrap';
import classnames from "classnames";
import TimeAgo from 'react-timeago';
import {getHistory, publish, setVersion} from "./actions";
import {Version, VersionStatus} from "../../model";
import {State} from "../../reducers/state";
import {showModal} from "../../common/containers/modal/actions";

interface Props {
  versions: Version[];
  onInit: () => any;
  onPublish: (id) => any;
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

  onPublish(id) {
    this.props.showModal({
      text: 'You are about to push your changes onto the live site. Are you sure?',
      onConfirm: () => this.props.onPublish(id),
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
      <div className={classnames('overflow-content', {fetching})}>

        <Table className="table table--row-center">
          <thead>
            <tr>
              <th>#</th>
              <th>Published</th>
              <th>By</th>
              <th/>
            </tr>
          </thead>

          <tbody>
          {versions &&
            versions.map(version => (
              <tr key={version.id}>
                <td>{version.id}</td>
                <td>
                  {version.status === VersionStatus.draft && 'Draft'}
                  {(version.status === VersionStatus.published || !version.status) && version.publishedOn &&
                    <TimeAgo date={version.publishedOn} live={false}/>
                  }
                </td>
                <td>{version.author}</td>
                <td>
                  {version.status === VersionStatus.draft &&
                    <Button color="primary" onClick={() => this.onPublish(version.id)}>Publish</Button>
                  }
                  {(version.status === VersionStatus.published || !version.status) &&
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

const mapStateToProps = (state: State) => ({
  versions: state.history.versions,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getHistory()),
    onPublish: id => dispatch(publish(id, VersionStatus.published)),
    onRevert: id => dispatch(setVersion(id, VersionStatus.draft)),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(History as any);
