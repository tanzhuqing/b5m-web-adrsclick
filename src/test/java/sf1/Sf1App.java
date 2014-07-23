package sf1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.b5m.base.common.httpclient.HttpClientFactory;

public class Sf1App {
	
	@Test
	public void testTanx() throws IOException {
		HttpClient httpClient = HttpClientFactory.getHttpClient();
		String url = "http://10.10.96.188:8888/sf1r/laser/recommend";
//		String url = "http://10.10.96.188:8888/sf1r/documents/search";
//		String url = "http://10.10.99.177:8888/sf1r/documents/search";
//		String url = "http://10.10.99.177:8888/sf1r/documents/search";
		String json = IOUtils.toString(this.getClass().getResourceAsStream("tanxRequest.json"));
		PostMethod method = createPostMethod(url, json);
		int statusCode = httpClient.executeMethod(method);
		System.out.println("statusCode : " + statusCode);
		String resultMsg = method.getResponseBodyAsString().trim();
		System.out.println(resultMsg);
//		category(resultMsg);
		// System.out.println(resultMsg);2b15be1b8583ad61a9c4715e6abda405
//		FileUtils.write(new java.io.File("/home/echo/jsonpsearchdata.txt"), resultMsg, "UTF-8");
		System.out.println("查询完成.......");
	}
	
	@Test
	public void testSf1() throws IOException {
		HttpClient httpClient = HttpClientFactory.getHttpClient();
		String url = "http://10.10.99.188:8888/sf1r/documents/search";
		String json = IOUtils.toString(this.getClass().getResourceAsStream("jsonpsearch.json"));
		PostMethod method = createPostMethod(url, json);
		int statusCode = httpClient.executeMethod(method);
		System.out.println("statusCode : " + statusCode);
		String resultMsg = method.getResponseBodyAsString().trim();
		System.out.println(resultMsg);
//		category(resultMsg);
		// System.out.println(resultMsg);2b15be1b8583ad61a9c4715e6abda405
//		FileUtils.write(new java.io.File("/home/echo/jsonpsearchdata.txt"), resultMsg, "UTF-8");
		System.out.println("查询完成.......");
	}
	
	private static PostMethod createPostMethod(String URL, String content) throws UnsupportedEncodingException {
		PostMethod method = new PostMethod(URL);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
		method.setRequestHeader("Connection", "Keep-Alive");
		return method;
	}
}
