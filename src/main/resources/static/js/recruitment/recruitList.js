// ===== 1. 지역 시/도 -> 시/군/구 연동 드롭다운 (register 페이지와 동일 패턴, 기존 선택값 복원 포함) =====
const currentScript = document.currentScript;
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
