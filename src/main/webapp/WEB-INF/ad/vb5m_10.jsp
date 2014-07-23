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
	<link rel="stylesheet" href="http://staticcdn.b5m.com/static/css/search/gg_recommend_v3.css"/>
</head>
<body>
	<c:if test="${fn:length(resList) >= 1}">
		<div class="goods-recommend">
			<h3>商品推荐</h3>
			<ul class="grid-view cf">
				<c:forEach items="${resList}" var="res" varStatus="stats">
					<li class="grid-ls">
						<div class="grid-mod">
							<div class="grid-in">
								<c:set value="${fn:indexOf(res.Picture, 'img.b5m.com')}" var="pictureIndex" />
								<c:set value="${res.Picture}" var="picturePath"/>
						        <c:if test="${pictureIndex > 0}">
						           <c:set value="${res.Picture}/228X228" var="picturePath"/>
						        </c:if>
								<div class="pic-wrap">
									<a class="pic" href="${res.Url}" target="_blank"><img src="${picturePath}" alt=""></a>
								</div>
								<div class="summary"><a href="${res.Url}" target="_blank">${res.Title}</a></div>
								<img alt="" src="${cpcRecordUrl}?uid=${uid}&cid=${cid}&aid=${res.aid}&da=V${stats.index+1}&ad=108&dl=${refere}&dstl=${res.durl}&lt=8800&dd=${res.DOCID}&t=${t}&rp=1002" style="display: none;">
								<div class="price">
									<strong><b>¥</b>${res.Price}</strong>
								</div>
							</div>
						</div>
					</li>
				</c:forEach>
			</ul>
		</div>
		<iframe id="iFrame_agent" name="iFrame_agent" src="" frameborder="0" style="display:none;"></iframe>
	    <script type="text/javascript" src="http://staticcdn.b5m.com/static/scripts/common/jquery-1.9.1.min.js"></script>
		<script>
			function SetHash(){
				var hashHeight = document.documentElement.scrollHeight,
					urlAgent = "http://${domain}.b5m.com/iFrame_agent.${pathSuffix}";
					document.getElementById('iFrame_agent').src = urlAgent + '#' + hashHeight;
			}
			window.onload = SetHash;
	
			;(function(){
				$(window).resize(function(event) {
					mediaQuery();
				});
				var mediaQuery = function(){
					//窗口宽度
					var win_w = $(this).width(),
					//body
					b = $(document.body),
					//参考宽度
	
					width_arr = [200,220,240,260];
	
					if(win_w < width_arr[0]){
						b.removeClass().addClass('size1');
					}else if(win_w >= width_arr[0] && win_w < width_arr[1]){
						b.removeClass().addClass('size2');
					}else if(win_w >= width_arr[1] && win_w < width_arr[2]){
						b.removeClass().addClass('size3');
					}else if(win_w >= width_arr[2] && win_w < width_arr[3]){
						b.removeClass().addClass('size4');
					}else{
						b.removeClass().addClass('size5');
					}
				}
				mediaQuery();
			})();
		</script>
	</c:if>
</body>
</html>