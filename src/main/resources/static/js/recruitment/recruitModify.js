const titleInput = document.getElementById("recruitmentTitle");
const modifyForm = document.getElementById("modifyForm");

// ===== 1. Toast UI 에디터 (register와 동일한 패턴 + 기존 내용 로드) =====
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

const contentSource = document.getElementById("contentSource");
if (contentSource && contentSource.value.trim()) {
    editor.setMarkdown(contentSource.value);
}

async function uploadPendingImages(markdown) {
    let result = markdown;
    for (const [previewUrl, blob] of pendingImages) {
        const formData = new FormData();
        formData.append("image", blob);
        formData.append("title", titleInput.value);

        const res = await fetch("/recruitment/uploadImage", {
            method: "POST",
            body: formData
        });
        if (!res.ok) throw new Error("이미지 업로드 실패: " + res.status);

        const fileDto = await res.json();
        result = result.split(previewUrl).join("/recruitment/image/" + fileDto.uuid);
    }
    return result;
}

modifyForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const markdown = editor.getMarkdown();
    if (!titleInput.value.trim() || !markdown.trim()) {
        alert("제목과 업무내용을 입력해주세요.");
        return;
    }

    uploadPendingImages(markdown)
        .then(finalMarkdown => {
            document.getElementById("content").value = finalMarkdown;
            modifyForm.submit(); // fetch가 아닌 실제 동기 폼 전송
        })
        .catch(err => {
            console.error(err);
            alert("이미지 업로드에 실패했습니다.");
        });
});

// ===== 2. 지역 시/도 -> 시/군/구 연동 드롭다운 (초기값은 서버가 이미 채워둠) =====
const sidoSelect = document.getElementById("regionSidoCode");
const sigunguSelect = document.getElementById("regionSigunguCode");

sidoSelect.addEventListener("change", () => {
    const sidoCode = sidoSelect.value;
    sigunguSelect.innerHTML = "";

    if (!sidoCode) {
        sigunguSelect.disabled = true;
        sigunguSelect.appendChild(new Option("시/도를 먼저 선택하세요", ""));
        return;
    }

    fetch("/api/region/sigungu?sidoCode=" + encodeURIComponent(sidoCode))
        .then(res => res.json())
        .then(list => {
            sigunguSelect.disabled = false;
            sigunguSelect.appendChild(new Option("시/군/구 선택", ""));
            list.forEach(region => {
                sigunguSelect.appendChild(new Option(region.name, region.code));
            });
        })
        .catch(err => {
            console.error(err);
            alert("시/군/구 목록을 불러오지 못했습니다.");
        });
});

// ===== 3. 모집인력 종류에 따른 세부 입력칸 표시/숨김 (종류 자체는 disabled라 값은 고정) =====
const categorySelect = document.getElementById("category");
const specificBlocks = document.querySelectorAll(".recruit-specific");

function updateSpecificVisibility() {
    const category = categorySelect.value;
    specificBlocks.forEach(block => {
        block.style.display = (block.dataset.category === category) ? "" : "none";
    });
}
categorySelect.addEventListener("change", updateSpecificVisibility);
updateSpecificVisibility();

// ===== 4. 근무 시작~종료 시간: 팝오버에서 시/분을 고르고 "확인"으로 확정, 총 시간은 자동 계산 =====
const totalHoursEl = document.getElementById("totalHours");

function applyTimeBound(hourSelect, minuteSelect, boundValue, direction) {
    const [boundH, boundM] = boundValue ? boundValue.split(":").map(Number) : [null, null];

    [...hourSelect.options].forEach(option => {
        const h = Number(option.value);
        option.disabled = !!boundValue && (direction === "after" ? h < boundH : h > boundH);
    });
    if (hourSelect.selectedOptions[0]?.disabled) {
        const firstEnabled = [...hourSelect.options].find(o => !o.disabled);
        if (firstEnabled) hourSelect.value = firstEnabled.value;
    }

    const selectedHour = Number(hourSelect.value);
    [...minuteSelect.options].forEach(option => {
        const m = Number(option.value);
        option.disabled = !!boundValue && selectedHour === boundH
            && (direction === "after" ? m <= boundM : m >= boundM);
    });
    if (minuteSelect.selectedOptions[0]?.disabled) {
        const firstEnabled = [...minuteSelect.options].find(o => !o.disabled);
        if (firstEnabled) minuteSelect.value = firstEnabled.value;
    }
}

