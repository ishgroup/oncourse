import React from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {getBlocks, saveBlock} from "./actions";
import {Block as BlockModel} from "../../../../model";
import {Block} from "./components/Block";
import {State} from "../../../../reducers/state";

interface Props {
  blocks: BlockModel[];
  onInit: () => any;
  match?: any;
  fetching: boolean;
  onEditHtml: (id, html) => any;
}

export const defaultBlock = {...new BlockModel(), id: -1};

export class Blocks extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match, blocks, onEditHtml, fetching} = this.props;
    const activeBlock = match.params.id && (blocks.find(block => block.id == match.params.id) || defaultBlock);

    return (
      <div className={classnames({fetching})}>
        {activeBlock &&
          <Block
            block={activeBlock}
            onSave={onEditHtml}
          />
        }
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  blocks: state.block.items,
  fetching: state.block.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getBlocks()),
    onEditHtml: (id, html) => dispatch(saveBlock(id, {html})),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Blocks);
