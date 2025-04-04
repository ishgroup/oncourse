import AddIcon from '@mui/icons-material/Add';
import { Menu, MenuItem, Typography } from '@mui/material';
import Fab from '@mui/material/Fab';
import $t from '@t';
import * as React from 'react';
import { change } from 'redux-form';
import { withStyles } from 'tss-react/mui';

const styles = theme =>
  ({
    menuWrapper: {
      marginTop: theme.spacing(2)
    },
    listItemPadding: {
      padding: theme.spacing(0.5, 8, 0.5, 2),
      minHeight: "unset"
    },
    fab: {
      position: "relative",
      left: theme.spacing(-1)
    }
  });

class ScriptAddMenu extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      anchorEl: null
    };
  }

  componentWillReceiveProps(nextProps) {}

  handleAddFieldClick = e => {
    this.setState({ anchorEl: e.currentTarget });
  };

  handleAddFieldClose = () => {
    this.setState({ anchorEl: null });
  };

  addComponent = e => {
    const { addComponent } = this.props;
    addComponent(e.currentTarget.getAttribute("data-component"));
  };

  addImport = () => {
    const { dispatch, form } = this.props;

    dispatch(change(form, "imports", [""]));
  };

  render() {
    const { anchorEl } = this.state;
    const { classes, values, hasUpdateAccess } = this.props;

    const hasImports = Boolean(values && values.imports);

    return (
      <>
        <Menu
          id="field-types-menu"
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={this.handleAddFieldClose}
          className={classes.menuWrapper}
          MenuListProps={{
            classes: {
              root: "p-1"
            }
          }}
        >
          <div className="outline-none mt-1">
            <div className="d-flex">
              <div className="flex-fill">
                <Typography className="heading pl-2 mb-1">{$t('action')}</Typography>
                <MenuItem
                  disabled={hasImports || !hasUpdateAccess}
                  className={classes.listItemPadding}
                  data-component="Import"
                  onClick={this.addImport}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    {$t('import')}
                  </Typography>
                </MenuItem>

                <MenuItem
                  className={classes.listItemPadding}
                  data-component="Query"
                  onClick={this.addComponent}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    {$t('query')}
                  </Typography>
                </MenuItem>

                <MenuItem
                  disabled={!hasUpdateAccess}
                  className={classes.listItemPadding}
                  data-component="Script"
                  onClick={this.addComponent}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    {$t('script')}
                  </Typography>
                </MenuItem>

                <MenuItem
                  className={classes.listItemPadding}
                  data-component="Message"
                  onClick={this.addComponent}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    {$t('message')}
                  </Typography>
                </MenuItem>

                <MenuItem
                  className={classes.listItemPadding}
                  data-component="Report"
                  onClick={this.addComponent}
                >
                  <Typography variant="subtitle1" color="textSecondary">
                    {$t('report')}
                  </Typography>
                </MenuItem>
              </div>
            </div>
          </div>
        </Menu>
        <Fab
          type="button"
          size="small"
          color="primary"
          classes={{
            sizeSmall: "appBarFab",
            root: classes.fab
          }}
          aria-owns={anchorEl ? "field-types-menu" : null}
          aria-haspopup="true"
          onClick={this.handleAddFieldClick}
        >
          <AddIcon />
        </Fab>
      </>
    );
  }
}

export default withStyles(ScriptAddMenu, styles);
