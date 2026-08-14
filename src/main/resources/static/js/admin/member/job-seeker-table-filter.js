document.querySelector('#jobSeekerForm').addEventListener('click', (event) => {

  const target = event.target.closest('.dropdown-item');
  console.log(target);

  if (target.dataset.role === 'update-status-filter') {
    const filterStatus = document.getElementById('filterStatus');
    filterStatus.value = target.dataset.value ?? '';
  }

  if (target.dataset.role === 'update-provider-filter') {
    const filterProvider = document.getElementById('filterProvider');
    filterProvider.value = target.dataset.value ?? '';
  }
    loadJobSeekerResult();

});