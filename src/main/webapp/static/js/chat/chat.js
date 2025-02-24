const messagesDiv = document.getElementById("messages");
const messageInput = document.getElementById("message-input");
const sendForm = document.getElementById("send-form");

const uuid = document.getElementById("room-uuid").value;

const ws = new WebSocket(`ws://localhost:8080/chats?uuid=${uuid}`);
const id = document.getElementById("user-info").dataset.id;

ws.onclose = () => {
    alert("세션이 종료되었습니다. 다시 로그인해주세요.");
    window.location.href = "/";
}

// 웹소켓으로 메시지 받았을 때
ws.onmessage = (event) => {

    const data = JSON.parse(event.data);
    let box = null;

    if (data.code === 100) {
        box = createMessageBox(`${data.id}님이 입장하였습니다.`, "enter");
    } else if (data.code === 101) {
        box = createMessageBox(`${data.id}님이 퇴장하였습니다.`, "exit");
    } else if (data.code === 200) {
        box = createMessageBox(data, "received");
    }

    messagesDiv.appendChild(box);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;

};


// 전송 버튼 눌렀을 때
sendForm.addEventListener("submit", async (event) => {
    event.preventDefault()

    const message = messageInput.value.trim();
    const images = await convertFilesToBase64(dataTransfer.files);

    if (message === "" && !( Array.isArray(images) && images.length > 0) ) {
        return;
    }
    const messageForm = {
        message: message,
        image: images
    }
    ws.send(JSON.stringify(messageForm));

    const box = createMessageBox(messageForm, "send");
    messagesDiv.appendChild(box);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;

    messageInput.value = "";
    previewContainer.innerHTML = "";
    for (let i = dataTransfer.files.length - 1; i >= 0; i--) {
        dataTransfer.items.remove(i);
    }
    addPreviewImage();

});

function convertFilesToBase64(files) {
    return Promise.all( // 모든 파일이 변환될 때까지 기다림
        Array.from(files).map(file =>  // 파일 목록을 순회하며 변환
            new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => resolve(reader.result);
                reader.onerror = reject;
            })
        )
    );
}

function createMessageBox(data, type) {
    if (type === 'send') {

        const sendBox = document.createElement("div");
        sendBox.className = "sendBox";

        /* 아이콘 및 유저 이름 */
        const user = document.createElement("div");
        user.className = "user-container"

        const userIcon = document.createElement("img");
        userIcon.className = "chat-icon"
        userIcon.src = `/icon/${id}`;

        const userId = document.createElement("p");
        userId.className = "chat-id";
        userId.textContent = id;

        user.appendChild(userIcon);
        user.appendChild(userId);

        sendBox.appendChild(user);
        /* ------------ */
        /* 채팅 내용 */
        if (data.message !== "") {
            const message = document.createElement("div");
            message.className = "message";
            message.textContent = data.message;

            sendBox.appendChild(message);
        }
        /* ------------ */
        if (Array.isArray(data.image) && data.image.length > 0) {
            const imageContainer = document.createElement("div");
            imageContainer.className = "imageContainer";

            data.image.forEach(base64Data => {
                const img = document.createElement("img");
                img.src = base64Data;

                imageContainer.appendChild(img);
        })
            sendBox.appendChild(imageContainer);
        }

        return sendBox;

    } else if (type === 'received') {

        const receivedBox = document.createElement("div");
        receivedBox.className = "receivedBox";

        /* 아이콘 및 유저 이름 */
        const user = document.createElement("div");
        user.className = "user-container";

        const userIcon = document.createElement("img");
        userIcon.className = "chat-icon";
        userIcon.src = `/icon/${data.id}`;

        const userId = document.createElement("p");
        userId.className = "chat-id";
        userId.textContent = data.id;

        user.appendChild(userIcon);
        user.appendChild(userId);

        receivedBox.appendChild(user);
        /* ------------ */
        /* 채팅 내용 */
        if (data.message !== "") {
            const message = document.createElement("div");
            message.className = "message";
            message.textContent = data.message;

            receivedBox.appendChild(message);
        }
        if (Array.isArray(data.images) && data.images.length > 0) {
            const imageContainer = document.createElement("div");
            imageContainer.className = "imageContainer";

            data.images.forEach(base64Data => {
                const img = document.createElement("img");
                img.src = base64Data;

                imageContainer.appendChild(img);
        })
            receivedBox.appendChild(imageContainer);
        }

        /* ------------ */
        return receivedBox;

    } else {
        const messageBox = document.createElement("div");
        messageBox.className = "messageBox";
        messageBox.textContent = data;

        return messageBox;
    }
}