// ##############################################
// 관리 메뉴
// ##############################################

document.querySelector('#form').addEventListener('click', (event) => {

  if (event.target.dataset.bsToggle === 'modal') {
    const modal = document.querySelector(`${event.target.dataset.bsTarget}`);
    const festival = event.target.closest('tr');
    const contentId = festival.querySelector(`[data-field="contentId"]`).textContent;

    modal.querySelectorAll('[data-festival-info]').forEach(
      (element) => {
        const loadHere = element.querySelector('[data-load-here]');
        const value = festival.querySelector(`[data-field="${element.dataset.festivalField}"]`).textContent;
        if (loadHere instanceof HTMLInputElement) loadHere.value = value;
        else loadHere.textContent = value;
      }
    );
    modal.querySelector('[data-role="submit"]').dataset.contentId = contentId;
  }

})

document.getElementById('loadMemberPreview').addEventListener('click', () => {

  const id = document.getElementById('assignFestivalManagerMemberId').value;

  fetch(`/admin/event/recruiter/${id}`).then(response => {

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    response.json().then(recruiter => {

      // 회원 확인 성공 시 FRAGMENT 불러오기

      fetch('/admin/event/fragment/member').then(response => {
          if (!response.ok) throw new Error(`HTTP ${response.status}`);
          response.text().then((resultHtml) => {
            const memberPreviewElement = document.querySelector('[data-role="memberPreview"]');
            memberPreviewElement.innerHTML = resultHtml;

            applyRecruiterInfoField(memberPreviewElement, recruiter);

          });
        }
      );
    })
  })
})

document.querySelectorAll('.modal-footer').forEach(element => {
  element.addEventListener('click', (event) => {
    if (event.target.dataset?.role !== 'submit') return;
    const modal = event.target.closest('.modal');
    if (modal.dataset.role === 'assignFestivalManager') {
      const contentId = event.target.dataset.contentId;
      const memberId = document.getElementById('assignFestivalManagerMemberId').value;
      assignFestivalManagerRequest(contentId, memberId).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult(window.location.pathname);
        }
      })
    }
  });
});

function applyRecruiterInfoField(memberPreviewElement, recruiter) {
  memberPreviewElement.querySelectorAll('[data-member-info]').forEach(
    (element) => {
      const loadHere = element.querySelector('[data-load-here]');
      loadHere.textContent = recruiter[`${element.dataset.memberField}`];
    }
  );
}

async function assignFestivalManagerRequest(contentId, memberId) {
  try {
    const response = await fetch(
      `/admin/event/festival/${contentId}/assign-manager/${memberId}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        }
      }
    );
    return await response.json();
  } catch (e) {
    console.log(e);
  }
}