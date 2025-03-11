import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { CogwhelAdornmentProps } from '../../../../model/common/ListView';
import { reconcileBanking } from '../actions';

class BankingCogwheelOptions extends React.PureComponent<CogwhelAdornmentProps & { reconcileBanking: any }, any> {
  reconcileBanking = () => {
    const {
     reconcileBanking, selection, closeMenu, showConfirm
    } = this.props;

    showConfirm({
     onConfirm: () => {
       reconcileBanking(selection);
     },
      confirmMessage: "You are about to reconcile selected records",
      confirmButtonText: "Continue"
    });
    closeMenu();
  };

  render() {
    const { selection, menuItemClass } = this.props;

    const hasSelected = Boolean(selection.length);

    const suffix = selection.length > 1 ? "s" : "";

    return (
      <MenuItem disabled={!hasSelected} className={menuItemClass} role="Completed" onClick={this.reconcileBanking}>
        {$t('reconcile_banking_deposit')}
        {suffix}
      </MenuItem>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  reconcileBanking: (ids: number[]) => dispatch(reconcileBanking(ids))
});

export default connect<any, any, any>(null, mapDispatchToProps)(BankingCogwheelOptions);
