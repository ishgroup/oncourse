import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getHistoryInstance} from "../../../history";
import {Block} from "../../../model";
import {BlockSettings} from "../components/BlockSettings";
import {URL} from "../../../routes";
import {deleteBlock, saveBlock} from "../actions";
import {SidebarList} from "../../../components/Sidebar/SidebarList";
import {defaultBlock} from "../Blocks";

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

  onAddBlock() {
    getHistoryInstance().push(`${URL.BLOCKS}/-1`);
  }

  render() {
    const {blocks, match, onEditSettings, onDeleteBlock} = this.props;
    const activeBlock = match.params.id && (blocks.find(block => block.id == match.params.id) || defaultBlock);

    return (
      <div>
        {!activeBlock &&
          <SidebarList
            items={blocks}
            onBack={this.goBack}
            category="blocks"
            onAdd={() => this.onAddBlock()}
          />
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
  blocks: state.block.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (blockId, settings) => dispatch(saveBlock(blockId, settings)),
    onDeleteBlock: id => dispatch(deleteBlock(id)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(BlockSidebar);
