package ish.oncourse.api.jetty

import com.fasterxml.jackson.annotation.JsonTypeName
import io.bootique.annotation.BQConfig
import io.bootique.jetty.connector.ConnectorFactory
import org.eclipse.jetty.server.ConnectionFactory
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory

@BQConfig
@JsonTypeName("willow")
class HttpConnectorFactory extends ConnectorFactory {

    protected ConnectionFactory[] buildHttpConnectionFactories(HttpConfiguration httpConfig) {
        return [new HttpConnectionFactory(httpConfig)]
    }

    protected HttpConfiguration buildHttpConfiguration() {
        HttpConfiguration httpConfig = super.buildHttpConfiguration()
        httpConfig.sendServerVersion = false
        return httpConfig
    }
}