/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo, useState } from "react";
import MenuItem from "@mui/material/MenuItem";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import { CertificateCreateForEnrolmentsRequest, Enrolment } from "@api/model";
import Grid from "@mui/material/Grid";
import FormControlLabel from "@mui/material/FormControlLabel";
import DialogTitle from "@mui/material/DialogTitle";
import CircularProgress from "@mui/material/CircularProgress";
import { Dispatch } from "redux";
import EntityService from "../../../../../services/EntityService";
import { openInternalLink } from "../../../../../utils/links";
import { StyledCheckbox } from "../../../../../ish-ui/formFields/CheckboxField";
import CertificateService from "../../../../../../containers/entities/certificates/services/CertificateService";
import instantFetchErrorHandler from "../../../../../api/fetch-errors-handlers/InstantFetchErrorHandler";

interface CreateCertificateMenuProps {
  entity: "CourseClass" | "Enrolment";
  dispatch: Dispatch;
  selection: string[];
  closeMenu?: any;
  disableMenu?: boolean;
}

export interface CertificateEnrolmentsState {
  all?: number[];
  vet?: number[];
  noCertificate?: number[];
  withCertificate?: number[];
  nonSufficient?: number[];
}

const enrilmentsStateInitial: CertificateEnrolmentsState = {
  all: [],
  vet: [],
  noCertificate: [],
  withCertificate: [],
  nonSufficient: []
};

const getEnrolmentsForCertificates = (search: string) => EntityService.getPlainRecords("Enrolment", "id", search);

