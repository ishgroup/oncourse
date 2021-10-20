/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from 'react';
import { useFormik } from 'formik';
import { darken } from '@mui/material/styles';
import { Button } from '@mui/material';
import { green } from '@mui/material/colors';
import { LoadingButton } from '@mui/lab';
import * as yup from 'yup';
import { SiteDTO } from '@api/model';
import { useHistory, useParams } from 'react-router-dom';
import { FormikErrors } from 'formik/dist/types';
import { makeAppStyles } from '../../styles/makeStyles';
import { renderSelectItemsWithEmpty } from '../../utils';
import Loading from '../common/Loading';
import GoogleLoginButton from '../common/GoogleLoginButton';
import { useAppDispatch, useAppSelector } from '../../redux/hooks/redux';
import { updateCollegeSites } from '../../redux/actions/Sites';
import { SiteValues } from '../../models/Sites';
import { showConfirm } from '../../redux/actions/Confirm';
import { GTMContainer } from '../../models/Google';
import { getTokenString, renderContainerLabel, renderWebPropertyLabel } from '../../utils/Google';
import { configureGoogleForSite } from '../../redux/actions/Google';
import GoogleService from '../../api/services/GoogleService';
import { MAPS_API_KEY_NAME } from '../../constant/Google';
import instantFetchErrorHandler from '../../api/fetch-errors-handlers/InstantFetchErrorHandler';
import URLs from './URLs';
import GoogleSetup from './GoogleSetup';

const useStyles = makeAppStyles()((theme, prop, createRef) => {
  const rootExpanded = {
    ref: createRef()
  };
  const domainsRoot = {
    ref: createRef()
  };
  const domainsInput = {
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
    hasClearIcon: {
      [`&.${domainsRoot.ref} .${domainsInput.ref}`]: {
        paddingRight: 0
      }
    },
    domainsRoot,
    domainsInput,
    rootExpanded
  };
});

const validationSchema = yup.object({
  key: yup.string().nullable()
    .required('Key is required')
    .max(40, 'Maximum length is 40 characters')
    .matches(/^[0-9a-z-]+$/i, 'You can only use letters, numbers and "-"'),
  primaryDomain: yup.string().nullable(),
  name: yup.mixed().required('Name is required'),
  webSiteTemplate: yup.string().nullable().when('id', {
    is: (val) => !val,
    then: yup.string().nullable().required('Site template is required'),
  }),
  gtmAccountId: yup.string().nullable().when('googleAnalyticsId', {
    is: (val) => val,
    then: yup.string().nullable().required('Tag manager account is required'),
  })
});

const newSite: SiteDTO = {
  id: null,
  key: '',
  name: '',
  primaryDomain: '',
  webSiteTemplate: null,
  domains: {},
  gtmContainerId: null
};

