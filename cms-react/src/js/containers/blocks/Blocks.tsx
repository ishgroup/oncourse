import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getBlocks, saveBlock} from "./actions";
import {Block as BlockModel} from "../../model";
import {Block} from "./components/Block";

interface Props {
  blocks: BlockModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (id, html) => any;
}

export const defaultBlock = {...new BlockModel(), id: -1};

export class Blocks extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match, blocks, onEditHtml} = this.props;
    const activeBlock = match.params.id && (blocks.find(block => block.id == match.params.id) || defaultBlock);

    return (
      <div>
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

const mapStateToProps = state => ({
  blocks: state.block.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getBlocks()),
    onEditHtml: (id, html) => dispatch(saveBlock(id, {html})),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Blocks);
