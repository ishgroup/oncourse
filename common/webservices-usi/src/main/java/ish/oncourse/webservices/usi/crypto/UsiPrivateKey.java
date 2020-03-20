package ish.oncourse.webservices.usi.crypto;

import org.opensaml.ws.wssecurity.Password;

import java.security.PrivateKey;

public class UsiPrivateKey implements PrivateKey {
    @Override
    public String getAlgorithm() {
        return null;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return new byte[0];
    }
}
