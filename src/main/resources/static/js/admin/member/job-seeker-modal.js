// ##############################################
// 관리 메뉴
// ##############################################

document.querySelector('#form').addEventListener('click', (event) => {

  if (event.target.dataset.bsToggle === 'modal') {
    const modal = document.querySelector(`${event.target.dataset.bsTarget}`);
    const member = event.target.closest('tr');
    const id = member.querySelector(`[data-field="id"]`).textContent;

    modal.querySelectorAll('[data-member-info]').forEach(
      (element) => {
        const loadHere = element.querySelector('[data-load-here]');
        const value = member.querySelector(`[data-field="${element.dataset.memberField}"]`).textContent;
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
    if (modal.dataset.role === 'suspendMember') {
      const memberId = event.target.dataset.id;
      const reason = document.getElementById('suspensionReason').value;
      suspendMemberRequest(memberId, reason).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult('/admin/member/job-seeker');
        }
      })
    }
    if (modal.dataset.role === 'unsuspendMember') {
      const memberId = event.target.dataset.id;
      unsuspendMemberRequest(memberId).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult('/admin/member/job-seeker');
        }
      })
    }
    if (modal.dataset.role === 'removePhoneVerification') {
      const memberId = event.target.dataset.id;
      removePhoneVerificationRequest(memberId).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult('/admin/member/job-seeker');
        }
      })
    }
    if (modal.dataset.role === 'addPhoneWithVerification') {
      const memberId = event.target.dataset.id;
      const newPhone = document.getElementById('addPhoneWithVerificationPhone').value;
      const newPhoneVerify = document.getElementById('addPhoneWithVerificationPhoneVerify').value;
      addPhoneWithVerificationRequest(memberId, newPhone, newPhoneVerify).then(response => {
        alert(response.message);
        if (response.result === true) {
          modal.querySelector('.btn-close').click();
          loadResult('/admin/member/job-seeker');
        }
      })
    }
  })
});


async function suspendMemberRequest(memberId, reason) {
  try {
    const response = await fetch(
      `/admin/member/job-seeker/${memberId}/suspend`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: reason
      }
    );
    return await response.json();
  } catch (e) {
    console.log(e);
  }
}

async function unsuspendMemberRequest(memberId) {
  console.log('unsuspendMemberRequest');
  try {
    const response = await fetch(
      `/admin/member/job-seeker/${memberId}/unsuspend`,
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

async function removePhoneVerificationRequest(memberId) {
  console.log('removePhoneVerificationRequest');
  try {
    const response = await fetch(
      `/admin/member/job-seeker/${memberId}/remove-phone-verification`,
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

async function addPhoneWithVerificationRequest(memberId, newPhone, newPhoneVerify) {
  console.log('addPhoneWithVerificationRequest');
  try {
    const response = await fetch(
      `/admin/member/job-seeker/${memberId}/add-phone-with-verification`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify({
          phone: newPhone,
          phoneVerify: newPhoneVerify
        })
      }
    );
    return await response.json();
  } catch (e) {
    console.log(e);
  }
}