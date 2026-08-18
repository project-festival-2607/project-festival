document.addEventListener('click', (event) => {

  const target = event.target.closest('span');

  if (target?.classList.contains('click-to-copy')) {
    const tooltipElement = target.querySelector('a');
    const text = tooltipElement.dataset.bsTitle;
    navigator.clipboard.writeText(text)
      .then(() => {
        alert('텍스트가 클립보드에 복사되었습니다.');
      })
      .catch(err => {
        console.error('복사 실패:', err);
      });
    document.querySelectorAll('.tooltip.bs-tooltip-auto.fade.show').forEach(element => {
      element.classList.remove('show');
    })
  }

});