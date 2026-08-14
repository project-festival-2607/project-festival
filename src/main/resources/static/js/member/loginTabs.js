const tabPersonal = document.getElementById("tab-personal");
const tabBusiness = document.getElementById("tab-business");
const socialLoginArea = document.getElementById("socialLoginArea");
const memberTypeInput = document.getElementById("memberType");

function selectTab(activeTab, inactiveTab, showSocial, memberType) {
    activeTab.classList.add("active");
    inactiveTab.classList.remove("active");
    memberTypeInput.value = memberType;

    if (showSocial) {
        socialLoginArea.classList.remove("d-none");
    } else {
        socialLoginArea.classList.add("d-none");
    }
}

tabPersonal.addEventListener("click", () => selectTab(tabPersonal, tabBusiness, true, "personal"));
tabBusiness.addEventListener("click", () => selectTab(tabBusiness, tabPersonal, false, "business"));