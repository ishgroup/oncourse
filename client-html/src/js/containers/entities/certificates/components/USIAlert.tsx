import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import Typography from "@material-ui/core/Typography";
import { CertificateValidationRequest, SearchQuery, Sorting } from "@api/model";
import { setCertificatesValidationStatus, validateCertificates } from "../actions/index";
import { State } from "../../../../reducers/state";
import { setPrintValidatingStatus } from "../../../../common/components/list-view/components/share/actions";
import { BooleanArgFunction } from "../../../../model/common/CommonFunctions";
import { getExpression } from "../../../../common/components/list-view/utils/listFiltersUtils";

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

  useEffect(
    () => {
      setPrintValidatingStatus(true);
      validate(getCertificateValidationRequest(selectAll, searchQuery, selection, sort));

      return () => setCertificatesValidationStatus(null);
    },
    [selectAll]
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
          Validating...
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
