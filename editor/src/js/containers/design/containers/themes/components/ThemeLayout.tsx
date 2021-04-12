import React from 'react';
import {Grid} from "@material-ui/core";
import {Theme as ThemeModel} from "../../../../../model";
import Source from "../containers/Source";

interface Props {
  theme: ThemeModel;
  blocks: any;
  onUpdate: (blockId, items) => any;
}

export class ThemeLayout extends React.Component<Props, any> {
  render() {
    const {theme, blocks, onUpdate} = this.props;

    const getThemeBlocks = key => {
      if (!theme.blocks) return [];

      return theme.blocks[key].map(({id}) => blocks.find(block => block.id === id));
    };

    return (
      <div className="theme__layout">
        <Grid container>
          <Grid item xs={12}>
            <Source
              placeholder="Header"
              id="top"
              onUpdate={onUpdate}
              list={getThemeBlocks('top')}
            />
          </Grid>
        </Grid>

        <Grid container>
          <Grid item xs={4}>
            <Source
              placeholder="Left"
              id="left"
              onUpdate={onUpdate}
              list={getThemeBlocks('left')}
            />
          </Grid>

          <Grid item xs={4}>
            <Source
              placeholder="Middle"
              id="centre"
              onUpdate={onUpdate}
              list={getThemeBlocks('centre')}
            />
          </Grid>

          <Grid item xs={4}>
            <Source
              placeholder="Right"
              id="right"
              onUpdate={onUpdate}
              list={getThemeBlocks('right')}
            />
          </Grid>
        </Grid>

        <Grid container>
          <Grid item xs={12}>
            <Source
              placeholder="Footer"
              id="footer"
              onUpdate={onUpdate}
              list={getThemeBlocks('footer')}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}
