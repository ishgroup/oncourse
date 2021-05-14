import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import {Grid, IconButton, Paper, Typography} from "@material-ui/core";
import clsx from "clsx";
import {AddCircle} from "@material-ui/icons";
import {withStyles} from "@material-ui/core/styles";
import {changeMenuTree, getMenuItems, saveMenuTree} from "./actions";
import {MenuState} from "./reducers/State";
import {showModal} from "../../../../common/containers/modal/actions";
import {State} from "../../../../reducers/state";
import CustomButton from "../../../../common/components/CustomButton";
import MenuItem from "./components/MenuItem";

const styles = theme => ({
  menuWrapper: {
    height: "calc(100vh - 30px)",
    overflowY: "auto",
  },
  deleteButton: {
    padding: `1px ${theme.spacing(1)}px`,
    fontWeight: 600,
  }
})

interface Props {
  classes: any;
  menu: MenuState;
  onInit: () => any;
  onChangeTree: (treeData) => any;
  onSaveTree: (treeData) => any;
  match: any;
  fetching: boolean;
  showModal: (props) => any;
}

class Menus extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      counter: 1
    }
  }

  componentDidMount() {
    this.props.onInit();
  }

  clearItemsBeforeSave = (menuItems: any[]) => {
    let result = [];

    if (menuItems && menuItems.length) {
      result = menuItems.map((elem) => {
        if (typeof elem.id === "string" && elem.id.includes('new')) {
          delete elem.id;
        }

        if (elem.dragId || elem.dragId === 0) delete elem.dragId;

        if (elem.children && elem.children.length) {
          this.clearItemsBeforeSave(elem.children);
        }

        return elem;
      })
    }

    return result;
  }

  setDragId = (items: any[], dragId) => {
    const itemsWithIds = items.map(item => {
      if (!item) return item;

      item.dragId = dragId.dragIndex;
      dragId.dragIndex += 1;
      if (item.children && item.children.length) {
        item.children = this.setDragId(item.children, dragId);
      }

      return item;
    })

    return itemsWithIds
  }

  save() {
    const {onSaveTree, menu} = this.props;

    const menuItems = this.clearItemsBeforeSave(menu.items);

    onSaveTree(menuItems);
  }

  updateValue = (items: any[], itemId: number, type: string, value: string) => {
    items.forEach((arrayItem: any) => {
      if (arrayItem.id === itemId) {
        arrayItem[type] = value;
      }
    });
    return items;
  }

  changeNode(value: string, type: string, itemId: number) {
    const {onChangeTree, menu} = this.props;
    const menuItems = menu.items;

    const result = this.updateValue(menuItems, itemId, type, value);

    onChangeTree(result);
  }

  removeNode(menuItemsWithIds: any[], item: any) {
    const {onChangeTree, showModal} = this.props;
    const removed = {removed: null};

    const remove = () => {
      this.removeItemFromList(menuItemsWithIds, item.id && item.id.toString(), removed);

      const dragId = {dragIndex: 0};
      const newListWithDragIds = this.setDragId(menuItemsWithIds, dragId);

      onChangeTree(newListWithDragIds);
    };

    if (item.children && item.children.length) {
      showModal({
        text: 'Are you sure to delete item with sub-items?',
        onConfirm: () => remove(),
      });
    } else {
      remove();
    }
  }

  addItem = () => {
    const {onChangeTree, menu} = this.props;

    const menuClone = JSON.parse(JSON.stringify(menu));

    onChangeTree(
      menuClone.items.concat({
        id: `new${this.state.counter}`,
        title: `new`,
        url: '/',
      }),
    );

    this.setState((state) => ({counter: state.counter + 1}));
  }

  putDeepValue = (items: any[], parentId: string, droppableItem: any) => {
    let parentItem;
    const BreakException = {};

    try {
      items.forEach((item, index) => {
        if (item.id && item.id.toString() === parentId) {
          parentItem = index;
          item.children.push(droppableItem);
          throw BreakException;
        } else if (item.children && item.children.length) {
          this.putDeepValue(item.children, parentId, droppableItem);
        }
      })
    } catch (e) {
      if (e !== BreakException) throw e;
    }
  }

  putValueWithoutCombine = (items: any[], frontDraggableId: number, draggedItem: any) => {
    const BreakException = {};

    try {
      items.forEach((item, index) => {
        if (item.dragId === frontDraggableId) {
          if (!(item.children && item.children.length)) {
            items.splice(draggedItem.dragId <= frontDraggableId ? index + 1 : index, 0, draggedItem)
            throw BreakException;
          } else {
            if (draggedItem.dragId >= frontDraggableId) {
              items.splice(index, 0, draggedItem)
            } else {
              item.children.unshift(draggedItem);
            }
            throw BreakException;
          }
        } else if (item.children && item.children.length) {
          this.putValueWithoutCombine(item.children, frontDraggableId, draggedItem);
        }
      })
    } catch (e) {
      if (e !== BreakException) throw e;
    }

    return items;
  }

  removeItemFromList = (items: any[], itemDraggableId: string, removed: any) => {
    const BreakException = {};

    try {
      items.forEach((item, index) => {
        if (item.id && item.id.toString() === itemDraggableId) {
          removed.removed = items.splice(index, 1)[0];
          throw BreakException;
        } else if (item.children && item.children.length) {
          this.removeItemFromList(item.children, itemDraggableId, removed);
        }
      })
    } catch (e) {
      if (e !== BreakException) throw e;
    }

    return removed
  }

  checkChildren = (item, parentId: string) => {
    let result = true;
    const BreakException = {};

    try {
      item.children && item.children.forEach((child) => {
        if (child.id.toString() === parentId) {
          result = false
        } else if (child.children && child.children.length) {
          this.checkChildren(child, parentId)
        }
      })
    } catch (e) {
      if (e !== BreakException) throw e;
    }

    return result;
  }

  canDrop = (items, parentDraggableId: string, currentDraggableId: string) => {
    let result = true;
    let element;

    const BreakException = {};

    try {
      items.forEach((item) => {
        if (item.id && item.id.toString() === currentDraggableId) {
          element = item;
        } else if (item.children && item.children.length) {
          this.canDrop(item.children, parentDraggableId, currentDraggableId);
        }

        if (element) {
          result = this.checkChildren(element, parentDraggableId);

          throw BreakException;
        }
      })
    } catch (e) {
      if (e !== BreakException) throw e;
    }

    return result
  }

  onDragEnd = (args: any) => {
    const {menu, onChangeTree} = this.props;
    const {combine, destination, source} = args;

    const menuItems = JSON.parse((JSON.stringify(menu.items)));

    if ((!destination && !combine) || (destination && destination.index === source.index)) return null;

    let result;

    if (combine) {
      if (!this.canDrop(menuItems, combine.draggableId, args.draggableId)) return null;
      const removed = {removed: null};
      this.removeItemFromList(menuItems, args.draggableId, removed);

      removed && this.putDeepValue(menuItems, combine.draggableId, removed.removed);

      result = menuItems;
    } else {
      const removed = {removed: null};

      const dragId = {dragIndex: 0}
      const menuItemsWithIds = menuItems && menuItems.length && menuItems[0].dragId !== 0
        ? this.setDragId(JSON.parse(JSON.stringify(menuItems)), dragId)
        : menuItems;

      this.removeItemFromList(menuItemsWithIds, args.draggableId, removed);

      result = this.putValueWithoutCombine(menuItemsWithIds, destination.index, removed.removed);
    }

    const dragId = {dragIndex: 0};
    const newListWithDragIds = this.setDragId(result, dragId);

    onChangeTree(newListWithDragIds);
  }

  render() {
    const {classes, fetching, menu, onChangeTree} = this.props;
    const { items: menuItems } = menu;

    const dragId = {dragIndex: 0}
    const menuItemsWithIds = menuItems && menuItems.length
      ? this.setDragId(JSON.parse(JSON.stringify(menuItems)), dragId)
      : menuItems;

    return (
      <Paper className={clsx(classes.menuWrapper, "p-2")}>
        <div className={"pb-3"}>
          <Grid container spacing={5}>
            <Grid item sm={12} lg={11} xl={8}>
              <div className="centeredFlex">
                <Typography className="heading">Add new item</Typography>
                <IconButton onClick={this.addItem}>
                  <AddCircle className="addButtonColor" width={20} />
                </IconButton>
              </div>

              <DragDropContext onDragEnd={this.onDragEnd}>
                <Droppable droppableId="ROOT" isCombineEnabled>
                  {provided => (
                    <div ref={provided.innerRef}>
                      {menuItemsWithIds && menuItemsWithIds.map((item, index) => (
                        <MenuItem
                          menuItemsWithIds={menuItemsWithIds}
                          removeItem={this.removeNode.bind(this)}
                          index={index}
                          item={item}
                          changeNode={this.changeNode.bind(this)}
                          key={item.id}
                        />
                      ))}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </DragDropContext>
            </Grid>
          </Grid>
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles as any)(Menus));
