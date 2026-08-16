document.querySelector('#jobSeekerForm').addEventListener('click', (event) => {

  let changed = false;
  const filterTarget = event.target.closest('#jobSeekerTableWrapper .dropdown-item');
  if (filterTarget) {
    changed = true;
    if (filterTarget.dataset.role === 'update-status-filter') {
      const filterStatus = document.getElementById('filterStatus');
      filterStatus.value = filterTarget.dataset.value ?? '';
    }

    if (filterTarget.dataset.role === 'update-provider-filter') {
      const filterProvider = document.getElementById('filterProvider');
      filterProvider.value = filterTarget.dataset.value ?? '';
    }
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