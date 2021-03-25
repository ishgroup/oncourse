import * as React from "react";
import {HTML5Backend} from "react-dnd-html5-backend";
import {DndProvider} from 'react-dnd';
import {Row, Col} from 'reactstrap';
import {Theme as ThemeModel, Block} from "../../../../../model";
import {ThemeLayout} from "./ThemeLayout";
import Source from "../containers/Source";

const getFreeBlocks = (theme, blocks) => {
  const themeBlockIds = [];
  Object.values(theme.blocks).map((a: any[]) => a.map(a => themeBlockIds.push(a.id)));
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
              showFilter={true}
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

export default props => <DndProvider backend={HTML5Backend}>
  <Theme {...props}/>
</DndProvider>;
