import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button} from 'reactstrap';
import SortableTree, {changeNodeAtPath, addNodeUnderParent, removeNodeAtPath} from 'react-sortable-tree';
import {changeMenuTree, getMenuItems} from "./actions";


export class Menus extends React.Component<any, any> {

  componentDidMount() {
    this.props.onInit();
  }

  getTitleField = (node, path) => {
    const getNodeKey = ({treeIndex}) => treeIndex;

    return (
      <div className="rst__field">
        <span>Title: </span>
        <input
          value={node.title}
          onChange={event => {
            const title = event.target.value;

            this.setState(state => ({
              treeData: changeNodeAtPath({
                path,
                getNodeKey,
                treeData: state.treeData,
                newNode: {...node, title},
              }),
            }));
          }}
        />
      </div>
    );
  }

  getSubTitleField = (node, path) => {
    const getNodeKey = ({treeIndex}) => treeIndex;

    return (
      <div className="rst__field">
        <span>Url: </span>
        <input
          value={node.url}
          onChange={event => {
            const url = event.target.value;

            this.setState(state => ({
              treeData: changeNodeAtPath({
                path,
                getNodeKey,
                treeData: state.treeData,
                newNode: {...node, url},
              }),
            }));
          }}
        />
      </div>
    );
  }

  getButtons = (node, path) => {
    const getNodeKey = ({treeIndex}) => treeIndex;

    return (
      [
        <Button
          color="info"
          size="sm"
          outline
          onClick={() =>
            this.setState(state => ({
              treeData: addNodeUnderParent({
                getNodeKey,
                treeData: state.treeData,
                parentKey: path[path.length - 1],
                expandParent: true,
                newNode: {
                  title: `new page`,
                  url: '/',
                },
              }).treeData,
            }))}
        >
          Add Child
        </Button>,
        <Button
          color="danger"
          size="sm"
          onClick={() =>
            this.setState(state => ({
              treeData: removeNodeAtPath({
                path,
                getNodeKey,
                treeData: state.treeData,
              }),
            }))}
        >
          Remove
        </Button>,
      ]
    );
  }

  constructor(props) {
    super(props);

    this.state = {
      treeData: [
        {
          title: 'Menu1 ',
          expanded: true,
          url: '/',
          children: [
            {
              title: 'Sub - Menu 1',
              url: '/',
              expanded: true,
              children: [
                {
                  title: 'Sub menu 2',
                  url: '/',
                },
              ],
            },
          ],
        },
      ],
    };
  }

  render() {
    const menuItems = this.props.menu;
    const {menu, onChangeTree} = this.props;

    return (
      <div style={{height: '700px'}} className="rst">
        <SortableTree
          treeData={this.state.treeData}
          onChange={treeData => this.setState({treeData})}
          generateNodeProps={({node, path}) => ({
            title: this.getTitleField(node, path),
            subtitle: this.getSubTitleField(node, path),
            buttons: this.getButtons(node, path),
          })}
        />
        <Button
          size="md"
          color="primary"
          onClick={() =>
            this.setState(state => ({
              treeData: state.treeData.concat({
                title: `new`,
                url: '/',
              }),
            }))}
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