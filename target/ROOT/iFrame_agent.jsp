<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<title>iframe代理页面</title>
	<meta name="keywords" content="关键字" />
	<meta name="description" content="页面描述" />
</head>
<body>
<script>
	(function(){
		var iObj = parent.parent.document.getElementById('iFrame1');
		iObjH = parent.parent.frames['iFrame1'].frames['iFrame_agent'].location.hash;
		iObj.style.height = iObjH.split('#')[1] + 'px';
	}());
</script>
</body>