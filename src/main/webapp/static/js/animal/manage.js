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

stompClient.connect({}, function() {

    stompClient.subscribe('/topic/progress', function (message) {

        const data = JSON.parse(message.body);
        const { keyword, progress } = data;

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
        }).then( response => {
            if( response.ok ){
                progressBar.style.display = "none";
                syncBtn.style.display = "block";

                console.log(" 동기화 작업 완료 ");
            }
        })
})
