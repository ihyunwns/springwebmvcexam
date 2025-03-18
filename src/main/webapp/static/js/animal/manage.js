const syncBtn = document.querySelector(".animal-syncBtn");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const socket = new WebSocket("ws://localhost:8080/ws-progress")
const stompClient = Stomp.over(socket);

const progressBar = document.querySelector(".progress-bar");
const progressFill = document.querySelector(".progress-bar-fill");
const progressKeyword = document.querySelector(".progress-bar-keyword");
const progressSpinner = document.querySelector(".loading-spinner");
const progressContent = document.querySelector(".progress-bar-content");

syncBtn.style.width = `${syncBtn.offsetWidth}px`;
syncBtn.style.height = `${syncBtn.offsetHeight}px`;
progressBar.style.width = `${syncBtn.offsetWidth}px`;
progressBar.style.height = `${syncBtn.offsetHeight}px`;

const isRunning = syncBtn.getAttribute("data-running") === "true";
if (isRunning) {
    syncBtn.classList.add("hidden");
    progressBar.style.display = "block";
    progressSpinner.style.display = "block";
    progressContent.classList.add("hidden");
} else {
    syncBtn.classList.remove("hidden");
    progressBar.style.display = "none";
}


stompClient.connect({}, function() {

    stompClient.subscribe('/topic/progress', function (message) {

        const data = JSON.parse(message.body);
        const { keyword, progress, isCompleted } = data;

        syncBtn.classList.add("hidden");
        progressBar.style.display = "block";

        progressSpinner.style.display = "none";
        progressContent.classList.remove("hidden");

        progressKeyword.textContent = keyword;
        progressFill.style.width = `${progress * 100}%`;

        if (isCompleted) {
            setTimeout( () =>  location.reload(), 500);
        }
    });
})

syncBtn.addEventListener("click", () => {
    syncBtn.classList.add("hidden");
    progressBar.style.display = "block";
    progressSpinner.style.display = "block";
    progressContent.classList.add("hidden");

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
