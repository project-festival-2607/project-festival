document.querySelector('#jobSeekerForm').addEventListener('submit', event => {
  event.preventDefault();

  if (event.submitter?.classList.contains('page-link')) {
    document.getElementById('pageIdx').value = event.submitter.value;
  }

  loadJobSeekerResult();
});

function jobSeekerPageUrl() {

  const form = document.getElementById('jobSeekerForm');
  const params = new URLSearchParams(new FormData(form));
  params.set('pageSize', document.getElementById('pageSize').value);

  return `/admin/member/job-seeker?${params}`;

}

function jobSeekerResultUrl() {

  const form = document.getElementById('jobSeekerForm');
  const params = new URLSearchParams(new FormData(form));
  params.set('pageSize', document.getElementById('pageSize').value);

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
        jobSeekerResult.innerHTML = resultHtml;
        history.pushState({}, '', pageUrl);

        const tooltipTriggerList = jobSeekerResult.querySelectorAll('[data-bs-toggle="tooltip"]');
        const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

      })
    }
  );
}

