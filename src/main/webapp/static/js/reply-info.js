function reply_info(comment) {
    const commentDiv = document.createElement("div");
    commentDiv.className = "reply";
    commentDiv.style.position = "relative";

    /* 아이콘 ID 부분 */
    const commentUserDiv = document.createElement("div");
    commentUserDiv.className = "reply-user";

    const commentUserIcon = document.createElement("img");
    commentUserIcon.className = "reply-icon";
    commentUserIcon.src = `/icon/${comment.commenterId}`;

    const commentIdP = document.createElement("p");
    commentIdP.className = "reply-id";
    commentIdP.textContent = comment.commenterId;

    commentUserDiv.appendChild(commentUserIcon);
    commentUserDiv.appendChild(commentIdP);

    /* 댓글 내용 */
    const commentContentDiv = document.createElement("div");
    commentContentDiv.className = "reply-content";

    const commentContentP = document.createElement("span");
    commentContentP.className = "reply-content-p";
    commentContentP.textContent = comment.content;

    commentContentDiv.appendChild(commentContentP);

    // 본인 댓글인 경우만 더보기 버튼 추가
    if (comment.commenterId === id) {
        const moreDiv = document.createElement("div");
        moreDiv.className = "reply-more-container";

        const commentPlus = document.createElement("span");
        commentPlus.className = "reply-more";
        commentPlus.textContent = "⋯";

        const menuBox = document.createElement("div");
        menuBox.className = "reply-menu";

        function createMenuItem(label, onClick) {
            const item = document.createElement("div");
            item.className = "reply-menu-item";
            item.textContent = label;
            item.addEventListener("click", (event) => {
                menuBox.style.display = "none";
                onClick(event);
            });
            return item;
        }

        // 삭제 버튼 추가
        menuBox.appendChild(createMenuItem("삭제", () => {
            fetch(`/comment/${comment.id}/delete`, { method: 'GET' })
                .then(response => {
                    if (response.ok) {
                        loadComment();
                    } else {
                        console.error('실패:', response.status);
                    }
                })
                .catch(err => console.error('에러', err));
        }));

        // 수정 버튼 추가
        menuBox.appendChild(createMenuItem("수정", () => {

            const isExistEditForm = commentDiv.querySelector(".reply-editForm");
            if (isExistEditForm) {
                return;
            }

            fetch(`/comment/${comment.id}/edit`, { method: 'GET' })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`응답 에러: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    const editForm = document.createElement("form");
                    editForm.className = "reply-editForm";

                    const editContent = document.createElement("input");
                    const editButton = document.createElement("button");
                    editButton.textContent = ">";

                    editContent.value = data.content;

                    editForm.appendChild(editContent);
                    editForm.appendChild(editButton);
                    commentDiv.appendChild(editForm);

                    editForm.addEventListener("submit", (event) => {
                        event.preventDefault();

                        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
                        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

                        fetch(`/comment/edit`, {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                [csrfHeader]: csrfToken
                            },
                            body: JSON.stringify({
                                content: editContent.value,
                                id: data.id
                            })
                        })
                            .then(response => {
                                if (response.ok) {
                                    loadComment();
                                } else {
                                    console.error("댓글 수정 실패", response.status);
                                }
                            })
                            .catch(error => console.error("댓글 수정 실패", error));
                    });
                })
                .catch(err => console.error('에러', err));
        }));

        commentPlus.addEventListener("click", (event) => {
            event.stopPropagation();
            const isOpen = (menuBox.style.display === "block");
            closeAllDropdownsReply();
            menuBox.style.display = isOpen ? "none" : "block";
        });

        moreDiv.appendChild(commentPlus);
        moreDiv.appendChild(menuBox);
        commentContentDiv.appendChild(moreDiv);
    }

    commentDiv.appendChild(commentUserDiv);
    commentDiv.appendChild(commentContentDiv);

    return commentDiv;
}

document.addEventListener("click", (event) => {
    // event.target reply-more-container 안에 있는지 확인
    const insideDropdownArea = event.target.closest(".reply-more-container");

    if (!insideDropdownArea) {
        closeAllDropdownsReply();
    }
});

function closeAllDropdownsReply() {
    const allMenus = document.querySelectorAll(".reply-menu");

    allMenus.forEach(menu => {
        menu.style.display = "none";
    });
}