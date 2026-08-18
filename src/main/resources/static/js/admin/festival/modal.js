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
    applyAdditionalInfoField(festival, modal);
  }
})

document.getElementById('assignFestivalManagerLoadMemberPreview').addEventListener('click', () => {

  const id = document.getElementById('assignFestivalManagerMemberId').value;
  const memberPreviewElement = document.querySelector('#assignFestivalManagerModal [data-role="memberPreview"]');
  loadRecruiterInfoField(id, memberPreviewElement);

})

document.querySelectorAll('.modal-footer').forEach(element => {
  element.addEventListener('click', (event) => {
    if (event.target.dataset?.role !== 'submit') return;
    const modal = event.target.closest('.modal');
    if (modal.dataset.role === 'assignFestivalManager') {
      const contentId = event.target.dataset.contentId;
      const memberId = document.getElementById('assignFestivalManagerMemberId').value;
      if (memberId === '' || /[^0-9]/.test(memberId)) {
        alert("유효하지 않은 사용자 고유 아이디입니다.");
        return;
      }
      assignFestivalManagerRequest(contentId, memberId).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult(window.location.pathname);
        }
      })
    }
    if (modal.dataset.role === 'unassignFestivalManager') {
      const contentId = event.target.dataset.contentId;
      unassignFestivalManagerRequest(contentId).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult(window.location.pathname);
        }
      })
    }
  });
});

function loadRecruiterInfoField(recruiterId, element) {
  fetch(`/admin/event/recruiter/${recruiterId}`).then(response => {

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    response.json().then(recruiter => {

      fetch('/admin/event/fragment/member').then(response => {
          if (!response.ok) throw new Error(`HTTP ${response.status}`);
          response.text().then((resultHtml) => {
            element.innerHTML = resultHtml;
            applyMemberInfoField(element, recruiter);
          });
        }
      );
    });
  });
}

function applyAdditionalInfoField(festival, modal) {
  if (['assignFestivalManager', 'unassignFestivalManager'].includes(modal.dataset.role)) {
    const memberId = festival.querySelector('[data-field="memberId"]').textContent;
    const memberPreviewElement = modal.querySelector('[data-role="memberPreview"]');
    if (memberId !== '') loadRecruiterInfoField(memberId, memberPreviewElement);
  }
}

function applyMemberInfoField(memberPreviewElement, member) {
  memberPreviewElement.querySelectorAll('[data-member-info]').forEach(
    (element) => {
      const loadHere = element.querySelector('[data-load-here]');
      loadHere.textContent = member[`${element.dataset.memberField}`];
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

async function unassignFestivalManagerRequest(contentId) {
  try {
    const response = await fetch(
      `/admin/event/festival/${contentId}/unassign-manager`,
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