import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {connect, Dispatch} from "react-redux";
import {getThemes} from "./actions";
import {Theme as ThemeModel} from "../../model";
import Theme from "./components/Theme";
import {Block} from "../../model/Block";
import {State} from "../../reducers/state";
import {getBlocks} from "../blocks/actions/index";

const getFreeBlocks = (theme, blocks) => {
  const themeBlockIds = [];
  [...Object.values(theme.schema)].map(a => a.map(a => themeBlockIds.push(a.id)));
  return blocks.filter(block => !themeBlockIds.includes(block.id));
};

interface Props {
  themes: ThemeModel[];
  blocks: Block[];
  onInit: () => any;
  match?: any;
  onEditLayout: (schema) => any;
}

export class Themes extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {themes, match, onEditLayout, blocks} = this.props;
    const theme = match.params.id && themes.find(theme => theme.id == match.params.id);

    return (
      <div>
        {theme &&
        <Theme
          theme={themes.find(theme => theme.id == match.params.id)}
          onSave={onEditLayout}
          blocks={blocks}
        />
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
    onEditLayout: schema => undefined,
  };
};


export default connect(mapStateToProps, mapDispatchToProps)(Themes);
