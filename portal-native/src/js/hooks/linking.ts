import { EventType, ParsedURL } from 'expo-linking/src/Linking.types';
import * as Linking from 'expo-linking';
import { useEffect, useState } from 'react';
import { Platform } from 'react-native';
import { LoginStage } from '@api/model';
import { useAppDispatch } from './redux';
import { setLoginStage, setLoginUrl, signIn } from '../actions/LoginActions';
import { LoginStages } from '../model/Login';

export const useLinkingRedirects = () => {
  const [urlData, setUrlData] = useState<ParsedURL & { url: string }>(null);
  const [isRedirecting, setIsRedirecting] = useState(false);

  const dispatch = useAppDispatch();

  useEffect(() => {
    const handleDeepLink = (e: EventType) => {
      setUrlData({ ...Linking.parse(e.url), url: e.url });
    };

    Linking.addEventListener('url', handleDeepLink);

    const getInitialUrl = async () => {
      const initial = await Linking.getInitialURL();
      if (initial) {
        setUrlData({ ...Linking.parse(initial), url: initial });
      }
    };

    if (!urlData) {
      getInitialUrl();
    }

    return () => {
      Linking.removeEventListener('url', handleDeepLink);
    };
  }, []);

  // Handle email login links
  useEffect(() => {
    if (!isRedirecting && urlData?.path === 'login' && urlData?.queryParams?.stage) {
      switch (urlData.queryParams.stage as LoginStage) {
        case 'authorize':
          dispatch(signIn({
            verificationUrl: urlData.url
          }));
          break;
        case 'create':
          dispatch(setLoginStage(LoginStages.CreateAccount));
          dispatch(setLoginUrl(urlData.url));
          break;
        case 'password':
          dispatch(setLoginStage(LoginStages.PasswordReset));
          dispatch(setLoginUrl(urlData.url));
          break;
      }
      setIsRedirecting(true);
    }
    if (isRedirecting && Platform.OS === 'web' && window.location.search) {
      window.history.replaceState(null, null, window.location.pathname);
    }
  }, [urlData, isRedirecting]);
};
