import React, { useEffect, useMemo, useRef, useState } from 'react';
import { LoginRequest } from '@api/model';
import * as yup from 'yup';
import { Formik } from 'formik';
import { Animated, Image, TouchableOpacity, View } from 'react-native';
import { Button, Caption, Card, Switch, TextInput } from 'react-native-paper';
import * as WebBrowser from 'expo-web-browser';
import { useAppDispatch, useAppSelector } from '../hooks/redux';
import TextField from '../components/fields/TextField';
import { connect, signIn } from '../actions/LoginActions';
import { createStyles, useCommonStyles } from '../hooks/styles';
import useGoogleConnect from '../hooks/useGoogleConnect';
import useFacebookConnect from '../hooks/useFacebookConnect';
import useMicrosoftConnect from '../hooks/useMicrosoftConnect';

WebBrowser.maybeCompleteAuthSession();

const useStyles = createStyles((theme) => ({
  topPart: {
    flex: 3,
    backgroundColor: theme.colors.background,
  },
  bottomPart: {
    flex: 2,
    backgroundColor: theme.colors.accent,
  },
  loginContainerWrapper: {
    position: 'absolute',
    left: 0,
    right: 0,
    alignItems: 'center',
    height: '100%',
  },
  loginContainer: {
    width: 340,
    borderRadius: theme.spacing(3),
    paddingLeft: theme.spacing(2),
    paddingRight: theme.spacing(2),
    paddingBottom: theme.spacing(2),
  },
  input: {
    marginTop: theme.spacing(2),
    backgroundColor: '#fff',
  },
  headline: {
    paddingLeft: 12,
  },
  logo: {
    height: 140,
    width: 140,
  },
  submit: {
    marginTop: theme.spacing(4),
    marginBottom: theme.spacing(2),
  },
  caption: {
    paddingTop: theme.spacing(3),
    width: 340,
  },
  socialNetworkImage: {
    margin: theme.spacing(1),
    height: 30,
    width: 30,
  },
}));

const GoogleConnect = ({ onSuccsess }) => {
  const [request, promptAsync] = useGoogleConnect({ onSuccsess });
  const styles = useStyles();
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../assets/images/google-color.png')}
      />
    </TouchableOpacity>
  );
};

const FacebookConnect = ({ onSuccsess }) => {
  const [request, promptAsync] = useFacebookConnect({ onSuccsess });
  const styles = useStyles();
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../assets/images/facebook-square-color.png')}
      />
    </TouchableOpacity>
  );
};

const MicrosoftConnect = ({ onSuccsess }) => {
  const [request, promptAsync] = useMicrosoftConnect({ onSuccsess });
  const styles = useStyles();
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../assets/images/microsoft.png')}
      />
    </TouchableOpacity>
  );
};

const SignInContent = (
  {
    isValid,
    loading,
    onPressSign,
    handleSubmit,
    dispatch
  }
) => {
  const [hidePassword, setHidePassword] = useState(true);
  const styles = useStyles();
  const cs = useCommonStyles();

  const onConnectSuccsess = (authentication) => {
    dispatch(connect(authentication));
  };

  return (
    <>
      <TextField
        name="email"
        label="Email"
        style={styles.input}
      />
      <TextField
        name="password"
        label="Password"
        style={styles.input}
        secureTextEntry={hidePassword}
        right={(
          <TextInput.Icon
            onPress={() => setHidePassword((pr) => !pr)}
            name={hidePassword ? 'eye' : 'eye-off'}
          />
      )}
      />
      <View style={[cs.flexRow, cs.justifyContentEnd]}>
        <Caption style={cs.colorPrimary} onPress={() => console.log('Pressed')}>
          Forgot password?
        </Caption>
      </View>
      <View style={styles.submit}>
        <Button
          mode="contained"
          dark
          onPress={handleSubmit}
          disabled={!isValid}
          loading={loading}
        >
          Log in?
        </Button>
      </View>
      <View style={[cs.flexCenter, cs.mt3, cs.mb1]}>
        <Caption>
          Or connect using
        </Caption>
      </View>
      <View style={[cs.flexRow, cs.justifyContentCenter]}>
        <GoogleConnect
          onSuccsess={onConnectSuccsess}
        />
        <MicrosoftConnect
          onSuccsess={onConnectSuccsess}
        />
        <FacebookConnect
          onSuccsess={onConnectSuccsess}
        />
      </View>
      <View style={[cs.flexCenter, cs.mt1]}>
        <Caption style={cs.colorText}>
          Don't have an account?
          {' '}
          <Caption onPress={onPressSign} style={cs.colorPrimary}>Sign Up</Caption>
        </Caption>
      </View>
    </>
  );
};

