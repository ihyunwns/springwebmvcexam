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
sendForm.addEventListener("submit", (event) => {
    event.preventDefault()

    if (messageInput.value.trim()) {
        ws.send(messageInput.value);

        const box = createMessageBox(messageInput.value, "send");

        messagesDiv.appendChild(box);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;

        messageInput.value = "";
    }
});

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

        /* ------------ */
        /* 채팅 내용 */
        const message = document.createElement("div");
        message.className = "message";
        message.textContent = data;
        /* ------------ */

        sendBox.appendChild(user);
        sendBox.appendChild(message);

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

        /* ------------ */
        /* 채팅 내용 */
        const message = document.createElement("div");
        message.className = "message";
        message.textContent = data.message;
        /* ------------ */

        receivedBox.appendChild(user);
        receivedBox.appendChild(message);

        return receivedBox;

    } else {
        const messageBox = document.createElement("div");
        messageBox.className = "messageBox";
        messageBox.textContent = data;

        return messageBox;
    }
}