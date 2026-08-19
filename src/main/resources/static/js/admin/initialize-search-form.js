//  ###############################################
//  KeywordType 초기화 및 드롭다운 설정
//  ###############################################

const currentKeywordType = document.getElementById('currentKeywordType');
const keywordType = document.getElementById('keywordType');

const keywordCriteriaAllWordsContainsGroup = document.getElementById('keywordCriteriaAllWordsContainsGroup');
const keywordCriteriaPhraseContainsGroup = document.getElementById('keywordCriteriaPhraseContainsGroup');
const keywordCriteriaExactGroup = document.getElementById('keywordCriteriaExactGroup');

function updateCurrentKeywordType() {

  let keywordTypeValue = keywordType.value;
  if (!keywordTypeValue) keywordTypeValue = 'ALL';

  const selectedKeywordElem = document.querySelector(`.keyword-type-li[data-value="${keywordTypeValue}"]`);
  currentKeywordType.textContent = selectedKeywordElem.textContent;

  let previousGroup;
  [keywordCriteriaAllWordsContainsGroup, keywordCriteriaPhraseContainsGroup, keywordCriteriaExactGroup].forEach(group => {
    group.classList.add('d-none');
    if (group.querySelector('input').checked) previousGroup = group;
  })
  console.log(selectedKeywordElem.dataset.supportedCriteria);
  let lastVisibleGroup;
  const supportedCriteria = selectedKeywordElem.dataset.supportedCriteria.split(',');
  supportedCriteria.forEach(criteria => {
    switch (criteria) {
      case 'ALL_WORDS_CONTAINS' :
        keywordCriteriaAllWordsContainsGroup.classList.remove('d-none');
        lastVisibleGroup = keywordCriteriaAllWordsContainsGroup;
        break;
      case 'PHRASE_CONTAINS' :
        keywordCriteriaPhraseContainsGroup.classList.remove('d-none');
        lastVisibleGroup = keywordCriteriaPhraseContainsGroup;
        break;
      case 'EXACT' :
        keywordCriteriaExactGroup.classList.remove('d-none');
        lastVisibleGroup = keywordCriteriaExactGroup;
        break;
    }
  });
  if (previousGroup !== null && previousGroup.classList.contains('d-none')) {
    lastVisibleGroup.querySelector('input').click();
  }


}

document.getElementById('keywordTypeDropdownList').addEventListener('click', (event) => {
  const target = event.target.closest('.keyword-type-li');
  if (!target) return;
  keywordType.value = target.dataset.value;
  updateCurrentKeywordType();
});

//  ###############################################
//  DateRangeType 초기화 및 드롭다운 설정
//  ###############################################

const startDateTime = document.getElementById('startDateTime');
const endDateTime = document.getElementById('endDateTime');
const startDate = document.getElementById('startDate');
const endDate = document.getElementById('endDate');
const dateRangeBetween = document.getElementById('dateRangeBetween');
const currentDateRangeType = document.getElementById('currentDateRangeType');
const dateRangeType = document.getElementById('dateRangeType');
const dateRangeTypeAutofill = document.getElementById('dateRangeTypeAutofill');
const dateRangeTypeNotSelected = document.querySelector('.date-range-criteria-li[data-value="NONE"]');

function updateDateRangeTypeAndToggleInputType() {
  const selected = document.querySelector(
    `.date-range-criteria-li[data-value="${dateRangeType.value}"]`
  ) ?? dateRangeTypeNotSelected;
  console.log(selected);
  currentDateRangeType.textContent = selected.textContent;
  const isNotSelected = selected.dataset.value === 'NONE';
  const isDateOnly = selected.dataset.dateOnly === 'true';
  startDateTime.classList.toggle('d-none', isDateOnly);
  endDateTime.classList.toggle('d-none', isDateOnly);
  startDate.classList.toggle('d-none', !isDateOnly);
  endDate.classList.toggle('d-none', !isDateOnly);
  dateRangeBetween.classList.toggle('disabled', isNotSelected);
  dateRangeTypeAutofill.classList.toggle('d-none', isDateOnly);

  [startDateTime, endDateTime, startDate, endDate].forEach(element => {
    element.disabled = isNotSelected;
  })
}

document.getElementById('dateRangeTypeDropdownList').addEventListener('click', (e) => {
  if (e.target.classList.contains('date-range-criteria-li')) {
    dateRangeType.value = e.target.dataset.value;
    updateDateRangeTypeAndToggleInputType();
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
  endDateTime.value = `${endDate.value}T23:59`;
})

//  ###############################################
//  DateRangeType 빠른 입력 기능
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
updateDateRangeTypeAndToggleInputType();

const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
[...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));