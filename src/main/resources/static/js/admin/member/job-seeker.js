//  ###############################################
//  pageSize 초기화 및 드롭다운 설정
//  ###############################################

const currentPageSize = document.getElementById('currentPageSize');
const pageSize = document.getElementById('pageSize');

function updatePageSize() {
  const selected = document.querySelector(
    `.page-size-li[data-value="${pageSize.value}"]`
  );
  currentPageSize.textContent = selected?.textContent ?? '30개씩';
}

document.getElementById('pageSizeDropdownList').addEventListener('click', (e) => {
  if (e.target.classList.contains('page-size-li')) {
    pageSize.value = e.target.dataset.value;
    updatePageSize();
  }
});

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
const dateRangeCriteriaAutofill = document.getElementById('dateRangeCriteriaAutofill');

function updateDateRangeCriteriaAndToggleInputType() {
  const selected = document.querySelector(
    `.date-range-criteria-li[data-value="${dateRangeCriteria.value}"]`
  );
  currentDateRangeCriteria.textContent = selected?.textContent ?? '선택';
  const isBirthDate = dateRangeCriteria.value === 'BIRTH_DATE';
  const isNotSelected = dateRangeCriteria.value === '';
  startDateTime.classList.toggle('d-none', isBirthDate);
  endDateTime.classList.toggle('d-none', isBirthDate);
  startDate.classList.toggle('d-none', !isBirthDate);
  endDate.classList.toggle('d-none', !isBirthDate);
  dateRangeCriteriaAutofill.classList.toggle('d-none', isBirthDate);

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
    const diff = e.target.dataset.value;
    console.log(diff);
    const hour = 60 * 60 * 1000;
    const now = new Date();
    endDateTime.value = toDateTimeLocal(now);
    switch (diff) {
      case '1h': startDateTime.value = toDateTimeLocal(new Date(now - hour)); break;
      case '6h': startDateTime.value = toDateTimeLocal(new Date(now - 6 * hour)); break;
      case '12h': startDateTime.value = toDateTimeLocal(new Date(now - 12 * hour)); break;
      case '1d': startDateTime.value = toDateTimeLocal(new Date(now - 24 * hour)); break;
      case '7d': startDateTime.value = toDateTimeLocal(new Date(now - 7 * 24 * hour)); break;
      case '1M': startDateTime.value = toDateTimeLocal(new Date(now - 30 * 24 * hour)); break;
    }
  })
})

updatePageSize();
updateCurrentKeywordType();
updateDateRangeCriteriaAndToggleInputType();

const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))