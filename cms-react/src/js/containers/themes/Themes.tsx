import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {connect, Dispatch} from "react-redux";
import {getThemes} from "./actions";
import {Theme as ThemeModel} from "../../model";
import {Theme} from "./components/Theme";

interface Props {
  themes: ThemeModel[];
  onInit: () => any;
  match?: any;
  onEditLayout: (schema) => any;
}

export class Themes extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {themes, match, onEditLayout} = this.props;

    return (
      <div>
        {match.params.id &&
        <Theme
          theme={themes.find(theme => theme.id == match.params.id)}
          onSave={onEditLayout}
        />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  themes: state.themes.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getThemes()),
    onEditLayout: (schema) => undefined,
  };
};


export default connect(mapStateToProps, mapDispatchToProps)(Themes);
