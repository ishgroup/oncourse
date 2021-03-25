import React from "react";
import update from "react-addons-update";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import classnames from "classnames";
import {
  Container,
  Row,
  Col,
  Button,
  Form,
  FormGroup,
  Label,
  Input,
  FormText,
} from "reactstrap";
import {getSpecialPageSettings, setSpecialPageSettings} from "./actions";
import {SpecialPageSettingsState} from "./reducers/State";
import {State} from "../../../../reducers/state";
import {SpecialPageItem} from "./components/SpecialPageItem";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  specialPages: SpecialPageSettingsState;
  fetching: boolean;
}

export class SpecialPage extends React.PureComponent<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      rules: props.specialPages.rules,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.specialPages.refreshSettings) {
      this.setState({rules: props.specialPages.rules});
    }
  }

  onChange(e, index, key) {
    this.setState(
      update(this.state, {
        rules: {
          [index]: {
            [key]: {$set: e.target.value},
            submitted: {$set: false},
          },
        },
      }),
    );
  }

  onSave() {
    const {onSave} = this.props;
    this.setState({
      rules: this.state.rules.map(rule => ({...rule, submitted: true})),
    });

    const rules = this.state.rules
      .map(({from, specialPage, matchType}) => ({from, specialPage, matchType}));


    this.setState({rules});
    onSave({rules});
  }

  render() {
    const {rules} = this.state;
    const {fetching} = this.props;

    return (
      <div className={classnames({fetching})}>
        <div className="rules">
          {rules &&
            rules
              .map((rule, index) => (
                <SpecialPageItem
                  key={index}
                  item={rule}
                  index={index}
                  onChange={this.onChange.bind(this)}
                />
              ))}
        </div>

        <Button onClick={() => this.onSave()} color="primary">
          Save
        </Button>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  specialPages: state.settings.specialPageSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getSpecialPageSettings()),
    onSave: settings => dispatch(setSpecialPageSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SpecialPage as any);
