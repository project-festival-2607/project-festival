console.log("map.js in");

fetch("/api/festival/map")
    .then(res => res.json())
    .then(festivals => {
        initMap(festivals);
    });

function initMap(festivals){
    let isListClick = false;
    const pageSize = 10;
    let currentFestivalList = []; // 지도 근처 리스트
    let currentSearchList = [];  // 검색 결과 리스트
    let visibleDefaultCount = pageSize;
    let visibleSearchCount = 5;  // 검색 시 각각 보여줄 기본 개수
    let visibleNearbyCount = 5;
    let searchedKeyword = "";

    // 지도 생성
    const container = document.getElementById("map");
    const options = {
        center: new kakao.maps.LatLng(37.5662952, 126.9779451),
        level: 5
    };
    const map = new kakao.maps.Map(container, options);
    const places = new kakao.maps.services.Places();

    function getPosition(festival){
        return new kakao.maps.LatLng(festival.mapY, festival.mapX);
    }

    // 마커 생성
    const markers = festivals.map(festival => {
        const position = getPosition(festival);
        const marker = new kakao.maps.Marker({ position: position });

        kakao.maps.event.addListener(marker, "click", function(){
            map.setCenter(position);
            map.setLevel(3);
            setTimeout(()=>{
                const collapse = document.getElementById(`festival_${festival.contentId}`);
                if(collapse){
                    new bootstrap.Collapse(collapse, { toggle: true });
                }
            }, 300);
        });
        return marker;
    });

    const clusterer = new kakao.maps.MarkerClusterer({
        map: map,
        averageCenter: true,
        minLevel: 6,
        markers: markers
    });

    // 검색 실행 함수
    function searchPlace() {
        const keyword = document.getElementById("keyword").value.trim();

        // 검색어가 비어있으면 평소 상태로 복구
        if (!keyword) {
            searchedKeyword = "";
            document.getElementById("defaultListWrapper").classList.remove("d-none");
            document.getElementById("splitListWrapper").classList.add("d-none");
            document.getElementById("cardHeaderTitle").innerText = "주변 축제 목록";
            visibleDefaultCount = pageSize;
            updateFestivalList();
            return;
        }

        searchedKeyword = keyword;
        document.getElementById("defaultListWrapper").classList.add("d-none");
        document.getElementById("splitListWrapper").classList.remove("d-none");
        document.getElementById("cardHeaderTitle").innerText = `"${keyword}" 검색 결과`;

        // 1. 검색 결과 데이터 필터링
        currentSearchList = festivals.filter(f =>
            f.title.toLowerCase().includes(keyword.toLowerCase()) ||
            (f.address && f.address.toLowerCase().includes(keyword.toLowerCase()))
        );

        // 지도 이동
        const matchedFestival = festivals.find(f => f.title.toLowerCase().includes(keyword.toLowerCase()));
        if (matchedFestival) {
            map.setLevel(2);
            map.setCenter(new kakao.maps.LatLng(matchedFestival.mapY, matchedFestival.mapX));
        } else {
            places.keywordSearch(keyword, function(data, status){
                if(status !== kakao.maps.services.Status.OK){
                    alert("검색 결과가 없습니다.");
                    return;
                }
                map.setLevel(8);
                map.setCenter(new kakao.maps.LatLng(data[0].y, data[0].x));
            });
        }

        visibleSearchCount = 5;
        visibleNearbyCount = 5;
        updateFestivalList();
    }

    document.getElementById("searchBtn").addEventListener("click", searchPlace);
    document.getElementById("keyword").addEventListener("keydown", function(e){
        if(e.key === "Enter") searchPlace();
    });

    kakao.maps.event.addListener(map, "idle", function(){
        if(isListClick) return;
        updateFestivalList();
    });

    updateFestivalList();

    // 지도 영역 변경 시 데이터 갱신
    function updateFestivalList(){
        const bounds = map.getBounds();
        currentFestivalList = festivals.filter(festival => bounds.contain(getPosition(festival)));
        renderLists();
    }

    // 아코디언 아이템 생성 헬퍼
    function createFestivalItem(festival, prefix) {

        let parentId;

        if (prefix === "default") {
            parentId = "festivalList";
        } else if (prefix === "search") {
            parentId = "searchFestivalList";
        } else {
            parentId = "nearbyFestivalList";
        }

        const item = document.createElement("div");
        item.className = "accordion-item";

        item.innerHTML = `
        <h2 class="accordion-header">
            <button class="accordion-button collapsed"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#${prefix}_${festival.contentId}">
                ${festival.title}
            </button>
        </h2>

        <div id="${prefix}_${festival.contentId}"
             class="accordion-collapse collapse"
             data-bs-parent="#${parentId}">
            <div class="accordion-body">
                <p class="mb-2"><strong>기간</strong><br>${festival.startDate} ~ ${festival.endDate}</p>
                <a href="/festival/detail?id=${festival.contentId}"
                   class="btn btn-outline-danger btn-sm">상세보기</a>
            </div>
        </div>
    `;

        // 리스트 클릭시 지도 이동
        const button = item.querySelector(".accordion-button");

        button.addEventListener("click", () => {
            isListClick = true;

            const position = getPosition(festival);

            map.setCenter(position);
            map.setLevel(3);

            setTimeout(() => {
                isListClick = false;
            }, 500);
        });

        return item;
    }

    // 렌더링 총괄 분기
    function renderLists(){
        if (!searchedKeyword) {
            renderDefaultList();
        } else {
            renderSplitLists();
        }
    }

    // 1. 평소 리스트 렌더링
    function renderDefaultList(){
        const festivalList = document.getElementById("festivalList");
        festivalList.innerHTML = "";

        if(currentFestivalList.length === 0){
            festivalList.innerHTML = `<div class="p-3 text-center text-muted">주변 축제가 없습니다.</div>`;
            return;
        }

        const showList = currentFestivalList.slice(0, visibleDefaultCount);
        showList.forEach(festival => {
            festivalList.appendChild(createFestivalItem(festival, "default"));
        });

        if(visibleDefaultCount < currentFestivalList.length){
            const wrapper = document.createElement("div");
            wrapper.className = "d-flex justify-content-center my-3";
            const button = document.createElement("button");
            button.className = "btn btn-outline-danger px-5 btn-sm";
            button.innerText = "더보기";
            button.onclick = function(){
                visibleDefaultCount += pageSize;
                renderDefaultList();
            };
            wrapper.appendChild(button);
            festivalList.appendChild(wrapper);
        }
    }

    // 2. 검색 시 상하 분할 리스트 렌더링
    function renderSplitLists(){
        // (1) 검색 결과 구역 렌더링
        const searchListEl = document.getElementById("searchFestivalList");
        searchListEl.innerHTML = "";

        if(currentSearchList.length === 0){
            searchListEl.innerHTML = `<div class="p-3 text-center text-muted fs-6">검색 결과가 없습니다.</div>`;
        } else {
            const showSearch = currentSearchList.slice(0, visibleSearchCount);
            showSearch.forEach(festival => {
                searchListEl.appendChild(createFestivalItem(festival, "search"));
            });

            if(visibleSearchCount < currentSearchList.length){
                const wrapper = document.createElement("div");
                wrapper.className = "d-flex justify-content-center my-2";
                const button = document.createElement("button");
                button.className = "btn btn-outline-danger px-4 btn-sm";
                button.innerText = "검색 결과 더보기";
                button.onclick = function(){
                    visibleSearchCount += 5;
                    renderSplitLists();
                };
                wrapper.appendChild(button);
                searchListEl.appendChild(wrapper);
            }
        }

        // (2) 지도 근처 구역 렌더링
        const nearbyListEl = document.getElementById("nearbyFestivalList");
        nearbyListEl.innerHTML = "";

        if(currentFestivalList.length === 0){
            nearbyListEl.innerHTML = `<div class="p-3 text-center text-muted fs-6">지도 주변 축제가 없습니다.</div>`;
        } else {
            const showNearby = currentFestivalList.slice(0, visibleNearbyCount);
            showNearby.forEach(festival => {
                nearbyListEl.appendChild(createFestivalItem(festival, "nearby"));
            });

            if(visibleNearbyCount < currentFestivalList.length){
                const wrapper = document.createElement("div");
                wrapper.className = "d-flex justify-content-center my-2";
                const button = document.createElement("button");
                button.className = "btn btn-outline-secondary px-4 btn-sm";
                button.innerText = "주변 축제 더보기";
                button.onclick = function(){
                    visibleNearbyCount += 5;
                    renderSplitLists();
                };
                wrapper.appendChild(button);
                nearbyListEl.appendChild(wrapper);
            }
        }
    }
}