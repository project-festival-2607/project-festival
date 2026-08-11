document.addEventListener("DOMContentLoaded", function () {
    const typeSelects = document.querySelectorAll(".portfolio-type");
    typeSelects.forEach(function (select) {

        select.addEventListener("change", function () {
            const portfolioItem = select.closest(".portfolio-item");
            const urlArea = portfolioItem.querySelector(".portfolio-url");
            const fileArea = portfolioItem.querySelector(".portfolio-file");
            if (select.value === "URL") {

                urlArea.style.display = "block";
                fileArea.style.display = "none";

            } else if (select.value === "FILE") {

                urlArea.style.display = "none";
                fileArea.style.display = "block";

            } else {

                urlArea.style.display = "none";
                fileArea.style.display = "none";
            }
        });
    });
});