const refundBackdrop =
    document.getElementById("pointRefundBackdrop");

const openRefundBtn =
    document.getElementById("openPointRefundBtn");

const closeRefundBtn =
    document.getElementById("closePointRefundBtn");

const summaryEl =
    document.getElementById("pointRefundSummary");

const inputEl =
    document.getElementById("pointRefundAmount");

const errorEl =
    document.getElementById("pointRefundError");

const confirmRefundBtn =
    document.getElementById("pointRefundConfirmBtn");

const successEl =
    document.getElementById("pointRefundSuccess");

const successMessageEl =
    document.getElementById("pointRefundSuccessMessage");


let maxRefundable = 0;


// ==============================
// 환불 모달 열기
// ==============================

openRefundBtn.addEventListener("click", async () => {

    refundBackdrop.style.display = "flex";

    // 초기화
    inputEl.value = "";
    errorEl.style.display = "none";

    successEl.style.display = "none";
    confirmRefundBtn.style.display = "block";
    inputEl.style.display = "block";

    await loadRefundablePoint();

});


// ==============================
// X 버튼
// ==============================

closeRefundBtn.addEventListener("click", () => {

    refundBackdrop.style.display = "none";

});


// ==============================
// 배경 클릭 → 닫기
// ==============================

refundBackdrop.addEventListener("click", (event) => {

    if (event.target === refundBackdrop) {

        refundBackdrop.style.display = "none";

    }

});


// ==============================
// 환불 가능 포인트 조회
// ==============================

async function loadRefundablePoint() {

    summaryEl.textContent = "불러오는 중...";

    try {

        const res =
            await fetch("/point/refundable");

        const data =
            await res.json();

        maxRefundable =
            data.refundablePoint;

        summaryEl.innerHTML =
            "환불 가능한 포인트: <b>"
            + maxRefundable.toLocaleString()
            + "</b>P";

        inputEl.max = maxRefundable;
        inputEl.value = "";

    } catch (e) {

        console.error(e);

        summaryEl.textContent =
            "환불 가능 포인트를 불러오지 못했습니다.";

    }

}


// ==============================
// 환불하기
// ==============================

confirmRefundBtn.addEventListener("click", async () => {

    errorEl.style.display = "none";

    const amount =
        parseInt(inputEl.value, 10);


    // ==============================
    // 입력값 검사
    // ==============================

    if (!amount || amount <= 0) {

        errorEl.textContent =
            "환불할 포인트를 입력해주세요.";

        errorEl.style.display = "block";

        return;
    }


    // ==============================
    // 최대 환불 가능 포인트 검사
    // ==============================

    if (amount > maxRefundable) {

        errorEl.textContent =
            "환불 가능한 포인트("
            + maxRefundable.toLocaleString()
            + "P)를 초과했습니다.";

        errorEl.style.display = "block";

        return;
    }


    try {

        const res =
            await fetch("/point/refund", {

                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body:
                    "refundAmount=" + amount

            });


        const data =
            await res.json();


        // ==============================
        // 환불 실패
        // ==============================

        if (!res.ok || !data.success) {

            errorEl.textContent =
                data.message || "환불에 실패했습니다.";

            errorEl.style.display = "block";

            return;
        }


        // ==============================
        // 환불 성공
        // ==============================

        successMessageEl.textContent =
            data.message
            + " ("
            + data.refundedAmount.toLocaleString()
            + "P)";


        // 기존 입력 UI 숨기기
        inputEl.style.display = "none";
        confirmRefundBtn.style.display = "none";

        // 성공 메시지 보여주기
        successEl.style.display = "block";

    } catch (e) {

        console.error(e);

        errorEl.textContent =
            "환불 요청 중 오류가 발생했습니다.";

        errorEl.style.display = "block";

    }

});


// ==============================
// 환불 완료 후 화면 아무 곳이나 클릭
// → 모달 닫기
// ==============================

successEl.addEventListener("click", () => {

    refundBackdrop.style.display = "none";

    // 마이페이지 포인트 숫자도 갱신
    location.reload();

});