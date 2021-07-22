import * as Google from 'expo-auth-session/providers/google';
import { useEffect } from 'react';
import { AuthRequest, AuthRequestPromptOptions, AuthSessionResult } from 'expo-auth-session/src/AuthSession';
import { Prompt } from 'expo-auth-session';

interface Props {
  onSuccsess: Function;
}

export default ({ onSuccsess }: Props): [AuthRequest | null, (options?: AuthRequestPromptOptions) => Promise<AuthSessionResult>] => {
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
    shouldAutoExchangeCode: false,
    prompt: Prompt.Consent
  });

  useEffect(() => {
    if (response?.type === 'success') {
      console.log(request, response);
      const { authentication } = response;

      const url = new URL('https://oauth2.googleapis.com/token');
      url.searchParams.append('code', response.params.code);
      url.searchParams.append('client_id', '568692144060-nku44p171f3sar4v06g7ve0vdmf2ppen.apps.googleusercontent.com');
      url.searchParams.append('client_secret', 'Br21_38hfMWFezArWITjrcwi');
      url.searchParams.append('redirect_uri', request.redirectUri);
      url.searchParams.append('grant_type', 'authorization_code');
      url.searchParams.append('code_verifier', request.codeVerifier);

      fetch(url.toString(), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
      }).then((r) => r.json()).then((r) => console.log(r)).catch((e) => console.log(e));

      // onSuccsess(authentication);
    }
  }, [response]);

  return [request, promptAsync] as any;
};
