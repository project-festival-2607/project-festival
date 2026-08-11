document.addEventListener("DOMContentLoaded", function () {

    const portfolioType =
        document.querySelector('select[name="portfolios[0].type"]');

    const portfolioUrl =
        document.getElementById("portfolioUrl");

    const portfolioFile =
        document.getElementById("portfolioFile");

    portfolioType.addEventListener("change", function () {
        // 일단 둘 다 숨김
        portfolioUrl.style.display = "none";
        portfolioFile.style.display = "none";

        // URL 선택
        if (this.value === "URL") {
            portfolioUrl.style.display = "block";
        }

        // FILE 선택
        else if (this.value === "FILE") {
            portfolioFile.style.display = "block";
        }
    });
});