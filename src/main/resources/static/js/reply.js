//	var aaa = (function(){
//		
//	})();
//	위 형식은 즉시 실행 함수로 단 한번만 실행된다.

var replyManager = (function() {
	var getAll = function(obj, callback) {
		// Ajax를 이용해서 GET 방식으로 JSON 데이터들을 가져온다.
		// Ajax 호출 결과는 파라미터로 전달받은 함수를 이용해서 처리한다.
		// jQuery.getJSON(url, data, success(data, textStatus, jqXHR))
		// 	- url 정보를 요청할 URL
		// 	- data 서버로 보낼 data
		// 	- success(data, textStatus, jqXHR) 요청이 성공하면 실행될 콜백 함수
		$.getJSON('/replies/' + obj, callback);
	};
	
	var add = function(obj, callback) {
		$.ajax({
			type : "post",
			url : "/replies/" + obj.bno,
			data : JSON.stringify(obj),
			dataType : "json",
			beforeSend : function(xhr) {
				// Ajax 전송 시 'X-CSRF-TOKEN' 헤더를 지정해준다.
				// csrf 객체에서 headerName과 token 값을 이용해서 HTTP 헤더 정보를 구성한다.
				xhr.setRequestHeader(obj.csrf.headerName, obj.csrf.token);
			},
			contentType : "application/json",
			success : callback
		});
	};
	
	var update = function(obj, callback) {
		$.ajax({
			type : "put",
			url : "/replies/" + obj.bno,
			data : JSON.stringify(obj),
			dataType : "json",
			contentType : "application/json",
			beforeSend : function(xhr) {
				// Ajax 전송 시 'X-CSRF-TOKEN' 헤더를 지정해준다.
				// csrf 객체에서 headerName과 token 값을 이용해서 HTTP 헤더 정보를 구성한다.
				xhr.setRequestHeader(obj.csrf.headerName, obj.csrf.token);
			},
			success : callback 
		});
	};
	
	var remove = function(obj, callback) {
		$.ajax({
			type : "delete",
			url : "/replies/" + obj.bno + "/" + obj.rno,
			dataType : "json",
			contentType : "application/json",
			beforeSend : function(xhr) {
				// Ajax 전송 시 'X-CSRF-TOKEN' 헤더를 지정해준다.
				// csrf 객체에서 headerName과 token 값을 이용해서 HTTP 헤더 정보를 구성한다.
				xhr.setRequestHeader(obj.csrf.headerName, obj.csrf.token);
			},
			success : callback
		});
	};
	
	return {
		getAll : getAll,
		add : add,
		update : update,
		remove : remove
	};
}) ();