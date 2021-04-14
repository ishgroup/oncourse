import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {State} from "../../../../../reducers/state";
import BlockSettings from "../components/BlockSettings";
import {URL} from "../../../../../routes";
import {addBlock, deleteBlock, saveBlock} from "../actions";
import SidebarList from "../../../../../components/Sidebar/SidebarList";
import {showModal} from "../../../../../common/containers/modal/actions";
import {BlockState} from "../reducers/State";

interface Props {
  blocks: BlockState[];
  match: any;
  onEditSettings: (blockId, settings) => any;
  onAddBlock: () => any;
  onDeleteBlock: (id) => any;
  history: any;
  fetching: boolean;
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
    const {onAddBlock} = this.props;
    onAddBlock();
  }

  render() {
    const {blocks, match, onEditSettings, onDeleteBlock, showModal, fetching} = this.props;
    const activeBlock = match.params.id && blocks.find(block => block.id == match.params.id);

    return (
      <div className={clsx((fetching && "fetching"))}>
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

const mapStateToProps = (state: State) => ({
  blocks: state.block.items,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (blockId, settings) => dispatch(saveBlock(blockId, settings)),
    onDeleteBlock: blockId => dispatch(deleteBlock(blockId)),
    showModal: props => dispatch(showModal(props)),
    onAddBlock: () => dispatch(addBlock()),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(BlockSidebar as any);
