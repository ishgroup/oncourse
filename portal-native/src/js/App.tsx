import React from 'react';
import { registerRootComponent } from 'expo';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { Provider } from 'react-redux';
import { Provider as PaperProvider } from 'react-native-paper';
import { StatusBar } from 'react-native';
import useCachedResources from './hooks/useCachedResources';
import useColorScheme from './hooks/useColorScheme';
import { initMockDB } from '../dev/MockAdapter';
import LoginScreen from './screens/LoginScreen';
import { theme } from './styles';
import store from './reducers/Store';
import { useAppSelector } from './hooks/redux';
import Navigation from './components/navigation';

if (__DEV__) {
  initMockDB();
}

const RootResolver = () => {
  const isLogged = useAppSelector((state) => state.login.isLogged);
  const colorScheme = useColorScheme();

  return isLogged ? (
    <>
      <Navigation colorScheme={colorScheme} />
      <StatusBar hidden />
    </>
  ) : <LoginScreen />;
};

const App = () => {
  const isLoadingComplete = useCachedResources();

  if (isLoadingComplete) {
    return (
      <SafeAreaProvider>
        <PaperProvider theme={theme}>
          <Provider store={store}>
            <RootResolver />
          </Provider>
        </PaperProvider>
      </SafeAreaProvider>
    );
  }
  return null;
};

export default registerRootComponent(App);
