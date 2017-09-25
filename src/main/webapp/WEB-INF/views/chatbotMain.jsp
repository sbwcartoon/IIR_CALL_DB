<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title>CALL SYSTEM</title>
	<link href="resources/css/chatbotMain.css?ver=1" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script src="resources/js/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="resources/js/chatbotMain.js?ver=3"></script>
    
    <!-- Animate.css -->
	<link rel="stylesheet" href="resources/css/animate.css">
	<!-- Icomoon Icon Fonts-->
	<link rel="stylesheet" href="resources/css/icomoon.css">
	<!-- Themify Icons-->
	<link rel="stylesheet" href="resources/css/themify-icons.css">
	<!-- Bootstrap  -->
	<link rel="stylesheet" href="resources/css/bootstrap.css">
	<!-- Magnific Popup -->
	<link rel="stylesheet" href="resources/css/magnific-popup.css">
	<!-- Owl Carousel  -->
	<link rel="stylesheet" href="resources/css/owl.carousel.min.css">
	<link rel="stylesheet" href="resources/css/owl.theme.default.min.css">
	<!-- Flexslider -->
	<link rel="stylesheet" href="resources/css/flexslider.css">
	<!-- Theme style  -->
	<link rel="stylesheet" href="resources/css/style.css">

	<!-- Modernizr JS -->
	<script src="resources/js/modernizr-2.6.2.min.js"></script>
	<!-- FOR IE9 below -->
	<!--[if lt IE 9]>
	<script src="js/respond.min.js"></script>
	<![endif]-->
	
</head>
<body>
	<c:import url="/WEB-INF/views/navigator.jsp" />
	<div class="dialog-div" style="width: 50%; float: left;">
	    <ul class="dialog-ul" style="width: 600px; height: 700px; overflow: auto">
			<li style="width:100%">
				<div class="msj macro">
					<div class="avatar"><img class="img-circle" style="width:100%;" src="" /></div>
					<div class="text text-l">
						<p>
						${initInfo}
						</p>
						<p></p>
					</div>
				</div>
			</li>
	    </ul>
		<div class="macro" style="background:whitesmoke; width:600px; height:20px;">
		대화를 입력하세요:   
			<input id="userInput" type="text" style="width:300px;">
			<button id="btnInput">입력</button>
		</div>
	</div>  
	<div class="dialog-div" id="dialogShowBox" style="width: 50%; float: left; overflow: auto;">
	${dialogLogStr}
	</div>
	<input type="hidden" id="imgSrc" value='${imgSrc}'>
	<input type="hidden" id="statusCd" value='${statusCd}'>
	<input type="hidden" id="exStatusCd" value='${statusCd}'>
	<input type="hidden" id="messageIdx" value='${messageIdx}'>
	<input type="hidden" id="conditionInfos" value='${conditionInfoMap}'>
	<input type="hidden" id="loginTime" value='${loginTime}'>
	
	<!-- jQuery -->
	<script src="resources/js/jquery.min.js"></script>
	<!-- jQuery Easing -->
	<script src="resources/js/jquery.easing.1.3.js"></script>
	<!-- Bootstrap -->
	<script src="resources/js/bootstrap.min.js"></script>
	<!-- Waypoints -->
	<script src="resources/js/jquery.waypoints.min.js"></script>
	<!-- Stellar -->
	<script src="resources/js/jquery.stellar.min.js"></script>
	<!-- Magnific Popup -->
	<script src="resources/js/jquery.magnific-popup.min.js"></script>
	<script src="resources/js/magnific-popup-options.js"></script>
	<!-- Main -->
	<script src="resources/js/nav.js"></script>
</body>
</html>
