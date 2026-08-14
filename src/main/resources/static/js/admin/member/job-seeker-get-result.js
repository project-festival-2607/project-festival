document.querySelector('#jobSeekerForm').addEventListener('submit', event => {
  event.preventDefault();

  loadJobSeekerResult(jobSeekerResultUrl());
});

function jobSeekerResultUrl() {

  const form = document.getElementById('jobSeekerForm');
  const params = new URLSearchParams(new FormData(form));
  params.set('pageIdx', '1');
  params.set('pageSize', document.getElementById('pageSize').value);

  return `/admin/member/job-seeker/result?${params}`;

}

function loadJobSeekerResult(url) {

  fetch(url).then(
    (response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      response.text().then((resultHtml) => {
        const jobSeekerResult = document.querySelector('#jobSeekerResult');
        jobSeekerResult.innerHTML = resultHtml;
      })
    }
  );
}