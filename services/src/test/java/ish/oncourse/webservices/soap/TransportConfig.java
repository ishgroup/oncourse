package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.util.GenericReferenceStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.SupportedVersions;
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
import org.springframework.core.GenericTypeResolver;
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

public class TransportConfig<TransactionGroup extends GenericTransactionGroup,
		ReplicationStub extends GenericReplicationStub,
		ReferenceStub extends GenericReferenceStub,
		ReferencePortType,
		ReplicationPortType,
		PaymentPortType> {
	public static final GZIPInInterceptor IN = new GZIPInInterceptor();
	public static final GZIPOutInterceptor OUT = new GZIPOutInterceptor();

	public static final long DEFAULT_TIMEOUT = 1000l * 60 * 5;
	public static final long PAYMENT_SERVICE_TIMEOUT = 1000l * 60 * 25;


	public static final String REPLICATION_SERVICE_NAME = "ReplicationService";
	public static final String REFERENCE_SERVICE_NAME = "ReferenceService";

	public static final String PACKAGE_NAME_REPLICATION_STUBS = "ish.oncourse.webservices.%s.stubs.replication";
	public static final String PACKAGE_NAME_REFERENCE_STUBS = "ish.oncourse.webservices.%s.stubs.reference";

	public static final String TRANSACTION_GROUP_CLASS_NAME = PACKAGE_NAME_REPLICATION_STUBS + ".TransactionGroup";
	public static final String REPLICATION_STUB_CLASS_NAME = PACKAGE_NAME_REPLICATION_STUBS + ".ReplicationStub";
	public static final String REFERENCE_STUB_CLASS_NAME = PACKAGE_NAME_REFERENCE_STUBS + ".ReferenceStub";

	public static final String SOAP_PACKAGE = "ish.oncourse.webservices.soap.%s";
	
	public static final String REFERENCE_PORT_CLASS_NAME  = SOAP_PACKAGE + ".ReferencePortType";
	public static final String PAYMENT_PORT_CLASS_NAME  = SOAP_PACKAGE + ".PaymentPortType";
	public static final String REPLICATION_PORT_CLASS_NAME = SOAP_PACKAGE + ".ReplicationPortType";

	public static final String REPLICATION_END_POINT = "/services/%s/replication";
	public static final String PAYMENT_END_POINT = "/services/%s/payment";
	public static final String REFERENCE_END_POINT = "/services/%s/reference";

	public static final String REFERENCE_NAMESPACE_URI = "http://ref.%s.soap.webservices.oncourse.ish/";
	public static final String REPLICATION_NAMESPACE_URI = "http://ref.%s.soap.webservices.oncourse.ish/";

	private SupportedVersions replicationVersion;
	private SupportedVersions referenceVersion;

	static {
		System.getProperties().put("org.apache.cxf.stax.allowInsecureParser", "true");
	}

	private TestServer server;

	private Class<TransactionGroup> transactionGroupClass;
	private Class<ReplicationStub> replicationStubClass;
	private Class<ReferenceStub> referenceStubClass;

	private String packageNameReplicationStubs;
	private String packageNameReferenceStubs;

	private ReferencePortType referencePortType;
	private PaymentPortType paymentPortType;
	private ReplicationPortType replicationPortType;

	Class getReplicationStubClass() {
		return replicationStubClass;
	}


	public List<String> getReplicationStubBeanNames() {
		return getStubClassNamesBy(packageNameReplicationStubs, replicationStubClass);
	}

	public List<String> getReferenceStubBeanNames() {
		return getStubClassNamesBy(packageNameReferenceStubs, referenceStubClass);
	}

	public TransactionGroup createTransactionGroupWithAllStubs() throws Throwable {
		TransactionGroup transactionGroup = transactionGroupClass.newInstance();
		List<String> stubClassNames = getReplicationStubBeanNames();

		ArrayList<ReplicationStub> stubs = new StubPopulator<>(packageNameReplicationStubs, stubClassNames, replicationStubClass).populate();
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(stubs);
		return transactionGroup;
	}

	public void assertTransactionGroup(TransactionGroup transactionGroup) {
		List<GenericReplicationStub> replicationStubs = transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		assertListStubs(replicationStubs, packageNameReplicationStubs, GenericReplicationStub.class);
	}

	public <T> void assertListStubs(List<T> stubs, String packageName, Class<T> parentStubClass) {
		List<String> stubBeanNames = getStubClassNamesBy(packageName, parentStubClass);
		for (String stubBeanName : stubBeanNames) {
			assertTrue("Stub:" + stubBeanName, containsStubBeanName(stubBeanName, stubs));
		}
	}


	public Client initPortType(BindingProvider bindingProvider, String url) throws JAXBException {
		bindingProvider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				String.format("%s%s", server.getServerUrl(), url));


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

	public <T> boolean containsStubBeanName(String stubBeanName, List<T> stubs) {
		for (T stub : stubs) {
			if (stub.getClass().getSimpleName().equals(StringUtils.capitalize(stubBeanName))) {
				return true;
			}
		}
		return false;
	}

	private List<String> getStubClassNamesBy(String packageName, @SuppressWarnings("rawtypes") Class filterClass) {
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


	protected PaymentPortType getPaymentPortType() {
		return paymentPortType;
	}


	protected ReplicationPortType getReplicationPortType() {
		return replicationPortType;
	}

	public <P> P getPortType(String endpointPath, QName serviceName, Class<P> portClass) {
		try {
			Service service = getService(endpointPath, serviceName);
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
			URL wsdlURL = new URL(String.format("%s%s", server.getServerUrl(), endpointPath) + "?wsdl");

			return Service.create(wsdlURL, serviceName);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void createBus() {
		// create bus
		SpringBusFactory busFactory = new SpringBusFactory();
		URL cxfConfig = TransportConfig.class.getClassLoader().getResource("ish/oncourse/webservices/soap/cxf-client.xml");
		Bus bus = busFactory.createBus(cxfConfig);
		BusFactory.setDefaultBus(bus);
	}

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> init() {
		try {
			String replicationVersionString = replicationVersion.name().toLowerCase();
			String referenceVersionString = referenceVersion.name().toLowerCase();
			packageNameReplicationStubs = String.format(PACKAGE_NAME_REPLICATION_STUBS, replicationVersionString);
			packageNameReferenceStubs = String.format(PACKAGE_NAME_REFERENCE_STUBS, referenceVersion);
			transactionGroupClass = (Class<TransactionGroup>) getClass().getClassLoader().loadClass(String.format(TRANSACTION_GROUP_CLASS_NAME, replicationVersionString));
			replicationStubClass = (Class<ReplicationStub>) getClass().getClassLoader().loadClass(String.format(REPLICATION_STUB_CLASS_NAME, replicationVersionString));
			referenceStubClass = (Class<ReferenceStub>) getClass().getClassLoader().loadClass(String.format(REFERENCE_STUB_CLASS_NAME, referenceVersionString));

			Class<ReferencePortType> referencePortTypeClass =  (Class<ReferencePortType>) getClass().getClassLoader().loadClass(String.format(REFERENCE_PORT_CLASS_NAME, referenceVersionString));
			referencePortType = getPortType(String.format(REFERENCE_END_POINT, referenceVersion), new QName(String.format(REFERENCE_NAMESPACE_URI, referenceVersion), REFERENCE_SERVICE_NAME), referencePortTypeClass);

			Class<PaymentPortType> paymentPortTypeClass = (Class<PaymentPortType>) getClass().getClassLoader().loadClass(String.format(PAYMENT_PORT_CLASS_NAME, replicationVersionString));
			paymentPortType = getPortType(String.format(PAYMENT_END_POINT, replicationVersion), new QName(String.format(REPLICATION_NAMESPACE_URI, replicationVersion), REPLICATION_SERVICE_NAME), paymentPortTypeClass);
			
			Class<ReplicationPortType> replicationPortTypeClass = (Class<ReplicationPortType>) getClass().getClassLoader().loadClass(String.format(REPLICATION_PORT_CLASS_NAME, replicationVersionString));
			replicationPortType = getPortType(String.format(REPLICATION_END_POINT, replicationVersion), new QName(String.format(REPLICATION_NAMESPACE_URI, replicationVersion), REPLICATION_SERVICE_NAME), replicationPortTypeClass);
	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> replicationVersion(SupportedVersions v) {
		replicationVersion = v;
		return this;
	}

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> referenceVersion(SupportedVersions v) {
		referenceVersion = v;
		return this;
	}

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> server(TestServer server) {
		this.server = server;
		return this;
	}
}



