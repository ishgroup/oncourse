import React from 'react';
import {SafeAreaProvider} from 'react-native-safe-area-context';
import useCachedResources from './js/hooks/useCachedResources';
import useColorScheme from './js/hooks/useColorScheme';
import {initMockDB} from "./js/apiMocks/MockAdapter";
import {DefaultHttpService} from "./js/constants/HttpService";
import LoginScreen from "./js/components/login/LoginScreen";
import { Provider as PaperProvider } from 'react-native-paper';
import {theme} from "./js/common/styles";

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
