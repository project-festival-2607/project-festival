//  ###############################################
//  KeywordType 초기화 및 드롭다운 설정
//  ###############################################

const currentKeywordType = document.getElementById('currentKeywordType');
const keywordType = document.getElementById('keywordType');

function updateCurrentKeywordType() {
  const selected = document.querySelector(
    `.keyword-type-li[data-value="${keywordType.value}"]`
  );
  currentKeywordType.textContent = selected?.textContent ?? '전체';
}

document.getElementById('keywordTypeDropdownList').addEventListener('click', (e) => {
  if (e.target.classList.contains('keyword-type-li')) {
    keywordType.value = e.target.dataset.value;
    updateCurrentKeywordType();
  }
});


//  ###############################################
//  DateRangeCriteria 초기화 및 드롭다운 설정
//  ###############################################

const startDateTime = document.getElementById('startDateTime');
const endDateTime = document.getElementById('endDateTime');
const startDate = document.getElementById('startDate');
const endDate = document.getElementById('endDate');
const currentDateRangeCriteria = document.getElementById('currentDateRangeCriteria');
const dateRangeCriteria = document.getElementById('dateRangeCriteria');

function updateDateRangeCriteriaAndToggleInputType() {
  const selected = document.querySelector(
    `.date-range-criteria-li[data-value="${dateRangeCriteria.value}"]`
  );
  currentDateRangeCriteria.textContent = selected?.textContent ?? '선택';
  const isBirthDate = dateRangeCriteria.value === 'BIRTH_DATE';
  const isNotSelected = dateRangeCriteria.value === '';
  startDateTime.classList.toggle('d-none', isBirthDate || isNotSelected);
  endDateTime.classList.toggle('d-none', isBirthDate || isNotSelected);
  startDate.classList.toggle('d-none', !isBirthDate || isNotSelected);
  endDate.classList.toggle('d-none', !isBirthDate || isNotSelected);
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

updateCurrentKeywordType();
updateDateRangeCriteriaAndToggleInputType();