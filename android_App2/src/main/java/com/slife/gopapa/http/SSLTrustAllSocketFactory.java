package com.slife.gopapa.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;


/**
 * @ClassName: SSLTrustAllSocketFactory
 * @Description: 自定义SSLTrustAllSocketFactory，用与跳过验证
 * @author 菲尔普斯
 * @date 2015-1-13 下午4:52:18
 * 
 */
public class SSLTrustAllSocketFactory extends SSLSocketFactory {

	private SSLContext mCtx;

	public class SSLTrustAllManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

	public SSLTrustAllSocketFactory(KeyStore truststore) throws Throwable {
		super(truststore);
		try {
			mCtx = SSLContext.getInstance("TLS");
			mCtx.init(null, new TrustManager[] { new SSLTrustAllManager() },
					null);
			setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception ex) {
		}
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return mCtx.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return mCtx.getSocketFactory().createSocket();
	}

	public static SSLSocketFactory getSocketFactory() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory factory = new SSLTrustAllSocketFactory(trustStore);
			return factory;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}