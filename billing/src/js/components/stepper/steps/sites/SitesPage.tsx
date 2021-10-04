/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from 'react';
import { useFormik } from 'formik';
import { darken } from '@mui/material/styles';
import { IconButton } from '@mui/material';
import { green } from '@mui/material/colors';
import { AddCircle } from '@mui/icons-material';
import { LoadingButton } from '@mui/lab';
import * as yup from 'yup';
import { SiteDTO } from '@api/model';
import { makeAppStyles } from '../../../../styles/makeStyles';
import { stopPropagation } from '../../../../utils';
import Loading from '../../../common/Loading';
import GoogleLoginButton from '../../../common/GoogleLoginButton';
import { SiteContent } from './SiteContent';
import { useAppDispatch, useAppSelector } from '../../../../redux/hooks/redux';
import { updateCollegeSites } from '../../../../redux/actions/Sites';

export const useStyles = makeAppStyles()((theme, prop, createRef) => {
  const rootExpanded = {
    ref: createRef()
  };

  return {
    container: {
      display: 'flex',
      padding: 0
    },
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color,
    },
    textFieldWrapper: {
      display: 'flex',
      alignItems: 'baseline',
      marginTop: theme.spacing(2)
    },
    errorMessage: {
      fontSize: '12px',
      color: '#f44336'
    },
    flexWrapper: {
      display: 'flex',
      alignItems: 'center',
    },
    plusButton: {
      color: darken(green[900], 0.1)
    },
    root: {
      border: `1px solid ${theme.palette.divider}`,
      [`&:not(:last-child):not(.${rootExpanded.ref})`]: {
        borderBottom: 0,
      },
      [`&.${rootExpanded.ref}:before`]: {
        opacity: 1,
        top: '-18px',
      },
      '&:before': {
        opacity: 0
      }
    },
    loading: {
      height: '25px!important',
      width: '25px!important',
    },
    buttonsWrapper: {
      display: 'flex',
      justifyContent: 'flex-end',
      marginTop: '30px',
      paddingBottom: '20px'
    },
    expandIcon: {
      right: 'unset',
      left: theme.spacing(1),
      position: 'absolute'
    },
    deleteIcon: {
      fontSize: '22px',
      '&:hover': {
        backgroundColor: 'transparent',
        color: theme.palette.error.main
      },
      padding: theme.spacing(1),
      margin: `-${theme.spacing(1)} -${theme.spacing(1)} -${theme.spacing(1)} auto`
    },
    summaryContent: {
      justifyContent: 'space-between',
      paddingLeft: theme.spacing(3),
    },
    summaryRoot: {
      '&$summaryFocused': {
        backgroundColor: 'inherit'
      }
    },
    summaryFocused: {},
    zeroMargin: {
      margin: 0
    },
    caption: {
      marginTop: theme.spacing(1)
    },
    pr: {
      paddingRight: '20px'
    },
    textFieldWrapper2: {
      display: 'flex',
      alignItems: 'baseline'
    },
    floatLabel: {
      position: 'relative',
      bottom: theme.spacing(0.5)
    },
    domainsInput: {},
    hasClearIcon: {
      '& $domainsInput': {
        paddingRight: 0
      }
    },
    rootExpanded
  };
});

