const syncBtn = document.querySelector(".animal-syncBtn");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

syncBtn.addEventListener("click", () => {

    fetch('/animal/syncData', {
        method: "POST",
        headers:
            {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken // CSRF 헤더 추가
            },
        body: JSON.stringify({

            })
        }).then( response => {
            if( response.ok ){
                console.log("ok");
            }
        })
})

