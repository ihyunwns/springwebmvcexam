const openModal = document.querySelector(".create-room-btn");
const cancel = document.querySelector(".cancel-btn");

window.onload = () => {
    const joinBtn = document.querySelectorAll(".join-btn");

    joinBtn.forEach(button => {
        button.addEventListener("click", (event) => {
            const uuid = event.target.dataset.uuid;
            window.location.href = `/chat/enter?uuid=${uuid}`;
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