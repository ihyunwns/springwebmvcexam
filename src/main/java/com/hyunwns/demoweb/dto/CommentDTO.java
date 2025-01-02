package com.hyunwns.demoweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentDTO {
    // 순환 참조를 해결하기 위한 DTO 객체
    // DTO 에서는 필요한 데이터를 작게 쪼개어 보내는 것이 좋다.

    // 예를 들어 작성자 정보를 포함해야 하기 때문에 Member 객체를 그대로 사용할 수 있지만
    // Member 객체에는 다시 댓글 객체가 있고 댓글 객체에는 다시 Member 객체가 있기 때문에 순환 참조 문제가 발생한다.
    // 이를 방지하기 위해서 Member 객체중 필요한 데이터를 세분화하여 보내는 것이 중요하다.

    // 이를 위해 MemberDTO 객체도 따로 만들어서 MemberDTO 객체를 보내는 것도 방법일 것 같은데 ?

    private String commenterId;
    private String content;
    private Long postId;

    // 삭제를 위해 CommentID도 필요할 듯
    private Long id;

}
