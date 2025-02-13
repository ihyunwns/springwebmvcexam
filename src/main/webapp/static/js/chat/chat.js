const messagesDiv = document.getElementById("messages");
const messageInput = document.getElementById("message-input");
const sendBtn = document.getElementById("send-btn");

const ws = new WebSocket("ws://localhost:8080/chats");
const id = document.getElementById("user-info").dataset.id;

// 웹소켓으로 메시지 받았을 때
ws.onmessage = (event) => {

    const receivedMessage = document.createElement("div");
    receivedMessage.textContent = event.data;
    receivedMessage.className = "receivedMessage";
    messagesDiv.appendChild(receivedMessage);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;

    console.log("message received from server : ", event.data);
};


// 전송 버튼 눌렀을 때
sendBtn.addEventListener("click", () => {
    if (messageInput.value.trim()) {
        ws.send(messageInput.value);

        const sendMessage = document.createElement("div");
        sendMessage.className = "sendMessage";
        sendMessage.textContent = id + ": " + messageInput.value;
        messagesDiv.appendChild(sendMessage);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;

        messageInput.value = "";
    }
});