const CreateCertificateMenu: React.FC<CreateCertificateMenuProps> = ({
 dispatch, entity, selection, disableMenu
}) => {
  const [dialogOpened, setDialogOpened] = useState(false);
  const [loading, setLoading] = useState(false);
  const [enrolments, setEnrolments] = useState<CertificateEnrolmentsState>({ ...enrilmentsStateInitial });
  const [createStatementOfAtteiment, setCreateStatementOfAtteiment] = useState<boolean>(false);

  useEffect(() => {
    if (!(selection.length && dialogOpened)) {
    } else {
      let search;

      if (entity === "Enrolment") {
        search = `id in ( ${selection.toString()} )`;
      }

      if (entity === "CourseClass") {
        search = `courseClass.id in ( ${selection.toString()} )`;
      }
      search = `${search} AND outcomes.module.id not is null`;

      setLoading(true);

      const state = JSON.parse(JSON.stringify(enrilmentsStateInitial));
      const hasOutcomesWithoutNotSetStatus = `${search} AND outcomes.status != STATUS_NOT_SET`;
      const hasCertificateSearch = `${search} AND outcomes.certificateOutcomes.id not is null and outcomes.certificateOutcomes.certificate.revokedOn is null`;
      const nonSufficientSearch = `${search} `
        + `and (courseClass.course.isSufficientForQualification = true `
        + `and (outcomes.status != STATUS_ASSESSABLE_PASS AND outcomes.status != STATUS_ASSESSABLE_RPL_GRANTED AND outcomes.status != STATUS_ASSESSABLE_RCC_GRANTED AND outcomes.status != STATUS_ASSESSABLE_CREDIT_TRANSFER))`;

      getEnrolmentsForCertificates(hasOutcomesWithoutNotSetStatus)
        .then(res => {
          res.rows.forEach(r => {
            state.vet.push(r.id);
          });
        })
        .then(() => {
          getEnrolmentsForCertificates(hasCertificateSearch)
            .then(res => {
              res.rows.forEach(r => {
                state.withCertificate.push(r.id);
              });
              state.noCertificate = state.vet.filter(id => !state.withCertificate.includes(id));
            })
            .then(() => {
              getEnrolmentsForCertificates(nonSufficientSearch)
                .then(res => {
                  res.rows.forEach(r => {
                    if (!state.withCertificate.includes(r.id)) {
                      state.nonSufficient.push(r.id);
                    }
                  });
                })
                .then(() => {
                  setEnrolments(state);
                  setLoading(false);
                });
            });
        });
    }

    return () => {
      setEnrolments(JSON.parse(JSON.stringify(enrilmentsStateInitial)));
    };
  }, [dialogOpened]);

  const onClick = useCallback(() => {
    setDialogOpened(true);
  }, []);

  const onClose = useCallback(() => {
    setDialogOpened(false);
  }, []);

  const onStatementOfAtteimentChange = useCallback((e, value) => setCreateStatementOfAtteiment(value), []);

  const onCreate = useCallback(() => {
    const request: CertificateCreateForEnrolmentsRequest = {
      enrolmentIds: createStatementOfAtteiment
        ? enrolments.noCertificate
        : enrolments.noCertificate.filter(id => !enrolments.nonSufficient.includes(id)),
      createStatementOfAtteiment
    };

    setLoading(true);

    CertificateService.createForEnrolments(request)
      .then(res => {
        openInternalLink(`/certificate/?search=id in (${res.toString()})`);

        setLoading(false);
        setDialogOpened(false);
      })
      .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to create certificates"));
  }, [createStatementOfAtteiment, enrolments.nonSufficient, enrolments.noCertificate]);

  const headerLabel = useMemo(
    () =>
      (enrolments.vet.length
        ? enrolments.noCertificate.length
          ? entity === "Enrolment"
            ? `You have selected ${enrolments.vet.length} VET enrolment${enrolments.vet.length !== 1 ? "s" : ""}.`
            : `You have selected ${selection.length} VET class${selection.length !== 1 ? "es" : ""} with a total of ${
                enrolments.vet.length
              } enrolments.`
          : `Certificates already created for all selected ${
              entity === "Enrolment" ? "enrolments" : "classes enrolments"
            }`
        : `You have not selected any VET ${entity === "Enrolment" ? "enrolments" : "classes"}.`),
    [enrolments, selection, entity]
  );

  const withCertificateLabel = useMemo(
    () =>
      (enrolments.withCertificate && enrolments.withCertificate.length ? (
        <li>
          <Typography variant="body2" className="pb-2">
            {enrolments.withCertificate.length}
            {' '}
            enrolment
            {enrolments.withCertificate.length !== 1 ? "s" : ""}
            {' '}
            already
            have certificates linked to their outcomes and certificates will not be created
          </Typography>
        </li>
      ) : null),
    [enrolments.withCertificate && enrolments.withCertificate.length]
  );

  const createdCertificatesCount = useMemo(
    () =>
      (createStatementOfAtteiment
        ? enrolments.noCertificate.length
        : enrolments.noCertificate.length - enrolments.nonSufficient.length),
    [createStatementOfAtteiment, enrolments.nonSufficient.length, enrolments.noCertificate.length]
  );

  const noCertificateLabel = useMemo(
    () =>
      (createdCertificatesCount ? (
        <li>
          <Typography variant="body2" className="pb-2">
            {createdCertificatesCount}
            {' '}
            new certificate
            {createdCertificatesCount > 1 ? "s" : ""}
            {' '}
            will be created
          </Typography>
        </li>
      ) : null),
    [createdCertificatesCount]
  );

  return (
    <>
      <MenuItem
        classes={{
          root: "listItemPadding"
        }}
        onClick={onClick}
        disabled={disableMenu}
      >
        Create certificates
      </MenuItem>
      <Dialog open={dialogOpened} onClose={onClose} fullWidth>
        <DialogTitle>{!loading && headerLabel}</DialogTitle>
        <DialogContent>
          <Grid container columnSpacing={3}>
            {!loading && Boolean(enrolments.vet.length) && Boolean(enrolments.noCertificate.length) && (
              <Grid item xs={12}>
                <ul className="m-0">
                  {Boolean(enrolments.nonSufficient.length) && (
                    <li>
                      <Typography variant="body2">
                        {enrolments.nonSufficient.length}
                        {' '}
                        enrolments do not have sufficient successful outcomes for a
                        qualification
                      </Typography>
                      <FormControlLabel
                        className="checkbox mb-2"
                        control={(
                          <StyledCheckbox
                            color="secondary"
                            checked={createStatementOfAtteiment}
                            onChange={onStatementOfAtteimentChange}
                          />
                        )}
                        label="Create a Statement of Attainment instead"
                      />
                    </li>
                  )}

                  {withCertificateLabel}
                  {noCertificateLabel}
                </ul>
              </Grid>
            )}
            <Grid container columnSpacing={3} item xs={12}>
              <CircularProgress classes={{ root: loading ? undefined : "d-none" }} size={40} thickness={5} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions className="p-2">
          <Button color="primary" onClick={onClose}>
            Cancel
          </Button>
          {Boolean(enrolments.vet.length) && Boolean(enrolments.noCertificate.length) && (
            <Button
              color="primary"
              variant="contained"
              onClick={onCreate}
              disabled={loading || !createdCertificatesCount}
            >
              Create certificates
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </>
  );
};

export default CreateCertificateMenu;
