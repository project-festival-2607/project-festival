function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;

            document.getElementById('zipCode').value = data.zonecode;
            document.getElementById('address').value = addr;

            var geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(addr, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    document.getElementById('mapX').value = result[0].x;
                    document.getElementById('mapY').value = result[0].y;
                }
            });

            document.getElementById('eventPlace').focus();
        }
    }).open();
}