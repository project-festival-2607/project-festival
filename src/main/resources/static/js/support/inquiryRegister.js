const titleInput = document.getElementById("title");
const contentInput = document.getElementById("content");
const fileRows = document.getElementById("fileRows");
const inquiryForm = document.getElementById("inquiryForm");

const MAX_FILE_COUNT = 5;

function setInputFile(input, file) {
    const dt = new DataTransfer();
    dt.items.add(file);
    input.files = dt.files;
}

function createFileRow(file) {
    const row = document.createElement("div");
    row.className = "input-group mb-2";

    const input = document.createElement("input");
    input.type = "file";
    input.name = "attachments";
    input.className = "form-control";

    const cancelBtn = document.createElement("button");
    cancelBtn.type = "button";
    cancelBtn.className = "btn btn-outline-secondary";
    cancelBtn.textContent = "취소";
    cancelBtn.style.display = "none";

    if (file) {
        setInputFile(input, file);
        cancelBtn.style.display = "";
    }

    cancelBtn.addEventListener("click", () => {
        input.value = "";
        renderRows();
    });
    input.addEventListener("change", renderRows);

    row.append(input, cancelBtn);
    return row;
}

// 현재 선택된 파일들을 모아 다시 그림: 파일이 있는 줄은 위로 몰리고, 맨 끝엔 빈 줄 하나만 남음
function renderRows() {
    const files = Array.from(fileRows.querySelectorAll("input[type=file]"))
        .map(input => input.files[0])
        .filter(Boolean);

    fileRows.innerHTML = "";
    files.forEach(file => fileRows.appendChild(createFileRow(file)));
    if (files.length < MAX_FILE_COUNT) {
        fileRows.appendChild(createFileRow());
    }
}

renderRows(); // 초기 빈 줄 하나 생성

inquiryForm.addEventListener("submit", (e) => {
    if (!titleInput.value.trim() || !contentInput.value.trim()) {
        e.preventDefault();
        alert("제목과 내용을 입력해주세요.");
    }
});
