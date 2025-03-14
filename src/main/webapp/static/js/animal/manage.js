const syncBtn = document.querySelector(".animal-syncBtn");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const socket = new WebSocket("ws://localhost:8080/ws-progress")
const stompClient = Stomp.over(socket);

const progressBar = document.querySelector(".progress-bar");
const progressFill = document.querySelector(".progress-bar-fill");
const progressKeyword = document.querySelector(".progress-bar-keyword");

syncBtn.style.width = `${syncBtn.offsetWidth}px`;
syncBtn.style.height = `${syncBtn.offsetHeight}px`;
progressBar.style.width = `${syncBtn.offsetWidth}px`;
progressBar.style.height = `${syncBtn.offsetHeight}px`;

window.addEventListener("DOMContentLoaded", () => {
    const isRunning = syncBtn.getAttribute("data-running") === "true";

    if (isRunning) {
        syncBtn.style.display = "none";
        progressBar.style.display = "block";
    } else {
        syncBtn.style.display = "block";
        progressBar.style.display = "none";
    }

});

stompClient.connect({}, function() {

    stompClient.subscribe('/topic/progress', function (message) {

        const data = JSON.parse(message.body);
        const { keyword, progress, isCompleted } = data;

        if (isCompleted === 'true') {
            syncBtn.style.display = "block";
            progressBar.style.display = "none";
            return;
        }

        syncBtn.style.display = "none";
        progressBar.style.display = "block";

        progressKeyword.textContent = keyword;
        progressFill.style.width = `${progress * 100}%`

        console.log("진행 상황:", message.body);
    });
})

syncBtn.addEventListener("click", () => {

    syncBtn.innerHTML = `<span class="loading-spinner"></span>`;

    fetch('/animal/syncData', {
        method: "POST",
        headers:
            {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken // CSRF 헤더 추가
            },
        body: {}
        }).then(response => {
        if (!response.ok) {
            throw new Error(`서버 오류: ${response.status}`);
        }
        })
        .catch(error => {
            console.error("동기화 중 오류 발생:", error);
        });
})
