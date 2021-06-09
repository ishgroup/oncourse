import React, {useRef, useState} from "react";
import {Image, View, StyleSheet, Animated } from "react-native";
import { TextInput, Card, Headline, Switch, Caption, Button } from 'react-native-paper';
import {cs, spacing, theme} from "../../common/styles";

const styles = StyleSheet.create({
  root: {
    height: 635
  },
  topPart: {
    flex: 3,
    backgroundColor: "#fbf9f0"
  },
  bottomPart: {
    flex: 2,
    backgroundColor: theme.colors.accent
  },
  loginContainerWrapper: {
    position: "absolute",
    left: 0,
    right: 0,
    height: "100%",
    alignItems: "center",
    justifyContent: "center"
  },
  loginContainer: {
    width: 340,
    borderRadius: spacing(3),
    padding: spacing(2)
  },
  input: {
    marginTop: spacing(2),
    backgroundColor: "#fff",
  },
  headline: {
    paddingLeft: 12
  },
  logo: {
    height: 140,
    width: 140
  },
  submit: {
    paddingTop: spacing(3),
    paddingLeft: spacing(2),
    paddingRight: spacing(2),
    justifyContent: "space-between"
  },
  caption: {
    paddingTop: spacing(3),
    paddingBottom: spacing(12),
    width: 340,
  }
})

const LoginScreen = () => {
  const [isCompany, setIsCompany] = useState(false);
  const [showCompany, setShowCompany] = useState(false);

  const heightAnim = useRef(new Animated.Value(160)).current

  const onSetCompany = () => {
    setIsCompany(prev => !prev);
    Animated.timing(
      heightAnim,
      {
        toValue: isCompany ? 160 : 80,
        duration: 200,
        useNativeDriver: false
      }
    ).start(() => {
      setShowCompany(prev => !prev);
    });
  }

  return (
    <View
      style={cs.flex1}
    >
      <View style={styles.topPart} />
      <View style={styles.bottomPart} />
      <View style={styles.loginContainerWrapper}
      >
        <Image
          source={require("../../../assets/images/ish-onCourse-icon-192.png")}
          style={styles.logo}
        />
        <View
          style={styles.root}
        >
          <Card
            elevation={3}
            style={styles.loginContainer}
          >
            <Card.Content>
              <View style={[
                cs.flexRow,
                cs.justifyContentSpaceBetween,
                cs.pb05
              ]}>
                <Headline style={styles.headline}>Log In</Headline>
                <View style={[cs.flexRow,cs.alignItemsCenter]}>
                  <Caption style={cs.pr1}>
                    As company
                  </Caption>
                  <Switch value={isCompany} onValueChange={onSetCompany} />
                </View>
              </View>
              <Animated.View
                style={{
                  height: heightAnim
                }}
              >
                {showCompany
                  ? <>
                    <TextInput
                      style={styles.input}
                      label="Company name"
                    />
                  </>
                  : <>
                    <TextInput
                      style={styles.input}
                      label="First name"
                    />
                    <TextInput
                      style={styles.input}
                      label="Last name"
                    />
                  </>
                }
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
            </Card.Content>
            <Card.Actions style={styles.submit}>
              <Caption style={cs.colorPrimary}>
                Forgot password?
              </Caption>
              <Button mode="contained" dark>Submit</Button>
            </Card.Actions>
          </Card>
          <Caption style={styles.caption}>
            Login to skillsOnCourse if you are a tutor or a student. Manage your classes, view your timetable and much more.
          </Caption>
        </View>
      </View>
    </View>
  );
}

export default LoginScreen;
