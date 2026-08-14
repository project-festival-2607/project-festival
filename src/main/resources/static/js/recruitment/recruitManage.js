const currentScript = document.currentScript;

const festivalSelect = document.querySelector("select[name='festivalContentId']");
const statusSelect = document.querySelector("select[name='status']");
const listCriteriaSelect = document.querySelector("select[name='listCriteria']");

festivalSelect.addEventListener("change", () => festivalSelect.form.submit());
statusSelect.addEventListener("change", () => statusSelect.form.submit());
listCriteriaSelect.addEventListener("change", () => listCriteriaSelect.form.submit());