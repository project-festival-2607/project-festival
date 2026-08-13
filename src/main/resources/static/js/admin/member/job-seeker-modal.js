// ##############################################
// 관리 메뉴
// ##############################################

document.querySelector('.result-table-wrapper').addEventListener('click', (event) => {

  console.log(event.target.dataset.role);

  if (event.target.dataset.role === 'suspendMember') {
    const member = event.target.closest('tr');
    const id = member.querySelector('[data-type="id"]').textContent;
    const username = member.querySelector('[data-type="username"]').textContent;
    const name = member.querySelector('[data-type="name"]').textContent;
    const phone = member.querySelector('[data-type="phone"]').textContent;
    const email = member.querySelector('[data-type="email"]').textContent;
    const lastLoginAt = member.querySelector('[data-type="lastLoginAt"]').textContent;

    document.getElementById('suspendMemberUsername').textContent = username;
    document.getElementById('suspendMemberName').textContent = name;
    document.getElementById('suspendMemberPhone').textContent = phone;
    document.getElementById('suspendMemberEmail').textContent = email;
    document.getElementById('suspendMemberLastLoginAt').textContent = lastLoginAt;

    document.getElementById('suspendMemberSubmit').dataset.id = id;
  }
})

document.querySelector('.modal-footer').addEventListener('click', (event) => {
  console.log(event.target.id);
  if (event.target.id === 'suspendMemberSubmit') {
    const memberId = event.target.dataset.id;
    const reason = document.getElementById('suspensionReason').value;
    suspendMemberRequest(memberId, reason).then(response => {
      alert(response.message);
      if (response.result === true) {
        document.querySelector(".btn-close").click();
      }
    })
  }
})

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