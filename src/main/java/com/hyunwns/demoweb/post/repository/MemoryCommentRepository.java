package com.hyunwns.demoweb.post.repository;

import com.hyunwns.demoweb.post.domain.Comment;
import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryCommentRepository implements CommentRepository {

    private static Map<Long, Comment> comments = new ConcurrentHashMap<>();
    private Long id = 0L;

    @Override
    public synchronized void save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(++id);
        }
        comments.put(comment.getId(), comment);

        // 하위 댓글일 경우 부모 댓글의 자식 리스트에 해당 댓글 추가
        if (comment.getParent() != null) {
            comment.getParent().getChildren().add(comment);
        }

        Post post = comment.getPost();
        post.addComment(comment);

    }

    // DELETE 할 때, 멤버 및 포스터의 댓글 목록에서도 제거 해주어야함
    // 멤버 댓글 리스트에 넣는 로직 넣었는지는 기억이 잘 안나긴하는데 아마 안넣었을 걸?
    // 근데 POST의 댓글 리스트에서는 제거 해주어야함

    @Override
    public void delete(Comment comment) {
        Long id = comment.getId();
        comment.getPost().getComments().remove(comment);

        // 부모 댓글이 있을 때 부모 댓글에서도 제거 해줘야함
        if (comment.getParent() != null) {
            comment.getParent().getChildren().remove(comment);
        }

        comments.remove(id);

    }

    // 모든 댓글 객체 가져오기
    @Override
    public List<Comment> findAll() {
        return new ArrayList<>(comments.values());
    }

    // 게시글 별 댓글 리스트 가져오기
    @Override
    public List<Comment> findByPost(Post post) {
        return post.getComments();
    }

    // 작성자 별 댓글 리스트 가져오기
    @Override
    public List<Comment> findByCommenter(Member commenter) {
        return commenter.getComments();
    }

    // 단일 댓글 가져오기
    @Override
    public Comment findById(Long id) {
        return comments.get(id);
    }

}
