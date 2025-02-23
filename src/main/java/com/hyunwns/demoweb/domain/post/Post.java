package com.hyunwns.demoweb.domain.post;

import com.hyunwns.demoweb.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Post {

    private Long id;
    private final Member author;
    private final LocalDateTime published;

    private String title;
    private String content;
    private boolean isDeleted;

    private String thumbnailURL;
    private String thumbnailName;

    // 댓글의 답글 포함 전부다 저장하는 건지
    // 이런식으로 순환 참조를 하게 되면 JSON 직렬화 할 때 문제가 발생할 수 있다.
    // 이를 해결하기 위해서는 1. 순환 참조 방지 어노테이션을 사용 @JsonBackReference 등
    // 2. DTO 사용, 엔티티를 그대로 반환하지 않고 필요한 데이터만 포함하는 DTO를 만들어서 사용
    private List<Comment> comments;

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public Post(Member author, String title, String content) {

        this.author = author;
        this.title = title;
        this.content = content;

        published = LocalDateTime.now();
        isDeleted = false;
        comments = new ArrayList<>();

    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

}
