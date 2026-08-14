// 상세요강: adminBoardDetail.js와 동일하게 Toast UI Editor를 읽기 전용 뷰어로 초기화
const contentInit = document.getElementById("contentInit");
const payment = document.getElementById("payment");

new toastui.Editor.factory({
    el: document.getElementById("viewer"),
    viewer: true,
    previewStyle: "vertical",
    height: "auto",
    initialEditType: "wysiwyg",
    hideModeSwitch: true,
    initialValue: contentInit.value
});

// 찜하기 버튼 (list 페이지와 동일한 토글 패턴)
const bookmarkBtn = document.querySelector(".recruit-detail-bookmark");
if (bookmarkBtn) {
    const isJobSeeker = document.currentScript.dataset.jobSeeker === "true";

    bookmarkBtn.addEventListener("click", () => {
        if (!isJobSeeker) {
            alert("로그인 시 이용 가능합니다.");
            return;
        }

        const recruitmentId = bookmarkBtn.dataset.id;
        fetch("/bookmark/recruitment/" + recruitmentId, { method: "POST" })
            .then(res => {
                if (!res.ok) throw new Error("찜하기 요청 실패: " + res.status);
                return res.json();
            })
            .then(bookmarked => {
                bookmarkBtn.classList.toggle("active", bookmarked);
                bookmarkBtn.textContent = bookmarked ? "★" : "☆";
            })
            .catch(err => {
                console.error(err);
                alert("찜하기 처리에 실패했습니다.");
            });
    });
}

if(payment){
    payment.addEventListener("click", ()=>{
        const recruitId = payment.dataset.id;
        // console.log("recruitId:",recruitId);
        fetch("/point/reduce?recruitId="+recruitId, {method: "POST"})
            .then(res => res.json().then(data=>({ok:res.ok, data})))
            .then(({ok, data})=>{
                alert(data.message);
                if(ok) location.reload();
        })
            .catch(err => {
                console.log(err);
                alert("결제 처리에 실패");
            });
    });
}