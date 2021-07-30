import React, { useState } from 'react';
import { registerRootComponent } from 'expo';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { Provider } from 'react-redux';
import { Provider as PaperProvider } from 'react-native-paper';
import { StatusBar } from 'expo-status-bar';
import useCachedResources from './hooks/useCachedResources';
import { initMockDB } from '../dev/MockAdapter';
import { getThemeByType, ThemeContext, ThemeType } from './styles';
import store from './reducers/Store';
import Navigation from './components/navigation';
import { ErrorBoundary } from './constants/Bugsnag';
import Message from './components/feedback/Message';

if (__DEV__) {
  initMockDB();
}

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
                <StatusBar
                  backgroundColor="#666666"
                  style="light"
                />
                <Navigation />
                <Message />
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
