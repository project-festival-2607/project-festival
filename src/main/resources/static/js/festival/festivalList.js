console.log("LIST IN");

document.querySelectorAll(".fes-img").forEach(img => {

    img.addEventListener("mousemove", (e) => {

        const rect = img.getBoundingClientRect();

        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        const centerX = rect.width / 2;
        const centerY = rect.height / 2;

        const rotateY = ((x - centerX) / centerX) * 5;
        const rotateX = -((y - centerY) / centerY) * 5;

        img.style.transform =
            `perspective(1000px)
             rotateX(${rotateX}deg)
             rotateY(${rotateY}deg)
             scale(1.05)`;
    });

    img.addEventListener("mouseleave", () => {
        img.style.transform =
            "perspective(1000px) rotateX(0) rotateY(0) scale(1)";
    });

});

document.querySelector(".topBtn").addEventListener("click", (e) => {
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
});


// monthBtn
for(let i = 1; i <=12; i ++){
    document.querySelector(".monthBtn").innerHTML += `<button class="month" value="${i}">${i}월</button>`;
}