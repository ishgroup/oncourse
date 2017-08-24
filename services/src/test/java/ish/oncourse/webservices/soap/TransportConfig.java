package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.util.GenericReferenceStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
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
			ReplicationPortType, PaymentPortType> server(TestServer server) {
		this.server = server;
		return this;
	}

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> transactionGroupClass(Class transactionGroupClass) {
		this.transactionGroupClass = transactionGroupClass;
		return this;
	}


	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> replicationStubClass(Class<ReplicationStub> replicationStubClass) {
		this.replicationStubClass = replicationStubClass;
		return this;
	}


	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> referenceStubClass(Class<ReferenceStub> referenceStubClass) {
		this.referenceStubClass = referenceStubClass;
		return this;
	}


	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> packageNameReplicationStubs(String packageNameReplicationStubs) {
		this.packageNameReplicationStubs = packageNameReplicationStubs;
		return this;
	}

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> packageNameReferenceStubs(String packageNameReferenceStubs) {
		this.packageNameReferenceStubs = packageNameReferenceStubs;
		return this;
	}

	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> referencePortType(ReferencePortType referencePortType) {
		this.referencePortType = referencePortType;
		return this;
	}


	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> paymentPortType(PaymentPortType paymentPortType) {
		this.paymentPortType = paymentPortType;
		return this;
	}


	public TransportConfig<TransactionGroup,
			ReplicationStub,
			ReferenceStub,
			ReferencePortType,
			ReplicationPortType, PaymentPortType> replicationPortType(ReplicationPortType replicationPortType) {
		this.replicationPortType = replicationPortType;
		return this;
	}
}



