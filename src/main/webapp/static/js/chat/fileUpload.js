const inputFile = document.getElementById("image-input");
const previewContainer = document.getElementById("preview-container");
const dragdrop = document.getElementById("dragdrop-container");
const chat = document.getElementById("chat-container");

const dataTransfer = new DataTransfer();

const MAX_IMAGE_COUNT = 6;

["dragenter", "dragover", "dragleave", "drop"].forEach(eventName => {
    document.addEventListener(eventName, (e) => e.preventDefault());
});


chat.addEventListener("dragover", () => {
    dragdrop.classList.add("enter");
    dragdrop.textContent = "이미지를 여기에 드래그 하세요.";
});


chat.addEventListener("dragleave", (event) => {
    if (!chat.contains(event.relatedTarget)) {
        dragdrop.classList.remove("enter");
        dragdrop.textContent = "";
    }
})

chat.addEventListener("drop", () => {
    dragdrop.classList.remove("enter");
    dragdrop.textContent = "";
});

dragdrop.addEventListener("drop", (event) => {
    dragdrop.classList.remove("enter");
    dragdrop.textContent = "";

    const files = Array.from(event.dataTransfer.files);
    if (files.length > 0) {
        if ( !checkCountImage(files.length)) {
            alert(`이미지 최대 전송 개수는 ${MAX_IMAGE_COUNT}장 입니다`);
            return;
        }

        files.forEach(file => {
            if (file.type.startsWith("image/")) {
                dataTransfer.items.add(file);
            }
        })
        addPreviewImage();
    }
});


inputFile.addEventListener("change", () => {

    let fileArr = Array.from(inputFile.files);

    if (fileArr != null && fileArr.length > 0) {
        if ( !checkCountImage(fileArr.length)) {
            alert(`이미지 최대 전송 개수는 ${MAX_IMAGE_COUNT}장 입니다`);
            return;
        }
        fileArr.forEach(file => {
            if (file.type.startsWith("image/")) {
                dataTransfer.items.add(file);
            }
        })
    }

    inputFile.value = "";
    addPreviewImage();

});

function checkCountImage(count) {
    let countImage = count + dataTransfer.files.length;

    return countImage <= MAX_IMAGE_COUNT;


}

function addPreviewImage() {
    previewContainer.innerHTML = "";

    Array.from(dataTransfer.files).forEach((file, index) => {
        const reader = new FileReader();

        reader.onload = (e) => {
            const wrapper = document.createElement("div");
            wrapper.classList.add("preview-wrapper");

            const img = document.createElement("img");
            img.className = "preview-img";
            img.src = e.target.result;

            const overlay = document.createElement("div");
            overlay.classList.add("overlay");

            const closeBtn = document.createElement("div");
            closeBtn.classList.add("close-btn");
            closeBtn.innerHTML = "X";

            closeBtn.addEventListener("click", () => {
                dataTransfer.items.remove(index);
                addPreviewImage();
            });

            overlay.appendChild(closeBtn);
            wrapper.appendChild(img);
            wrapper.appendChild(overlay);

            previewContainer.appendChild(wrapper);
        }
        reader.readAsDataURL(file);
    })
}

