package ish.oncourse.webservices.usi.tapestry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class СredentialStoreReader {

    private String credentialPath = null;
    private String passwordPath = null;

    private String getId = null;
    private String publicCertificate = null;
    private String privateKey = null;
    private String salt = null;
    private String password = null;

    private СredentialStoreReader() {}

    public static СredentialStoreReader valueOf(String credentialStorePath, String passwordPath) {
        СredentialStoreReader obj = new СredentialStoreReader();
        obj.credentialPath = credentialStorePath;
        obj.passwordPath = passwordPath;
        return obj;
    }

    public void read() throws ParserConfigurationException, IOException, SAXException, FileNotFoundException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(credentialPath));
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();

        salt = root.getElementsByTagName("credential").item(0).getAttributes().getNamedItem("credentialSalt").getTextContent();
        getId = root.getElementsByTagName("credential").item(0).getAttributes().getNamedItem("id").getTextContent();
        publicCertificate = root.getElementsByTagName("publicCertificate").item(0).getTextContent().trim();
        privateKey = root.getElementsByTagName("protectedPrivateKey").item(0).getTextContent().trim();

        BufferedReader reader = new BufferedReader(new FileReader(passwordPath));
        password = reader.readLine().trim();
        reader.close();
    }

    public String getId() {
        return getId;
    }

    public String getSalt() {
        return salt;
    }

    public String getPublicCertificate() {
        return publicCertificate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPassword() {
        return password;
    }
}
