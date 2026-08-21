const businessNumberDisplay = document.getElementById("businessNumberDisplay");
const businessNumberInput = document.getElementById("businessNumber");
const verifyBtn = document.getElementById("verifyBusinessNumberBtn");
const verificationTokenInput = document.getElementById("verificationToken");
const businessNumberForm = businessNumberInput.closest("form");

// 이미 등록된 값이 있으면(수정 페이지) 그 값을 "인증된 상태"로 간주, 없으면(가입 폼) null
let verifiedBusinessNumber = businessNumberInput.value || null;

function formatBusinessNumber(digits) {
    if (digits.length > 5) {
        return digits.slice(0, 3) + "-" + digits.slice(3, 5) + "-" + digits.slice(5);
    }
    if (digits.length > 3) {
        return digits.slice(0, 3) + "-" + digits.slice(3);
    }
    return digits;
}

// 페이지 로드 시 초기값이 있으면 하이픈 포맷으로 즉시 표시
if (businessNumberDisplay.value) {
    businessNumberDisplay.value = formatBusinessNumber(businessNumberDisplay.value.replace(/\D/g, ""));
}

// 화면에는 3-2-5 하이픈 포맷으로 보여주고, 실제 전송용 hidden 필드엔 숫자만 저장
businessNumberDisplay.addEventListener("input", () => {
    const cursorPosBefore = businessNumberDisplay.selectionStart;
    const digitsBeforeCursor = businessNumberDisplay.value.slice(0, cursorPosBefore).replace(/\D/g, "").length;

    const digitsOnly = businessNumberDisplay.value.replace(/\D/g, "").slice(0, 10);
    const formatted = formatBusinessNumber(digitsOnly);

    businessNumberDisplay.value = formatted;
    businessNumberInput.value = digitsOnly;

    // 하이픈 위치를 건너뛰고 커서를 원래 입력하던 숫자 위치로 복원
    let newCursorPos = 0;
    let digitCount = 0;
    while (digitCount < digitsBeforeCursor && newCursorPos < formatted.length) {
        if (/\d/.test(formatted[newCursorPos])) digitCount++;
        newCursorPos++;
    }
    businessNumberDisplay.setSelectionRange(newCursorPos, newCursorPos);

    // 인증 후 번호를 다시 수정하면 인증 상태 초기화 (다른 번호로 그냥 통과되는 것 방지)
    if (digitsOnly !== verifiedBusinessNumber) {
        verificationTokenInput.value = "";
    }
});

verifyBtn.addEventListener("click", async () => {
    const businessNumber = businessNumberInput.value;

    if (!businessNumber) {
        alert("사업자등록번호를 입력해주세요.");
        return;
    }

    verifyBtn.disabled = true;
    verifyBtn.textContent = "확인 중...";

    try {
        const response = await fetch("/member/business-number/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ businessNumber })
        });

        const result = await response.json();

        if (result.verified) {
            verificationTokenInput.value = result.verificationToken;
            verifiedBusinessNumber = businessNumber;
            alert("사업자번호 인증에 성공했습니다.");
        } else {
            verificationTokenInput.value = "";
            verifiedBusinessNumber = null;
            alert(result.message || "사업자번호 인증에 실패했습니다.");
        }
    } catch (e) {
        verificationTokenInput.value = "";
        verifiedBusinessNumber = null;
        alert("인증 서버 호출에 실패했습니다. 잠시 후 다시 시도해주세요.");
    } finally {
        verifyBtn.disabled = false;
        verifyBtn.textContent = "인증하기";
    }
});

businessNumberForm.addEventListener("submit", (e) => {
    const businessNumber = businessNumberInput.value;
    if (businessNumber && businessNumber !== verifiedBusinessNumber) {
        e.preventDefault();
        alert("사업자등록번호 인증을 완료해주세요.");
    }
});