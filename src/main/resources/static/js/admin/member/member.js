//  ###############################################
//  KeywordType 초기화 및 드롭다운 설정
//  ###############################################

const currentKeywordType = document.getElementById('currentKeywordType');
const keywordType = document.getElementById('keywordType');
const keywordCriteriaEqualsGroup = document.getElementById('keywordCriteriaEqualsGroup');

function updateCurrentKeywordType() {

  let keywordTypeValue = keywordType.value;
  if (!keywordTypeValue) keywordTypeValue = 'ALL';

  const selected = document.querySelector(`.keyword-type-li[data-value="${keywordTypeValue}"]`);
  currentKeywordType.textContent = selected.textContent;
  const exactMatchSupported = selected.dataset.exactMatchSupported === 'true';
  if (!exactMatchSupported) document.getElementById('keywordCriteriaContains').click();
  keywordCriteriaEqualsGroup.classList.toggle('d-none', !exactMatchSupported);
}

document.getElementById('keywordTypeDropdownList').addEventListener('click', (event) => {
  const target = event.target.closest('.keyword-type-li');
  if (!target) return;
  keywordType.value = target.dataset.value;
  updateCurrentKeywordType();
});

//  ###############################################
//  DateRangeCriteria 초기화 및 드롭다운 설정
//  ###############################################

const startDateTime = document.getElementById('startDateTime');
const endDateTime = document.getElementById('endDateTime');
const startDate = document.getElementById('startDate');
const endDate = document.getElementById('endDate');
const dateRangeBetween = document.getElementById('dateRangeBetween');
const currentDateRangeCriteria = document.getElementById('currentDateRangeCriteria');
const dateRangeCriteria = document.getElementById('dateRangeCriteria');
const dateRangeCriteriaAutofill = document.getElementById('dateRangeCriteriaAutofill');

function updateDateRangeCriteriaAndToggleInputType() {
  const selected = document.querySelector(
    `.date-range-criteria-li[data-value="${dateRangeCriteria.value}"]`
  );
  currentDateRangeCriteria.textContent = selected?.textContent ?? '선택';
  const isDateOnly = selected?.dataset.dateOnly === 'true';
  const isNotSelected = [undefined, '', 'NONE'].includes(dateRangeCriteria.value);
  startDateTime.classList.toggle('d-none', isDateOnly);
  endDateTime.classList.toggle('d-none', isDateOnly);
  startDate.classList.toggle('d-none', !isDateOnly);
  endDate.classList.toggle('d-none', !isDateOnly);
  dateRangeBetween.classList.toggle('disabled', isNotSelected);
  dateRangeCriteriaAutofill.classList.toggle('d-none', isDateOnly);

  [startDateTime, endDateTime, startDate, endDate].forEach(element => {
    element.disabled = isNotSelected;
  })
}

document.getElementById('dateRangeCriteriaDropdownList').addEventListener('click', (e) => {
  if (e.target.classList.contains('date-range-criteria-li')) {
    dateRangeCriteria.value = e.target.dataset.value;
    updateDateRangeCriteriaAndToggleInputType();
  }
})

startDateTime.addEventListener('change', () => {
  startDate.value = startDateTime.value.substring(0, 10);
})
endDateTime.addEventListener('change', () => {
  endDate.value = endDateTime.value.substring(0, 10);
})
startDate.addEventListener('change', () => {
  startDateTime.value = `${startDate.value}T00:00`;
})
endDate.addEventListener('change', () => {
  endDateTime.value =`${endDate.value}T23:59`;
})

//  ###############################################
//  DateRangeCriteria 빠른 입력 기능
//  ###############################################

function toDateTimeLocal(date) {
  const offset = date.getTimezoneOffset();
  return new Date(date.getTime() - offset * 60 * 1000)
    .toISOString()
    .slice(0, 16);
}

document.querySelectorAll('.date-range-criteria-autofill').forEach((element) => {
  element.addEventListener('click', (e) => {
    const value = parseInt(e.target.dataset.value);
    const unit = e.target.dataset.unit;
    const unitMultiplier = {
      'MINUTE': 60 * 1000,
      'HOUR': 60 * 60 * 1000,
      'DAY': 24 * 60 * 60 * 1000
    };
    const now = new Date();
    endDateTime.value = toDateTimeLocal(now);
    startDateTime.value = toDateTimeLocal(new Date(now - value * unitMultiplier[unit]));
  })
})

updateCurrentKeywordType();
updateDateRangeCriteriaAndToggleInputType();

const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));