// =======================
// SIDEBAR TOGGLE (mobile)
// =======================

const menuToggle =
    document.getElementById("menuToggle");

const sidebarNav =
    document.getElementById("sidebarNav");

const sidebarOverlay =
    document.getElementById("sidebarOverlay");

if(menuToggle && sidebarNav && sidebarOverlay){

    function closeSidebar(){
        sidebarNav.classList.remove("active");
        sidebarOverlay.classList.remove("active");
        menuToggle.classList.remove("active");
        menuToggle.setAttribute("aria-expanded", "false");
    }

    function openSidebar(){
        sidebarNav.classList.add("active");
        sidebarOverlay.classList.add("active");
        menuToggle.classList.add("active");
        menuToggle.setAttribute("aria-expanded", "true");
    }

    menuToggle.addEventListener("click", function(){
        sidebarNav.classList.contains("active")
            ? closeSidebar()
            : openSidebar();
    });

    sidebarOverlay.addEventListener("click", closeSidebar);
}


// =======================
// CLOCK
// =======================

const clock =
    document.getElementById("clock");

if(clock){

    function updateClock(){

        clock.textContent =
            new Date().toLocaleString();
    }

    updateClock();

    setInterval(updateClock,1000);
}


// =======================
// SEARCH + PAGINATION
// =======================

const searchInput =
    document.querySelector(".search-input");

const tableBody =
    document.querySelector(".table-container table tbody");

const paginationContainer =
    document.getElementById("pagination");

if(tableBody){

    const pageSize = 15;

    const allRows =
        Array.from(tableBody.querySelectorAll("tr"));

    let currentPage = 1;

    function getMatchingRows(){

        const filter =
            searchInput
                ? searchInput.value.toLowerCase()
                : "";

        return allRows.filter(row =>
            row.textContent
               .toLowerCase()
               .includes(filter)
        );
    }

    function renderTable(){

        const matchingRows = getMatchingRows();

        const totalPages =
            Math.max(1, Math.ceil(matchingRows.length / pageSize));

        if(currentPage > totalPages){
            currentPage = totalPages;
        }

        const start = (currentPage - 1) * pageSize;
        const end = start + pageSize;

        allRows.forEach(row => {
            row.style.display = "none";
        });

        matchingRows
            .slice(start, end)
            .forEach(row => {
                row.style.display = "";
            });

        renderPagination(totalPages);
    }

    function renderPagination(totalPages){

        if(!paginationContainer){
            return;
        }

        paginationContainer.innerHTML = "";

        if(totalPages <= 1){
            return;
        }

        function makeButton(label, page, options){

            options = options || {};

            const btn = document.createElement("button");

            btn.type = "button";
            btn.textContent = label;

            if(options.active){
                btn.classList.add("active");
            }

            if(options.disabled){
                btn.disabled = true;
            }

            btn.addEventListener("click", function(){
                currentPage = page;
                renderTable();
            });

            return btn;
        }

        paginationContainer.appendChild(
            makeButton("‹", currentPage - 1, { disabled: currentPage === 1 })
        );

        for(let page = 1; page <= totalPages; page++){
            paginationContainer.appendChild(
                makeButton(String(page), page, { active: page === currentPage })
            );
        }

        paginationContainer.appendChild(
            makeButton("›", currentPage + 1, { disabled: currentPage === totalPages })
        );
    }

    if(searchInput){

        searchInput.addEventListener("keyup", function(){
            currentPage = 1;
            renderTable();
        });
    }

    renderTable();
}

