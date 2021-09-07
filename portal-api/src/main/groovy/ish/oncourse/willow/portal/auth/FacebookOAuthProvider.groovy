package ish.oncourse.willow.portal.auth

import com.google.inject.Inject
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.willow.portal.v1.model.ClientId
import ish.oncourse.willow.portal.v1.model.Platform
import ish.oncourse.willow.portal.v1.model.SSOproviders
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.willow.portal.v1.model.Platform.WEB
import static ish.oncourse.willow.portal.v1.model.SSOproviders.FACEBOOK

@CompileDynamic
class FacebookOAuthProvider extends OAuthProvider {

    String clientId
    String clientSecret
    private Logger logger = LogManager.getLogger();

    @Inject
    FacebookOAuthProvider() {
        FileInputStream inputStream = readSecret(FACEBOOK, WEB)
        Map<String, Object> secretJson = new JsonSlurper().parse(inputStream) as Map<String, Object>
        clientId = secretJson['web']['client_id'] as String
        clientSecret = secretJson['web']['client_secret']
    }
    
    

    @Override
    SSOCredantials authorize(String activationCode, String redirectUrl, String codeVerifier) {
        Closure failureHandler = { resp, result ->
            logger.error(resp)
            logger.error(result)
            throw new LoginException('Login failed')
        }
        SSOCredantials credantials = new SSOCredantials()

        RESTClient client = new RESTClient('https://graph.facebook.com')
        
        //obtain access_tooken
        client.request(Method.GET, ContentType.JSON) {
            uri.path = "/oauth/access_token"
            uri.query = [client_id    : clientId,
                         redirect_uri : redirectUrl,
                         client_secret: clientSecret,
                         codeVerifier: codeVerifier,
                         code         : activationCode]

            response.success = { resp, result ->
                credantials.accessToken = result.access_token
            }
            response.failure = failureHandler
        }
        
        //obtain facebook user ID
        client.request(Method.GET, ContentType.JSON) {
            uri.path = "/debug_token"
            uri.query = [
                    input_token : credantials.accessToken,
                    access_token: "$clientId|$clientSecret"
            ]

            response.success = { resp, result ->
                credantials.userId = result.data.user_id
            }
            response.failure = failureHandler
        }
        
        //obtain facebook user ID email + profile picture
        client.request(Method.GET, ContentType.JSON) {
            uri.path = "/$credantials.userId"

            uri.query = [
                    fields: 'picture,email',
                    access_token: "$clientId|$clientSecret"
            ]
            response.success = { resp, result ->
                credantials.email = result.email
                credantials.profilePicture = result.picture.data.url

            }
            response.failure = failureHandler

        }

        return credantials
    }

    @Override
    List<ClientId> getClientIds() {
        return [new ClientId(ssOProvider: FACEBOOK, platform: WEB, clientId: clientId)]
    }
}

