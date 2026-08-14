// ##############################################
// 관리 메뉴
// ##############################################

document.querySelector('#jobSeekerForm').addEventListener('click', (event) => {

  console.log(event.target.dataset.role);

  if (event.target.dataset.role === 'suspendMember') {
    const member = event.target.closest('tr');
    const id = member.querySelector('[data-type="id"]').textContent;
    const username = member.querySelector('[data-type="username"]').textContent;
    const name = member.querySelector('[data-type="name"]').textContent;
    const phone = member.querySelector('[data-type="phone"]').textContent;
    const email = member.querySelector('[data-type="email"]').textContent;
    const lastLoginAt = member.querySelector('[data-type="lastLoginAt"]').textContent;
    const suspendedReason = member.querySelector('[data-type="suspendedReason"]').textContent;

    document.getElementById('suspendMemberUsername').textContent = username;
    document.getElementById('suspendMemberName').textContent = name;
    document.getElementById('suspendMemberPhone').textContent = phone;
    document.getElementById('suspendMemberEmail').textContent = email;
    document.getElementById('suspendMemberLastLoginAt').textContent = lastLoginAt;
    document.getElementById('suspensionReason').value = suspendedReason;

    document.getElementById('suspendMemberSubmit').dataset.id = id;
  }

  if (event.target.dataset.role === 'unsuspendMember') {
    const member = event.target.closest('tr');
    const id = member.querySelector('[data-type="id"]').textContent;
    const username = member.querySelector('[data-type="username"]').textContent;
    const name = member.querySelector('[data-type="name"]').textContent;
    const suspendedAt = member.querySelector('[data-type="suspendedAt"]').textContent;
    const suspendedReason = member.querySelector('[data-type="suspendedReason"]').textContent;

    document.getElementById('unsuspendMemberUsername').textContent = username;
    document.getElementById('unsuspendMemberName').textContent = name;
    document.getElementById('unsuspendMemberSuspendedAt').textContent = suspendedAt;
    document.getElementById('unsuspendMemberSuspendedReason').textContent = suspendedReason;

    document.getElementById('unsuspendMemberSubmit').dataset.id = id;
  }

  if (event.target.dataset.role === 'removePhoneVerification') {
    const member = event.target.closest('tr');
    const id = member.querySelector('[data-type="id"]').textContent;
    const username = member.querySelector('[data-type="username"]').textContent;
    const name = member.querySelector('[data-type="name"]').textContent;
    const phone = member.querySelector('[data-type="phone"]').textContent;

    document.getElementById('removePhoneVerificationUsername').textContent = username;
    document.getElementById('removePhoneVerificationName').textContent = name;
    document.getElementById('removePhoneVerificationPhone').textContent = phone;

    document.getElementById('removePhoneVerificationSubmit').dataset.id = id;
  }

  if (event.target.dataset.role === 'addPhoneWithVerification') {
    const member = event.target.closest('tr');
    const id = member.querySelector('[data-type="id"]').textContent;
    const username = member.querySelector('[data-type="username"]').textContent;
    const name = member.querySelector('[data-type="name"]').textContent;
    const phone = member.querySelector('[data-type="phone"]').textContent;

    document.getElementById('addPhoneWithVerificationUsername').textContent = username;
    document.getElementById('addPhoneWithVerificationName').textContent = name;
    document.getElementById('addPhoneWithVerificationOldPhone').textContent = phone;

    document.getElementById('addPhoneWithVerificationSubmit').dataset.id = id;
  }
})

document.querySelectorAll('.modal-footer').forEach(element => {
  element.addEventListener('click', (event) => {
    console.log(event.target.id);
    if (event.target.id === 'suspendMemberSubmit') {
      const memberId = event.target.dataset.id;
      const reason = document.getElementById('suspensionReason').value;
      suspendMemberRequest(memberId, reason).then(response => {
        alert(response.message);
        if (response.result === true) {
          event.target.closest('.modal').querySelector('.btn-close').click();
          loadJobSeekerResult(jobSeekerResultUrl());
        }
      })
    }
    if (event.target.id === 'unsuspendMemberSubmit') {
      const memberId = event.target.dataset.id;
      unsuspendMemberRequest(memberId).then(response => {
        alert(response.message);
        if (response.result === true) {
          event.target.closest('.modal').querySelector('.btn-close').click();
          loadJobSeekerResult(jobSeekerResultUrl());
        }
      })
    }
    if (event.target.id === 'removePhoneVerificationSubmit') {
      const memberId = event.target.dataset.id;
      removePhoneVerificationRequest(memberId).then(response => {
        alert(response.message);
        if (response.result === true) {
          event.target.closest('.modal').querySelector('.btn-close').click();
          loadJobSeekerResult(jobSeekerResultUrl());
        }
      })
    }
    if (event.target.id === 'addPhoneWithVerificationSubmit') {
      const memberId = event.target.dataset.id;
      const newPhone = document.getElementById('addPhoneWithVerificationPhone').value;
      const newPhoneVerify = document.getElementById('addPhoneWithVerificationPhoneVerify').value;
      addPhoneWithVerificationRequest(memberId, newPhone, newPhoneVerify).then(response => {
        alert(response.message);
        if (response.result === true) {
          event.target.closest('.modal').querySelector('.btn-close').click();
          loadJobSeekerResult(jobSeekerResultUrl());
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