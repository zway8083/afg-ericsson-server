var stompClient = null;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	$("#subject").prop("disabled", connected);
	if (connected) {
		$("#conversation").show();
	} else {
		$("#conversation").hide();
	}
	$("#outputs").html("");
	$("#spinner").hide();
}

function connect() {
	var socket = new SockJS('/websocket');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/output', function(output) {
			showOutput(JSON.parse(output.body).message);
		});
		stompClient.send("/app/input", {}, JSON.stringify({
			'id' : $("#subject").val(),
			'message' : null,
			'connected' : true
		}));
	});
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.send("/app/input", {}, JSON.stringify({
			'id' : $("#subject").val(),
			'message' : null,
			'connected' : false
		}));
		stompClient.disconnect();
		stompClient = null;
	}
	setConnected(false);
	console.log("Disconnected");
}

function sendInput() {
	if (stompClient !== null) {
		stompClient.send("/app/input", {}, JSON.stringify({
			'id' : $("#subject").val(),
			'message' : $("#input").val(),
			'connected' : true
		}));
		$("#spinner").show();
	}
}

function showOutput(message) {
	$("#outputs").append("<tr><td>" + message + "</td></tr>");
	$("#spinner").hide();
}

$(function() {
	$("#spinner").hide();
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() {
		connect();
	});
	$("#disconnect").click(function() {
		disconnect();
	});
	$("#send").click(function() {
		sendInput();
	});
	window.onbeforeunload = disconnect;
});
