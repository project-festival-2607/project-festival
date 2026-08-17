const contentInit = document.getElementById("contentInit");
const contentInput = document.getElementById("content");
const editorContainer = document.getElementById("editor");
const isAdmin = editorContainer.dataset.admin === "true";
const titleInput = document.getElementById("title");

// 로컬 미리보기 URL(blob:...) -> 실제 업로드할 이미지 파일 매핑 (admin 수정 모드에서만 사용)
const pendingImages = new Map();

// admin이면 바로 수정 가능한 에디터로, 아니면 읽기 전용 뷰어로 초기화
const editor = isAdmin
    ? new toastui.Editor({
        el: editorContainer,
        previewStyle: "vertical",
        height: "500px",
        initialEditType: "wysiwyg",
        hideModeSwitch: true,
        initialValue: contentInit.value,
        hooks: {
            addImageBlobHook(blob, callback) {
                const previewUrl = URL.createObjectURL(blob);
                pendingImages.set(previewUrl, blob);
                callback(previewUrl, blob.name || "image");
            }
        }
    })
    : new toastui.Editor.factory({
        el: editorContainer,
        viewer: true,
        previewStyle: "vertical",
        height: "500px",
        initialEditType: "wysiwyg",
        hideModeSwitch: true,
        initialValue: contentInit.value
    });

if (isAdmin) {
    // register.js와 동일한 방식: 대기 중인 이미지들을 비동기로 업로드하고 마크다운의 미리보기 URL을 실제 URL로 교체
    async function uploadPendingImages(markdown) {
        let result = markdown;
        for (const [previewUrl, blob] of pendingImages) {
            const formData = new FormData();
            formData.append("image", blob);
            formData.append("title", titleInput.value);

            const res = await fetch("/notice/uploadImage", {
                method: "POST",
                body: formData
            });
            if (!res.ok) throw new Error("이미지 업로드 실패: " + res.status);

            const fileDto = await res.json();
            result = result.split(previewUrl).join("/notice/image/" + fileDto.uuid);
        }
        return result;
    }

    const modifyForm = document.getElementById("modifyForm");
    modifyForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const markdown = editor.getMarkdown();
        if (!titleInput.value.trim() || !markdown.trim()) {
            alert("제목과 내용을 입력해주세요.");
            return;
        }

        uploadPendingImages(markdown)
            .then(finalMarkdown => {
                contentInput.value = finalMarkdown;
                modifyForm.submit();
            })
            .catch(err => {
                console.error(err);
                alert("이미지 업로드에 실패했습니다.");
            });
    });

    document.getElementById("deleteBtn").addEventListener("click", () => {
        if (confirm("삭제하시겠습니까?")) {
            document.getElementById("deleteForm").submit();
        }
    });
}
