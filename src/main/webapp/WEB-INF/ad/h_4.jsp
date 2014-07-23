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
	<link rel="stylesheet" href="http://staticcdn.b5m.com/static/css/search/gg-recommend-taobao.css"/>
	<style type="text/css">
	.goods-recommend .goods-list li{ width: 24%; float: left; _display: inline;}
	</style>
</head>
<body>
	<c:if test="${fn:length(resList) >= 1}">
		<div class="goods-recommend">
			<h3>商品推荐</h3>
			<ul class="goods-list clear-fix">
				<c:forEach items="${resList}" var="res" varStatus="stats">
					<li>
						<div class="item-box">
							<p class="pic"><a href="${res.Url}" title="${res.Title}" class="s250" target="_blank"><img src="${res.Picture}" alt="${res.Title}"  onerror="this.src='/images/search_default.png'"></a></p>
							<p class="tit"><a href="${res.Url}" title="${res.Title}" target="_blank">${res.Title}</a></p>
							<img alt="" src="${cpcRecordUrl}?uid=${uid}&cid=${cid}&ip=${ip}&aid=${res.aid}&da=H${stats.index+1}&ad=108&dstl=${res.durl}&dl=${refere}&lt=8800&dd=${res.DOCID}&t=${t}&rp=1001" style="display: none;">
							<strong><b>¥</b>${res.Price}</strong>
						</div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
	<script type="text/javascript" src="http://staticcdn.b5m.com/static/scripts/common/jquery-1.9.1.min.js?t=${today}"></script>
	<script type="text/javascript">
		$(function(){
			var mediaQuery = function(){
			    var win_w = "${width}";
			    if(win_w > 1600){
			        $(".goods-list li").children(".item-box").width("262px");
			        $(".goods-list li").find("p.pic").children('a').attr("class","s250");
			    }else{
			    	if(win_w > 1420){
			    		$(".goods-list li").children(".item-box").width("242px");
			        	$(".goods-list li").find("p.pic").children('a').attr("class","s230");
			        }else{
			       	 	if(win_w > 1260){
			       	 		$(".goods-list li").children(".item-box").width("222px");
			        		$(".goods-list li").find("p.pic").children('a').attr("class","s210");
			        	}else{
			        		if(win_w > 1150){
			        			$(".goods-list li").children(".item-box").width("202px");
			        			$(".goods-list li").find("p.pic").children('a').attr("class","s190");
			        		}else{
			        			$(".goods-list li").children(".item-box").width("182px");
			        			$(".goods-list li").find("p.pic").children('a').attr("class","s170");
			        		}
			        	}
			        }
			    }
			}
			mediaQuery();
		})
	</script>
</body>
</html>