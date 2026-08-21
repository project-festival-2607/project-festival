// 상세요강: adminBoardDetail.js와 동일하게 Toast UI Editor를 읽기 전용 뷰어로 초기화
const contentInit = document.getElementById("contentInit");
const payment = document.getElementById("payment");
const closeRecruit = document.getElementById("closeRecruit");
const reopenRecruit = document.getElementById("reopenRecruit");

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

if (closeRecruit) {
    closeRecruit.addEventListener("click", () => {
        if (!confirm("모집을 마감하시겠습니까? (마감 후에도 다시 올리기로 재개할 수 있습니다)")) return;

        const recruitId = closeRecruit.dataset.id;
        fetch("/recruitment/close/" + recruitId, { method: "POST" })
            .then(res => res.json().then(data => ({ok: res.ok, data})))
            .then(({ok, data}) => {
                alert(data.message);
                if (ok) location.reload();
            })
            .catch(err => {
                console.error(err);
                alert("모집 마감 처리에 실패했습니다.");
            });
    });
}

if (reopenRecruit) {
    reopenRecruit.addEventListener("click", () => {
        if (!confirm("모집을 다시 시작하시겠습니까?")) return;

        const recruitId = reopenRecruit.dataset.id;
        fetch("/recruitment/reopen/" + recruitId, { method: "POST" })
            .then(res => res.json().then(data => ({ok: res.ok, data})))
            .then(({ok, data}) => {
                alert(data.message);
                if (ok) location.reload();
            })
            .catch(err => {
                console.error(err);
                alert("모집 재게시 처리에 실패했습니다.");
            });
    });
}

if(payment){
    payment.addEventListener("click", ()=>{
        if(!confirm("결제 후에는 모집 날짜를 변경할 수 없습니다. 포인트를 차감하시겠습니까?")) return;

        const recruitId = payment.dataset.id;
        // console.log("recruitId:",recruitId);
        fetch("/point/reduce?recruitId="+recruitId, {method: "POST"})
            .then(res => res.json().then(data=>({ok:res.ok, data})))
            .then(({ok, data})=>{
                if(!ok){
                    alert(data.message);
                    return;
                }
                // 포인트 차감 성공 시에만 게시 처리 (publishedAt은 DB가 UPDATE 시점의 시간으로 채움)
                return fetch("/recruitment/publish/" + recruitId, {method: "POST"})
                    .then(res => res.json().then(publishData=>({ok:res.ok, data:publishData})))
                    .then(({data: publishData})=>{
                        alert(publishData.message);
                        location.reload();
                    });
        })
            .catch(err => {
                console.log(err);
                alert("결제 처리에 실패");
            });
    });
}