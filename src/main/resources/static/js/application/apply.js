console.log("apply.js in");

const applyBtn = document.querySelector("#applyBtn");

applyBtn.addEventListener("click", function () {

    const check = confirm("지원하시겠습니까?");

    if(!check){
        return;
    }

    // form 제출
    document.querySelector("#applyForm").submit();

});