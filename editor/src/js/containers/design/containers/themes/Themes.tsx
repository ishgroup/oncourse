import React from 'react';
import {Grid} from "@material-ui/core";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {getLayouts, getThemes, saveTheme, updateThemeState} from "./actions";
import {Theme as ThemeModel} from "../../../../model";
import Theme from "./components/Theme";
import {State} from "../../../../reducers/state";
import {getBlocks, saveBlock} from "../../../content/containers/blocks/actions";
import {BlockState} from "../../../content/containers/blocks/reducers/State";

interface Props {
  themes: ThemeModel[];
  fetching: boolean;
  blocks: BlockState[];
  onInit: () => any;
  match?: any;
  onUpdateTheme: (theme) => any;
  onSaveTheme: (theme) => any;
  saveBlock: (blockId, settings) => any;
}

export class Themes extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {themes, match, onUpdateTheme, onSaveTheme, blocks, fetching, saveBlock} = this.props;
    const theme = match.params.id && themes.find(theme => theme.id == match.params.id);

    return (
      <>
        {theme &&
        <Grid item xs={12} className={clsx((fetching && "fetching"), "h-100")}>
          <Theme
            theme={theme}
            onUpdateTheme={(theme) => onUpdateTheme(theme)}
            onSaveTheme={(theme) => onSaveTheme(theme)}
            blocks={blocks}
            saveBlock={saveBlock}
          />
        </Grid>
        }
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  themes: state.theme.items,
  blocks: state.block.items,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => {
      dispatch(getThemes());
      dispatch(getBlocks());
      dispatch(getLayouts());
    },
    onUpdateTheme: (theme) => dispatch(updateThemeState(theme)),
    saveBlock: (blockId, settings) => dispatch(saveBlock(blockId, settings)),
    onSaveTheme: (theme: ThemeModel) => dispatch(saveTheme(theme)),
  };
};


export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Themes as any);
