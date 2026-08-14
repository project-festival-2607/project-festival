const phoneInput = document.getElementById("phone");
const sendCodeBtn = document.getElementById("sendCodeBtn");
const codeInput = document.getElementById("code");
const verifyCodeBtn = document.getElementById("verifyCodeBtn");
const phoneVerificationTokenInput = document.getElementById("phoneVerificationToken");
const phoneForm = phoneInput.closest("form");

let verifiedPhone = phoneInput.value || null;

sendCodeBtn.addEventListener("click", async () => {
    const phone = phoneInput.value.trim();

    if (!phone) {
        alert("전화번호를 입력해주세요.");
        return;
    }

    sendCodeBtn.disabled = true;
    sendCodeBtn.textContent = "발송 중...";

    try {
        const response = await fetch("/member/phone/send-code", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ phone })
        });

        const result = await response.json();

        if (result.success) {
            alert("인증번호를 발송했습니다.");
        } else {
            alert(result.message || "인증번호 발송에 실패했습니다.");
        }
    } catch (e) {
        alert("인증번호 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
    } finally {
        sendCodeBtn.disabled = false;
        sendCodeBtn.textContent = "인증번호 받기";
    }
});

verifyCodeBtn.addEventListener("click", async () => {
    const phone = phoneInput.value.trim();
    const code = codeInput.value.trim();

    if (!code) {
        alert("인증번호를 입력해주세요.");
        return;
    }

    try {
        const response = await fetch("/member/phone/verify-code", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ phone, code })
        });

        const result = await response.json();

        if (result.success) {
            phoneVerificationTokenInput.value = result.verificationToken;
            verifiedPhone = phone;
            alert("전화번호 인증이 완료되었습니다.");
        } else {
            phoneVerificationTokenInput.value = "";
            verifiedPhone = null;
            alert(result.message || "인증번호가 일치하지 않습니다.");
        }
    } catch (e) {
        alert("인증 확인에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }
});

// 인증 후 전화번호를 다시 수정하면 인증 상태 초기화
phoneInput.addEventListener("input", () => {
    if (phoneInput.value.trim() !== verifiedPhone) {
        phoneVerificationTokenInput.value = "";
    }
});

// 인증 안 된 상태로 폼 제출 시도하면 막는다 (서버 쪽 토큰 검증이 최종 방어선)
phoneForm.addEventListener("submit", (e) => {
    const phone = phoneInput.value.trim();
    if (phone && phone !== verifiedPhone) {
        e.preventDefault();
        alert("전화번호 인증을 완료해주세요.");
    }
});