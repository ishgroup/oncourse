import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getHistoryInstance} from "../../../history";
import {Block} from "../../../model";
import {BlocksList} from "../components/BlocksList";
import {BlockSettings} from "../components/BlockSettings";
import {URL} from "../../../routes";
import {deleteBlock, saveBlock} from "../actions";

interface Props {
  blocks: Block[];
  match: any;
  onEditSettings: (blockId, settings) => any;
  onDeleteBlock: (id) => any;
}

export class BlockSidebar extends React.Component<Props, any> {

  goBack() {
    getHistoryInstance().push(URL.CONTENT);
  }

  resetActiveBlock() {
    getHistoryInstance().push(URL.BLOCKS);
  }

  render() {
    const {blocks, match, onEditSettings, onDeleteBlock} = this.props;
    const activeBlock = match.params.id && blocks.find(block => block.id == match.params.id);

    return (
      <div>
        {!activeBlock &&
          <BlocksList blocks={blocks} onBack={this.goBack}/>
        }

        {activeBlock &&
          <BlockSettings
            block={activeBlock}
            onBack={this.resetActiveBlock}
            onEdit={prop => onEditSettings(activeBlock.id, prop)}
            onDelete={id => onDeleteBlock(id)}
          />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  blocks: state.block.blocks,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (blockId, settings) => dispatch(saveBlock(blockId, settings)),
    onDeleteBlock: id => dispatch(deleteBlock(id)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(BlockSidebar);
