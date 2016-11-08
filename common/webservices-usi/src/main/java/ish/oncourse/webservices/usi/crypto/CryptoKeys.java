package ish.oncourse.webservices.usi.crypto;

/**
 * Created by akoira on 11/8/16.
 */
public interface CryptoKeys {
    String getServicesSecurityKey();
    String getAuskeyCertificate();
    String getAuskeyPrivateKey();
    String getAuskeySalt();
    String getAuskeyPassword();
}
