import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Block} from "../../../model";
import {BlockSettings} from "../components/BlockSettings";
import {URL} from "../../../routes";
import {deleteBlock, saveBlock} from "../actions";
import {SidebarList} from "../../../components/Sidebar/SidebarList";
import {showModal} from "../../../common/containers/modal/actions";
import {defaultBlock} from "../Blocks";

interface Props {
  blocks: Block[];
  match: any;
  onEditSettings: (blockId, settings) => any;
  onDeleteBlock: (id) => any;
  history: any;
  showModal: (props) => any;
}

export class BlockSidebar extends React.Component<Props, any> {

  goBack() {
    this.props.history.push(URL.CONTENT);
  }

  resetActiveBlock() {
    this.props.history.push(URL.BLOCKS);
  }

  onAddBlock() {
    this.props.history.push(`${URL.BLOCKS}/-1`);
  }

  render() {
    const {blocks, match, onEditSettings, onDeleteBlock, showModal} = this.props;
    const activeBlock = match.params.id && (blocks.find(block => block.id == match.params.id) || defaultBlock);

    return (
      <div>
        {!activeBlock &&
          <SidebarList
            items={blocks}
            category="blocks"
            onBack={() => this.goBack()}
            onAdd={() => this.onAddBlock()}
          />
        }

        {activeBlock &&
          <BlockSettings
            block={activeBlock}
            onBack={() => this.resetActiveBlock()}
            onEdit={prop => onEditSettings(activeBlock.id, prop)}
            onDelete={id => onDeleteBlock(id)}
            showModal={showModal}
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
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(BlockSidebar);
