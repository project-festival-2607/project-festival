const contentTextarea = document.getElementById("content");
const editorContainer = document.getElementById("editor");
const isAdmin = editorContainer.dataset.admin === "true";

// admin이면 바로 수정 가능한 에디터로, 아니면 읽기 전용 뷰어로 초기화 (저장 버튼/제출 로직은 아직 없음)
const editor = new toastui.Editor.factory({
    el: editorContainer,
    viewer: !isAdmin,
    previewStyle: "vertical",
    height: "500px",
    initialEditType: "wysiwyg",
    hideModeSwitch: true,
    initialValue: contentTextarea.value
});
