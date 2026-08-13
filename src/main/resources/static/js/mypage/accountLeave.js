const agreeCheckbox = document.getElementById("agreeTerms");
const confirmSection = document.getElementById("confirmSection");
const withdrawBtn = document.getElementById("withdrawBtn");
const passwordInput = document.getElementById("password");
const confirmNameInput = document.getElementById("confirmName");

agreeCheckbox.addEventListener("change", () => {
    confirmSection.style.display = agreeCheckbox.checked ? "" : "none";
});

withdrawBtn.addEventListener("click", async () => {
    if (!confirm("정말 탈퇴하시겠습니까?")) {
        return;
    }

    const confirmValue = passwordInput ? passwordInput.value : confirmNameInput.value;

    try {
        const response = await fetch("/mypage/withdraw", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ confirmValue })
        });

        if (!response.ok) {
            alert("입력하신 정보가 일치하지 않습니다.");
            return;
        }

        alert("탈퇴가 완료되었습니다.");
        window.location.href = "/";
    } catch (e) {
        alert("탈퇴 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
});