import React from 'react';
import update from 'react-addons-update';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import classnames from 'classnames';
import {Button, Col, FormGroup, Input, Row} from 'reactstrap';
import {getRedirectSettings, setRedirectSettings} from "./actions";
import {RedirectItem} from "./components/RedirectItem";
import {RedirectSettingsState} from "./reducers/State";
import {State} from "../../../../reducers/state";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  redirect: RedirectSettingsState;
  fetching: boolean;
}

export class Redirect extends React.PureComponent<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      rules: props.redirect.rules,
      filter: '',
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.redirect.refreshSettings) {
      this.setState({rules: props.redirect.rules});
    }
  }

  onChange(e, index, key) {
    this.setState(update(this.state, {
      rules: {[index]: {
        [key]: {$set: e.target.value},
        submitted: {$set: false},
      }},
    }));
  }

  onAddNew() {
    this.setState(update(this.state, {
      rules: {
        $unshift: [{from: '', to: ''}],
      },
    }));
  }

  onSave = () => {
    const {onSave} = this.props;
    this.setState({
      rules: this.state.rules.map(rule => ({...rule, submitted: true})),
    });

    const rules = this.state.rules
      .filter(rule => rule.from || rule.to)
      .map(rule => ({from: rule.from, to: rule.to}));

    if (rules.filter(rule => (rule.from && !rule.to) || (!rule.from && rule.to)).length) return;

    this.setState({rules});
    onSave({rules});
  }

  onRemove(index) {
    this.setState(update(this.state, {
      rules: {
        $splice: [
          [index, 1],
        ],
      },
    }));
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
        <p>
          Add 301 redirects to your website by entering the local path on the left (starting with '/')
          and the destination on the right (either starting with '/' for another local page or starting with
          http/https for redirecting to another website).
        </p>

        {rules && rules.length > 0 &&
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
        }

        <FormGroup>
          <Button onClick={() => this.onAddNew()} color="primary">
            <span className="icon icon-add_circle"/> Add new
          </Button>
          <Button onClick={this.onSave} color="primary">
            Save
          </Button>
        </FormGroup>

        <div className="rules">
          {rules && rules
            .filter(r => r.from.indexOf(filter) !== -1 || r.to.indexOf(filter) !== -1 || !r.from || !r.to)
            .map((rule, index) =>
              <RedirectItem
                key={index}
                item={rule}
                index={index}
                onChange={this.onChange.bind(this)}
                onRemove={this.onRemove.bind(this)}
              />,
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  redirect: state.settings.redirectSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getRedirectSettings()),
    onSave: settings => dispatch(setRedirectSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Redirect as any);
