import React, {useState} from "react";
import { Image, View, StyleSheet} from "react-native";
import { TextInput, Card, Headline } from 'react-native-paper';
import { commonStyles } from "../../common/styles";

const styles = StyleSheet.create({
  topPart: {
    flex: 3,
    backgroundColor: "#fbf9f0"
  },
  bottomPart: {
    flex: 2,
    backgroundColor: "#37caad"
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
    width: "320px",
    borderRadius: 24
  },
  input: {
    backgroundColor: "#fff"
  },
  headline: {
    paddingLeft: "12px"
  },
  logo: {
    height: "140px",
    width: "140px"
  }
})

const LoginScreen = () => {
  const [rootWidth, setRootWidth] = useState(1);

  const onLayout = e => {
    const { width } = e.nativeEvent.layout;
    setRootWidth(width);
  }

  return (
    <View
      onLayout={onLayout}
      style={commonStyles.flex1}
    >
      <View style={styles.topPart} />
      <View style={styles.bottomPart} />
      <View style={styles.loginContainerWrapper}
      >
        <Image
          source={require("../../assets/images/ish-onCourse-icon-192.png")}
          style={styles.logo}
        />
        <Card
          elevation={3}
          style={styles.loginContainer}
        >
          <Card.Content>
            <Headline style={styles.headline}>Log In</Headline>
            <TextInput
              style={styles.input}
              label="First name"
            />
            <TextInput
              style={styles.input}
              label="Last name"
            />
            <TextInput
              style={styles.input}
              label="Email"
            />
          </Card.Content>
        </Card>
      </View>
    </View>
  );
}

export default LoginScreen;
