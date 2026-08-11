function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            const addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;

            document.getElementById('streetAddress').value = addr;
            document.getElementById('detailAddress').focus();
        }
    }).open();
}
