/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author HHR
 * @version 0.0.1
 * @since       
 */
public class HttpClientUtil {
    private static PoolingHttpClientConnectionManager connMgr;  
    private static RequestConfig requestConfig;  
    private static final int MAX_TIMEOUT = 130000;  // 130秒
    
    protected final static Logger log = Logger.getLogger(HttpClientUtil.class);
    
    private static List<HttpPost> retryList = new LinkedList<HttpPost>();
  
    static {  
        // 设置连接池  
        connMgr = new PoolingHttpClientConnectionManager();  
        // 设置连接池大小  
        connMgr.setMaxTotal(500);  
        //每个连接能够使用的最大连接数
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());  
//        //关闭过期的连接
//        connMgr.closeExpiredConnections();
//        //五秒之后，关闭空闲连接
//        connMgr.closeIdleConnections(5, TimeUnit.SECONDS);
  
        RequestConfig.Builder configBuilder = RequestConfig.custom();  
        // 设置连接超时  
        configBuilder.setConnectTimeout(MAX_TIMEOUT);  
        // 设置读取超时  
        configBuilder.setSocketTimeout(MAX_TIMEOUT);  
        // 设置从连接池获取连接实例的超时  
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);  
        // 在提交请求之前 测试连接是否可用  
        configBuilder.setStaleConnectionCheckEnabled(true);  
        requestConfig = configBuilder.build(); 
    }  
    
    private static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

        public boolean retryRequest(IOException exception,int executionCount,HttpContext context) {
            if (executionCount < Integer.MAX_VALUE) {
                log.debug("正在重试连接。。。");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
            if (executionCount >= Integer.MAX_VALUE) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                // Connection refused
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }

    };
    
    /** 
     * 发送 GET 请求（HTTP），不带输入数据 
     * @param url 
     * @return 
     */  
    public static String doGet(String url) {  
        return doGet(url, new HashMap<String, Object>());  
    }  
  
    /** 
     * 发送 GET 请求（HTTP），K-V形式 
     * @param url 请求地址
     * @param params 参数map
     * @param headerParams 非必填   http请求头参数map 
     * @return 
     */  
    public static String doGet(String url, Map<String, Object> params, Map<String, Object> ...headerParams) {  
        String apiUrl = url;  
        StringBuffer param = new StringBuffer();  
        int i = 0;  
        for (String key : params.keySet()) {  
            if (i == 0)  
                param.append("?");  
            else  
                param.append("&");  
            param.append(key).append("=").append(params.get(key));  
            i++;  
        }  
        apiUrl += param;  
        String result = null;  
        HttpClient httpclient = new DefaultHttpClient();  
        try {  
            HttpGet httpGet = new HttpGet(apiUrl); 
            httpGet.setConfig(requestConfig);  
            for(int j=0;j<headerParams.length;j++){
                for (Map.Entry<String, Object> entry : headerParams[j].entrySet()) {  
                    httpGet.setHeader(entry.getKey(), entry.getValue().toString());   
                } 
            }
            HttpResponse response = httpclient.execute(httpGet);  
            int statusCode = response.getStatusLine().getStatusCode();  
  
            log.debug("执行状态码 : " + statusCode);  
  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                InputStream instream = entity.getContent();  
                result = IOUtils.toString(instream, "UTF-8");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();
            throw new RuntimeException(e);
        }  
        return result;  
    }  
  
    /** 
     * 发送 POST 请求（HTTP），不带输入数据 
     * @param apiUrl 
     * @return 
     */  
    public static String doPost(String apiUrl) {  
        return doPost(apiUrl, new HashMap<String, Object>());  
    }  
  
    /** 
     * 发送 POST 请求（HTTP），K-V形式 
     * @param apiUrl API接口URL 
     * @param params 参数map 
     * @param headerParams 非必填   http请求头参数map 
     * @return 
     */  
    public static String doPost(String apiUrl, Map<String, Object> params, Map<String, Object> ...headerParams) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(myRetryHandler).build(); //默认失败后重发3次（这里设置不断重复）
        
        String httpStr = null;  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            for(int i=0;i<headerParams.length;i++){
                for (Map.Entry<String, Object> entry : headerParams[i].entrySet()) {  
                    httpPost.setHeader(entry.getKey(), entry.getValue().toString());   
                } 
            }
            
            List<NameValuePair> pairList = new ArrayList<>(params.size());  
            for (Map.Entry<String, Object> entry : params.entrySet()) {  
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry  
                        .getValue().toString());  
                pairList.add(pair);  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));  
            response = httpClient.execute(httpPost);  
            log.debug("response = "+response.toString());  
            HttpEntity entity = response.getEntity();  
            httpStr = EntityUtils.toString(entity, "UTF-8");  
        } catch (IOException e) {  
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
    
    /** 
     * 发送 POST 请求（HTTP），K-V形式 
     * @param apiUrl API接口URL 
     * @param params 参数map 
     * @param needRetry 如果发送失败后是否需要定时重试  (定时器会每隔一小时重试一次)
     * @param headerParams 非必填   http请求头参数map 
     * @return 
     */  
    public static String doPost(String apiUrl, Map<String, Object> params, Boolean needRetry, Map<String, Object> ...headerParams) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(myRetryHandler).build(); //默认失败后重发3次（这里设置不断重复）
        
        String httpStr = null;  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
        
        try {  
            httpPost.setConfig(requestConfig);  
            for(int i=0;i<headerParams.length;i++){
                for (Map.Entry<String, Object> entry : headerParams[i].entrySet()) {  
                    httpPost.setHeader(entry.getKey(), entry.getValue().toString());   
                } 
            }
            
            List<NameValuePair> pairList = new ArrayList<>(params.size());  
            for (Map.Entry<String, Object> entry : params.entrySet()) {  
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry  
                        .getValue().toString());  
                pairList.add(pair);  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));  
            response = httpClient.execute(httpPost);
            log.debug("response = "+response.toString());  
            log.debug("响应码： " + response.getStatusLine().getStatusCode());
            if (200 != response.getStatusLine().getStatusCode()) { //如果发送失败
                if (needRetry) { //是否需要以后重试(定时器会每隔一小时重试一次)
                    retryList.add(httpPost); 
                }
            }
            HttpEntity entity = response.getEntity();  
            httpStr = EntityUtils.toString(entity, "UTF-8");  
        } catch (IOException e) {  
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
    
    /** 
     * 发送 POST 请求（HTTP）
     * @param httpPost  HttpPost对象
     * @param needRetry 如果发送失败后是否需要定时重试 (定时器会每隔一小时重试一次)
     * @return 
     */  
    public static CloseableHttpResponse doPost(HttpPost httpPost, Boolean needRetry) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(myRetryHandler).build(); //默认失败后重发3次（这里设置不断重复）
        
        CloseableHttpResponse response = null;  
        
        try {  
            response = httpClient.execute(httpPost);
            if (200 != response.getStatusLine().getStatusCode()) { //如果发送失败
                if (needRetry) {
                    retryList.add(httpPost);
                }
            }
        } catch (IOException e) {  
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return response;  
    }  
  
    /** 
     * 发送 POST 请求（HTTP），JSON形式 
     * @param apiUrl 
     * @param json json对象 
     * @return 
     */  
    public static String doPost(String apiUrl, String json) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        String httpStr = null;  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            StringEntity stringEntity = new StringEntity(json,"UTF-8");//解决中文乱码问题  
            stringEntity.setContentEncoding("UTF-8");  
            stringEntity.setContentType("application/json");  
            httpPost.setEntity(stringEntity);  
            response = httpClient.execute(httpPost);  
            HttpEntity entity = response.getEntity();  
            log.debug("响应码 = "+response.getStatusLine().getStatusCode());  
            httpStr = EntityUtils.toString(entity, "UTF-8");  
        } catch (IOException e) {  
            log.error(e);
            throw new RuntimeException(e);
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    log.error(e);  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 发送 SSL POST 请求（HTTPS），K-V形式 
     * @param apiUrl API接口URL 
     * @param params 参数map 
     * @return 
     */  
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {  
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
        String httpStr = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());  
            for (Map.Entry<String, Object> entry : params.entrySet()) {  
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry  
                        .getValue().toString());  
                pairList.add(pair);  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));  
            response = httpClient.execute(httpPost);  
            int statusCode = response.getStatusLine().getStatusCode();  
            if (statusCode != HttpStatus.SC_OK) {  
                return null;  
            }  
            HttpEntity entity = response.getEntity();  
            if (entity == null) {  
                return null;  
            }  
            httpStr = EntityUtils.toString(entity, "utf-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw new RuntimeException(e);
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 发送 SSL POST 请求（HTTPS），JSON形式 
     * @param apiUrl API接口URL 
     * @param json JSON对象 
     * @return 
     */  
    public static String doPostSSL(String apiUrl, Object json) {  
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
        String httpStr = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题  
            stringEntity.setContentEncoding("UTF-8");  
            stringEntity.setContentType("application/json");  
            httpPost.setEntity(stringEntity);  
            response = httpClient.execute(httpPost);  
            int statusCode = response.getStatusLine().getStatusCode();  
            if (statusCode != HttpStatus.SC_OK) {  
                return null;  
            }  
            HttpEntity entity = response.getEntity();  
            if (entity == null) {  
                return null;  
            }  
            httpStr = EntityUtils.toString(entity, "utf-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw new RuntimeException(e);
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 创建SSL安全连接 
     * 
     * @return 
     */  
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {  
        SSLConnectionSocketFactory sslsf = null;  
        try {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
  
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                    return true;  
                }  
            }).build();  
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {  
  
                @Override  
                public boolean verify(String arg0, SSLSession arg1) {  
                    return true;  
                }  
  
                @Override  
                public void verify(String host, SSLSocket ssl) throws IOException {  
                }  
  
                @Override  
                public void verify(String host, X509Certificate cert) throws SSLException {  
                }  
  
                @Override  
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {  
                }  
            });  
        } catch (GeneralSecurityException e) {  
            e.printStackTrace();  
            throw new RuntimeException(e);
        }  
        return sslsf;  
    }  
    
    public static List<HttpPost> getRetryList() {
        return retryList;
    }

    public static void setRetryList(List<HttpPost> retryList) {
        HttpClientUtil.retryList = retryList;
    }

    /** 
     * 发送 POST 请求（HTTP），K-V形式 
     * @param apiUrl API接口URL 
     * @param params 参数map 
     * @param headerParams 非必填   http请求头参数map 
     * @return 
     */
    public static String doPostMultipart(String apiUrl, Map<String, Object> params, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            if (null != headerParams) {
                for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            if (null != params) {
                for (String key : params.keySet()) {
                    Object value = params.get(key);
                    if (value instanceof ContentBody) {
                        entityBuilder.addPart(key, (ContentBody) value);
                    } else if (value instanceof File) {
                        entityBuilder.addBinaryBody(key, (File) value);
                    } else if (null != value) {
                        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, MIME.UTF8_CHARSET);
                        entityBuilder.addTextBody(key, value.toString(), contentType);
                    } else {
                        // do nothing
                    }
                }
            }
            httpPost.setEntity(entityBuilder.build());
            response = httpClient.execute(httpPost);
            log.debug("response = "+response.toString());
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
        return httpStr;
    }


    /** 
     * 测试方法 
     * @param args 
     */  
    public static void main(String[] args) throws Exception {  
  
    }  
}