export const SitesPage = () => {
  const loading = useAppSelector((state) => state.loading);
  const sites = useAppSelector((state) => state.sites);
  const collegeKey = useAppSelector((state) => state.college.collegeKey);
  const { id, page } = useParams<any>();

  const dispatch = useAppDispatch();
  const appHistory = useHistory();

  const {
    profile,
    gtmAccounts,
    gaAccounts,
    gtmContainers,
    gaWebProperties,
    token,
  } = useAppSelector((state) => state.google);

  const googleLoading = useAppSelector((state) => state.google.loading);

  const loggedWithGoogle = Boolean(profile);

  const initialSite = useMemo(() => sites?.find((s) => s.id == id), [id, sites]);
  const gtmContainer = useMemo(() => {
    let container: GTMContainer = {};

    for (const key in gtmContainers) {
      gtmContainers[key]?.forEach((con) => {
        if (con.containerId === initialSite?.gtmContainerId) {
          container = con;
        }
      });
    }
    return container;
  }, [gtmContainers, initialSite]);
  const gtmAccountId = useMemo(() => gtmAccounts?.find((ga) => ga.accountId === gtmContainer?.accountId)?.accountId, [gtmAccounts, gtmContainer]);

  const {
    handleSubmit,
    setValues,
    setFieldError,
    dirty,
    handleChange,
    values,
    errors,
    setFieldValue,
    isValid,
    resetForm
  } = useFormik<SiteValues>({
    initialValues: {
      ...initialSite, collegeKey, gtmAccountId, googleMapsApiKey: '', gaWebPropertyId: '', googleAnalyticsId: ''
    },
    validationSchema,
    onSubmit: (vals) => {
      // dispatch(updateCollegeSites({ [vals.id ? 'changed' : 'created']: [vals] }));
      dispatch(configureGoogleForSite(vals));
    },
    validate: (vals) => {
      const errorsResult: FormikErrors<SiteValues> = {};
      if (sites.length
        && vals.key
        && sites.filter((s) => s.key === `${collegeKey}-${vals.key}`).length === 1) {
        errorsResult.key = 'Key should be unique';
      }
      return errorsResult;
    }
  });

  const gaAccountItems = useMemo(() => renderSelectItemsWithEmpty({
    items: gaAccounts,
    valueKey: 'id',
    labelKey: 'name'
  }), [gaAccounts]);

  const gtmAccountItems = useMemo(() => renderSelectItemsWithEmpty(
    {
      items: gtmAccounts,
      valueKey: 'accountId',
      labelKey: 'name'
    }
  ), [gtmAccounts]);

  const gtmContainerItems = useMemo(() => renderSelectItemsWithEmpty(
    {
      items: (gtmContainers || {})[values.gtmAccountId],
      valueKey: 'containerId',
      labelKey: 'name',
      labelCondition: renderContainerLabel
    }
  ), [gtmContainers, values.gtmAccountId]);

  const gaWebPropertyItems = useMemo(() => renderSelectItemsWithEmpty(
    {
      items: (gaWebProperties || {})[values.googleAnalyticsId],
      valueKey: 'id',
      labelKey: 'name',
      labelCondition: renderWebPropertyLabel
    }
  ), [gaWebProperties, values.googleAnalyticsId]);

  const concurentAccounts = values.gtmContainerId
    && gtmContainers
    && !Object.keys(gtmContainers)
      .some((k) => gtmContainers[k]
        .some((c) => c.containerId === values.gtmContainerId));


  useEffect(() => {
    if (initialSite) {
      resetForm({
        values: {
          ...initialSite || {}, collegeKey, gtmAccountId, googleMapsApiKey: '', gaWebPropertyId: '', googleAnalyticsId: ''
        }
      });
    }
  }, [initialSite, collegeKey, gtmAccountId]);

  const getGoogleMapsAPiKey = async (site: SiteValues) => {
    try {
      const googleToken = getTokenString({ token } as any);

      if (token?.access_token && site.gtmAccountId && site.gtmContainerId) {
        const workspaces = await GoogleService.getGTMWorkspaces(
          googleToken,
          site.gtmAccountId,
          site.gtmContainerId
        );
        const workspace = workspaces?.workspace[0]?.workspaceId;

        if (workspace) {
          GoogleService.getGTMVariables(
            googleToken,
            site.gtmAccountId,
            site.gtmContainerId,
            workspace
          ).then((res) => {
            if (res.variable) {
              res.variable.forEach((v) => {
                if (v.name === MAPS_API_KEY_NAME) {
                  v.parameter.forEach((p) => {
                    if (p.key === 'value') {
                      resetForm({
                        values: {
                          ...values, googleMapsApiKey: p.value
                        }
                      });
                    }
                  });
                }
              });
            }
          });
        }
      }
    } catch (e) {
      instantFetchErrorHandler(dispatch, e);
    }
  };

  useEffect(() => {
    getGoogleMapsAPiKey(values);
  }, [values.gtmAccountId, values.gtmContainerId]);

  useEffect(() => {
    if (values && !values.id) {
      setValues({
        ...newSite, collegeKey, gtmAccountId, googleMapsApiKey: '', gaWebPropertyId: '', googleAnalyticsId: ''
      });
    }
  }, [id]);

  const { classes, cx } = useStyles();

  const onClickDelete = () => {
    if (values.id) {
      dispatch(showConfirm({
        confirmButtonText: 'Delete',
        confirmMessage: 'Site will be removed with all settings and configuration. This action can not be undone',
        onConfirm: () => {
          dispatch(updateCollegeSites({ removed: [values.id] }));
        }
      }));
    }
    appHistory.push(`/websites/${sites[0]?.id}`);
  };

  const renderPage = () => {
    switch (page) {
      case 'urls':
        return (
          <URLs
            classes={classes}
            isNew={!values.id}
            collegeKey={collegeKey}
            site={values}
            initial={initialSite}
            error={errors as any}
            setFieldValue={setFieldValue}
            setFieldError={setFieldError}
            handleChange={handleChange}
          />
        );
      case 'googleSettings':
        return (
          <GoogleSetup
            cx={cx}
            classes={classes}
            site={values}
            error={errors as any}
            setFieldValue={setFieldValue}
            handleChange={handleChange}
            gaAccountItems={gaAccountItems}
            gtmAccountItems={gtmAccountItems}
            gaWebPropertyItems={gaWebPropertyItems}
            gtmContainerItems={gtmContainerItems}
            loggedWithGoogle={loggedWithGoogle}
            googleProfileEmail={profile?.email}
            googleLoading={googleLoading}
            concurentAccounts={concurentAccounts}
          />
        );
      default:
      case 'analytics':
        return null;
    }
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
              <div className="d-flex justify-content-end mb-3">
                <GoogleLoginButton />
              </div>

              <div>
                {values.hasOwnProperty('key') && renderPage()}
              </div>
              <div className={classes.buttonsWrapper}>
                {page === 'urls' && (
                  <Button
                    onClick={onClickDelete}
                    disableElevation
                    color="error"
                    variant="contained"
                    className="mr-2"
                  >
                    Delete
                  </Button>
                )}

                {page !== 'analytics' && (
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
                )}
              </div>
            </div>
          </form>
        )
        : <Loading />}
    </div>
  );
};
