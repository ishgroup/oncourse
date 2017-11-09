import React from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {Block} from "../../../../../model";
import {BlockSettings} from "../components/BlockSettings";
import {URL} from "../../../../../routes";
import {addBlock, deleteBlock, saveBlock} from "../actions";
import {SidebarList} from "../../../../../components/Sidebar/SidebarList";
import {showModal} from "../../../../../common/containers/modal/actions";

interface Props {
  blocks: Block[];
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
      <div className={classnames({fetching})}>
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
  fetching: state.block.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (blockId, settings) => dispatch(saveBlock(blockId, settings)),
    onDeleteBlock: id => dispatch(deleteBlock(id)),
    showModal: props => dispatch(showModal(props)),
    onAddBlock: () => dispatch(addBlock()),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(BlockSidebar);
