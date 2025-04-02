document.addEventListener("DOMContentLoaded", function () {
    let sections = document.querySelectorAll("section");
    let navLinks = document.querySelectorAll(".nav-link");

    function setActiveLink() {
        let scrollPosition = window.scrollY + 100;

        sections.forEach((section) => {
            if (scrollPosition >= section.offsetTop && scrollPosition < section.offsetTop + section.offsetHeight) {
                let id = section.getAttribute("id");

                navLinks.forEach((link) => {
                    link.classList.toggle("active", link.getAttribute("href") === `#${id}`);
                });
            }
        });
    }

    window.addEventListener("scroll", setActiveLink);
    navLinks.forEach((link) => {
        link.addEventListener("click", function () {
            navLinks.forEach((nav) => nav.classList.remove("active"));
            this.classList.add("active");
        });
    });

    setActiveLink(); 
});






const roles = ["a Passionate learner", "an Explorer of new Technologies", "a Great Coder", "an Efficient Team Player"];
let roleIndex = 0;
let charIndex = 0;
const roleText = document.getElementById("role-text");

function typeEffect() {
    if (charIndex < roles[roleIndex].length) {
        roleText.innerHTML += roles[roleIndex].charAt(charIndex);
        charIndex++;
        setTimeout(typeEffect, 100);
    } else {
        setTimeout(eraseEffect, 1500);
    }
}

function eraseEffect() {
    if (charIndex > 0) {
        roleText.innerHTML = roles[roleIndex].substring(0, charIndex - 1);
        charIndex--;
        setTimeout(eraseEffect, 50);
    } else {
        roleIndex = (roleIndex + 1) % roles.length;
        setTimeout(typeEffect, 500);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    setTimeout(typeEffect, 500);
});