function buildTimePicker(prefix, direction, getBoundValue) {
    const trigger = document.getElementById(prefix + "TimeTrigger");
    const popover = document.getElementById(prefix + "TimePopover");
    const hourSelect = document.getElementById(prefix + "Hour");
    const minuteSelect = document.getElementById(prefix + "Minute");
    const confirmBtn = document.getElementById(prefix + "TimeConfirm");
    const hiddenInput = document.getElementById(prefix + "Time");

    for (let h = 0; h < 24; h++) {
        const hh = String(h).padStart(2, "0");
        hourSelect.appendChild(new Option(hh, hh));
    }
    [0, 10, 20, 30, 40, 50].forEach(m => {
        const mm = String(m).padStart(2, "0");
        minuteSelect.appendChild(new Option(mm, mm));
    });

    // 기존 값이 있으면 시/분 select도 맞춰서 선택해둠
    if (hiddenInput.value) {
        const [h, m] = hiddenInput.value.split(":");
        hourSelect.value = h;
        minuteSelect.value = String(Math.floor(Number(m) / 10) * 10).padStart(2, "0");
    }

    hourSelect.addEventListener("change", () => applyTimeBound(hourSelect, minuteSelect, getBoundValue(), direction));

    trigger.addEventListener("click", (e) => {
        e.stopPropagation();
        if (!popover.hidden) {
            popover.hidden = true;
            return;
        }
        applyTimeBound(hourSelect, minuteSelect, getBoundValue(), direction);
        popover.hidden = false;
    });

    confirmBtn.addEventListener("click", () => {
        const value = `${hourSelect.value}:${minuteSelect.value}`;
        const boundValue = getBoundValue();
        const isValid = !boundValue || (direction === "after" ? value > boundValue : value < boundValue);
        if (!isValid) {
            alert(direction === "after"
                ? "근무 종료 시간은 근무 시작 시간보다 늦어야 합니다."
                : "근무 시작 시간은 근무 종료 시간보다 빨라야 합니다.");
            return;
        }
        hiddenInput.value = value;
        trigger.textContent = value;
        popover.hidden = true;
        updateTotalHours();
    });

    return { trigger, popover, hiddenInput };
}

const startTimePicker = buildTimePicker("workingStart", "before", () => endTimePicker.hiddenInput.value);
const endTimePicker = buildTimePicker("workingEnd", "after", () => startTimePicker.hiddenInput.value);

function updateTotalHours() {
    const start = startTimePicker.hiddenInput.value;
    const end = endTimePicker.hiddenInput.value;
    if (!start || !end) {
        totalHoursEl.textContent = "시간을 고른 뒤 확인 버튼을 눌러주세요.";
        return;
    }

    const [startH, startM] = start.split(":").map(Number);
    const [endH, endM] = end.split(":").map(Number);
    const minutes = (endH * 60 + endM) - (startH * 60 + startM);

    const hours = Math.floor(minutes / 60);
    const remainMinutes = minutes % 60;
    totalHoursEl.textContent = `총 근무시간: ${hours}시간` + (remainMinutes > 0 ? ` ${remainMinutes}분` : "");
}
updateTotalHours(); // 기존 값이 있으면 페이지 로드 시점에 바로 총 시간 표시

// ===== 5. 날짜 입력칸 최소값을 오늘로 설정 (@FutureOrPresent 검증과 맞춤) =====
const today = new Date().toISOString().split("T")[0];
["applicationDeadline", "workingStartDate", "workingEndDate"].forEach(id => {
    document.getElementById(id).min = today;
});

// ===== 6. 근무 종료일이 근무 시작일보다 빠를 수 없도록 입력창 자체에서 막기 =====
const workingStartDateInput = document.getElementById("workingStartDate");
const workingEndDateInput = document.getElementById("workingEndDate");

workingStartDateInput.addEventListener("change", () => {
    workingEndDateInput.min = workingStartDateInput.value || today;
    if (workingEndDateInput.value && workingEndDateInput.value < workingEndDateInput.min) {
        workingEndDateInput.value = "";
    }
});
workingEndDateInput.addEventListener("change", () => {
    workingStartDateInput.max = workingEndDateInput.value || "";
    if (workingStartDateInput.value && workingEndDateInput.value && workingStartDateInput.value > workingEndDateInput.value) {
        workingStartDateInput.value = "";
    }
});