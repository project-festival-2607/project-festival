console.log("LIST IN");

document.querySelector(".topBtn").addEventListener("click", (e) => {
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
});

const ImageHoverAnimation = () => {
    document.querySelectorAll(".fes-img").forEach(img => {
        img.addEventListener("mousemove", (e) => {
            const rect = img.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const centerX = rect.width / 2;
            const centerY = rect.height / 2;

            const rotateY = ((x - centerX) / centerX) * 5;
            const rotateX = -((y - centerY) / centerY) * 5;

            img.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.05)`;
        });

        img.addEventListener("mouseleave", () => {
            img.style.transform = "perspective(1000px) rotateX(0) rotateY(0) scale(1)";
        });
    });
};

ImageHoverAnimation();

// monthBtn
for(let i = 1; i <=12; i ++){
    document.querySelector(".monthBtn").innerHTML += `<button class="month" value="${i}"
                                                                    onclick="clickMonthBtn(event)"
                                                                    >${i}월</button>`;
}

const clickMonthBtn = (e) => {

    const selectedBtn = e.target;

    document.querySelectorAll(".month").forEach(m => m.classList.remove("active"));
    selectedBtn.classList.add("active");

    loadFestivalMonth(selectedBtn.value);
}

async function loadFestivalMonth(month) {
    const response = await fetch(`/api/festival/list?page=1&month=${month}`)
    const datas = await response.json();

    renderMonthFes(datas.content);
}

const renderMonthFes = (data) => {
    const container = document.querySelector(".fesContainer");
    container.innerHTML = '';

    if(!data || data.length === 0){
        container.innerHTML = '<div>해당 달에 등록된 축제가 없습니다.</div>'
        return;
    }

    let html = '';

    data.forEach((fes) => {

            let statusClass = 'past';
            let statusText = '종료';

            if (fes.status === '진행예정') {
                statusClass = 'upComing';
                statusText = '진행예정';
            } else if (fes.status === '진행중') {
                statusClass = 'onGoing';
                statusText = '진행중';
            } else if (fes.status === '종료된행사' || fes.status === '종료') {
                statusClass = 'past';
                statusText = '종료';
            }

            html += `
            <div class="fes-box">
                <div class="fes-pro ${statusClass}">${statusText}</div>

                <div class="fes-img" style="background-image: url('${fes.firstImage || ''}')"></div>
                
                <div class="fes-title">
                    <a href="/festival/detail?id=${fes.contentId}">
                        ${fes.title}
                    </a>
                </div>
                
                <div class="fes-date">
                    <span>${fes.startDate} ~ ${fes.endDate}</span>
                </div>
            </div>
        `;

        container.innerHTML = html;
    });

    ImageHoverAnimation();

}