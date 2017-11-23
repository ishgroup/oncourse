import React from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {getBlocks, saveBlock} from "./actions";
import {Block} from "./components/Block";
import {State} from "../../../../reducers/state";
import {BlockState} from "./reducers/State";

interface Props {
  blocks: BlockState[];
  onInit: () => any;
  match?: any;
  fetching: boolean;
  onEditHtml: (id, html) => any;
}

export class Blocks extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match, blocks, onEditHtml, fetching} = this.props;
    const activeBlock = match.params.id && blocks.find(block => block.id == match.params.id);

    return (
      <div>
        {activeBlock &&
          <div className={classnames({fetching})}>
            <Block
              block={activeBlock}
              onSave={onEditHtml}
            />
          </div>
        }
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  blocks: state.block.items,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getBlocks()),
    onEditHtml: (id, content) => dispatch(saveBlock(id, {content})),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Blocks);
