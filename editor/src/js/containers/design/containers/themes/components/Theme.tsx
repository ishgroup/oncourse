import * as React from "react";
import { DragDropContext } from "react-beautiful-dnd";
import {Grid, makeStyles} from "@material-ui/core";
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
  onUpdateTheme: (theme) => any;
  onSaveTheme: (theme) => any;
  saveBlock: (blockId, settings) => any;
  blocks?: Block[];
}

const Theme = (props: Props) => {
  const {theme, blocks, onSaveTheme, onUpdateTheme, saveBlock} = props;
  const classes = useStyles();

  const onDragEnd = (result) => {
    const updatedTheme = JSON.parse(JSON.stringify(theme));

    if (result.destination.droppableId === result.source.droppableId
      && result.destination.index === result.source.index) return null;

    if (result.destination.droppableId === result.source.droppableId
      && result.destination.droppableId === "default") return null;

    if (result.destination.droppableId === result.source.droppableId
      && result.destination.index !== result.source.index) {

      const [movedItem] = updatedTheme.blocks[result.source.droppableId].splice(result.source.index, 1);
      updatedTheme.blocks[result.destination.droppableId].splice(result.destination.index, 0, movedItem);

      onUpdateTheme(updatedTheme);
    } else if (result.destination.droppableId !== result.source.droppableId) {

      if (result.source.droppableId === "default") {
        const movedItem = blocks.find(elem => elem.id === +result.draggableId);
        updatedTheme.blocks[result.destination.droppableId].splice(result.destination.index, 0, {
          id: movedItem.id, title: movedItem.title}
        );
        updatedTheme.blocks[result.destination.droppableId].forEach((elem, index) => elem.position = index);
      } else if (result.destination.droppableId === "default") {
        updatedTheme.blocks[result.source.droppableId].splice(result.source.index, 1);
      } else {
        const [movedItem] = updatedTheme.blocks[result.source.droppableId].splice(result.source.index, 1);
        updatedTheme.blocks[result.destination.droppableId].splice(result.destination.index, 0, movedItem);

        updatedTheme.blocks[result.source.droppableId].forEach((elem, index) => elem.position = index);
        updatedTheme.blocks[result.destination.droppableId].forEach((elem, index) => elem.position = index);
      }

      onUpdateTheme(updatedTheme);
      onSaveTheme(updatedTheme);
    }
  }

  const removeBlock = (index, sourceId) => {
    const updatedTheme = JSON.parse(JSON.stringify(theme));

    updatedTheme.blocks[sourceId].splice(index, 1);

    onUpdateTheme(updatedTheme);
    onSaveTheme(updatedTheme);
  }

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <Grid container className={"h-100"}>
        <Grid item xs={7} className={classes.maxHeight100}>
          <ThemeLayout
            theme={theme}
            blocks={blocks}
            saveBlock={saveBlock}
            removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
          />
        </Grid>

        <Grid item xs={5} className={classes.blocksWrapper}>
          <Source
            className="blocks"
            showFilter={true}
            placeholder={(<span>BLOCKS<br/>Drag into regions<br/>to the left.</span>)}
            noUpperCase={true}
            id="default"
            cards={getFreeBlocks(theme, blocks)}
            saveBlock={saveBlock}
            removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
          />
        </Grid>
      </Grid>
    </DragDropContext>
  );
}

export default (props) => <Theme {...props}/>;
