import * as Facebook from 'expo-auth-session/providers/facebook';
import * as Google from 'expo-auth-session/providers/google';
import { useEffect } from 'react';
import { AuthRequest, AuthRequestPromptOptions, AuthSessionResult } from 'expo-auth-session/src/AuthSession';
import { makeRedirectUri, useAuthRequest } from 'expo-auth-session';

interface Props {
  onSuccsess: (...args: any[]) => void;
  clientId: string;
}

export const useGoogleConnect = (
  { onSuccsess, clientId }: Props): [
    AuthRequest | null,
    (options?: AuthRequestPromptOptions
    ) => Promise<AuthSessionResult>
  ] => {
  const [request, response, promptAsync] = Google.useAuthRequest({
    responseType: 'code',
    expoClientId: clientId,
    webClientId: clientId,
    scopes: [
      'openid',
      'https://www.googleapis.com/auth/calendar.events',
      'https://www.googleapis.com/auth/drive.file',
      'https://www.googleapis.com/auth/drive.metadata',
      'https://www.googleapis.com/auth/userinfo.profile',
      'https://www.googleapis.com/auth/userinfo.profile'
    ],
    shouldAutoExchangeCode: false
  });

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code);
    }
  }, [response]);

  return [request, promptAsync] as any;
};

export const useFacebookConnect = (
  { onSuccsess, clientId }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    )
    => Promise<AuthSessionResult>] => {
  const [request, response, promptAsync] = Facebook.useAuthRequest({
    responseType: 'code',
    clientId,
  });

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code);
    }
  }, [response]);

  return [request, promptAsync] as any;
};

export const useMicrosoftConnect = (
  { onSuccsess, clientId }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    ) => Promise<AuthSessionResult>] => {
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
      redirectUri: makeRedirectUri(),
    },
    {
      authorizationEndpoint: 'https://login.microsoftonline.com/common/oauth2/v2.0/authorize',
      tokenEndpoint: 'https://login.microsoftonline.com/common/oauth2/v2.0/token'
    }
  );

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code);
    }
  }, [response]);

  return [request, promptAsync] as any;
};
