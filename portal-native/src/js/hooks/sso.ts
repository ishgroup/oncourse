import * as Facebook from 'expo-auth-session/providers/facebook';
import * as Google from 'expo-auth-session/providers/google';
import { useEffect, useMemo } from 'react';
import { AuthRequest, AuthRequestPromptOptions, AuthSessionResult } from 'expo-auth-session/src/AuthSession';
import { makeRedirectUri, Prompt, useAuthRequest } from 'expo-auth-session';
import { Platform } from 'react-native';

interface Props {
  onSuccsess: (...args: any[]) => void;
  androidClientId: string;
  iosClientId: string;
  webClientId: string;
}

const redirectUri = Platform.OS === 'web'
  ? makeRedirectUri({ path: '/new' })
  : makeRedirectUri();

export const useGoogleConnect = (
  {
    onSuccsess,
    webClientId,
    iosClientId,
    androidClientId
  }: Props): [
    AuthRequest | null,
    (options?: AuthRequestPromptOptions
    ) => Promise<AuthSessionResult>
  ] => {
  const [request, response, promptAsync] = Google.useAuthRequest({
    responseType: 'code',
    expoClientId: webClientId,
    webClientId,
    iosClientId,
    androidClientId,
    scopes: [
      'openid',
      'https://www.googleapis.com/auth/calendar.events',
      'https://www.googleapis.com/auth/drive.file',
      'https://www.googleapis.com/auth/drive.metadata',
      'https://www.googleapis.com/auth/userinfo.profile'
    ],
    shouldAutoExchangeCode: false,
    prompt: Prompt.Consent,
    redirectUri
  });

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code, request.codeVerifier);
    }
  }, [response]);

  return [request, promptAsync] as any;
};

export const useFacebookConnect = (
  {
    onSuccsess, webClientId, iosClientId, androidClientId
  }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    )
    => Promise<AuthSessionResult>] => {
  const [request, response, promptAsync] = Facebook.useAuthRequest({
    responseType: 'code',
    webClientId,
    iosClientId,
    androidClientId,
    redirectUri
  });

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code, request.codeVerifier);
    }
  }, [response]);

  return [request, promptAsync] as any;
};

export const useMicrosoftConnect = (
  {
    onSuccsess, webClientId, iosClientId, androidClientId
  }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    ) => Promise<AuthSessionResult>] => {
  const clientId = useMemo(() => {
    switch (Platform.OS) {
      default:
      case 'web':
        return webClientId;
      case 'android':
        return androidClientId;
      case 'ios':
        return iosClientId;
    }
  }, [webClientId, iosClientId, androidClientId]);

  const [request, response, promptAsync] = useAuthRequest(
    {
      responseType: 'code',
      clientId,
      scopes: [
        'openid',
        'profile',
        'email',
        'offline_access',
        'Files.Read',
        'Files.Read.All',
        'Files.ReadWrite',
        'Files.ReadWrite.All',
        'Files.ReadWrite.AppFolder',
        'Files.Read.Selected',
        'Files.ReadWrite.Selected',
        'Calendars.ReadWrite'
      ],
      redirectUri,
    },
    {
      authorizationEndpoint: 'https://login.microsoftonline.com/common/oauth2/v2.0/authorize',
      tokenEndpoint: 'https://login.microsoftonline.com/common/oauth2/v2.0/token'
    }
  );

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code, request.codeVerifier);
    }
  }, [response]);

  return [request, promptAsync] as any;
};
