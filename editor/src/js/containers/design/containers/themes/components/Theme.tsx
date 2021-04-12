import * as React from "react";
import {Grid} from "@material-ui/core";
import {HTML5Backend} from "react-dnd-html5-backend";
import {DndProvider} from 'react-dnd';
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
      <div>
        <Grid container>
          <Grid item xs={7}>
            <ThemeLayout
              theme={theme}
              blocks={blocks}
              onUpdate={onUpdateLayout}
            />
          </Grid>

          <Grid item xs={5}>
            <Source
              className="blocks"
              showFilter={true}
              placeholder="Drag into regions to the left"
              id="default"
              list={getFreeBlocks(theme, blocks)}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

export default props => <DndProvider backend={HTML5Backend}>
  <Theme {...props}/>
</DndProvider>;
