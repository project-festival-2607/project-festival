const currentScript = document.currentScript;

const festivalSelect = document.querySelector("select[name='festivalContentId']");
const listCriteriaSelect = document.querySelector("select[name='listCriteria']");

festivalSelect.addEventListener("change", () => festivalSelect.form.submit());
listCriteriaSelect.addEventListener("change", () => listCriteriaSelect.form.submit());