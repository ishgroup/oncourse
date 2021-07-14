import { useEffect } from 'react';
import {
  AuthRequest,
  AuthRequestPromptOptions,
  AuthSessionResult,
  makeRedirectUri,
  useAuthRequest
} from 'expo-auth-session';

interface Props {
  onSuccsess: Function;
}

export default ({ onSuccsess }: Props): [AuthRequest | null, (options?: AuthRequestPromptOptions) => Promise<AuthSessionResult>] => {
  // Endpoint
  // const discovery = useAutoDiscovery('https://login.microsoftonline.com/common/v2.0');
  // Request
  const [request, response, promptAsync] = useAuthRequest(
    {
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
      console.log(response);
      const { authentication } = response;
      onSuccsess(authentication);
    }
  }, [response]);

  return [request, promptAsync] as any;
};
