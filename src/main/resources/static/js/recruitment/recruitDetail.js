// 상세요강: adminBoardDetail.js와 동일하게 Toast UI Editor를 읽기 전용 뷰어로 초기화
const contentInit = document.getElementById("contentInit");

new toastui.Editor.factory({
    el: document.getElementById("viewer"),
    viewer: true,
    previewStyle: "vertical",
    height: "auto",
    initialEditType: "wysiwyg",
    hideModeSwitch: true,
    initialValue: contentInit.value
});
