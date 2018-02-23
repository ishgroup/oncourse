import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {DragDropContext, DragSource} from 'react-dnd';
import HTML5Backend, {NativeTypes} from 'react-dnd-html5-backend';
import {Theme as ThemeModel, Block} from "../../../../../model";
import {ThemeLayout} from "./ThemeLayout";
import Source from "../containers/Source";

const getFreeBlocks = (theme, blocks) => {
  const themeBlockIds = [];
  [...Object.values(theme.schema)].map(a => a.map(a => themeBlockIds.push(a.id)));
  return blocks.filter(block => !themeBlockIds.includes(block.id));
};

interface Props {
  theme: ThemeModel;
  blocks?: Block[];
  onUpdateLayout: (blockId, items) => any;
  onSaveTheme: () => any;
}

class Theme extends React.Component<Props, any> {

  render() {
    const {theme, blocks, onUpdateLayout} = this.props;

    return (
      <div className="theme">
        <Row>
          <Col md="7">
            <ThemeLayout
              theme={theme}
              blocks={blocks}
              onUpdate={onUpdateLayout}
            />
          </Col>

          <Col md="5">
            <Source
              className="blocks"
              placeholder="Blocks"
              id="default"
              list={getFreeBlocks(theme, blocks)}
            />
          </Col>
        </Row>

      </div>
    );
  }
}

export default DragDropContext(HTML5Backend)(Theme);
