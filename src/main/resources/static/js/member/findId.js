const nameInput = document.getElementById("name");
const searchPhoneInput = document.getElementById("searchPhone");
const searchBtn = document.getElementById("searchBtn");
const resultList = document.getElementById("resultList");
const verifySection = document.getElementById("verifySection");
const codeInput = document.getElementById("code");
const verifyCodeBtn = document.getElementById("verifyCodeBtn");
const sendCodeBtn = document.getElementById("sendCodeBtn");

let pendingMemberId = null;

function renderItem(item) {
    const div = document.createElement("div");
    div.className = "border rounded p-2 mb-2 d-flex justify-content-between align-items-center";
    div.dataset.memberId = item.memberId;

    const label = document.createElement("span");
    label.textContent = item.social ? "소셜 로그인 계정" : item.maskedUsername;
    div.appendChild(label);

    const revealBtn = document.createElement("button");
    revealBtn.type = "button";
    revealBtn.className = "btn btn-outline-primary btn-sm";
    revealBtn.textContent = "전체보기";
    revealBtn.addEventListener("click", () => reveal(item.memberId, div));
    div.appendChild(revealBtn);

    resultList.appendChild(div);
}

searchBtn.addEventListener("click", async () => {
    const name = nameInput.value.trim();
    const phone = searchPhoneInput.value.trim();

    if (!name || !phone) {
        alert("이름과 전화번호를 입력해주세요.");
        return;
    }

    const response = await fetch("/member/find-id/search", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, phone })
    });

    const results = await response.json();
    resultList.innerHTML = "";

    if (results.length === 0) {
        resultList.textContent = "일치하는 계정이 없습니다.";
        return;
    }

    results.forEach(renderItem);
});

async function reveal(memberId, itemDiv) {
    const phone = searchPhoneInput.value.trim();

    const response = await fetch(`/member/find-id/reveal?memberId=${memberId}&phone=${encodeURIComponent(phone)}`, {
        method: "POST"
    });
    const result = await response.json();

    if (!result.verified) {
        pendingMemberId = memberId;
        verifySection.style.display = "";
        return;
    }

    applyReveal(itemDiv, result);
}

function applyReveal(itemDiv, result) {
    const label = itemDiv.querySelector("span");
    label.innerHTML = "";

    if (result.social) {
        const badge = document.createElement("span");
        badge.className = "provider-badge provider-" + result.provider.toLowerCase();
        badge.textContent = result.provider;
        label.appendChild(badge);
        label.append(" 소셜 로그인 계정");
    } else {
        label.textContent = result.maskedUsername;
    }

    itemDiv.querySelector("button").remove();
}

verifyCodeBtn.addEventListener("click", async () => {
    const phone = searchPhoneInput.value.trim();
    const code = codeInput.value.trim();

    const response = await fetch("/member/find-id/verify-phone", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, code })
    });
    const result = await response.json();

    if (!result.success) {
        alert(result.message || "인증번호가 일치하지 않습니다.");
        return;
    }

    verifySection.style.display = "none";

    const itemDiv = resultList.querySelector(`[data-member-id="${pendingMemberId}"]`);
    const revealResponse = await fetch(`/member/find-id/reveal?memberId=${pendingMemberId}&phone=${encodeURIComponent(phone)}`, {
        method: "POST"
    });
    applyReveal(itemDiv, await revealResponse.json());
});

sendCodeBtn.addEventListener("click", async () => {
    const phone = searchPhoneInput.value.trim();

    await fetch("/member/phone/send-code", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone })
    });

    alert("인증번호를 발송했습니다.");
});