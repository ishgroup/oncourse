import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {DragDropContext, DragSource} from 'react-dnd';
import HTML5Backend, {NativeTypes} from 'react-dnd-html5-backend';
import {Theme as ThemeModel} from "../../../model";
import {Block} from "../../../model/Block";
import {ThemeLayout} from "./ThemeLayout";

interface Props {
  theme: ThemeModel;
  blocks?: Block[];
  onSave: (themeId, schema) => void;
}

class Theme extends React.Component<Props, any> {

  render() {
    const {theme, blocks} = this.props;

    return (
      <div className="draggable-area">

        <ThemeLayout
          theme={theme}
          blocks={blocks}
        />
      </div>
    );
  }
}

export default DragDropContext(HTML5Backend)(Theme);
