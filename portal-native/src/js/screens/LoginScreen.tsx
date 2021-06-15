import React, { useRef, useState } from 'react';
import {
  Image, View, StyleSheet, Animated,
} from 'react-native';
import {
  TextInput, Card, Switch, Caption, Button,
} from 'react-native-paper';
import { cs, spacing, theme } from '../styles';
import '@expo/match-media';

const styles = StyleSheet.create({
  topPart: {
    flex: 3,
    backgroundColor: '#fbf9f0',
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
    borderRadius: spacing(3),
    paddingLeft: spacing(2),
    paddingRight: spacing(2),
    paddingBottom: spacing(2),
  },
  input: {
    marginTop: spacing(2),
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
    marginTop: spacing(4),
    marginBottom: spacing(2),
  },
  caption: {
    paddingTop: spacing(3),
    width: 340,
  },
  socialNetworkImage: {
    margin: spacing(1),
    height: 30,
    width: 30,
  },
});

const SignInContent = ({ setIsSignIn }) => (
  <>
    <TextInput
      style={styles.input}
      label="Email"
    />
    <TextInput
      label="Password"
      style={styles.input}
      secureTextEntry
      right={<TextInput.Icon name="eye" />}
    />
    <View style={[cs.flexRow, cs.justifyContentEnd]}>
      <Caption style={cs.colorPrimary} onPress={() => console.log('Pressed')}>
        Forgot password?
      </Caption>
    </View>
    <View style={styles.submit}>
      <Button mode="contained" dark onPress={() => console.log('Pressed')}>Log in</Button>
    </View>
    <View style={[cs.flexCenter, cs.mt3, cs.mb1]}>
      <Caption>
        Or connect using
      </Caption>
    </View>
    <View style={[cs.flexRow, cs.justifyContentCenter]}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../assets/images/google-color.png')}
      />
      <Image
        style={styles.socialNetworkImage}
        source={require('../../assets/images/facebook-square-color.png')}
      />
    </View>
    <View style={[cs.flexCenter, cs.mt1]}>
      <Caption style={cs.colorText}>
        Don't have an account?
        {' '}
        <Caption onPress={() => setIsSignIn((prev) => !prev)} style={cs.colorPrimary}>Sign Up</Caption>
      </Caption>
    </View>
  </>
);

const SignUpContent = ({ setIsSignIn }) => {
  const [isCompany, setIsCompany] = useState(false);
  const [showCompany, setShowCompany] = useState(false);

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
              <TextInput
                style={styles.input}
                label="Company name"
              />
            </>
          )
          : (
            <>
              <TextInput
                style={styles.input}
                label="First name"
              />
              <TextInput
                style={styles.input}
                label="Last name"
              />
            </>
          )}
      </Animated.View>
      <TextInput
        style={styles.input}
        label="Email"
      />
      <TextInput
        label="Password"
        style={styles.input}
        secureTextEntry
        right={<TextInput.Icon name="eye" />}
      />
      <TextInput
        label="Confirm password"
        style={styles.input}
        secureTextEntry
        right={<TextInput.Icon name="eye" />}
      />
      <View style={styles.submit}>
        <Button mode="contained" dark onPress={() => console.log('Pressed')}>Create account</Button>
      </View>
      <View style={[cs.flexCenter, cs.mt1]}>
        <Caption style={cs.colorText}>
          Already have an account?
          {' '}
          <Caption onPress={() => setIsSignIn((prev) => !prev)} style={cs.colorPrimary}>Sign In</Caption>
        </Caption>
      </View>
    </>
  );
};

const LoginScreen = () => {
  const [isSignIn, setIsSignIn] = useState(true);

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
              {isSignIn
                ? <SignInContent setIsSignIn={setIsSignIn} />
                : <SignUpContent setIsSignIn={setIsSignIn} />}
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
