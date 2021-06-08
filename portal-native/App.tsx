import React from 'react';
import {SafeAreaProvider} from 'react-native-safe-area-context';
import useCachedResources from './hooks/useCachedResources';
import useColorScheme from './hooks/useColorScheme';
import {initMockDB} from "./apiMocks/MockAdapter";
import {DefaultHttpService} from "./constants/HttpService";
import LoginScreen from "./components/login/LoginScreen";

if (__DEV__) {
  initMockDB();
}

export default function App() {
  const [isLogged, setIsLogged] = React.useState(false);

  const isLoadingComplete = useCachedResources();
  const colorScheme = useColorScheme();

  React.useEffect(() => {
    (new DefaultHttpService()).GET("/v1/login/").then(r => {
      setIsLogged(r);
    })
  }, [])


  if (isLoadingComplete) {
    return (
      <SafeAreaProvider>
        {/*<Navigation colorScheme={colorScheme} />*/}
        {/*<StatusBar hidden />*/}
        <LoginScreen />
      </SafeAreaProvider>
    );
  } else {
    return null;
  }
}
