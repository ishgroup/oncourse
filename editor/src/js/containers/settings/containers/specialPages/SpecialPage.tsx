import React from "react";
import update from "react-addons-update";
import {connect, Dispatch} from "react-redux";
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
import {
  SpecialPage as SpecialPageType,
  URLMatchRule,
} from "../../../../model";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  specialPage: SpecialPageSettingsState;
  fetching: boolean;
}

export class SpecialPage extends React.PureComponent<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      rules: props.specialPage.rules,
      filter: "",
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.specialPage.refreshSettings) {
      this.setState({rules: props.specialPage.rules});
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

  onAddNew() {
    this.setState(
      update(this.state, {
        rules: {
          $unshift: [{from: "", specialPage: SpecialPageType[Object.keys(SpecialPageType)[0]], matchType: URLMatchRule[Object.keys(URLMatchRule)[0]]}],
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
      .filter(({from, specialPage, matchType}) => from || specialPage || matchType)
      .map(({from, specialPage, matchType}) => ({from, specialPage, matchType}));

    if (
      rules.filter(({from, specialPage, matchType}) => !from || !specialPage || !matchType)
        .length
    )
      return;

    this.setState({rules});
    onSave({specialPages: rules});
  }

  onRemove(index) {
    this.setState(
      update(this.state, {
        rules: {
          $splice: [[index, 1]],
        },
      }),
    );
  }

  onChangeFilter(e) {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {rules, filter} = this.state;
    const {fetching} = this.props;

    return (
      <div className={classnames({fetching})}>
        {rules &&
          rules.length > 0 && (
            <Row>
              <Col sm="2">
                <FormGroup>
                  <Input
                    type="text"
                    name="filter"
                    placeholder="Filter"
                    id="filter"
                    value={filter}
                    onChange={e => this.onChangeFilter(e)}
                  />
                </FormGroup>
              </Col>
            </Row>
          )}

        <FormGroup>
          <Button onClick={() => this.onAddNew()} color="primary">
            <span className="icon icon-add_circle" /> Add new
          </Button>
        </FormGroup>

        <div className="rules">
          {rules &&
            rules
              .filter(
                r =>
                  r.from.indexOf(filter) !== -1 ||
                  r.specialPage.indexOf(filter) !== -1 ||
                  r.matchType.indexOf(filter) !== -1 ||
                  !r.from ||
                  !r.specialPage ||
                  !r.matchType,
              )
              .map((rule, index) => (
                <SpecialPageItem
                  key={index}
                  item={rule}
                  index={index}
                  onChange={this.onChange.bind(this)}
                  onRemove={this.onRemove.bind(this)}
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
  specialPage: state.settings.specialPageSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getSpecialPageSettings()),
    onSave: settings => dispatch(setSpecialPageSettings(settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(SpecialPage);
