document.addEventListener('click', async (event) => {

  const target = event.target.closest('span');

  if (target?.classList.contains('show-and-click-to-copy')) {
    const tooltipElement = target.querySelector('a');
    const id = tooltipElement.getAttribute('aria-describedby');
    const text = tooltipElement.dataset.bsTitle;
    navigator.clipboard.writeText(text)
      .catch(err => {
        console.error('복사 실패:', err);
      });
    await successPopupMessage(tooltipElement, id);
  }

  if (target?.classList.contains('click-to-copy')) {
    const tooltipElement = target.querySelector('a');
    const id = tooltipElement.getAttribute('aria-describedby');
    const value = tooltipElement.dataset.value;
    navigator.clipboard.writeText(value)
      .catch(err => {
        console.error('복사 실패:', err);
      });
    await successPopupMessage(tooltipElement, id);
  }
});

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function successPopupMessage(tooltipElement, id) {
  const tooltipPopup = document.getElementById(id);
  tooltipPopup.style.setProperty('--bs-tooltip-bg', 'var(--bs-success)');
  tooltipPopup.querySelector('.tooltip-inner').textContent = '클립보드에 복사되었습니다.';
  await sleep(1000);
  tooltipPopup.classList.remove('show');
  tooltipElement.blur();
  await sleep(150);
  tooltipPopup.remove();
}