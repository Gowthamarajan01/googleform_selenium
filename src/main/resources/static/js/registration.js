const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const passwordMatchMessage = document.getElementById("passwordMatchMessage");

function showPasswordMatchStatus() {
    if (!confirmPasswordInput.value) {
        passwordMatchMessage.textContent = "";
        return;
    }

    const passwordsMatch = passwordInput.value === confirmPasswordInput.value;
    passwordMatchMessage.textContent = passwordsMatch ? "Passwords match." : "Passwords do not match yet.";
    passwordMatchMessage.classList.toggle("text-success", passwordsMatch);
    passwordMatchMessage.classList.toggle("text-danger", !passwordsMatch);
}

passwordInput.addEventListener("input", showPasswordMatchStatus);
confirmPasswordInput.addEventListener("input", showPasswordMatchStatus);
