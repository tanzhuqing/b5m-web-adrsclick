package mogujie;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.b5m.base.common.utils.StringTools;

public class FileuploadTest {
	public static void main(String[] args) throws Exception{
		PostMethod method = null;
		// 锟斤拷锟斤拷锟斤拷锟襟方凤拷
		String url = "http://ucenter.stage.bang5mai.com/user/info/data/editAvatar.htm";
		HttpClient httpClient = new HttpClient();
		method = new PostMethod(url);
		method.setRequestHeader("Content-Type", "multipart/form-data; boundary=4092");
		method.setRequestHeader("lf-None-Match", "59e532f501ac13174dd9c488f897ee75");
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4092; i++) {
			sb.append("b");
		}
		method.setRequestBody(sb.toString());
		httpClient.getParams().setSoTimeout(5000);
		httpClient.getParams().setConnectionManagerTimeout(5000);
		// 执锟斤拷锟斤拷锟斤拷
		int statusCode = httpClient.executeMethod(method);
		System.out.println("statusCode :" + statusCode);
		String s = method.getResponseBodyAsString().trim();
		String s1 = "{\"code\":-1,\"ok\":false,\"data\":\"\u5934\u50cf\u4e0a\u4f20\u5f02\u5e38\"}";
		byte[] sbyte = "5934".getBytes("utf-8");
		System.out.println(sbyte.length);
		int i = Integer.valueOf("5f02", 16);
		System.out.println((char)i);
		System.out.println(s1);
//		\u5934\u50cf\u4e0a\u4f20\u5f02\u5e38
	//	String ss = new String(s.getBytes("utf-8"),"utf-8");
		System.out.println(new String(s.getBytes("utf-8"), "utf-8"));
	}
	
	@Test
	public void testEncode() throws Exception{
		File file = new File("/home/echo/test.txt");
		System.out.println(IOUtils.toString(new FileInputStream(file)));
		String s = IOUtils.toString(this.getClass().getResourceAsStream("test.txt"));
		String s1 = "\u5934\u50cf\u4e0a\u4f20\u5f02\u5e38";
		System.out.println(StringUtils.newStringUsAscii(s.getBytes()));
	}
}