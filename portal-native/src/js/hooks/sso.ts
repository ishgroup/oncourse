import * as Facebook from 'expo-auth-session/providers/facebook';
import * as Google from 'expo-auth-session/providers/google';
import { useEffect } from 'react';
import { AuthRequest, AuthRequestPromptOptions, AuthSessionResult } from 'expo-auth-session/src/AuthSession';
import { makeRedirectUri, useAuthRequest } from 'expo-auth-session';

interface Props {
  onSuccsess: (...args: any[]) => void;
}

export const useGoogleConnect = (
  { onSuccsess }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    ) => Promise<AuthSessionResult>] => {
  const [request, response, promptAsync] = Google.useAuthRequest({
    responseType: 'code',
    // TODO Replace with real credentials for production
    expoClientId: '568692144060-nku44p171f3sar4v06g7ve0vdmf2ppen.apps.googleusercontent.com',
    webClientId: '568692144060-nku44p171f3sar4v06g7ve0vdmf2ppen.apps.googleusercontent.com',
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
  { onSuccsess }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    )
    => Promise<AuthSessionResult>] => {
  const [request, response, promptAsync] = Facebook.useAuthRequest({
    responseType: 'code',
    // TODO Replace with real credentials for production
    clientId: '837945397102277',
  });

  useEffect(() => {
    if (response?.type === 'success') {
      onSuccsess(response.params?.code);
    }
  }, [response]);

  return [request, promptAsync] as any;
};

export const useMicrosoftConnect = (
  { onSuccsess }: Props): [AuthRequest | null,
    (options?: AuthRequestPromptOptions
    ) => Promise<AuthSessionResult>] => {

  const [request, response, promptAsync] = useAuthRequest(
    {
      responseType: 'code',
      // TODO Replace with real credentials for production
      clientId: '5aa7f417-c05e-49ae-8c10-026999bd12d2',
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
        'Files.ReadWrite.Selected'
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
