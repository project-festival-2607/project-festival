const dropdown = document.getElementById("searchTypeDropdown");
const toggle = document.getElementById("searchTypeToggle");
const menu = document.getElementById("searchTypeMenu");
const label = document.getElementById("searchTypeLabel");
const input = document.getElementById("searchTypeInput");

toggle.addEventListener("click", () => {
    dropdown.classList.toggle("open");
});

menu.querySelectorAll("li").forEach((item) => {
    item.addEventListener("click", () => {
        input.value = item.dataset.value;
        label.textContent = item.textContent;
        menu.querySelectorAll("li").forEach((li) => li.classList.remove("active"));
        item.classList.add("active");
        dropdown.classList.remove("open");
    });
});

document.addEventListener("click", (e) => {
    if (!dropdown.contains(e.target)) dropdown.classList.remove("open");
});