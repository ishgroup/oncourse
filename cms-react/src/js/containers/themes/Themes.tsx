import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {connect, Dispatch} from "react-redux";
import {getThemes, saveTheme, updateThemeState} from "./actions";
import {Theme as ThemeModel} from "../../model";
import Theme from "./components/Theme";
import {Block} from "../../model/Block";
import {State} from "../../reducers/state";
import {getBlocks} from "../blocks/actions/index";

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
    const theme = match.params.id && themes.find(theme => theme.id == match.params.id);

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
