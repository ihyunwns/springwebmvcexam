const openModal = document.querySelector(".create-room-btn");
const cancel = document.querySelector(".cancel-btn");
const pagination = document.getElementById("pagination");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const page = parseInt(document.getElementById("page").dataset.page);
const size = parseInt(document.getElementById("size").dataset.size);
const totalElements = parseInt(document.getElementById("totalElements").dataset.totalElements);
const totalPages = parseInt(document.getElementById("totalPages").dataset.totalPages);

const elementsPerPage = 5;

window.onload = () => {
    const joinBtn = document.querySelectorAll(".join-btn");
    const deleteBtn = document.querySelectorAll(".delete-btn");

    joinBtn.forEach(button => {
        button.addEventListener("click", (event) => {
            const uuid = event.target.dataset.uuid;
            window.location.href = `/chat/enter?uuid=${uuid}`;
        })
    })

    deleteBtn.forEach(button => {
        button.addEventListener("click", (event) => {

            const uuid = event.target.dataset.uuid;
            fetch("/chat/delete", {
                method: "POST",
                headers: {
                    [csrfHeader]: csrfToken,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    uuid: uuid
                })
            }).then(response => {
                if (response.ok) {
                    window.location.href = "/waiting";
                }
            });
        })
    })

    const previous = document.createElement("a");
    previous.textContent = "<";
    previous.href = `/waiting?page=${page-1}&size=${elementsPerPage}`;
    if (page <= 1) {
        previous.classList.add("disabled");
    }

    const first = document.createElement("a");
    first.textContent = "<<";
    first.href = `/waiting?page=1&size=${elementsPerPage}`;
    if (page === 1) {
        first.classList.add("disabled");
    }


    const next = document.createElement("a");
    next.textContent = ">";
    next.href = `/waiting?page=${page+1}&size=${elementsPerPage}`;
    if (page >= totalPages) {
        next.classList.add("disabled");
    }

    const end = document.createElement("a");
    end.textContent = ">>";
    end.href = `/waiting?page=${totalPages}&size=${elementsPerPage}`;
    if (page === totalPages) {
        end.classList.add("disabled");
    }

    let startPage = Math.floor(((page - 1) / 9)) * 9 + 1;
    let endPage = Math.min(startPage + 8, totalPages);

    pagination.appendChild(first); pagination.appendChild(previous);
    for (let i = startPage; i <= endPage; i++) {
        const a = document.createElement("a");
        a.textContent = `${i}`;
        if (i === page) {
            a.classList.add("active");
        } else {
            a.href = `/waiting?page=${i}&size=${elementsPerPage}`;
        }
        pagination.appendChild(a);
    }
    pagination.appendChild(next); pagination.appendChild(end);

}

openModal.addEventListener("click", (event) => {
    document.getElementById("roomModal").style.display = "block";

    event.stopPropagation();
})

cancel.addEventListener("click", () => {
    document.getElementById("roomModal").style.display = "none";
});

document.addEventListener("click", (event) => {
    if (document.getElementById("roomModal").style.display === "block") {
        const isInModal = event.target.closest(".modal-content");

        if (!isInModal) {
            document.getElementById("roomModal").style.display = "none";
        }
    }
});