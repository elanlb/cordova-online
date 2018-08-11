var id_token = ""; // this will be set later by a function

// get the user's token after they sign in and show their username
function onSignIn (googleUser) {
    console.log("OnSuccess");
    id_token = googleUser.getAuthResponse().id_token;

    const profile = googleUser.getBasicProfile();
    const email = profile.getEmail();
    const name = profile.getName();
    document.getElementById("staticEmail").value = email;
    document.getElementById("staticName").value = name;
}

// sign in with the server when they click the button
function signIn () {
	// send the token to the server with an HTTPS POST request
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/tokensignin");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onload = function () {
    	console.log('Redirecting to: ' + xhr.responseText);
    	window.location.href = xhr.responseText;
    };

    xhr.send('idtoken=' + id_token);
}
