var recognition = null;

if (window.hasOwnProperty('webkitSpeechRecognition')) {
	function initRecognition(inputId, button) {
		var recognition = new webkitSpeechRecognition();
		
		recognition.continuous = false;
		recognition.interimResults = false;
		recognition.lang = "fr-FR";
		
		recognition.onstart = function() {
			document.getElementById(inputId).placeholder = "Parlez...";
			button.style.background = "LimeGreen";
		}
		
		recognition.onresult = function(event) {
			document.getElementById(id).value = event.results[0][0].transcript;
			recognition.stop();
		}
		
		recognition.onerror = function(event) {
			concole.error(event);
		}
		
		recognition.onend = function() {
			document.getElementById(inputId).placeholder = "";
			button.style.background = "none";
			recognition = null;
		}
	
		return recognition;
	}
	
	function dictation(inputId, button) {
		if (recognition !== null) {
			recognition.stop();
		}
		recognition = initRecognition(inputId, button);
		recognition.start();
	}
} else {
	alert("Can't use mic");
}