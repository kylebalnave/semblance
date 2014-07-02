/*
 * Copyright (C) 2014 balnave
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package semblance.io;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Loads a URL and returns it's content
 *
 * @author balnave
 */
public class URLReader extends AbstractReader implements IReader {

    private int status;
    private String message;
    private String source;

    public URLReader(String uri) {
        super(uri);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    private static Proxy proxy;

    public static void setProxyDetails(String ip, int port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    }

    /**
     * Gets a valid URLConnection
     *
     * @param uri
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static URLConnection getConnection(String uri) throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException, IOException {
        if (uri.startsWith("https://")) {
            setHTTPSCertificateValidation();
        }
        if (proxy != null) {
            return new URL(uri).openConnection(proxy);
        }
        return new URL(uri).openConnection();
    }

    /**
     * Allows all HTTPS Certificates
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static void setHTTPSCertificateValidation() throws NoSuchAlgorithmException, KeyManagementException {
        /*
         *  fix for
         *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
         *       sun.security.validator.ValidatorException:
         *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
         *               unable to find valid certification path to requested target
         */
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }

        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    @Override
    public String load() {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) getConnection(uri);
            return load(connection);
        } catch (IOException ioex) {
            this.status = 404;
            this.message = ioex.getMessage();
            this.source = "";
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return this.source;
    }
    
    /**
     * 
     * @param connection
     * @return 
     */
    public String load(HttpURLConnection connection) {
        try {
            connection.setConnectTimeout(1000 * 30);
            connection.setReadTimeout(1000 * 60);
            this.status = connection.getResponseCode();
            this.message = connection.getResponseMessage();
            if (connection.getContentType().contains("text")) {
                this.source = readInputStream(connection.getInputStream());
            } else if (connection.getContentType().contains("xml")) {
                this.source = stripNonValidXMLCharacters(readInputStream(connection.getInputStream()));
            } else if (connection.getContentType().contains("json")) {
                this.source = readInputStream(connection.getInputStream());
            }
        } catch (IOException ioex) {
            this.status = 404;
            this.message = ioex.getMessage();
            this.source = "";
        }
        if (connection != null) {
            connection.disconnect();
        }
        return this.source;
    }

    /**
     * This method ensures that the output String has only valid XML unicode
     * characters as specified by the XML 1.0 standard. For reference, please
     * see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty String if the input is
     * null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public String stripNonValidXMLCharacters(String in) {
        StringBuilder out = new StringBuilder(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) {
            return ""; // vacancy test.
        }
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9)
                    || (current == 0xA)
                    || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            }
        }
        return out.toString();
    }

}
