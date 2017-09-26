import React from 'react';
import {connect, Dispatch} from "react-redux";
import {withRouter} from 'react-router-dom';
import {getBlocks, saveBlock} from "./actions";
import {Block as BlockModel} from "../../model";
import {Block} from "./components/Block";

interface Props {
  blocks: BlockModel[];
  onInit: () => any;
  match?: any;
}

export class Blocks extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match, blocks} = this.props;

    return (
      <div>
        {match.params.id &&
        <Block
          block={blocks.find(block => block.id == match.params.id)}
          onSave={() => undefined}
        />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  blocks: state.block.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getBlocks()),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Blocks);
