import * as React from "react";
import {Grid, makeStyles} from "@material-ui/core";
import {HTML5Backend} from "react-dnd-html5-backend";
import {DndProvider} from 'react-dnd';
import {Theme as ThemeModel, Block} from "../../../../../model";
import ThemeLayout from "./ThemeLayout";
import Source from "../containers/Source";

const useStyles = makeStyles({
  blocksWrapper: {
    maxHeight: "100%",
    overflowY: "auto",
  },
  maxHeight100: {
    maxHeight: "100%",
  },
});

const getFreeBlocks = (theme, blocks) => {
  const themeBlockIds = [];
  Object.values(theme.blocks).forEach((a: any[]) => a.forEach(a => themeBlockIds.push(a.id)));
  return blocks.filter(block => !themeBlockIds.includes(block.id));
};

interface Props {
  theme: ThemeModel;
  onUpdateLayout: (blockId, items) => any;
  onSaveTheme: () => any;
  saveBlock: (blockId, settings) => any;
  blocks?: Block[];
}

const Theme = (props: Props) => {
  const {theme, blocks, onUpdateLayout, saveBlock} = props;
  const classes = useStyles();

  return (
    <>
      <Grid container className={"h-100"}>
        <Grid item xs={7} className={classes.maxHeight100}>
          <ThemeLayout
            theme={theme}
            blocks={blocks}
            onUpdate={onUpdateLayout}
            saveBlock={saveBlock}
          />
        </Grid>

        <Grid item xs={5} className={classes.blocksWrapper}>
          <Source
            className="blocks"
            showFilter={true}
            placeholder={(<span>BLOCKS<br/>Drag into regions<br/>to the left.</span>)}
            noUpperCase={true}
            id="default"
            list={getFreeBlocks(theme, blocks)}
            saveBlock={saveBlock}
          />
        </Grid>
      </Grid>
    </>
  );
}

export default (props) => <DndProvider backend={HTML5Backend}>
  <Theme {...props}/>
</DndProvider>;