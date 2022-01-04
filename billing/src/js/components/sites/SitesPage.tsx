/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo, useState } from 'react';
import { useFormik } from 'formik';
import { darken } from '@mui/material/styles';
import { Button, Alert, AlertTitle } from '@mui/material';
import { green } from '@mui/material/colors';
import { LoadingButton } from '@mui/lab';
import * as yup from 'yup';
import { SiteDTO } from '@api/model';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { FormikErrors } from 'formik/dist/types';
import SettingsIcon from '@mui/icons-material/Settings';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import { makeAppStyles } from '../../styles/makeStyles';
import { renderSelectItemsWithEmpty } from '../../utils';
import Loading from '../common/Loading';
import GoogleLoginButton from '../common/GoogleLoginButton';
import { useAppDispatch, useAppSelector } from '../../redux/hooks/redux';
import { updateCollegeSites } from '../../redux/actions/Sites';
import { SitePageParams, SiteValues } from '../../models/Sites';
import { showConfirm } from '../../redux/actions/Confirm';
import { GTMContainer } from '../../models/Google';
import { getTokenString, renderContainerLabel, renderWebPropertyLabel } from '../../utils/Google';
import {
  configureGoogleForSite,
  getGaProfiles,
  getGaWebPropertiesByAccount,
  getGtmDataByAccount
} from '../../redux/actions/Google';
import GoogleService from '../../api/services/GoogleService';
import { GAS_VARIABLE_NAME, MAPS_API_KEY_NAME } from '../../constant/Google';
import instantFetchErrorHandler from '../../api/fetch-errors-handlers/InstantFetchErrorHandler';
import URLs from './URLs';
import GoogleSetup from './GoogleSetup';
import TagManager from './TagManager';
import Analytics from './Analytics';

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
  primaryDomain: yup.string().nullable().when('domains', {
    is: (val) => Object.keys(val).length,
    then: yup.string().nullable().required('Primary domain is required when domains specified'),
    otherwise: yup.string().nullable()
  }),
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
  const [isConfig, setIsConfig] = useState(false);
  const [customLoading, setCustomLoading] = useState(false);

  const loading = useAppSelector((state) => state.loading);
  const sites = useAppSelector((state) => state.sites);
  const collegeKey = useAppSelector((state) => state.college.collegeKey);

  const { id, page } = useParams<SitePageParams>();
  const location = useLocation();

  const dispatch = useAppDispatch();
  const appHistory = useHistory();

  const {
    profile,
    gtmAccounts,
    gaAccounts,
    gtmContainers,
    gaWebProperties,
    gaWebProfiles,
    token,
  } = useAppSelector((state) => state.google);

  const loggedWithGoogle = Boolean(profile);

  const initialSite = useMemo(() => sites?.find((s) => String(s.id) === id), [id, sites]);

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
      ...initialSite,
      collegeKey,
      googleMapsApiKey: '',
      gaWebPropertyId: ''
    },
    validationSchema,
    onSubmit: (vals) => {
      switch (page) {
        case 'urls':
          const {
            collegeKey,
            googleMapsApiKey,
            gaWebPropertyId,
            ...changedSite
          } = vals;
          dispatch(updateCollegeSites({ [changedSite.id ? 'changed' : 'created']: [changedSite] }));
          break;
      }
      if (isConfig) {
        dispatch(configureGoogleForSite(vals));
      }
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

  const gtmContainer = useMemo<GTMContainer>(() => {
    let container: GTMContainer = null;

    if (!values.gtmContainerId) {
      return container;
    }

    for (const key in gtmContainers) {
      gtmContainers[key]?.forEach((con) => {
        if (con.publicId === values.gtmContainerId) {
          container = con;
        }
      });
    }
    return container;
  }, [gtmContainers, values.gtmContainerId]);

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
      valueKey: 'publicId',
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
        .some((c) => c.publicId === values.gtmContainerId));

  // Update GTM data on account change
  useEffect(() => {
    if (values.gtmAccountId && !gtmContainers[values.gtmAccountId] && loggedWithGoogle) {
      dispatch(getGtmDataByAccount(values.gtmAccountId));
    }
  }, [gtmContainers, values.gtmAccountId, loggedWithGoogle]);

  // Update GA web properties on account change
  useEffect(() => {
    if (values.googleAnalyticsId && !gaWebProperties[values.googleAnalyticsId] && loggedWithGoogle) {
      dispatch(getGaWebPropertiesByAccount(values.googleAnalyticsId));
    }
  }, [gaWebProperties, values.googleAnalyticsId, loggedWithGoogle]);

  // Update GA profiles on property change
  useEffect(() => {
    if (values.googleAnalyticsId && values.gaWebPropertyId && !gaWebProfiles[values.gaWebPropertyId] && loggedWithGoogle) {
      dispatch(getGaProfiles(values.googleAnalyticsId, values.gaWebPropertyId));
    }
  }, [gaWebProfiles, values.gaWebPropertyId, loggedWithGoogle]);

  useEffect(() => {
    if (initialSite) {
      resetForm({
        values: {
          ...initialSite,
          collegeKey,
          googleMapsApiKey: '',
          gaWebPropertyId: '',
          googleAnalyticsId: ''
        }
      });
    }
  }, [collegeKey, initialSite]);

  const getGoogleData = async () => {
    setCustomLoading(true);
    try {
      const googleToken = getTokenString({ token } as any);

      const googleData = {
        googleMapsApiKey: '',
        gaWebPropertyId: '',
      };

      const workspaces = await GoogleService.getGTMWorkspaces(
        googleToken,
        initialSite.gtmAccountId,
        gtmContainer.containerId
      );

      const workspace = workspaces?.workspace[0]?.workspaceId;

      if (workspace) {
        await GoogleService.getGTMPreview(
          googleToken,
          initialSite.gtmAccountId,
          gtmContainer.containerId,
          workspace
        ).then((res) => {
          if (res.containerVersion.variable) {
            res.containerVersion.variable.forEach((v) => {
              if (v.name === MAPS_API_KEY_NAME) {
                v.parameter.forEach((p) => {
                  if (p.key === 'value') {
                    googleData.googleMapsApiKey = p.value;
                  }
                });
              }
              if (v.name === GAS_VARIABLE_NAME) {
                v.parameter.forEach((p) => {
                  if (p.key === 'trackingId') {
                    googleData.gaWebPropertyId = p.value;
                  }
                });
              }
            });
          }
        });

        resetForm({
          values: {
            ...initialSite,
            ...googleData,
            collegeKey,
          }
        });
      }
      setCustomLoading(false);
    } catch (e) {
      setCustomLoading(false);
      instantFetchErrorHandler(dispatch, e);
    }
  };

  useEffect(() => {
    if (initialSite?.id && initialSite?.gtmContainerId && initialSite.gtmAccountId && gtmContainer && token?.access_token) {
      getGoogleData();
    }
  }, [initialSite?.id, gtmContainer, token?.access_token]);

  useEffect(() => {
    if (id === 'new') {
      setValues({
        ...newSite,
        collegeKey,
        gtmAccountId: '',
        googleMapsApiKey: '',
        gaWebPropertyId: '',
        googleAnalyticsId: ''
      });
    }
  }, [id]);

  useEffect(() => {
    const search = new URLSearchParams(location.search);
    const openSettings = search.get('openSettings');
    if (openSettings) {
      window.history.replaceState(null, null, window.location.pathname);
      setIsConfig(true);
    } else {
      setIsConfig(false);
    }
  }, [id, page, location]);

  const { classes } = useStyles();

  const onClickDelete = () => {
    if (values.id) {
      dispatch(showConfirm({
        confirmButtonText: 'Delete',
        confirmMessage: 'Site will be removed with all settings and configuration. This action can not be undone',
        onConfirm: () => {
          dispatch(updateCollegeSites({ removed: [values.id] }));
          appHistory.push(`/websites/${sites[0]?.id}/urls`);
        }
      }));
    }
  };

  const notLoggedWarning = (
    <Alert severity="warning">
      <AlertTitle>Not logged into Google</AlertTitle>
      Please login above to set up Google services and see website analytics
    </Alert>
  );

  const renderPage = () => {
    if (isConfig) {
      return loggedWithGoogle ? (
        <GoogleSetup
          classes={classes}
          site={values}
          error={errors as any}
          setFieldValue={setFieldValue}
          handleChange={handleChange}
          gaAccountItems={gaAccountItems}
          gtmAccountItems={gtmAccountItems}
          gaWebPropertyItems={gaWebPropertyItems}
          gtmContainerItems={gtmContainerItems}
          googleProfileEmail={profile?.email}
          concurentAccounts={concurentAccounts}
        />
      ) : (
        notLoggedWarning
      );
    }

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

      case 'tagManager':
        return (
          <TagManager
            gtmContainerId={values.gtmContainerId}
            gtmContainer={gtmContainer}
          />
        );
      case 'analytics':
        return loggedWithGoogle ? (
          <Analytics
            key={values.gaWebPropertyId}
            gaWebPropertyId={values.gaWebPropertyId}
            googleAnalyticsId={values.googleAnalyticsId}
          />
        ) : notLoggedWarning;
      default:
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
                {isConfig && (
                  <>
                    <Button
                      className="text-secondary"
                      onClick={() => setIsConfig(false)}
                      disabled={loading}
                      startIcon={<ChevronLeftIcon fontSize="medium" />}
                    >
                      Back
                    </Button>
                    <div className="flex-1" />
                  </>
                )}

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

                {(isConfig || page === 'urls') && (
                  <LoadingButton
                    variant="contained"
                    color="primary"
                    type="submit"
                    loading={customLoading || loading}
                    disabled={!isValid || !dirty}
                    disableElevation
                  >
                    Save
                  </LoadingButton>
                )}

                {!isConfig && page === 'tagManager'
                && (
                  <Button
                    onClick={() => window.open(gtmContainer?.tagManagerUrl || 'https://tagmanager.google.com/#/home', 'blank')}
                    disableElevation
                    color="primary"
                    variant="contained"
                    className="mr-2"
                    endIcon={<OpenInNewIcon />}
                  >
                    Open tag manager
                  </Button>
                )}

                {loggedWithGoogle && !isConfig && page !== 'urls' && (
                  <LoadingButton
                    onClick={() => setIsConfig(true)}
                    disableElevation
                    loading={customLoading || loading}
                    color="primary"
                    variant="contained"
                    endIcon={<SettingsIcon />}
                  >
                    Configure
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
