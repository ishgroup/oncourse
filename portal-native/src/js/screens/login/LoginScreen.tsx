import React, { useCallback, useState } from 'react';
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
import { LoginStages, LoginValues } from '../../model/Login';
import { useAppDispatch } from '../../hooks/redux';
import EmaiConfirmContent from './CheckEmailContent';
import ResetPasswordContent from './ResetPasswordContent';

WebBrowser.maybeCompleteAuthSession();

const renderScene = SceneMap<any>({
  login: LoginContent,
  email: EmaiConfirmContent,
  password: ResetPasswordContent
});

const initialValues: LoginValues = { submitBy: 'LoginEmail' };

const validationSchema: yup.SchemaOf<LoginValues> = yup.object({
  confirmPassword: yup.string().when('password', {
    is: (val) => val,
    then: yup.string().required('Please confirm password').oneOf([yup.ref('password')], 'Passwords should match'),
    otherwise: yup.string().notRequired(),
  }),
  email: yup.string().required('Email is required').email('Please enter valid email'),
  password: yup.string().when('submitBy', {
    is: (val) => val !== 'LoginEmail',
    then: yup.string().required('Password is required'),
    otherwise: yup.string().notRequired(),
  }),
  firstName: yup.string().nullable().notRequired(),
  lastName: yup.string().nullable().notRequired(),
  companyName: yup.string().nullable().notRequired(),
  submitBy: yup.mixed().nullable().notRequired(),
  sSOToken: yup.string().nullable().notRequired(),
  sSOProvider: yup.mixed().nullable().notRequired()
});

const LoginScreen = () => {
  const isSmallScreen = useMediaQuery({ query: '(max-device-height: 800px)' });
  const [stage, setStage] = useState<number>(0);
  const routes = [
    { key: 'login' },
    { key: 'email' },
    { key: 'password' }
  ];

  const styles = useStyles();
  const cs = useCommonStyles();
  const dispatch = useAppDispatch();

  // const validate = useCallback<any>((values: LoginValues): FormikErrors<LoginValues> => {
  //   const errors: any = {};
  //
  //   try {
  //     yup.string().required('Email is required').email('Please enter valid email').validateSync(values.email);
  //   } catch (e) {
  //     errors.email = e.message;
  //   }
  //
  //   if (values.submitBy !== 'LoginEmail' && !values.password) {
  //     errors.password = 'Password is required';
  //   }
  //
  //   if ([LoginStages.PasswordConfirm, LoginStages.PasswordReset].includes(stage) && values.password) {
  //     if (!values.confirmPassword) {
  //       errors.confirmPassword = 'Please confirm password';
  //     } else if (values.confirmPassword !== values.password) {
  //       errors.confirmPassword = 'Passwords should match';
  //     }
  //   }
  //
  //   return errors;
  // }, [stage]);

  const onSubmit = useCallback(({ confirmPassword, submitBy, ...values }) => {
    if (stage === LoginStages.Login) {
      setStage(LoginStages.EmaiConfirm);
    }
    // dispatch(signIn(values));
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
                onIndexChange={setStage}
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
