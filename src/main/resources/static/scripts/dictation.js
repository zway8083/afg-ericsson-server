function startDictation(id) {

	if (window.hasOwnProperty('webkitSpeechRecognition')) {

		var recognition = new webkitSpeechRecognition();

		recognition.continuous = false;
		recognition.interimResults = false;

		recognition.lang = "fr-FR";
		recognition.start();

		recognition.onresult = function(e) {
			document.getElementById(id).value = e.results[0][0].transcript;
			recognition.stop();
		};

		recognition.onerror = function(e) {
			recognition.stop();
		}

	}
}