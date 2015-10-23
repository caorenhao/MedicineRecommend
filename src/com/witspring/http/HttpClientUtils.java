package com.witspring.http;

import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.witspring.util.LoggerConfig;
import com.witspring.util.Pair;

/**
 * HttpClient连接池.
 *
 * @author renhao.cao.
 *         Created 2015年4月1日.
 */
public class HttpClientUtils {
	
	private static Log LOGGER = LoggerConfig.getLog(HttpClientUtils.class);
	
	/** 连接超时设置*/
    private static int connectTimeout = 20 * 1000;
    
    /** 连接超时设置*/
    private static int soTimeout = 20 * 1000;
	
    /** 连接失败重试次数*/
    //private static int retryNum = 3;
    
    private static CloseableHttpClient httpclient = null;
    
    static {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[] {new X509TrustManager(){
            		public X509Certificate[] getAcceptedIssuers() {
            			return null;
            		}
            		public void checkClientTrusted(
            				X509Certificate[] certs, String authType) {
            		}
            		public void checkServerTrusted(
            				X509Certificate[] certs, String authType) {
            		}
            }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = 
            		RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();
            
            PoolingHttpClientConnectionManager connManager = 
            		new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpclient = HttpClients.custom().setConnectionManager(connManager)
            		.build();
            
            // Create retryHandler configuration
//            HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
//            	@Override
//            	public boolean retryRequest(IOException exception, int executionCount, 
//                		HttpContext context) {
//                    if (executionCount >= retryNum) {
//                        // Do not retry if over max retry count
//                        return false;
//                    }
//                    if (exception instanceof InterruptedIOException) {
//                        // Timeout
//                        return false;
//                    }
//                    if (exception instanceof UnknownHostException) {
//                        // Unknown host
//                        return false;
//                    }
//                    if (exception instanceof ConnectTimeoutException) {
//                        // Connection refused
//                        return false;
//                    }
//                    if (exception instanceof SSLException) {
//                        // SSL handshake exception
//                        return false;
//                    }
//                    HttpClientContext clientContext = HttpClientContext.adapt(context);
//                    HttpRequest request = clientContext.getRequest();
//                    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
//                    if (idempotent) {
//                        // Retry if the request is considered idempotent
//                        return true;
//                    }
//                    return false;
//                }
//            };
            //httpclient = HttpClients.custom().setRetryHandler(myRetryHandler).build();
            
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true)
            		.build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(100);
        } catch (KeyManagementException e) {
        	LOGGER.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
        	LOGGER.error("NoSuchAlgorithmException", e);
        }
    }
    
    /**
     * 获取一个HttpClient连接.
     * 
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getHttpClient() {
		return httpclient;
	}
    
    /**
     * 构建一个HttpGet.
     * 
     * @param url 
     * @return HttpGet
     */
    public static HttpGet buildHttpGet(String url) {
    	HttpGet get = new HttpGet(url);
    	get.setConfig(buildRequestConfig());
    	
    	return get;
    }
    
    /**
     * 构建一个HttpPost.
     * 
     * @param url 
     * @return HttpPost
     */
    public static HttpPost buildHttpPost(String url) {
    	HttpPost post = new HttpPost(url);
    	post.setConfig(buildRequestConfig());
    	
    	return post;
    }
    
    /**
     * 构建HttpClient的超时设置.
     * 
     * @return RequestConfig
     */
    public static RequestConfig buildRequestConfig() {
    	RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(soTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout)
                .build();
    	
    	return requestConfig;
    }
    
    /**
     * 为HttpGet/HttpPost请求添加请求头.
     *
     * @param request
     * @param params
     */
    public static void addHeader(HttpUriRequest request, Map<String, String> params) {
    	for(Map.Entry<String, String> param : params.entrySet()) {
    		request.addHeader(param.getKey(), param.getValue());
    	}
    }
    
    /**
     * HttpGet设置代理服务器.
     * 
     * @param get 
     * @param pair
     */
    public static HttpGet setProxy(HttpGet get, Pair<String, Integer> pair) {
    	HttpHost proxy = new HttpHost(pair.first, pair.second);
    	RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
    	get.setConfig(config);
    	
    	return get;
    }
    
    /**
     * HttpPost设置代理服务器.
     * 
     * @param post 
     * @param pair 
     */
    public static void setProxy(HttpPost post, Pair<String, Integer> pair) {
    	HttpHost proxy = new HttpHost(pair.first, pair.second);
    	RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
    	post.setConfig(config);
    }
}