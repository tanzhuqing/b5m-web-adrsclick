package com.b5m.adrs.web.base;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.common.Constants;
import com.b5m.base.common.Lang;

/**
 * 控制器的基类
 */
public abstract class AbstractBaseController {
	private ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
	private ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();
	private ThreadLocal<PrintWriter> out = new ThreadLocal<PrintWriter>();

	public AbstractBaseController() {
		super();
	}

	public final void _setServlet(HttpServletRequest request, HttpServletResponse response) {
		this.request.set(request);
		this.response.set(response);
		response.setCharacterEncoding(Constants.DEFAULT_ENCODING);
		response.setContentType(Constants.HTML_CONTENT_TYPE);
	}

	protected final PrintWriter getOut() {
		if (this.out.get() == null) {
			try {
				this.out.set(this.response.get().getWriter());
			} catch (IOException ioe) {
			}
		}
		return this.out.get();
	}

	protected final HttpServletRequest getRequest() {
		return this.request.get();
	}

	protected final HttpServletResponse getResponse() {
		return this.response.get();
	}
	
	protected final void output(HttpServletResponse response, HttpServletRequest request, Object val) throws Exception {
		output(response, request, 0, "success", val);
	}
	
	protected final void outputFailed(HttpServletResponse response, HttpServletRequest request, Object val) throws Exception {
		output(response, request, -1, "failed", val);
	}
	
	protected final void output(int code, String msg, Object val) throws Exception {
		HttpServletResponse response = getResponse();
		HttpServletRequest request = getRequest();
		setExpires(response, null);
		response.setCharacterEncoding("UTF-8");
		output(response, request, code, msg, val);
		
	}
	
	
	protected final void output(HttpServletResponse response, HttpServletRequest request, int code, String msg, Object val) throws Exception {
		setExpires(response, null);
		response.setCharacterEncoding("UTF-8");
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("code", code);
		json.put("msg", msg);
		json.put("val", val);
		String jsonstr = json.toJSONString();
		String jsonCallback = request.getParameter("jsonCallback");
		if(Lang.isEmpty(jsonCallback)){
			jsonCallback = request.getParameter("jsoncallback");
		}
		if (!StringUtils.isEmpty(jsonCallback)) {
			response.setContentType("application/x-javascript");
			jsonstr = jsonCallback + "(" + jsonstr + ")";
		} else {
			response.setContentType("application/json");
		}
		String unicode = request.getParameter("unicode");
		byte[] bs = null;
		if (!StringUtils.isEmpty(unicode)) {
			bs = chinaToUnicode(jsonstr).getBytes();
		} else {
			bs = jsonstr.getBytes("UTF-8");
		}
		response.setContentLength(bs.length);
		response.getOutputStream().write(bs);
		response.getOutputStream().close();
	}
	
	protected final void output(HttpServletResponse response, HttpServletRequest request, int code, String msg, Object val, boolean isAjax) throws Exception {
		if (isAjax) {
			setExpires(response, null);
			response.setCharacterEncoding("UTF-8");
			com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
			json.put("code", code);
			json.put("msg", msg);
			json.put("val", val);
			String jsonstr = json.toString();
			response.setContentType("application/json");
			byte[] bs = jsonstr.getBytes("UTF-8");
			response.setContentLength(bs.length);
			response.getOutputStream().write(bs);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} else {
			PrintWriter out = response.getWriter();
			out.write("<script>");
			out.write("alert('" + msg + "');");
			out.write("window.location.href='" + request.getHeader("referer")+"';");
			out.write("</script>");
			out.flush();
			out.close();
		}
	}

	private String chinaToUnicode(String str) {
		StringBuilder result = new StringBuilder(str.length() * 2);
		int length = str.length();
		for (int i = 0; i < length; i++) {
			int chr1 = str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result.append("\\u").append(Integer.toHexString(chr1));
			} else {
				result.append(str.charAt(i));
			}
		}
		return result.toString();
	}

	private void setExpires(HttpServletResponse response, Integer expiresTime) {
		if (expiresTime != null && expiresTime > 0) {
			long now = System.currentTimeMillis();
			response.setHeader("Cache-Control", "max-age=" + expiresTime);
			response.setHeader("Cache-Control", "must-revalidate");
			response.setDateHeader("Last-Modified", now);
			response.setDateHeader("Expires", now + expiresTime * 1000);
		} else {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
		}
	}
}
