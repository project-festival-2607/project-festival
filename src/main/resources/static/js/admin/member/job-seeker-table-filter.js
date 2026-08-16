document.querySelector('#jobSeekerForm').addEventListener('click', (event) => {

  let changed = false;
  const filterTarget = event.target.closest('button[data-role="update-filter"]');
  if (filterTarget) {
    changed = true;
    const filterElement = document.getElementById(filterTarget.dataset.targetElement);
    filterElement.value = filterTarget.dataset.value ?? '';
  }

  const sortTarget = event.target.closest('button[data-role="update-sort-criteria"]');
  if (sortTarget) {
    changed = true;
    const sortCriteria = document.getElementById("sortCriteria");
    const ascending = document.getElementById("ascending");

    sortCriteria.value = sortTarget.dataset.criteria;
    ascending.value = sortTarget.dataset.ascending;
  }

  if (changed) loadJobSeekerResult();

});