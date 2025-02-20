const openModal = document.querySelector(".create-room-btn");
const cancel = document.querySelector(".cancel-btn");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

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