import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {getLayouts, getThemes, saveTheme, updateThemeState} from "./actions";
import {Theme as ThemeModel, Block} from "../../../../model";
import Theme from "./components/Theme";
import {State} from "../../../../reducers/state";
import {getBlocks} from "../../../content/containers/blocks/actions";

interface Props {
  themes: ThemeModel[];
  fetching: boolean;
  blocks: Block[];
  onInit: () => any;
  match?: any;
  onUpdateLayout: (themeId, blockId, items) => any;
  onSaveTheme: (theme) => any;
}

export class Themes extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {themes, match, onUpdateLayout, onSaveTheme, blocks, fetching} = this.props;
    const theme = match.params.id && themes.find(theme => theme.id == match.params.id);

    return (
      <div>
        {theme &&
        <Col sm="12" className={classnames({fetching})}>
          <Theme
            theme={theme}
            onUpdateLayout={(blockId, items) => onUpdateLayout(theme, blockId, items)}
            onSaveTheme={() => onSaveTheme(theme)}
            blocks={blocks}
          />
        </Col>
        }
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  themes: state.theme.items,
  blocks: state.block.items,
  fetching: state.theme.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => {
      dispatch(getThemes());
      dispatch(getBlocks());
      dispatch(getLayouts());
    },
    onUpdateLayout: (theme, blockId, items) => {
      dispatch(updateThemeState(
        {
          ...theme,
          schema: {
            ...theme.schema,
            [blockId]: items.map((block, index) => ({id: block.id, position: index})),
          },
        },
      ));
    },
    onSaveTheme: (theme: ThemeModel) => dispatch(saveTheme(theme.id, theme)),
  };
};


export default connect(mapStateToProps, mapDispatchToProps)(Themes);
