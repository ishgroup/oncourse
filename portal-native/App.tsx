import React from 'react';
import {SafeAreaProvider} from 'react-native-safe-area-context';
import {StatusBar} from 'expo-status-bar';
import useCachedResources from './hooks/useCachedResources';
import useColorScheme from './hooks/useColorScheme';
import Navigation from './navigation';
import {initMockDB} from "./apiMocks/MockAdapter";
import {DefaultHttpService} from "./constants/HttpService";

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


  if (isLoadingComplete && isLogged) {
    return (
      <SafeAreaProvider>
        <Navigation colorScheme={colorScheme} />
        <StatusBar hidden />
      </SafeAreaProvider>
    );
  } else {
    return null;
  }
}
