import * as Facebook from 'expo-auth-session/providers/facebook';
import { useEffect } from 'react';
import { AuthRequest, AuthRequestPromptOptions, AuthSessionResult } from 'expo-auth-session/src/AuthSession';

interface Props {
  onSuccsess: Function;
}

export default ({ onSuccsess }: Props): [AuthRequest | null, (options?: AuthRequestPromptOptions) => Promise<AuthSessionResult>] => {
  const [request, response, promptAsync] = Facebook.useAuthRequest({
    clientId: '837945397102277',
  });

  useEffect(() => {
    if (response?.type === 'success') {
      const { authentication } = response;
      onSuccsess(authentication);
    }
  }, [response]);

  return [request, promptAsync] as any;
};
