import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import classnames from 'classnames';
import SortableTree, {changeNodeAtPath, addNodeUnderParent, removeNodeAtPath} from 'react-sortable-tree';
import {changeMenuTree, getMenuItems} from "./actions";
import {MenuState} from "./reducers/State";

interface Props {
  menu: MenuState;
  onInit: () => any;
  onChangeTree: (treeData) => any;
  match: any;
}

export class Menus extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  changeNode(props, node, path) {
    const getNodeKey = ({treeIndex}) => treeIndex;
    const {onChangeTree, menu} = this.props;

    const key = Object.keys(props)[0];
    node.errors = node.errors || {};
    node.errors[key] = props[key] === '';

    onChangeTree(changeNodeAtPath({
      path,
      getNodeKey,
      treeData: menu.items,
      newNode: {...node, ...props},
    }));
  }

  addNode(path) {
    const getNodeKey = ({treeIndex}) => treeIndex;
    const {onChangeTree, menu} = this.props;

    onChangeTree(addNodeUnderParent({
      getNodeKey,
      treeData: menu.items,
      parentKey: path[path.length - 1],
      expandParent: true,
      newNode: {
        title: `new page`,
        url: '/',
      },
    }).treeData);
  }

  removeNode(node, path) {
    const getNodeKey = ({treeIndex}) => treeIndex;
    const {onChangeTree, menu} = this.props;

    const remove = () => {
      onChangeTree(removeNodeAtPath({
        path,
        getNodeKey,
        treeData: menu.items,
      }));
    };

    if (node.children && node.children.length) {
      if (confirm('Are you sure to delete item with sub-items?')) {
        remove();
      } else {
        return;
      }
    }

    remove();
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

  getTitleField = (node, path) => {
    return (
      <div className={classnames("rst__field", {invalid: node.errors && node.errors.title})}>
        <span>Title: </span>
        <input
          value={node.title}
          onChange={event => this.changeNode({title: event.target.value}, node, path)}
        />
      </div>
    );
  }

  getSubTitleField = (node, path) => {
    return (
      <div className={classnames("rst__field", {invalid: node.errors && node.errors.url})}>
        <span>Url: </span>
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
        <Button
          color="success"
          size="sm"
          outline
          onClick={() => this.addNode(path)}
        >
          <span className="icon icon-add_circle"/>
          Add child
        </Button>,
        <Button
          color="danger"
          size="sm"
          onClick={() => this.removeNode(node, path)}
        >
          <span className={classnames("icon", node.children && node.children.length ? "icon-delete_sweep" :"icon-delete")}/>
          Remove
        </Button>,
      ]
    );
  }

  render() {
    const {menu, onChangeTree} = this.props;

    return (
      <div style={{height: '700px'}} className="rst">
        <SortableTree
          treeData={menu.items}
          rowHeight={48}
          onChange={treeData => onChangeTree(treeData)}
          generateNodeProps={({node, path}) => ({
            title: this.getTitleField(node, path),
            subtitle: this.getSubTitleField(node, path),
            buttons: this.getButtons(node, path),
          })}
        />
        <Button
          size="md"
          color="primary"
          onClick={() => this.addItem()}
        >
          Add more
        </Button>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  menu: state.menu,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getMenuItems()),
    onChangeTree: treeData => dispatch(changeMenuTree(treeData)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Menus);
