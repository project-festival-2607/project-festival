console.log("detail js in");

kakao.maps.load(function () {
    const container = document.querySelector(".mapBox");

    if (!container) return;

    const mapY = Number(container.dataset.mapY);
    const mapX = Number(container.dataset.mapX);

    if (!mapY || !mapX) {
        container.innerHTML = "<div class='p-3 text-center text-muted'>지도 정보를 불러올 수 없습니다.</div>";
        return;
    }

    const mapOption = {
        center: new kakao.maps.LatLng(mapY, mapX),
        level: 3,
        draggable: false,
        zoomable: false
    };

    const map = new kakao.maps.Map(container, mapOption);

    const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(mapY, mapX)
    });

    marker.setMap(map);
});