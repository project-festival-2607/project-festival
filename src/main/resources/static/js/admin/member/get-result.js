document.querySelector('#form').addEventListener('submit', event => {
  event.preventDefault();

  if (event.submitter?.classList.contains('page-link')) {
    document.getElementById('pageIdx').value = event.submitter.value;
  }

  if (event.submitter?.classList.contains('page-size-li')) {
    console.log("PAGE SIZE LI");
    document.getElementById('pageIdx').value = 1;
    document.getElementById('pageSize').value = event.submitter.value;
  }

  loadResult(window.location.pathname);
});

document.querySelector('button[data-role="reset-search-form"]').addEventListener('click', event => {
  loadResult(window.location.pathname, true);
})

function getPageUrl(baseUrl, reset = false) {

  if (reset) return `${baseUrl}`;

  const form = document.getElementById('form');
  const params = new URLSearchParams(new FormData(form));

  return `${baseUrl}?${params}`;

}

function getResultRequestUrl(baseUrl, reset = false) {

  if (reset) return `${baseUrl}/result`;

  const form = document.getElementById('form');
  const params = new URLSearchParams(new FormData(form));

  return `${baseUrl}/result?${params}`;

}

function loadResult(baseUrl, reset = false) {
  
  const pageUrl = getPageUrl(baseUrl, reset);
  const resultUrl = getResultRequestUrl(baseUrl, reset);

  console.log('resultUrl: ', resultUrl);

  fetch(resultUrl).then(
    (response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      response.text().then((resultHtml) => {

        const resultElement = document.querySelector('#result');
        let tableWrapper = document.querySelector('#tableWrapper');

        // 이전 스크롤 임시 저장
        if (tableWrapper) {
          localStorage.setItem(
            'admin-table-scroll-x',
            `${tableWrapper.scrollLeft}`
          );
          localStorage.setItem(
            'admin-table-scroll-y',
            `${tableWrapper.scrollTop}`
          );
        }

        resultElement.innerHTML = resultHtml;
        history.pushState({}, '', pageUrl);

        // 저장한 스크롤 불러오기 및 삭제
        tableWrapper = document.querySelector('#tableWrapper');
        tableWrapper.scrollLeft = Number(localStorage.getItem('admin-table-scroll-x') ?? 0);
        tableWrapper.scrollTop = Number(localStorage.getItem('admin-table-scroll-y') ?? 0);
        localStorage.removeItem('admin-table-scroll-x');
        localStorage.removeItem('admin-table-scroll-y');


        const tooltipTriggerList = resultElement.querySelectorAll('[data-bs-toggle="tooltip"]');
        [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

        //  pageSize 초기화 및 드롭다운 설정

        const currentPageSize = document.getElementById('currentPageSize');
        const pageSize = document.getElementById('pageSize');
        currentPageSize.textContent = pageSize.value + '개씩';

      })
    }
  );
}

