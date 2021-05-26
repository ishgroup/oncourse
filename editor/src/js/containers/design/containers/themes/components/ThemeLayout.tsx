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
  onUpdate: (blockId, items) => any;
  saveBlock: (blockId, settings) => any;
}

class ThemeLayout extends React.Component<Props, any> {
  render() {
    const {blocks, classes, theme, onUpdate, saveBlock} = this.props;

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
              onUpdate={onUpdate}
              list={getThemeBlocks('top')}
              saveBlock={saveBlock}
            />
          </Grid>
        </Grid>

        <Grid container className={"pr-3"}>
          <Grid item xs={4} className={clsx(classes.blockWrapper, "pr-1-5")}>
            <Source
              placeholder="Left"
              id="left"
              onUpdate={onUpdate}
              list={getThemeBlocks('left')}
              saveBlock={saveBlock}
            />
          </Grid>

          <Grid item xs={4} className={clsx(classes.blockWrapper, "pr-1-5 pl-1-5")}>
            <Source
              placeholder="Middle"
              id="centre"
              onUpdate={onUpdate}
              list={getThemeBlocks('centre')}
              saveBlock={saveBlock}
            />
          </Grid>

          <Grid item xs={4} className={clsx(classes.blockWrapper, "pl-1-5")}>
            <Source
              placeholder="Right"
              id="right"
              onUpdate={onUpdate}
              list={getThemeBlocks('right')}
              saveBlock={saveBlock}
            />
          </Grid>
        </Grid>

        <Grid container className={"pr-3"}>
          <Grid item xs={12} className={classes.blockWrapper}>
            <Source
              placeholder="Footer"
              id="footer"
              onUpdate={onUpdate}
              list={getThemeBlocks('footer')}
              saveBlock={saveBlock}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

export default (withStyles(styles as any)(ThemeLayout));
