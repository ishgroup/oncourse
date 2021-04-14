import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import AddCircleIcon from '@material-ui/icons/AddCircle';
import {Paper} from "@material-ui/core";
import clsx from "clsx";
import SortableTree, {changeNodeAtPath, removeNodeAtPath} from 'react-sortable-tree';
import {changeMenuTree, getMenuItems, saveMenuTree} from "./actions";
import {MenuState} from "./reducers/State";
import {showModal} from "../../../../common/containers/modal/actions";
import {State} from "../../../../reducers/state";
import CustomButton from "../../../../common/components/CustomButton";
import 'react-sortable-tree/style.css';

interface Props {
  menu: MenuState;
  onInit: () => any;
  onChangeTree: (treeData) => any;
  onSaveTree: (treeData) => any;
  match: any;
  fetching: boolean;
  showModal: (props) => any;
}

export class Menus extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  save() {
    const {onSaveTree, menu} = this.props;
    onSaveTree(menu.items);
  }

  changeNode(props, node, path) {
    const getNodeKey = ({treeIndex}) => treeIndex;
    const {onChangeTree, menu} = this.props;

    onChangeTree(changeNodeAtPath({
      path,
      getNodeKey,
      treeData: menu.items,
      newNode: {...node, ...props},
    }));
  }

  removeNode(node, path) {
    const getNodeKey = ({treeIndex}) => treeIndex;
    const {onChangeTree, menu, showModal} = this.props;

    const remove = () => {
      onChangeTree(removeNodeAtPath({
        path,
        getNodeKey,
        treeData: menu.items,
      }));
    };

    if (node.children && node.children.length) {
      showModal({
        text: 'Are you sure to delete item with sub-items?',
        onConfirm: () => remove(),
      });
    } else {
      remove();
    }
  }

  addItem() {
    const {onChangeTree, menu} = this.props;

    onChangeTree(
      menu.items.concat({
        title: `new`,
        url: '/',
      }),
    );
  }

  getMenuItemsCount() {
    const {menu} = this.props;
    let count = 0;

    const getCount = items => {
      const expandedItems = items.filter(item => item.expanded);
      count += items.length;

      expandedItems.forEach(item => {
        if (item.children && item.children.length) {
          getCount(item.children);
        }
      });
    };

    getCount(menu.items);
    return count;
  }

  getTitleField = (node, path) => {

    return (
      <div className={clsx("rst__field", node.error && "invalid")}>
        <span>Title </span>
        <input
          value={node.title}
          onChange={event => this.changeNode({title: event.target.value}, node, path)}
        />
        {node.error && <span className="rst__rowError">{node.error}</span>}
      </div>
    );
  }

  getSubTitleField = (node, path) => {
    return (
      <div className={clsx("rst__field", node.error && "invalid")}>
        <span>Url </span>
        <input
          value={node.url}
          onChange={event => this.changeNode({url: event.target.value}, node, path)}
        />
      </div>
    );
  }

  getButtons = (node, path) => {
    return (
      [
        <CustomButton
          styleType="delete"
          onClick={() => this.removeNode(node, path)}
        >
          Remove
        </CustomButton>,
      ]
    );
  }

  render() {
    const {menu, onChangeTree, fetching} = this.props;

    return (
      <Paper className="p-2">
        <CustomButton
          styleType="submit"
          onClick={() => this.addItem()}
          startIcon={<AddCircleIcon />}
        >
          Add new item
        </CustomButton>

        {/*<div*/}
        {/*  style={{*/}
        {/*    height: `${menu.items.length ? this.getMenuItemsCount() * 54 : 54}px`,*/}
        {/*    maxHeight: '80vh',*/}
        {/*    minHeight: '200px',*/}
        {/*  }}*/}
        {/*  className={clsx("rst", fetching && "fetching")}*/}
        {/*>*/}
        {/*  <DragDropContext>*/}
        {/*  /!*<DragDropContext onDragEnd={this.onDragEnd}>*!/*/}
        {/*    <Droppable droppableId="ROOT" isCombineEnabled>*/}
        {/*      {provided => (*/}
        {/*        <div ref={provided.innerRef}>*/}
        {/*          <TagItem*/}
        {/*            // noTransformClass={classes.noTransform}*/}
        {/*            item={menu.items}*/}
        {/*            // onDelete={this.removeChildTag}*/}
        {/*            // openTagEditView={openTagEditView}*/}
        {/*            // validatTagsNames={validatTagsNames}*/}
        {/*          />*/}
        {/*          {provided.placeholder}*/}
        {/*        </div>*/}
        {/*      )}*/}
        {/*    </Droppable>*/}
        {/*  </DragDropContext>*/}
        {/*</div>*/}

        <div
          style={{
            height: `${menu.items.length ? this.getMenuItemsCount() * 54 : 54}px`,
            maxHeight: '80vh',
            minHeight: '200px',
          }}
          className={clsx("rst", (fetching && "fetching"))}
        >
          <SortableTree
            treeData={menu.items}
            rowHeight={54}
            onChange={treeData => onChangeTree(treeData)}
            generateNodeProps={({node, path}) => ({
              title: this.getTitleField(node, path),
              subtitle: this.getSubTitleField(node, path),
              buttons: this.getButtons(node, path),
            })}
          />
        </div>
        <CustomButton
          styleType="submit"
          onClick={() => this.save()}
        >
          Save
        </CustomButton>
      </Paper>
    );
  }
}

const mapStateToProps = (state: State) => ({
  menu: state.menu,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getMenuItems()),
    onChangeTree: treeData => dispatch(changeMenuTree(treeData)),
    onSaveTree: treeData => dispatch(saveMenuTree(treeData)),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Menus as any);
