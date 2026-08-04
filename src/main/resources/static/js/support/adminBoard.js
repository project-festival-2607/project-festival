const titleInput = document.getElementById("title");
const registerForm = document.getElementById("registerForm");

// 로컬 미리보기 URL(blob:...) -> 실제 업로드할 이미지 파일 매핑
// 이미지를 넣는 시점에는 서버에 저장하지 않고, 전송 버튼을 눌렀을 때 한 번에 업로드함
const pendingImages = new Map();

const editor = new toastui.Editor({
    el: document.querySelector('#editor'),
    previewStyle: 'vertical',
    height: '500px',
    initialEditType: 'wysiwyg',
    hideModeSwitch: true,
    hooks: {
        addImageBlobHook(blob, callback) {
            const previewUrl = URL.createObjectURL(blob);
            pendingImages.set(previewUrl, blob);
            callback(previewUrl, blob.name || "image");
        }
    }
});

// 대기 중인 이미지들을 비동기로 서버에 업로드하고,
// 마크다운 안의 로컬 미리보기 URL을 실제 저장된 이미지 URL로 교체함
async function uploadPendingImages(markdown) {
    let result = markdown;
    for (const [previewUrl, blob] of pendingImages) {
        const formData = new FormData();
        formData.append("image", blob);
        formData.append("title", titleInput.value);

        const res = await fetch("/adminBoard/uploadImage", {
            method: "POST",
            body: formData
        });
        if (!res.ok) throw new Error("이미지 업로드 실패: " + res.status);

        const fileDto = await res.json();
        console.log("image uploaded:", fileDto);
        result = result.split(previewUrl).join("/adminBoard/image/" + fileDto.uuid);
    }
    return result;
}

// 전송 버튼을 누르면: 대기 중인 이미지 업로드(비동기)를 먼저 끝내고,
// hidden input을 채운 뒤 실제 폼 전송은 동기(페이지 이동) 방식으로 진행함
registerForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const markdown = editor.getMarkdown();
    if (!titleInput.value.trim() || !markdown.trim()) {
        alert("제목과 내용을 입력해주세요.");
        return;
    }

    uploadPendingImages(markdown)
        .then(finalMarkdown => {
            document.getElementById("content").value = finalMarkdown;
            registerForm.submit(); // fetch가 아닌 실제 동기 폼 전송 (submit 이벤트 재발생 없음)
        })
        .catch(err => {
            console.error(err);
            alert("이미지 업로드에 실패했습니다.");
        });
});
