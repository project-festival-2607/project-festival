const tabPersonal = document.getElementById("tab-personal");
const tabBusiness = document.getElementById("tab-business");
const socialLoginArea = document.getElementById("socialLoginArea");

function selectTab(activeTab, inactiveTab, showSocial) {
    activeTab.classList.add("active");
    inactiveTab.classList.remove("active");

    if (showSocial) {
        socialLoginArea.classList.remove("d-none");
    } else {
        socialLoginArea.classList.add("d-none");
    }
}

tabPersonal.addEventListener("click", () => selectTab(tabPersonal, tabBusiness, true));
tabBusiness.addEventListener("click", () => selectTab(tabBusiness, tabPersonal, false));