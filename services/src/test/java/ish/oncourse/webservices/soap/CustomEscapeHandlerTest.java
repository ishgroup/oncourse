package ish.oncourse.webservices.soap;

import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CustomEscapeHandlerTest {

    /**
     * JAXB XmlElement fields:
     * Leave TAB LF CR as is, remove other invalid xml chars, escape xml-value
     * @throws IOException
     */
    @Test
    public void escapeTagValueTest() throws IOException {

        StringBuilder strb = new StringBuilder();
        strb.append("<replication attr=\"abcdef");
        strb.append(getControlChars());
        strb.append("\">value");
        strb.append(getControlChars());
        strb.append("</replication");
        strb.append(getControlChars());
        strb.append('>');

        try (Writer w = new CharArrayWriter()) {
            CustomEscapeHandler handler = new CustomEscapeHandler();
            handler.escape(strb.toString().toCharArray(), 0, strb.toString().length(), false, w);
            assertEquals("&lt;replication attr=\"abcdef\t\n" +
                    "\"&gt;value\t\n" +
                    "&lt;/replication\t\n" +
                    "&gt;", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    /**
     * JAXB XmlAttribute fields;
     * Escape TAB LF CR, remove other invalid xml chars, escape xml-value
     * @throws IOException
     */
    @Test
    public void escapeTagAttributeTest() throws IOException {

        StringBuilder strb = new StringBuilder();
        strb.append("<replication attr=\"abcdef");
        strb.append(getControlChars());
        strb.append("\">value");
        strb.append(getControlChars());
        strb.append("</replication");
        strb.append(getControlChars());
        strb.append('>');

        try (Writer w = new CharArrayWriter()) {
            CustomEscapeHandler handler = new CustomEscapeHandler();
            handler.escape(strb.toString().toCharArray(), 0, strb.toString().length(), true, w);
            assertEquals("&lt;replication attr=&quot;abcdef&#9;&#10;&#13;&quot;&gt;value&#9;&#10;&#13;&lt;/replication&#9;&#10;&#13;&gt;", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    /**
     * Marshalling/unmarshalling with custom escape handler on unicode range 0x - 0xFFFF
     *
     * @throws Exception
     */
    @Test
    public void emulateUnmarshalling() throws Exception {
        String testData = generateUTFData();

        JAXBContext context = JAXBContextImpl.newInstance(JAXBTestBean.class);
        Marshaller m = context.createMarshaller();

        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty("com.sun.xml.bind.characterEscapeHandler", new CustomEscapeHandler());

        JAXBTestBean srcBean = new JAXBTestBean();
        srcBean.setValue(testData);
        srcBean.setAttribute(testData);

        Writer w = new StringWriter();
        m.marshal(srcBean, w);

        Unmarshaller unm = context.createUnmarshaller();
        JAXBTestBean destBean = (JAXBTestBean) unm.unmarshal(new StringReader(w.toString()));
    }

    /**
     * Test data: symbols in range 0x0 - 0xFFFF
     *
     * @return
     */
    private String generateUTFData() {
        StringBuffer buf = new StringBuffer();
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            buf.append(c);
        }
        return buf.toString();
    }

    /**
     * Characters that breaks replication
     *
     * @return
     */
    private char[] getControlChars() {
        StringBuilder res = new StringBuilder();
        for (char i = 0; i < 0x20; i++) {
            res.append(i);
        }
        return res.toString().toCharArray();
    }
}
