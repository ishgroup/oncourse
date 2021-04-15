import React from 'react';
import update from 'react-addons-update';
import {withStyles} from "@material-ui/core/styles";
import {Grid, Paper, TextField} from "@material-ui/core";
import AddCircleIcon from '@material-ui/icons/AddCircle';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {getRedirectSettings, setRedirectSettings} from "./actions";
import RedirectItem from "./components/RedirectItem";
import {RedirectSettingsState} from "./reducers/State";
import {State} from "../../../../reducers/state";
import CustomButton from "../../../../common/components/CustomButton";

const styles: any = theme => ({
  redirectWrapper: {
    maxHeight: "calc(100vh - 30px)",
    boxSizing: "border-box",
    overflow: "hidden",
  },
  redirectItemWrapper: {
    maxHeight: "calc(100vh - 230px)",
    overflowY: "auto",
  },
  saveButton: {
    marginLeft: theme.spacing(2),
  },
});

interface Props {
  classes: any;
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
    const {rules} = this.state;

    this.setState({rules: rules.filter(elem => elem.from !== index)})
  }

  onChangeFilter(e) {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {rules, filter} = this.state;
    const {classes, fetching} = this.props;

    return (
      <Paper className={clsx((fetching && "fetching"), "p-3", classes.redirectWrapper)}>
        <p className="mb-1">
          Add 301 redirects to your website by entering the local path on the left (starting with '/')
          and the destination on the right (either starting with '/' for another local page or starting with
          http/https for redirecting to another website).
        </p>

        {rules && rules.length > 0 &&
          <TextField
            type="text"
            name="filter"
            placeholder="Filter"
            id="filter"
            value={filter}
            onChange={e => this.onChangeFilter(e)}
          />
        }

        <Grid className="mt-2 mb-3">
          <CustomButton
            styleType="submit"
            onClick={() => this.onAddNew()}
            startIcon={<AddCircleIcon />}
          >
            Add new
          </CustomButton>
          <CustomButton
            styleType="submit"
            onClick={() => this.onSave()}
            styles={classes.saveButton}
          >
            Save
          </CustomButton>
        </Grid>

        <Grid container className={clsx("mt-3", classes.redirectItemWrapper)}>
          <Grid item xs={12} md={10} lg={8} xl={6}>
            {rules && rules
              .filter(r => r.from.indexOf(filter) !== -1 || r.to.indexOf(filter) !== -1 || !r.from || !r.to)
              .map((rule, index) =>
                <RedirectItem
                  key={rule.from}
                  item={rule}
                  index={index}
                  onChange={this.onChange.bind(this)}
                  onRemove={this.onRemove.bind(this, rule.from)}
                />,
              )}
          </Grid>
        </Grid>
      </Paper>
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Redirect));
