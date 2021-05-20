/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CertificateOutcome, Contact } from "@api/model";
import { FormControlLabel, Theme } from "@material-ui/core";
import Grid from "@material-ui/core/Grid/Grid";
import Link from "@material-ui/core/Link";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";
import { format } from "date-fns";
import QRCode from "qrcode.react";
import React, { useCallback, useEffect, useMemo, useRef } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { arrayRemove, change } from "redux-form";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import EntityService from "../../../../common/services/EntityService";
import { III_DD_MMM_YYYY } from "../../../../common/utils/dates/format";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { AnyArgFunction, NumberArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import { EditViewProps } from "../../../../model/common/ListView";
import { State } from "../../../../reducers/state";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import { openQualificationLink } from "../../qualifications/utils";
import { clearCertificateOutcomes, getCertificateOutcomes, setCertificateOutcomesSearch } from "../actions";

interface Props extends EditViewProps {
  status?: string;
  getCertificateOutcomes?: NumberArgFunction;
  clearCertificateOutcomes?: AnyArgFunction;
  setCertificateOutcomesSearch?: StringArgFunction;
  studentOutcomes?: CertificateOutcome[];
  studentOutcomesLoading?: boolean;
  classes?: any;
}

const styles = createStyles(({ spacing }: Theme) => ({
  root: {
    overflowX: "hidden",
    "& > *": {
      zIndex: 1
    }
  },
  switch: {
    marginLeft: `-${spacing(2)}px`
  },
  select1: {
    zIndex: 5
  },
  select2: {
    zIndex: 4
  },
  select3: {
    zIndex: 3
  },
  link: {
    position: "absolute",
    transform: "rotate(90deg)",
    left: "56px",
    top: "96px"
  },
  dataRowClass: {
    gridTemplateColumns: "1fr 2fr"
  }
}));

const outcomesSort = (a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1);

const SecondaryTextComponent = (item: CertificateOutcome) => (
  <div>
    <div className="text-truncate">{item.name}</div>
    <div className="textSecondaryColor text-truncate">{item.status}</div>
  </div>
);

const validateOutcomes = value => (!value || value.length ? undefined : "At least one outcome should be added");

const CertificateEditView: React.FunctionComponent<Props> = React.memo(props => {
  const {
    values,
    twoColumn,
    isNew,
    dispatch,
    form,
    classes,
    getCertificateOutcomes,
    clearCertificateOutcomes,
    studentOutcomes,
    studentOutcomesLoading,
    setCertificateOutcomesSearch,
    submitSucceeded
  } = props;

  const listRef = useRef(null);

  useEffect(() => {
    if (isNew && window.location.search) {
      const searchParam = new URLSearchParams(window.location.search);

      let contactId: any = searchParam.get("contactId");
      contactId = Number(contactId);

      if (contactId && !isNaN(contactId)) {
          EntityService.getPlainRecords("Contact", "firstName,lastName,email,birthDate", `id is ${contactId}`, 1).then(
            res => {
              if (res.rows.length > 0) {
                dispatch(change(form, "studentContactId", contactId));
                dispatch(
                  change(
                    form,
                    "studentName",
                    contactLabelCondition({ firstName: res.rows[0].values[0], lastName: res.rows[0].values[1] })
                  )
                );
              }
            }
          );
        }
    }
  }, [isNew]);

  const transformForNestedList = useCallback(
    (item: CertificateOutcome): NestedListItem => ({
        id: String(item.id),
        entityName: item.code,
        primaryText: format(new Date(item.issueDate), III_DD_MMM_YYYY),
        secondaryText: SecondaryTextComponent(item),
        active: !values.printedOn,
        link: `/outcome?search=id is ${item.id}`
      }),
    [values.printedOn]
  );

  const onStudentIdChange = useCallback(
    (contact: Contact) => {
      dispatch(change(form, "studentName", contactLabelCondition(contact)));
      dispatch(change(form, "outcomes", []));
      clearCertificateOutcomes(false);
      if (listRef.current.state.searchEnabled) {
        listRef.current.toggleSearch();
      }
    },
    [form]
  );

  const onQualificationCodeChange = useCallback(
    q => {
      dispatch(change(form, "qualificationId", q ? q.id : null));
      dispatch(change(form, "level", q ? q.level : null));
      dispatch(change(form, "title", q ? q.title : null));
    },
    [form]
  );

  const onQualificationTitleChange = useCallback(
    q => {
      dispatch(change(form, "qualificationId", q ? q.id : null));
      dispatch(change(form, "level", q ? q.level : null));
      dispatch(change(form, "nationalCode", q ? q.nationalCode : null));
    },
    [form]
  );

  const addOutcome = useCallback(
    outArr => {
      const updated = values.outcomes.concat(outArr.map(o1 => studentOutcomes.find(o2 => o2.id === Number(o1.id))));

      updated.sort(outcomesSort);
      //
      dispatch(change(form, "outcomes", updated));
    },
    [form, studentOutcomes, values.outcomes]
  );

  const searchOutcomes = useCallback(
    search => {
      setCertificateOutcomesSearch(`~"${search}" and status not is STATUS_NOT_SET and status not is null`);
      getCertificateOutcomes(values.studentContactId);
    },
    [values.studentContactId]
  );

  const onToggleOutcomesSearch = useCallback(() => {
    setCertificateOutcomesSearch("status not is STATUS_NOT_SET and status not is null");
    getCertificateOutcomes(values.studentContactId);
  }, [values.studentContactId]);

  const deleteOutcome = useCallback(
    values.printedOn
      ? undefined
      : (out, index) => {
          dispatch(arrayRemove(form, "outcomes", index));
        },
    [values.printedOn]
  );

  const revokedValue = useMemo(
    () => (values.revokedOn ? (
        format(new Date(values.revokedOn), III_DD_MMM_YYYY)
      ) : (
        <span className="textSecondaryColor">None</span>
      )),

    [values.revokedOn]
  );

  const printedValue = useMemo(
    () => (values.printedOn ? (
        format(new Date(values.printedOn), III_DD_MMM_YYYY)
      ) : (
        <span className="textSecondaryColor">Not Printed</span>
      )),
    [values.printedOn]
  );

  const outcomes = useMemo(() => (values.outcomes ? values.outcomes.map(transformForNestedList) : []), [
    values.outcomes
  ]);

  const certificateNumber = useMemo(() => !isNew && values.number, [values.number, isNew]);

  const qrCode = useMemo(
    () => !isNew
      && values.code && (
        <div className="relative pt-2">
          <Typography variant="subtitle2" color="textSecondary">
            Certificate verification
          </Typography>
          <Typography variant="body2">{values.code}</Typography>
          <Link
            target="_blank"
            href={`http://www.skills.courses/${values.code}`}
            variant="body2"
            className={classes.link}
          >
            www.skills.courses
          </Link>
          <QRCode size={106} value={`www.skills.courses/${values.code}`} />
        </div>
      ),
    [values.code, isNew]
  );

  const foundOutcomes = useMemo(() => studentOutcomes.map(transformForNestedList), [studentOutcomes]);

  return (
    <Grid container className={clsx("pt-2 pr-3 pb-0 pl-3 relative h-100 align-content-start defaultBackgroundColor", classes.root)}>
      {isNew && <div className={clsx("backgroundText paperTextColor", { "fs19": twoColumn })}>Draft</div>}
      {Boolean(values.revokedOn) && (
        <div className={clsx("backgroundText errorColorFade-0-2", { "fs19": twoColumn })}>Revoked</div>
      )}

      <Grid item xs={12} className={clsx(classes.switch, "pb-1", "centeredFlex")}>
        <FormControlLabel
          control={<FormField type="switch" name="isQualification" color="primary" />}
          label={(
            <Typography display="inline" variant="body2">
              Full qualification or skillset
            </Typography>
          )}
          labelPlacement="start"
          disabled={!!values.printedOn}
        />
      </Grid>

      <Grid item xs={12}>
        <Typography className="heading pb-1">
          {values.isQualification ? "This is to certify that" : "This is a statement that"}
        </Typography>
      </Grid>

      <Grid item xs={12} className={clsx(classes.select1, "pb-1")}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Contact"
          aqlFilter="isStudent is true"
          name="studentContactId"
          label="Student name"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={values && defaultContactName(values.studentName)}
          onInnerValueChange={onStudentIdChange}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openContactLink}
              link={values.studentContactId}
              disabled={!values.studentContactId}
            />
          )}
          disabled={!isNew}
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          required
        />
      </Grid>

      <Grid item xs={12}>
        <Typography className="heading pb-1">
          {values.isQualification ? "Has fulfilled the requirements for the" : "With competencies from"}
        </Typography>
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className={classes.select2}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Qualification"
          name="nationalCode"
          label="National code"
          selectValueMark="nationalCode"
          selectLabelMark="nationalCode"
          validate={values.isQualification ? validateSingleMandatoryField : undefined}
          onInnerValueChange={onQualificationCodeChange}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openQualificationLink}
              link={values.qualificationId}
              disabled={!values.qualificationId}
            />
          )}
          allowEmpty={!values.isQualification}
          disabled={!!values.printedOn}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className={classes.select3}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Qualification"
          name="title"
          label="Qualification"
          selectValueMark="title"
          selectLabelMark="title"
          validate={values.isQualification ? validateSingleMandatoryField : undefined}
          onInnerValueChange={onQualificationTitleChange}
          labelAdornment={(
            <LinkAdornment
              linkHandler={openQualificationLink}
              link={values.qualificationId}
              disabled={!values.qualificationId}
            />
          )}
          disabled={!!values.printedOn}
          allowEmpty={!values.isQualification}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className="textField">
        <div>
          <Typography variant="caption" color="textSecondary">
            Level
          </Typography>
          <Typography variant="body1">{values.level || <span className="textSecondaryColor">None</span>}</Typography>
        </div>
      </Grid>

      {twoColumn && <Grid item xs={3} />}

      <Grid item xs={12} className={twoColumn ? "pt-2 pb-2" : undefined}>
        <FormField
          type="multilineText"
          name="publicNotes"
          label="Printed public notes / Specialization"
          fullWidth
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="date"
          name="awardedOn"
          label="Awarded"
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className="textField">
        <div>
          <Typography variant="caption" color="textSecondary">
            Printed
          </Typography>
          <Typography variant="body1">{printedValue}</Typography>
        </div>
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className="textField">
        <div className={clsx({ "d-none": isNew })}>
          <Typography variant="caption" color="textSecondary">
            Certificate Number
          </Typography>
          <Typography variant="body1">{certificateNumber}</Typography>
        </div>
      </Grid>

      {twoColumn && <Grid item xs={3} />}

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="date" name="issuedOn" label="Issued" />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="date" name="expiryDate" label="Expiry" />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className="textField">
        <div className={clsx({ "d-none": isNew })}>
          <Typography variant="caption" color="textSecondary">
            Revoked
          </Typography>
          <Typography variant="body1">{revokedValue}</Typography>
        </div>
      </Grid>

      {twoColumn && <Grid item xs={3} />}

      <Grid item xs={12} className={twoColumn ? "pt-2 pb-2" : undefined}>
        <FormField type="multilineText" name="privateNotes" label="Private notes" fullWidth />
      </Grid>

      <Grid item xs={twoColumn ? 8 : 12} className={clsx({ "saveButtonTableOffset": twoColumn })}>
        <NestedList
          formId={values.id}
          name="outcomes"
          title="Transcript"
          searchType="immediate"
          innerRef={listRef}
          values={outcomes}
          validate={validateOutcomes}
          hideAddButton={!!values.printedOn}
          dataRowClass={classes.dataRowClass}
          onSearch={searchOutcomes}
          clearSearchResult={clearCertificateOutcomes}
          pending={studentOutcomesLoading}
          searchValues={foundOutcomes}
          onAdd={addOutcome}
          onDelete={deleteOutcome}
          onToggleSearch={onToggleOutcomesSearch}
          resetSearch={submitSucceeded}
        />
      </Grid>

      <Grid
        item
        xs={twoColumn ? 4 : 12}
        className={clsx("saveButtonTableOffset", {
          "d-flex align-items-end justify-content-end": twoColumn
        })}
      >
        {qrCode}
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  studentOutcomes: state.certificates.outcomes.items,
  studentOutcomesLoading: state.certificates.outcomes.loading
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getCertificateOutcomes: (studentId: number) => dispatch(getCertificateOutcomes(studentId)),
  clearCertificateOutcomes: (loading?: boolean) => dispatch(clearCertificateOutcomes(loading)),
  setCertificateOutcomesSearch: (search: string) => dispatch(setCertificateOutcomesSearch(search))
});

const Connected = connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(CertificateEditView));

export default props => (props.values ? <Connected {...props} /> : null);
