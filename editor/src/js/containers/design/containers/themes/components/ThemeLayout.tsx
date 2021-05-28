import React from 'react';
import {Grid} from "@material-ui/core";
import {withStyles} from "@material-ui/core/styles";
import clsx from "clsx";
import {Theme as ThemeModel} from "../../../../../model";
import Source from "../containers/Source";

const styles = () => ({
  layoutWrapper: {
    display: "grid",
    height: "100%",
    maxHeight: "100%",
    overflowY: "hidden",
    gridGap: "16px",
    gridTemplateRows: "repeat(3, calc((100% - 30px) / 3))",
  },
  blockWrapper: {
    height: "100%",
    overflowY: "auto",
  }
})

interface Props {
  classes: any;
  theme: ThemeModel;
  blocks: any;
  saveBlock: (blockId, settings) => any;
  removeBlock: (index, sourceId) => any;
}

class ThemeLayout extends React.Component<Props, any> {
  render() {
    const {blocks, classes, theme, removeBlock, saveBlock} = this.props;

    const getThemeBlocks = key => {
      if (!theme.blocks) return [];

      return theme.blocks[key].map(({id}) => blocks.find(block => block.id === id));
    };

    return (
      <div className={classes.layoutWrapper}>
        <Grid container className={"pr-3"}>
          <Grid item xs={12} className={classes.blockWrapper}>
            <Source
              placeholder="Header"
              id="top"
              cards={getThemeBlocks('top')}
              saveBlock={saveBlock}
              removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
            />
          </Grid>
        </Grid>

        <Grid container className={"pr-3"}>
          <Grid item xs={4} className={clsx(classes.blockWrapper, "pr-1-5")}>
            <Source
              placeholder="Left"
              id="left"
              cards={getThemeBlocks('left')}
              saveBlock={saveBlock}
              removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
            />
          </Grid>

          <Grid item xs={4} className={clsx(classes.blockWrapper, "pr-1-5 pl-1-5")}>
            <Source
              placeholder="Middle"
              id="centre"
              cards={getThemeBlocks('centre')}
              saveBlock={saveBlock}
              removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
            />
          </Grid>

          <Grid item xs={4} className={clsx(classes.blockWrapper, "pl-1-5")}>
            <Source
              placeholder="Right"
              id="right"
              cards={getThemeBlocks('right')}
              saveBlock={saveBlock}
              removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
            />
          </Grid>
        </Grid>

        <Grid container className={"pr-3"}>
          <Grid item xs={12} className={classes.blockWrapper}>
            <Source
              placeholder="Footer"
              id="footer"
              cards={getThemeBlocks('footer')}
              saveBlock={saveBlock}
              removeBlock={(index, sourceId) => removeBlock(index, sourceId)}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

export default (withStyles(styles as any)(ThemeLayout));
