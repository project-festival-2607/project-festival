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

updateCurrentKeywordType();

const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));