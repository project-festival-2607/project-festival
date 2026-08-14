const passwordInput = document.getElementById("password");
const passwordCheckInput = document.getElementById("password-check");

if (passwordInput && passwordCheckInput) {
    const passwordForm = passwordInput.closest("form");

    passwordForm.addEventListener("submit", (e) => {
        if (passwordInput.value !== passwordCheckInput.value) {
            e.preventDefault();
            alert("비밀번호가 일치하지 않습니다.");
        }
    });
}