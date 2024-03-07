function closeSession() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('username');
    localStorage.removeItem('loggedIn');
    window.location.href = "/auth/logout";
}

function toggleTheme(el) {
    const html = document.documentElement;
    if(html.getAttribute("data-theme")==null || html.getAttribute("data-theme")=="dark"){
        html.setAttribute("data-theme","light");
        el.innerText = '🌙';
    } else {
        html.setAttribute("data-theme","dark");
        el.innerText = '🌞';
    }
}