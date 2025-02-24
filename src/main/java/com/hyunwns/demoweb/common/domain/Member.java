package com.hyunwns.demoweb.common.domain;

import com.hyunwns.demoweb.chat.domain.ChatRoom;
import com.hyunwns.demoweb.post.domain.Comment;
import com.hyunwns.demoweb.post.domain.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Member {

    private final String id;
    private final String nickname;
    private final String password;
    private final int age;

    private final List<Post> posts;
    private final List<Comment> comments;

    private String iconURL;

    private final List<ChatRoom> chatRooms;

    @Setter
    private String role;

    public Member(String id, String nickname, String password, int age) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.age = age;
        this.posts = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.chatRooms = new ArrayList<>();
    }

    public void addPosts(Post post) {
        posts.add(post);
    }
    public void addComments(Comment comment) {
        comments.add(comment);
    }
    public void addChatRoom(ChatRoom chatRoom) { chatRooms.add(chatRoom); }
    public void removeChatRoom(ChatRoom chatRoom) { chatRooms.remove(chatRoom); }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", role='" + role + '\'' +
                '}';
    }
}
