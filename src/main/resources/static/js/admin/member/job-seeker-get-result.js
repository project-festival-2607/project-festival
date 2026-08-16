document.querySelector('#jobSeekerForm').addEventListener('submit', event => {
  event.preventDefault();

  if (event.submitter?.classList.contains('page-link')) {
    document.getElementById('pageIdx').value = event.submitter.value;
  }

  if (event.submitter?.classList.contains('page-size-li')) {
    console.log("PAGE SIZE LI");
    document.getElementById('pageIdx').value = 1;
    document.getElementById('pageSize').value = event.submitter.value;
  }

  loadJobSeekerResult();
});

function jobSeekerPageUrl() {

  const form = document.getElementById('jobSeekerForm');
  const params = new URLSearchParams(new FormData(form));

  return `/admin/member/job-seeker?${params}`;

}

function jobSeekerResultUrl() {

  const form = document.getElementById('jobSeekerForm');
  const params = new URLSearchParams(new FormData(form));

  return `/admin/member/job-seeker/result?${params}`;

}

function loadJobSeekerResult() {

  const pageUrl = jobSeekerPageUrl();
  const resultUrl = jobSeekerResultUrl();

  fetch(resultUrl).then(
    (response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      response.text().then((resultHtml) => {

        const jobSeekerResult = document.querySelector('#jobSeekerResult');
        let jobSeekerTableWrapper = document.querySelector('#jobSeekerTableWrapper');

        // 이전 스크롤 임시 저장
        if (jobSeekerTableWrapper) {
          localStorage.setItem(
            'admin-table-scroll-x',
            jobSeekerTableWrapper.scrollLeft
          );
          localStorage.setItem(
            'admin-table-scroll-y',
            jobSeekerTableWrapper.scrollTop
          );
        }

        jobSeekerResult.innerHTML = resultHtml;
        history.pushState({}, '', pageUrl);

        // 저장한 스크롤 불러오기 및 삭제
        jobSeekerTableWrapper = document.querySelector('#jobSeekerTableWrapper');
        jobSeekerTableWrapper.scrollLeft = Number(localStorage.getItem('admin-table-scroll-x') ?? 0);
        jobSeekerTableWrapper.scrollTop = Number(localStorage.getItem('admin-table-scroll-y') ?? 0);
        localStorage.removeItem('admin-table-scroll-x');
        localStorage.removeItem('admin-table-scroll-y');


        const tooltipTriggerList = jobSeekerResult.querySelectorAll('[data-bs-toggle="tooltip"]');
        const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

        //  pageSize 초기화 및 드롭다운 설정

        const currentPageSize = document.getElementById('currentPageSize');
        const pageSize = document.getElementById('pageSize');
        currentPageSize.textContent = pageSize.value + '개씩';

      })
    }
  );
}

