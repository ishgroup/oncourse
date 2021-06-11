import React, {useState} from 'react';
import { registerRootComponent } from 'expo';
import {SafeAreaProvider} from 'react-native-safe-area-context';
import useCachedResources from './hooks/useCachedResources';
import useColorScheme from './hooks/useColorScheme';
import {initMockDB} from "../dev/MockAdapter";
import {DefaultHttpService} from "./constants/HttpService";
import LoginScreen from "./screens/LoginScreen";
import { Provider as PaperProvider } from 'react-native-paper';
import {theme} from "./styles";


if (__DEV__) {
  initMockDB();
}

function App() {
  const [isLogged, setIsLogged] = useState(false);

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
        <PaperProvider theme={theme}>
          {/*<Navigation colorScheme={colorScheme} />*/}
          {/*<StatusBar hidden />*/}
          <LoginScreen />
        </PaperProvider>
      </SafeAreaProvider>
    );
  } else {
    return null;
  }
}

export default registerRootComponent(App);
