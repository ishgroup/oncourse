import React from 'react';
import update from 'react-addons-update';
import {connect, Dispatch} from "react-redux";
import classnames from 'classnames';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {getRedirectSettings, setRedirectSettings} from "./actions";
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
        $push: [{from: '', to: ''}],
      },
    }));
  }

  onSave() {
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

  render() {
    const {rules} = this.state;
    const {fetching} = this.props;

    return (
      <div className={classnames({fetching})}>
        <p>
          Add 301 redirects to your website by entering the local path on the left (starting with '/')
          and the destination on the right (either starting with '/' for another local page or starting with
          http/https for redirecting to another website).
        </p>

        <FormGroup>
          <Button onClick={() => this.onAddNew()} color="primary">
            <span className="icon icon-add_circle"/> Add new
          </Button>
        </FormGroup>

        <div className="rules">
          {rules && rules.map((rule, index) =>
            <FormGroup key={index}>
              <div className="form-inline rule">

                <Label>From</Label>
                <Input
                  className={classnames({invalid: (rule.submitted && rule.to && !rule.from) || rule.error})}
                  type="text"
                  name={`from-${index}`}
                  id={`from-${index}`}
                  value={rule.from}
                  onChange={e => this.onChange(e, index, 'from')}
                />
                <Label>To</Label>
                <Input
                  className={classnames({invalid: (rule.submitted && !rule.to && rule.from) || rule.error})}
                  type="text"
                  name={`to-${index}`}
                  id={`to-${index}`}
                  value={rule.to}
                  onChange={e => this.onChange(e, index, 'to')}
                />

                <Button
                  color="danger"
                  className="outline"
                  onClick={() => this.onRemove(index)}
                >
                  <span className="icon icon-delete"/>
                  Remove
                </Button>
              </div>

              {rule.error &&
                <div className="form-inline">
                  <label className="error">{rule.error}</label>
                </div>
              }
            </FormGroup>,
          )}
        </div>

        <Button onClick={() => this.onSave()} color="primary">
          Save
        </Button>
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

export default connect(mapStateToProps, mapDispatchToProps)(Redirect);
