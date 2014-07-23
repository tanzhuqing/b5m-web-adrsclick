<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%-- <meta http-equiv="Refresh" content="1; url=${_durl}"/> --%>
<title>跳转</title>
</head>
<body>
	<%-- <c:if test="${isDirectOut}"><img alt="" src='${chargeIntimeUrl}?doc_id=["${dd}"]&sign=${sign}&ip=${ip}'></c:if> --%>
	<%-- <img alt="" id="img" src="${cpcRecordUrl}?kid=${kid}&key=${key}&source=${source}&dr=${dr}&uid=${uid}&cid=${cid}&aid=${aid}&da=${da}&ad=103&dstl=${durl}&lt=8800&dd=${dd}&dl=${curl}&t=${t}&rp=${rp}&isbanxclick=true"> --%>
	<a href="${_durl}" id="durlclick"></a>
	<script type="text/javascript">
		document.getElementById("durlclick").click();
	</script>
    <script type="text/javascript">
    _atrk_opts = { atrk_acct:"InfVh1aUXR00ax", domain:"b5m.com",dynamic: true};
    (function() { var as = document.createElement('script'); as.type = 'text/javascript'; as.async = true; as.src = "https://d31qbv1cthcecs.cloudfront.net/atrk.js?t=${today}"; 
    var s = document.getElementsByTagName('script')[0];s.parentNode.insertBefore(as, s); })();
    </script>
<noscript><img src="https://d5nxst8fruw4z.cloudfront.net/atrk.gif?account=InfVh1aUXR00ax" style="display:none" height="1" width="1" alt="" /></noscript>
</body>
</html>