const validationSchema = yup.object({
  sites: yup.array().of(yup.object().shape({
    key: yup.string().nullable()
      .required('Required')
      .max(40, 'Maximum length is 40 characters')
      .test('uniqueKey', 'Key should be unique', (value, context: any) => {
        const { collegeKey, sites } = context.from[1].value;
        return sites.filter((s) => (s.id ? s.key : `${collegeKey}-${s.key}`) === (context.parent.id ? value : `${collegeKey}-${value}`)).length === 1;
      })
      .matches(/^[0-9a-z-]+$/i, 'You can only use letters, numbers and "-"'),
    primaryDomain: yup.string().nullable(),
    name: yup.mixed().required('Required').test(
      'uniqueName',
      'Name should be unique',
      (value, context: any) => context.from[1].value.sites.filter((s) => s.name === value).length === 1
    ),
    webSiteTemplate: yup.string().nullable().when('id', {
      is: (val) => !val,
      then: yup.string().nullable().required('Required'),
    }),
    domains: yup.array().of(yup.string().matches(/\b((?=[a-z0-9-]{1,63}\.)(xn--)?[a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,63}\b/, 'Domain name is invalid'))
  }))
});

const getChangedSites = (initial: SiteDTO[], current: SiteDTO[]) => {
  const changed = [];
  current.forEach((site) => {
    const initialSite = initial.find((i) => i.id === site.id);
    if (initialSite) {
      Object.keys(site).forEach((key) => {
        if (Array.isArray(site[key])) {
          site[key].forEach((i, index) => {
            if (site[key][index] !== initialSite[key][index]) {
              changed.push(site);
            }
          });
        } else if (site[key] !== initialSite[key]) {
          changed.push(site);
        }
      });
    }
  });
  return changed;
};

export const SitesPage: React.FC<any> = () => {
  const loading = useAppSelector((state) => state.loading);
  const sites = useAppSelector((state) => state.sites);
  const collegeKey = useAppSelector((state) => state.college.collegeKey);
  const dispatch = useAppDispatch();

  const [expanded, setExpanded] = useState<number | boolean>(false);

  const handleExpand = (panel) => (event, isExpanded) => {
    setExpanded(isExpanded ? panel : false);
  };

  const {
    handleSubmit, setValues, setFieldError, initialValues, dirty, handleChange, values, errors, setFieldValue, isValid, resetForm
  } = useFormik({
    initialValues: { sites, collegeKey },
    validationSchema,
    onSubmit: (submitted) => {
      const parsed = submitted.sites.map((s) => ({ ...s, domains: s.domains.map((d) => d.replace(/https?:\/\//, '')) }));
      dispatch(updateCollegeSites({
        changed: getChangedSites(initialValues.sites, parsed.filter((s) => s.id)),
        created: parsed.filter((s) => !s.id).map((s) => ({ ...s, key: `${collegeKey}-${s.key}` })),
        removed: initialValues.sites.filter((s) => s.id && !parsed.some((c) => c.id === s.id))
      }));
    },
  });

  useEffect(() => {
    if (sites || collegeKey) {
      resetForm({ values: { sites, collegeKey } });
    }
  }, [sites, collegeKey]);

  const { classes, cx } = useStyles();

  const onKeyChange: any = (e, index, isNew, initial, initialMatchPattern) => {
    const { value } = e.target;
    const name = `sites[${index}].key`;
    const newValue = initialMatchPattern ? `${collegeKey}-${value}` : value;
    let shouldValidte = true;
    if (initial && initial.key !== newValue && !newValue.match(`${collegeKey}-`)) {
      shouldValidte = false;
      setFieldError(name, `Location should start with ${collegeKey}-`);
    }
    if (initial && initial.key !== newValue && newValue.match(`${collegeKey}-`) && !newValue.match(new RegExp(`${collegeKey}-[^\\s]`))) {
      shouldValidte = false;
      setFieldError(name, 'Location has invalid format');
    }
    setFieldValue(name, newValue, shouldValidte);
  };

  const onSetPrimaryDomain = (domain, index) => {
    setFieldValue(`sites[${index}].primaryDomain`, domain);
  };

  const onAddDomain = (site, index, params) => {
    setFieldValue(`sites[${index}].domains`, [...site.domains, params.inputProps.value]);
    if (!site.domains.length && !site.primaryDomain) onSetPrimaryDomain(params.inputProps.value, index);
  };

  const onClickDelete = (index) => (e) => {
    stopPropagation(e);
    const updated = [...values.sites];
    updated.splice(index, 1);
    setFieldValue('sites', updated);
  };

  const onAddSite = () => {
    setExpanded(0);
    setValues({
      sites: [
        {
          id: null,
          key: null,
          name: null,
          primaryDomain: null,
          webSiteTemplate: null,
          domains: []
        },
        ...values.sites
      ],
      collegeKey,
    });
  };

  return (
    <div className={classes.container}>
      {sites
        ? (
          <form
            className="w-100"
            onSubmit={handleSubmit}
          >
            <div>
              <div className={classes.flexWrapper}>
                <div className={cx('flex-1', classes.flexWrapper)}>
                  <h2 className={classes.coloredHeaderText}>Websites</h2>
                  <IconButton
                    onClick={onAddSite}
                    size="large"
                  >
                    <AddCircle className={classes.plusButton} />
                  </IconButton>
                </div>

                <GoogleLoginButton />
              </div>
              <div>
                {(
                  values?.sites?.map((site: SiteDTO, index) => {
                    const isNew = typeof site.id !== 'number';
                    const error = (errors.sites && errors.sites[index]) || {};
                    const initial = site.id && initialValues.sites.find((i) => i.id === site.id);
                    const initialMatchPattern = initial && initial.key.match(`${collegeKey}-`);

                    return (
                      <SiteContent
                        cx={cx}
                        classes={classes}
                        key={site.id || index}
                        isNew={isNew}
                        expanded={expanded}
                        handleExpand={handleExpand}
                        collegeKey={collegeKey}
                        site={site}
                        onClickDelete={onClickDelete(index)}
                        index={index}
                        initial={initial}
                        error={error}
                        initialMatchPattern={initialMatchPattern}
                        onKeyChange={onKeyChange}
                        onAddDomain={onAddDomain}
                        setFieldValue={setFieldValue}
                        values={values}
                        handleChange={handleChange}
                        onSetPrimaryDomain={onSetPrimaryDomain}
                      />
                    );
                  })
            )}
              </div>
              <div className={classes.buttonsWrapper}>
                <LoadingButton
                  variant="contained"
                  color="primary"
                  type="submit"
                  loading={loading}
                  disabled={!isValid || !dirty}
                  disableElevation
                >
                  Save
                </LoadingButton>
              </div>
            </div>
          </form>
        )
        : <Loading />}
    </div>
  );
};
