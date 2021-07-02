import React, { useState } from 'react';
import { registerRootComponent } from 'expo';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { Provider } from 'react-redux';
import { Provider as PaperProvider } from 'react-native-paper';
import { StatusBar } from 'expo-status-bar';
import useCachedResources from './hooks/useCachedResources';
import { initMockDB } from '../dev/MockAdapter';
import LoginScreen from './screens/LoginScreen';
import {
  getThemeByType, ThemeContext, ThemeType
} from './styles';
import store from './reducers/Store';
import { useAppSelector } from './hooks/redux';
import Navigation from './components/navigation';
import { ErrorBoundary } from './constants/Bugsnag';

if (__DEV__) {
  initMockDB();
}

const RootResolver = () => {
  const isLogged = useAppSelector((state) => state.login.isLogged);

  return isLogged ? (
    <>
      <StatusBar
        backgroundColor="#666666"
        style="light"
      />
      <Navigation />
    </>
  ) : <LoginScreen />;
};

const App = () => {
  const [theme, setTheme] = useState<ThemeType>('light');
  const currentTheme = getThemeByType(theme);
  const isLoadingComplete = useCachedResources();

  if (isLoadingComplete) {
    return (
      <ErrorBoundary>
        <SafeAreaProvider>
          <ThemeContext.Provider value={currentTheme}>
            <PaperProvider theme={currentTheme}>
              <Provider store={store}>
                <RootResolver />
              </Provider>
            </PaperProvider>
          </ThemeContext.Provider>
        </SafeAreaProvider>
      </ErrorBoundary>
    );
  }
  return null;
};

export default registerRootComponent(App);
