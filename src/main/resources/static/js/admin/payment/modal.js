// ##############################################
// 관리 메뉴
// ##############################################

document.querySelector('#form').addEventListener('click', (event) => {

  if (event.target.dataset.bsToggle === 'modal') {
    const modal = document.querySelector(`${event.target.dataset.bsTarget}`);
    const member = event.target.closest('tr');
    const id = member.querySelector(`[data-field="productId"]`).textContent;

    modal.querySelectorAll('[data-product-info]').forEach(
      (element) => {
        const loadHere = element.querySelector('[data-load-here]');
        const value = member.querySelector(`[data-field="${element.dataset.productField}"]`).textContent;
        if (loadHere instanceof HTMLInputElement) loadHere.value = value;
        else loadHere.textContent = value;
      }
    );
    modal.querySelector('[data-role="submit"]').dataset.id = id;
  }

})

document.querySelectorAll('.modal-footer').forEach(element => {
  element.addEventListener('click', (event) => {
    if (event.target.dataset?.role !== 'submit') return;
    const modal = event.target.closest('.modal');
    if (modal.dataset.role === 'deleteProduct') {
      const productId = event.target.dataset.id;
      deleteProductRequest(productId).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult(window.location.pathname);
        }
      })
    }
  });
});

async function deleteProductRequest(productId) {
  try {
    const response = await fetch(
      `/admin/payment/product/${productId}/delete`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
      }
    );
    return await response.json();
  } catch (e) {
    console.log(e);
  }
}