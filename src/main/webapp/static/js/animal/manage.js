const syncBtn = document.querySelector(".animal-syncBtn");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const socket = new WebSocket("ws://localhost:8080/ws-progress")
const stompClient = Stomp.over(socket);

syncBtn.style.width = `${syncBtn.offsetWidth}px`;
syncBtn.style.height = `${syncBtn.offsetHeight}px`;

stompClient.connect({}, function() {

    stompClient.subscribe('/topic/progress', function (message) {

        const data = JSON.parse(message.body);
        const { keyword, progress } = data;

        syncBtn.textContent = keyword;
        animateProgress(progress);

        console.log("진행 상황:", message.body);
    });
})

syncBtn.addEventListener("click", () => {

    syncBtn.disabled = true;
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
                syncBtn.disabled = false;
                syncBtn.innerHTML = '';
                syncBtn.textContent = '데이터 갱신';
                syncBtn.style.color = "#fff";

                console.log(" 동기화 작업 완료 ");
            }
        })
})

/* 버튼 방식하지 말고 div 방식으로 변경하자 ㅇㅇ */
function animateProgress(target) {
    let currentProgress = 0.0;

    if (Math.abs(currentProgress - target) < 0.01) return; // 너무 미세한 차이는 무시
    currentProgress += (target - currentProgress) * 0.1; // 부드러운 보간 (lerp)

    let progressPercent = (currentProgress * 100).toFixed(2);

    syncBtn.style.background = `linear-gradient(90deg, #3f51b5 ${progressPercent}%, #f9f9f9 ${progressPercent}%)`;
    syncBtn.style.color = '#888888'

    requestAnimationFrame(() => animateProgress(target)); // 부드럽게 이어짐
}
