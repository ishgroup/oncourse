import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {connect, Dispatch} from "react-redux";
import {getThemes, saveTheme, updateThemeState} from "./actions";
import {Theme as ThemeModel, Block, ThemeSchema} from "../../model";
import Theme from "./components/Theme";
import {State} from "../../reducers/state";
import {getBlocks} from "../blocks/actions";

export const getDefaultTheme = () => {
  const defaultTheme = new ThemeModel();
  defaultTheme.id = -1;
  defaultTheme.schema = new ThemeSchema();
  defaultTheme.schema.top = [];
  defaultTheme.schema.footer = [];
  defaultTheme.schema.middle1 = [];
  defaultTheme.schema.middle2 = [];
  defaultTheme.schema.middle3 = [];

  return defaultTheme;
};

interface Props {
  themes: ThemeModel[];
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
    const {themes, match, onUpdateLayout, onSaveTheme, blocks} = this.props;
    const theme = match.params.id && (themes.find(theme => theme.id == match.params.id) || getDefaultTheme());

    return (
      <div>
        {theme &&
        <Col sm="12">
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
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => {
      dispatch(getThemes());
      dispatch(getBlocks());
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
