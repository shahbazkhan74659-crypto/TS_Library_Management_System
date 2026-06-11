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
// SEARCH
// =======================

const searchInput =
    document.querySelector(".search-input");

if(searchInput){

    searchInput.addEventListener(
        "keyup",
        function(){

            const filter =
                this.value.toLowerCase();

            const rows =
                document.querySelectorAll(
                    "tbody tr"
                );

            rows.forEach(row => {

                const text =
                    row.textContent
                       .toLowerCase();

                row.style.display =
                    text.includes(filter)
                    ? ""
                    : "none";
            });
        }
    );
}

