<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<title>广告推荐位</title>
	<meta name="keywords" content="关键字" />
	<meta name="description" content="页面描述" />
	<link rel="stylesheet" href="http://staticcdn.b5m.com/static/css/search/gg-recommend.css"/>
</head>
<body>
	<c:if test="${fn:length(resList) >= 1}">
		<div class="hw-goods-recommend">
			<h3>海外商品推荐</h3>
			<ul class="goods-list clear-fix">
				<c:forEach items="${resList}" var="res">
					<c:set value="http://haiwai.b5m.com/item/__${res.DOCID}.html" var="url"></c:set>
					<li>
						<p class="pic">
							<a href="${url}" title="${res.Title}" target="_blank"><img src="${res.Picture}" alt="${res.Title}"  onerror="this.src='/images/search_default.png'"></a>
						</p>
						<p class="tit">
							<a href="${url}" title="${res.Title}" target="_blank">
								${res.Title}
							</a>
						</p>
						<strong><b>¥</b>${res.Price}</strong>
						<p class="btns">
							<a href="${url}" title="点击购买" target="_blank">点击购买</a>
						</p>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
</body>
</html>