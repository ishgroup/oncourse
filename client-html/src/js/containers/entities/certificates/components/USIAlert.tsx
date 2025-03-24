import { CertificateValidationRequest, SearchQuery, Sorting } from '@api/model';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { BooleanArgFunction } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { Dispatch, useCallback, useEffect } from 'react';
import { connect } from 'react-redux';
import { setPrintValidatingStatus } from '../../../../common/components/list-view/components/share/actions';
import { getExpression } from '../../../../common/components/list-view/utils/listFiltersUtils';
import { State } from '../../../../reducers/state';
import { setCertificatesValidationStatus, validateCertificates } from '../actions';

interface USIAlertProps {
  selection?: any[];
  setPrintValidatingStatus?: BooleanArgFunction;
  setCertificatesValidationStatus?: (status: string) => void;
  validate?: (validationRequest: CertificateValidationRequest) => void;
  validating?: boolean;
  validationStatus?: string;
  searchQuery: SearchQuery
  sort?: Sorting[];
  selectAll?: boolean;
}

const getCertificateValidationRequest = (
  selectAll: boolean,
  searchQuery: SearchQuery,
  selection: string[],
  sorting: Sorting[]
): CertificateValidationRequest => {
  const requestFilters = selectAll ? {
    search: searchQuery.search,
    filter: searchQuery.filter,
    tagGroups: searchQuery.tagGroups
  } : {
    search: getExpression(selection),
    filter: "",
    tagGroups: []
  };

  return {
    ...requestFilters,
    sorting
  };
};

const USIAlert: React.FunctionComponent<USIAlertProps> = props => {
  const {
    validate,
    selection,
    setPrintValidatingStatus,
    validationStatus,
    validating,
    setCertificatesValidationStatus,
    sort,
    searchQuery,
    selectAll
  } = props;
  
  const debounceValidate = useCallback(debounce((...args: [any, any, any, any]) => {
    setPrintValidatingStatus(true);
    validate(getCertificateValidationRequest(...args));
  }, 300), []);

  useEffect(
    () => {
      debounceValidate(selectAll, searchQuery, selection, sort);
      return () => setCertificatesValidationStatus(null);
    },
    [selectAll, searchQuery, selection, sort]
  );

  return (
    <>
      {validationStatus && (
        <Typography variant="body2" color="error" className="text-pre-wrap" paragraph>
          {validationStatus}
        </Typography>
      )}
      {validating && (
        <Typography variant="body2" paragraph>
          {$t('validating')}
        </Typography>
      )}
    </>
  );
};

const mapStateToProps = (state: State) => ({
  validationStatus: state.certificates.validationStatus
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  validate: (validationRequest: CertificateValidationRequest) => dispatch(validateCertificates(validationRequest)),
  setPrintValidatingStatus: (status: boolean) => dispatch(setPrintValidatingStatus(status)),
  setCertificatesValidationStatus: (status: string) => dispatch(setCertificatesValidationStatus(status))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(USIAlert);
