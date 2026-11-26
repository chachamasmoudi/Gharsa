
const loginTab = document.getElementById("loginTab");
const registerTab = document.getElementById("registerTab");

const loginSection = document.getElementById("loginSection");
const registerSection = document.getElementById("registerSection");

const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

function showAuthSection(section) {
    const isLogin = section === "login";

    loginSection.hidden = !isLogin;
    registerSection.hidden = isLogin;

    loginTab.classList.toggle("active", isLogin);
    registerTab.classList.toggle("active", !isLogin);

    loginTab.setAttribute("aria-selected", String(isLogin));
    registerTab.setAttribute("aria-selected", String(!isLogin));

    clearMessages();
}

function clearMessages() {
    document.querySelectorAll(".field-error").forEach((element) => {
        element.textContent = "";
    });

    document.querySelectorAll(".form-message").forEach((element) => {
        element.textContent = "";
        element.classList.remove("success");
    });
}

loginTab.addEventListener("click", () => {
    showAuthSection("login");
});

registerTab.addEventListener("click", () => {
    showAuthSection("register");
});

document.querySelectorAll("[data-show-register]").forEach((button) => {
    button.addEventListener("click", () => {
        showAuthSection("register");
    });
});

document.querySelectorAll("[data-show-login]").forEach((button) => {
    button.addEventListener("click", () => {
        showAuthSection("login");
    });
});

document.querySelectorAll(".password-toggle").forEach((button) => {
    button.addEventListener("click", () => {
        const passwordInput = document.getElementById(
            button.dataset.passwordTarget
        );

        const isHidden = passwordInput.type === "password";

        passwordInput.type = isHidden ? "text" : "password";

        button.setAttribute(
            "aria-label",
            isHidden ? "Masquer le mot de passe" : "Afficher le mot de passe"
        );

        button.setAttribute("aria-pressed", String(isHidden));
    });
});

function validateEmail(input, errorElement) {
    const email = input.value.trim();

    if (!email) {
        errorElement.textContent = "Veuillez saisir votre adresse e-mail.";
        return false;
    }

    if (!input.validity.valid) {
        errorElement.textContent = "Veuillez saisir une adresse e-mail valide.";
        return false;
    }

    errorElement.textContent = "";
    return true;
}

function validatePassword(input, errorElement) {
    if (!input.value.trim()) {
        errorElement.textContent = "Veuillez saisir votre mot de passe.";
        return false;
    }

    errorElement.textContent = "";
    return true;
}

loginForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const emailInput = document.getElementById("loginEmail");
    const passwordInput = document.getElementById("loginPassword");

    const emailError = document.getElementById("loginEmailError");
    const passwordError = document.getElementById("loginPasswordError");
    const message = document.getElementById("loginMessage");

    message.textContent = "";
    message.classList.remove("success");

    const isEmailValid = validateEmail(emailInput, emailError);
    const isPasswordValid = validatePassword(passwordInput, passwordError);

    if (!isEmailValid || !isPasswordValid) {
        return;
    }

    message.textContent =
        "L'interface est prête. La connexion sera activée après l'intégration de l'API Backend.";
});

registerForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const nameInput = document.getElementById("registerName");
    const emailInput = document.getElementById("registerEmail");
    const passwordInput = document.getElementById("registerPassword");

    const nameError = document.getElementById("registerNameError");
    const emailError = document.getElementById("registerEmailError");
    const passwordError = document.getElementById("registerPasswordError");
    const message = document.getElementById("registerMessage");

    message.textContent = "";
    message.classList.remove("success");

    let isNameValid = true;

    if (!nameInput.value.trim()) {
        nameError.textContent = "Veuillez saisir votre nom complet.";
        isNameValid = false;
    } else {
        nameError.textContent = "";
    }

    const isEmailValid = validateEmail(emailInput, emailError);
    const isPasswordValid = validatePassword(passwordInput, passwordError);

    if (!isNameValid || !isEmailValid || !isPasswordValid) {
        return;
    }

    message.textContent =
        "Votre formulaire est prêt. La création du compte sera activée après l'intégration de l'API Backend.";
});
