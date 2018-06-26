// get the user's token after they sign in
function onSignIn (googleUser) {
    console.log("OnSuccess");

    var id_token = googleUser.getAuthResponse().id_token;

    // send the token to the server with an HTTPS POST request
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "https://localhost:5000/tokensignin");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onload = function () {
    	console.log('Signed in as: ' + xhr.responseText);
    };
    xhr.send('idtoken=' + id_token);
}
