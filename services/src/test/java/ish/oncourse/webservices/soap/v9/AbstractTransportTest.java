package ish.oncourse.webservices.soap.v9;

import ish.oncourse.webservices.soap.StubPopulator;
import ish.oncourse.webservices.soap.v4.ReferencePortType;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;
import ish.oncourse.webservices.v9.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v9.stubs.replication.TransactionGroup;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class AbstractTransportTest {
    static {
        System.getProperties().put("org.apache.cxf.stax.allowInsecureParser", "true");
    }

    public static final GZIPInInterceptor IN = new GZIPInInterceptor();
    public static final GZIPOutInterceptor OUT = new GZIPOutInterceptor();

    public static final long DEFAULT_TIMEOUT = 1000l * 60 * 5;
    public static final long PAYMENT_SERVICE_TIMEOUT = 1000l * 60 * 25;
    //protected static TestServer server;

    public static final String PACKAGE_NAME_REPLICATION_STUBS = "ish.oncourse.webservices.v9.stubs.replication";
    public static final String PACKAGE_NAME_REFERENCE_STUBS = "ish.oncourse.webservices.v4.stubs.reference";


    protected static TestServer startServer() throws Exception {
        /* please note, in actuality, for multiple tests you will have
         to ensure a single version of the server is running only.

		 For each test case, it will invoke start and will give an error.
		 This is simplified for Blog consumption here only. */
        TestServer server = new TestServer();
        server.start();
        return server;
    }

    protected abstract TestServer getServer();

    protected static void stopServer(TestServer server) throws Exception {
        server.stop();
    }

    public Client initPortType(BindingProvider bindingProvider, String url) throws JAXBException {
        bindingProvider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                String.format("%s%s", getServer().getServerUrl(), url));


        Client client = ClientProxy.getClient(bindingProvider);

        client.getInInterceptors().add(IN);
        client.getOutInterceptors().add(OUT);
        client.getOutFaultInterceptors().add(OUT);


        HTTPConduit conduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpClientPolicy.setConnectionTimeout(DEFAULT_TIMEOUT);
        httpClientPolicy.setReceiveTimeout(DEFAULT_TIMEOUT);

        conduit.setClient(httpClientPolicy);

        List<Header> headers = new ArrayList<>();
        headers.add(new Header(new QName("SecurityCode"), getSecurityCode(), new JAXBDataBinding(String.class)));
        headers.add(new Header(new QName("AngelVersion"), "angelVersion", new JAXBDataBinding(String.class)));
        headers.add(new Header(new QName("CommunicationKey"), getCommunicationKey(), new JAXBDataBinding(String.class)));
        bindingProvider.getRequestContext().put(Header.HEADER_LIST, headers);
        return client;
    }

    protected Long getCommunicationKey() {
        return Long.MAX_VALUE;
    }

    protected String getSecurityCode() {
        return "securityCode";
    }

    public static <T> boolean containsStubBeanName(String stubBeanName, List<T> stubs) {
        for (T stub : stubs) {
            if (stub.getClass().getSimpleName().equals(StringUtils.capitalize(stubBeanName))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> getStubClassNamesBy(String packageName, @SuppressWarnings("rawtypes") Class filterClass) {
        BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

        TypeFilter tf = new AssignableTypeFilter(filterClass);
        s.addIncludeFilter(tf);
        s.scan(packageName);

        String[] beanNames = bdr.getBeanDefinitionNames();

        ArrayList<String> result = new ArrayList<>();

        for (String beanName : beanNames) {

            String className = org.apache.commons.lang.StringUtils.capitalize(beanName);
            if (className.endsWith("Stub") && !className.equals("HollowStub")) {
                result.add(className);
            }
        }
        return result;
    }

    public static List<String> getReplicationStubBeanNames() {
        return getStubClassNamesBy(PACKAGE_NAME_REPLICATION_STUBS, ReplicationStub.class);
    }

    public static List<String> getReferenceStubBeanNames() {
        return getStubClassNamesBy(PACKAGE_NAME_REFERENCE_STUBS, ReferenceStub.class);
    }


    public static TransactionGroup createTransactionGroupWithAllStubs() throws Throwable {
        TransactionGroup transactionGroup = new TransactionGroup();
        List<String> stubClassNames = AbstractTransportTest.getReplicationStubBeanNames();

        ArrayList<ReplicationStub> stubs = new StubPopulator<ReplicationStub>(PACKAGE_NAME_REPLICATION_STUBS, stubClassNames, ReplicationStub.class).populate();
        transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(stubs);
        return transactionGroup;
    }

    public static void assertTransactionGroup(TransactionGroup transactionGroup) {
        List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
        assertListStubs(replicationStubs, PACKAGE_NAME_REPLICATION_STUBS, GenericReplicationStub.class);
    }

    public static <T> void assertListStubs(List<T> stubs, String packageName, Class<T> parentStubClass) {
        List<String> stubBeanNames = getStubClassNamesBy(packageName, parentStubClass);
        for (String stubBeanName : stubBeanNames) {
            assertTrue("Stub:" + stubBeanName, containsStubBeanName(stubBeanName, stubs));
        }
    }

    protected ReferencePortType getReferencePortType() {
        return getPortType("/services/v4/reference", new QName("http://ref.v4.soap.webservices.oncourse.ish/", "ReferenceService"), ReferencePortType.class);
    }

    protected PaymentPortType getPaymentPortType() {
        return getPortType("/services/v9/payment",  new QName("http://repl.v9.soap.webservices.oncourse.ish/", "ReplicationService"), PaymentPortType.class);
    }



    protected ReplicationPortType getReplicationPortType() {
        return getPortType("/services/v9/replication", new QName("http://repl.v9.soap.webservices.oncourse.ish/", "ReplicationService") , ReplicationPortType.class);
    }

    private <P> P getPortType(String endpointPath, QName serviceName, Class<P> portClass) {
        try {
            Service service = getService(endpointPath,serviceName);
            P portType = service.getPort(portClass);
            initPortType((BindingProvider) portType, endpointPath);
            return portType;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private Service getService(String endpointPath, QName serviceName) {
        try {
            createBus();
            // create service
            URL wsdlURL = new URL(String.format("%s%s", getServer().getServerUrl(), endpointPath) + "?wsdl");

            return Service.create(wsdlURL, serviceName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void createBus() {
        // create bus
        SpringBusFactory busFactory = new SpringBusFactory();
        URL cxfConfig = AbstractTransportTest.class.getClassLoader().getResource("ish/oncourse/webservices/soap/cxf-client.xml");
        Bus bus = busFactory.createBus(cxfConfig);
        BusFactory.setDefaultBus(bus);
    }
}