const SignUpContent = (
  {
    onPressSign,
    handleSubmit,
    isValid,
    loading,
    values,
    setFieldTouched,
    touched
  }
) => {
  const [isCompany, setIsCompany] = useState(false);
  const [showCompany, setShowCompany] = useState(false);
  const [hidePassword, setHidePassword] = useState(true);
  const [hidePasswordConfirm, setHidePasswordConfirm] = useState(true);

  const cs = useCommonStyles();
  const styles = useStyles();

  useEffect(() => {
    if (!touched.confirmPassword && values.password) {
      setFieldTouched('confirmPassword');
    }
  }, [values.password]);

  const heightAnim = useRef(new Animated.Value(160)).current;

  const onSetCompany = () => {
    setIsCompany((prev) => !prev);
    Animated.timing(
      heightAnim,
      {
        toValue: isCompany ? 160 : 80,
        duration: 200,
        useNativeDriver: false,
      },
    ).start(() => {
      setShowCompany((prev) => !prev);
    });
  };

  return (
    <>
      <View style={[cs.flexRow, cs.justifyContentEnd, cs.mt1]}>
        <View style={[cs.flexRow, cs.alignItemsCenter]}>
          <Caption style={cs.pr1}>
            Is company?
          </Caption>
          <Switch value={isCompany} onValueChange={onSetCompany} />
        </View>
      </View>
      <Animated.View
        style={{
          height: heightAnim,
        }}
      >
        {showCompany
          ? (
            <>
              <TextField
                name="companyName"
                label="Company name"
                style={styles.input}
              />
            </>
          )
          : (
            <>
              <TextField
                name="firstName"
                label="First name"
                style={styles.input}
              />
              <TextField
                name="lastName"
                label="Last name"
                style={styles.input}
              />
            </>
          )}
      </Animated.View>
      <TextField
        name="email"
        label="Email"
        style={styles.input}
      />
      <TextField
        name="password"
        label="Password"
        style={styles.input}
        secureTextEntry={hidePassword}
        right={(
          <TextInput.Icon
            onPress={() => setHidePassword((pr) => !pr)}
            name={hidePassword ? 'eye' : 'eye-off'}
          />
        )}
      />
      <TextField
        name="confirmPassword"
        label="Confirm password"
        style={styles.input}
        secureTextEntry={hidePasswordConfirm}
        right={(
          <TextInput.Icon
            onPress={() => setHidePasswordConfirm((pr) => !pr)}
            name={hidePasswordConfirm ? 'eye' : 'eye-off'}
          />
        )}
      />
      <View style={styles.submit}>
        <Button
          mode="contained"
          dark
          onPress={handleSubmit}
          disabled={!isValid}
          loading={loading}
        >
          Create account
        </Button>
      </View>
      <View style={[cs.flexCenter, cs.mt1]}>
        <Caption style={cs.colorText}>
          Already have an account?
          {' '}
          <Caption onPress={onPressSign} style={cs.colorPrimary}>Sign In</Caption>
        </Caption>
      </View>
    </>
  );
};

const LoginScreen = () => {
  const [isSignIn, setIsSignIn] = useState(true);

  const loading = useAppSelector((state) => state.login.loading);

  const dispatch = useAppDispatch();

  const styles = useStyles();
  const cs = useCommonStyles();

  const validationSchema = useMemo<yup.SchemaOf<LoginRequest & { confirmPassword: string }>>(() => yup.object({
    confirmPassword: isSignIn ? yup.string().notRequired() : yup.string().when('password', {
      is: (val) => val,
      then: yup.string().required('Please confirm password').oneOf([yup.ref('password')], 'Passwords should match'),
      otherwise: yup.string().notRequired(),
    }),
    firstName: yup.string().notRequired(),
    lastName: yup.string().notRequired(),
    companyName: yup.string().notRequired(),
    email: yup.string().required('Email is required').email('Please enter valid email'),
    password: yup.string().required('Password is required'),
  }), [isSignIn]);

  return (
    <View
      style={[cs.flex1, cs.overflowHidden]}
    >
      <View style={styles.topPart} />
      <View style={styles.bottomPart} />
      <View style={[
        styles.loginContainerWrapper,
        cs.justifyContentCenter,
      ]}
      >
        <Image
          source={require('../../assets/images/ish-onCourse-icon-192.png')}
          style={styles.logo}
        />
        <View>
          <Card
            elevation={3}
            style={styles.loginContainer}
          >
            <Card.Content>
              <Formik
                initialValues={{} as any}
                validationSchema={validationSchema}
                onSubmit={({ confirmPassword, ...values }) => {
                  dispatch(signIn(values));
                }}
              >
                {({
                  resetForm,
                  handleSubmit,
                  isValid,
                  touched,
                  values,
                  setFieldTouched
                }) => {
                  const onPressSign = () => {
                    resetForm();
                    setIsSignIn((prev) => !prev);
                  };

                  return (isSignIn
                    ? (
                      <SignInContent
                        onPressSign={onPressSign}
                        handleSubmit={handleSubmit}
                        isValid={isValid}
                        loading={loading}
                        dispatch={dispatch}
                      />
                    )
                    : (
                      <SignUpContent
                        onPressSign={onPressSign}
                        handleSubmit={handleSubmit}
                        isValid={isValid}
                        loading={loading}
                        values={values}
                        touched={touched}
                        setFieldTouched={setFieldTouched}
                      />
                    ));
                }}
              </Formik>
            </Card.Content>
          </Card>
          <Caption style={styles.caption}>
            Login to skillsOnCourse if you are a tutor or a student. Manage your classes, view your timetable and much more.
          </Caption>
        </View>
      </View>
    </View>
  );
};

export default LoginScreen;
