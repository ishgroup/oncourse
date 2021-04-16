import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import clsx from "clsx";
import TimeAgo from 'react-timeago';
import {getHistory, publish, setVersion} from "./actions";
import {Version, VersionStatus} from "../../model";
import {State} from "../../reducers/state";
import {showModal} from "../../common/containers/modal/actions";
import CustomButton from "../../common/components/CustomButton";

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
      <div className={clsx('overflow-content', (fetching && "fetching"))}>
        <TableContainer component={Paper} className="p-3">
          <Table  className="table table--row-center" aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell align="left">#</TableCell>
                <TableCell align="left">Published</TableCell>
                <TableCell align="left">By</TableCell>
                <TableCell align="left"/>
              </TableRow>
            </TableHead>
            <TableBody>
              {versions && versions.map((version) => (
                <TableRow key={version.id}>
                  <TableCell align="left">{version.id}</TableCell>
                  <TableCell align="left">
                    {version.status === VersionStatus.draft && 'Draft'}
                    {(version.status === VersionStatus.published || !version.status) && version.publishedOn &&
                    <TimeAgo date={version.publishedOn} live={false}/>
                    }
                  </TableCell>
                  <TableCell align="left">{version.author}</TableCell>
                  <TableCell align="left">
                    {version.status === VersionStatus.draft &&
                      <CustomButton
                        styleType="submit"
                        onClick={() => this.onPublish(version.id)}
                      >
                        Publish
                      </CustomButton>
                    }
                    {(version.status === VersionStatus.published || !version.status) &&
                      <CustomButton
                        styleType="cancel"
                        onClick={() => this.onRevert(version.id)}
                      >
                        Revert
                      </CustomButton>
                    }
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
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
