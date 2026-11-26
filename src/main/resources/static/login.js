
"use strict";

document.addEventListener("DOMContentLoaded", initializeLogin);

async function initializeLogin() {

    const loginForm = document.getElementById("loginForm");
    const loginButton = document.getElementById("loginButton");
    const loginStatus = document.getElementById("loginStatus");
    const csrfToken = document.getElementById("csrfToken");
    const password = document.getElementById("password");
    const showPassword = document.getElementById("showPassword");

    const loginError = document.getElementById("loginError");
    const logoutMessage = document.getElementById("logoutMessage");
    const connectionError = document.getElementById("connectionError");

    let csrfReady = false;
    let submitting = false;

    loginButton.disabled = true;

    // Display login and logout notifications

    const parameters = new URLSearchParams(
        window.location.search
    );

    if (parameters.has("error")) {

        loginError.hidden = false;
    }

    if (parameters.has("logout")) {

        logoutMessage.hidden = false;
    }

    // Toggle password visibility

    showPassword.addEventListener("change", function () {

        password.type = showPassword.checked
            ? "text"
            : "password";
    });

    // Protect login form submission

    loginForm.addEventListener("submit", function (event) {

        if (!csrfReady || !csrfToken.value || submitting) {

            event.preventDefault();

            return;
        }

        submitting = true;

        loginButton.disabled = true;
        loginButton.textContent = "Signing In...";

        loginStatus.textContent =
            "Authenticating your credentials...";

        password.type = "password";
        showPassword.checked = false;
    });

    // Retrieve session-specific CSRF token

    try {

        const response = await fetch("/login/csrf", {

            method: "GET",

            credentials: "same-origin",

            cache: "no-store",

            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {

            throw new Error(
                "Unable to retrieve security token"
            );
        }

        const data = await response.json();

        if (
            typeof data.token !== "string"
            || !data.token.trim()
            || typeof data.parameterName !== "string"
            || !data.parameterName.trim()
        ) {

            throw new Error(
                "Invalid security token response"
            );
        }

        csrfToken.name = data.parameterName;
        csrfToken.value = data.token;

        csrfReady = true;

        connectionError.hidden = true;

        loginButton.disabled = false;

        loginStatus.textContent =
            "Ready for secure sign-in.";

    } catch (error) {

        csrfReady = false;

        csrfToken.value = "";

        loginButton.disabled = true;

        connectionError.hidden = false;

        loginStatus.textContent =
            "Secure login is currently unavailable.";

        console.error(
            "Login security initialization failed."
        );
    }

    // Reset the login page when restored from browser history

    window.addEventListener("pageshow", function (event) {

        if (event.persisted) {

            window.location.reload();
        }
    });
}
