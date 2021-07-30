import React, { useCallback, useMemo } from 'react';
import * as yup from 'yup';
import { Formik } from 'formik';
import { Image, View } from 'react-native';
import { Caption, Card } from 'react-native-paper';
import * as WebBrowser from 'expo-web-browser';
import '@expo/match-media';
import { useMediaQuery } from 'react-responsive';
import { SceneMap, TabView } from 'react-native-tab-view';
import { useCommonStyles } from '../../hooks/styles';
import { useStyles } from './styles';
import LoginContent from './LoginContent';
import { LoginRoute, LoginStages, LoginValues } from '../../model/Login';
import { useAppDispatch, useAppSelector } from '../../hooks/redux';
import EmaiConfirmContent from './CheckEmailContent';
import ResetPasswordContent from './ResetPasswordContent';
import { emailLogin, setLoginUrl, signIn } from '../../actions/LoginActions';
import CreateAccountContent from './CreateAccountContent';

WebBrowser.maybeCompleteAuthSession();

const renderScene = SceneMap<any>({
  Login: LoginContent,
  PasswordReset: ResetPasswordContent,
  CreateAccount: CreateAccountContent,
  EmaiConfirm: EmaiConfirmContent,
});

const initialValues: LoginValues = { submitBy: 'LoginEmail' };

const routes: LoginRoute[] = [
  { key: 'Login' },
  { key: 'PasswordReset' },
  { key: 'CreateAccount' },
  { key: 'EmaiConfirm' },
];

const LoginScreen = () => {
  const isSmallScreen = useMediaQuery({ query: '(max-device-height: 800px)' });
  const stage = useAppSelector((state) => state.login.stage);
  const verificationUrl = useAppSelector((state) => state.login.verificationUrl);

  const styles = useStyles();
  const cs = useCommonStyles();
  const dispatch = useAppDispatch();

  const onSubmit = useCallback(({ confirmPassword, submitBy, ...values }: LoginValues) => {
    if ([LoginStages.PasswordReset, LoginStages.CreateAccount].includes(stage)) {
      dispatch(signIn({
        ...values,
        verificationUrl
      }));
      dispatch(setLoginUrl(null));
      return;
    }
    if (submitBy === 'LoginEmail') {
      dispatch(emailLogin(values.email));
      return;
    }
    if (submitBy === 'SignIn') {
      dispatch(signIn(values));
    }
  }, [stage, verificationUrl]);

  const validationSchema: yup.SchemaOf<LoginValues> = useMemo(() => {
    const isPasswordScreens = [LoginStages.PasswordReset, LoginStages.CreateAccount].includes(stage);

    return yup.object({
      confirmPassword: isPasswordScreens ? yup.string().when('password', {
        is: (val) => val,
        then: yup.string().required('Please confirm password').oneOf([yup.ref('password')], 'Passwords should match'),
        otherwise: yup.string().notRequired(),
      }) : yup.string().nullable().notRequired(),
      email: isPasswordScreens
        ? yup.string().nullable().notRequired()
        : yup.string().required('Email is required').email('Please enter valid email'),
      password: isPasswordScreens ? yup.string().required('Password is required') : yup.string().when('submitBy', {
        is: (val) => val !== 'LoginEmail',
        then: yup.string().required('Password is required'),
        otherwise: yup.string().notRequired(),
      }),
      verificationUrl: yup.string().nullable().notRequired(),
      submitBy: yup.mixed().nullable().notRequired(),
      sSOToken: yup.string().nullable().notRequired(),
      sSOProvider: yup.mixed().nullable().notRequired()
    });
  }, [stage]);

  const Logo = (
    <Image
      source={require('../../../assets/images/ish-onCourse-icon-192.png')}
      style={styles.logo}
    />
  );

  const Info = (
    <Caption style={styles.caption}>
      Login to skillsOnCourse if you are a tutor or a student. Manage your classes, view your timetable and much more.
    </Caption>
  );

  return (
    <View
      style={cs.flex1}
    >
      <View style={styles.topPart} />
      <View style={styles.bottomPart} />
      <View style={[
        styles.loginContainerWrapper,
        cs.justifyContentCenter,
      ]}
      >
        {!isSmallScreen && Logo}
        <Card
          elevation={3}
          style={isSmallScreen ? styles.loginContainerFullScreen : styles.loginContainer}
        >
          <View style={styles.content}>
            <Formik
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={onSubmit}
            >
              <TabView
                renderTabBar={() => null}
                navigationState={{ index: stage, routes }}
                renderScene={renderScene}
                onIndexChange={() => null}
                swipeEnabled={false}
              />
            </Formik>
          </View>
        </Card>
        {!isSmallScreen && Info}
      </View>
    </View>
  );
};

export default LoginScreen;
