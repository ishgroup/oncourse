package ish.oncourse.webservices.usi;

import ish.oncourse.webservices.usi.crypto.CryptoKeys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by akoira on 11/8/16.
 */
@Configuration
public class TestConfig {

    public static final CryptoKeys qa10871 = new CryptoKeys() {
        @Override
        public String getServicesSecurityKey() {
            return "";
        }

        @Override
        public String getCertificate() {
            return "";
        }

        @Override
        public String getPrivateKey() {
            return "";
        }

        @Override
        public String getSalt() {
            return "";
        }

        @Override
        public String getPassword() {
            return "";
        }

//        @Override
//        public String getAuskeyCertificate() {
//            return "";
//        }
//
//        @Override
//        public String getAuskeyPrivateKey() {
//            return "";
//        }
//
//        @Override
//        public String getAuskeySalt() {
//            return "";
//        }
//
//        @Override
//        public String getAuskeyPassword() {
//            return "";
//        }
    };

    @Bean
    public CryptoKeys  cryptoKeys() {
        return qa10871;
    }
}
