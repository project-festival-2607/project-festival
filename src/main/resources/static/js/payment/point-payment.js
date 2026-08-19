// /*mypage.html에서 사용함.*/
// DOM
const pointPaymentBackdrop = document.getElementById("pointPaymentBackdrop");
const pointPaymentNextBtn = document.getElementById("pointPaymentNextBtn");
const pointPaymentFinalBtn = document.getElementById("pointPaymentFinalBtn");
const pointPaymentProductList = document.getElementById("pointPaymentProductList");
const pointPaymentStepSelect = document.getElementById("pointPaymentStepSelect");
const pointPaymentStepPayment = document.getElementById("pointPaymentStepPayment");
const pointPaymentModalTitle = document.getElementById("pointPaymentModalTitle");
const pointPaymentSummary = document.getElementById("pointPaymentSummary");

// 상태값
let selectedProduct = null;
let widgets = null;

// Toss 테스트 클라이언트 키
const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";


// 랜덤 문자열 생성
const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);


// 포인트 구매 버튼 클릭
document
    .getElementById("openPointPaymentBtn")
    .addEventListener("click", async function () {
        resetPointPayment();
        pointPaymentBackdrop.style.display = "flex";
        await loadPointProducts();
    });


// 닫기 버튼
document
    .getElementById("closePointPaymentBtn")
    .addEventListener("click", closePointPayment);


// 모달 바깥 클릭하면 닫기
pointPaymentBackdrop.addEventListener(
    "click",
    function (event) {
        if (event.target === pointPaymentBackdrop) {
            closePointPayment();
        }
    }
);


// 모달 닫기
function closePointPayment() {
    pointPaymentBackdrop.style.display = "none";
}


// 처음 상태로 돌아가기
function resetPointPayment() {
    selectedProduct = null;
    widgets = null;
    pointPaymentNextBtn.disabled = true;
    pointPaymentStepSelect.style.display = "block";
    pointPaymentStepPayment.style.display = "none";
    pointPaymentModalTitle.textContent = "포인트 구매";
    pointPaymentSummary.innerHTML = "";
}


// 상품 목록 가져오기
async function loadPointProducts() {
    pointPaymentProductList.innerHTML =
        "불러오는 중...";
    try {
        const response = await fetch("/products");
        if (!response.ok) {
            throw new Error(
                "상품 목록 조회 실패"
            );
        }
        const products = await response.json();
        pointPaymentProductList.innerHTML = "";


        // 상품이 없는 경우
        if (products.length === 0) {
            pointPaymentProductList.innerHTML =
                "등록된 상품이 없습니다.";
            return;
        }


        // 포인트 적은 순으로 정렬
        products.sort(
            (a, b) =>
                a.pointGet - b.pointGet
        );

        // 상품 생성
        products.forEach(function (product) {
            const item = document.createElement("button");
            item.type = "button";
            item.className = "point-payment-product-item";
            item.innerHTML =
                "<span>"
                + product.productName
                + "</span>"
                +
                "<b>"
                + product.productPrice.toLocaleString()
                + "원"
                + "</b>";

            item.addEventListener(
                "click",
                function () {
                    choosePointProduct(
                        product,
                        item
                    );
                }
            );

            pointPaymentProductList.appendChild(item);

        });


    } catch (error) {
        console.error(error);
        pointPaymentProductList.innerHTML = "상품을 불러오지 못했습니다.";
    }
}

// 상품 선택
function choosePointProduct(product, itemElement) {
    selectedProduct = product;
    // 기존 선택 제거
    document
        .querySelectorAll(
            ".point-payment-product-item"
        )
        .forEach(function (element) {
            element.classList.remove(
                "selected"
            );
        });


    // 현재 상품 선택
    itemElement.classList.add(
        "selected"
    );

    // 결제하기 버튼 활성화
    pointPaymentNextBtn.disabled = false;
}


// STEP 1 → STEP 2
pointPaymentNextBtn.addEventListener(
    "click",
    async function () {
        if (!selectedProduct) {
            return;
        }
        pointPaymentStepSelect.style.display = "none";
        pointPaymentStepPayment.style.display = "block";
        pointPaymentModalTitle.textContent = "결제하기";
        pointPaymentSummary.innerHTML =
            "<b>"
            + selectedProduct.productName
            + "</b> · "
            + selectedProduct.productPrice.toLocaleString()
            + "원";
        try {
            await renderPointPaymentWidget(
                selectedProduct
            );
        } catch (error) {
            console.error(
                "결제 위젯 렌더링 실패:",
                error
            );
        }
    }
);


// 상품 다시 선택
document
    .getElementById("pointPaymentBackBtn")
    .addEventListener(
        "click",
        function () {
            pointPaymentStepSelect.style.display = "block";
            pointPaymentStepPayment.style.display = "none";
            pointPaymentModalTitle.textContent = "포인트 구매";
        }
    );


// Toss 결제 위젯 생성
async function renderPointPaymentWidget(product) {
    const amount = {
        currency: "KRW",
        value: product.productPrice
    };


    widgets = TossPayments(clientKey)
        .widgets({
            customerKey:
                generateRandomString()
        });


    // 금액 설정
    await widgets.setAmount(amount);
    // 결제수단 + 약관 렌더링
    await Promise.all([
        widgets.renderPaymentMethods({
            selector: "#pointPaymentMethod",
            variantKey: "DEFAULT"
        }),


        widgets.renderAgreement({
            selector: "#pointPaymentAgreement",
            variantKey: "AGREEMENT"
        })
    ]);
}


// 최종 결제 버튼
pointPaymentFinalBtn.addEventListener("click", async function () {
        if (!widgets || !selectedProduct) {
            return;
        }
        try {
            await widgets.requestPayment({
                orderId: generateRandomString(),
                orderName:
                selectedProduct.productName,
                successUrl: window.location.origin + "/widget/success",
                failUrl: window.location.origin + "/fail",
                customerEmail: "customer123@gmail.com",
                customerName: "hoogil"
            });

        } catch (error) {
            console.error(
                "결제 요청 실패:",
                error
            );

        }
    }
);