document.querySelectorAll(".unlink-btn").forEach(button => {
    button.addEventListener("click", async () => {
        const provider = button.dataset.provider;
        const response = await fetch(`/mypage/social-login/${provider}/unlink`, { method: "POST" });

        if (!response.ok) {
            alert("연동 해제에 실패했습니다.");
            return;
        }

        const link = document.createElement("a");
        link.href = `/mypage/social-login/${provider}/link`;
        link.className = "btn btn-outline-primary btn-sm";
        link.textContent = "연동하기";
        button.replaceWith(link);
    });
});

// OAuth2 연동 왕복 후 돌아왔을 때 결과 안내
const params = new URLSearchParams(window.location.search);
if (params.get("linkError") === "true") {
    alert("이미 사용중인 계정입니다.");
}