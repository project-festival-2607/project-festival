// ===== 0. 찜하기 버튼: 카드 전체가 <a>라서 클릭 전파를 막아야 페이지 이동이 안 됨 =====
const currentScript = document.currentScript;
const isJobSeeker = currentScript.dataset.jobSeeker === "true";

document.querySelectorAll(".recruit-card-bookmark").forEach(btn => {
    btn.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (!isJobSeeker) {
            alert("로그인 시 이용 가능합니다.");
            return;
        }

        const recruitmentId = btn.dataset.id;
        fetch("/bookmark/recruitment/" + recruitmentId, { method: "POST" })
            .then(res => {
                if (!res.ok) throw new Error("찜하기 요청 실패: " + res.status);
                return res.json();
            })
            .then(bookmarked => {
                btn.classList.toggle("active", bookmarked);
                btn.textContent = bookmarked ? "★" : "☆";
            })
            .catch(err => {
                console.error(err);
                alert("찜하기 처리에 실패했습니다.");
            });
    });
});

// ===== 1. 지역 시/도 -> 시/군/구 연동 드롭다운 (register 페이지와 동일 패턴, 기존 선택값 복원 포함) =====
const initialSido = currentScript.dataset.sido || "";
const initialSigungu = currentScript.dataset.sigungu || "";

const sidoSelect = document.getElementById("filterSido");
const sigunguSelect = document.getElementById("filterSigungu");

function loadSigunguList(sidoCode, selectedCode) {
    sigunguSelect.innerHTML = "";

    if (!sidoCode) {
        sigunguSelect.disabled = true;
        sigunguSelect.appendChild(new Option("전체", ""));
        return;
    }

    fetch("/api/region/sigungu?sidoCode=" + encodeURIComponent(sidoCode))
        .then(res => res.json())
        .then(list => {
            sigunguSelect.disabled = false;
            sigunguSelect.appendChild(new Option("전체", ""));
            list.forEach(region => {
                const option = new Option(region.name, region.code);
                if (region.code === selectedCode) option.selected = true;
                sigunguSelect.appendChild(option);
            });
        })
        .catch(err => {
            console.error(err);
            alert("시/군/구 목록을 불러오지 못했습니다.");
        });
}

sidoSelect.addEventListener("change", () => loadSigunguList(sidoSelect.value, ""));

if (initialSido) loadSigunguList(initialSido, initialSigungu);

const categorySelect = document.getElementById("filterCategory");
const listCriteriaSelect = document.querySelector("select[name='listCriteria']");

// ===== 2. 모집인력 종류별 세부 필터 표시/숨김 (알바=급여유형, 푸드트럭=조건, 장비/기타=없음) =====
const specificBlocks = document.querySelectorAll(".recruit-sidebar-specific");

function updateSpecificVisibility() {
    const category = categorySelect.value;
    specificBlocks.forEach(block => {
        block.style.display = (block.dataset.category === category) ? "" : "none";
    });
}
categorySelect.addEventListener("change", updateSpecificVisibility);
updateSpecificVisibility();

// ===== 3. 모집인력 종류 칩 버튼: 클릭한 칩만 active로 표시하고 숨은 input에 값 반영 =====
document.querySelectorAll("#categoryChips .recruit-chip").forEach(chip => {
    chip.addEventListener("click", () => {
        document.querySelectorAll("#categoryChips .recruit-chip").forEach(c => c.classList.remove("active"));
        chip.classList.add("active");
        categorySelect.value = chip.dataset.value;
        categorySelect.dispatchEvent(new Event("change"));
    });
});

// ===== 4. 정렬은 고르는 즉시 다시 검색 =====
listCriteriaSelect.addEventListener("change", () => listCriteriaSelect.form.submit());

// ===== 5. 근무 시작일이 근무 종료일보다 늦을 수 없도록 입력창 자체에서 막기 (register 페이지와 동일 패턴) =====
const workingStartDateInput = document.getElementById("workingStartDate");
const workingEndDateInput = document.getElementById("workingEndDate");

workingStartDateInput.addEventListener("change", () => {
    workingEndDateInput.min = workingStartDateInput.value || "";
    if (workingEndDateInput.value && workingStartDateInput.value && workingEndDateInput.value < workingStartDateInput.value) {
        workingEndDateInput.value = "";
    }
});
workingEndDateInput.addEventListener("change", () => {
    workingStartDateInput.max = workingEndDateInput.value || "";
    if (workingStartDateInput.value && workingEndDateInput.value && workingStartDateInput.value > workingEndDateInput.value) {
        workingStartDateInput.value = "";
    }
});

// ===== 6. 근무시간: register 페이지와 동일하게 시/분을 고르고 "확인"으로 확정, 종료 시간이 시작 시간보다 빠를 수 없음 =====
// direction "after": boundValue보다 늦은 시간만 허용 (종료 시간 <- 시작 시간 기준)
// direction "before": boundValue보다 빠른 시간만 허용 (시작 시간 <- 종료 시간 기준)
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
    });

    return { trigger, popover, hiddenInput };
}

const startTimePicker = buildTimePicker("workingStart", "before", () => endTimePicker.hiddenInput.value);
const endTimePicker = buildTimePicker("workingEnd", "after", () => startTimePicker.hiddenInput.value);
