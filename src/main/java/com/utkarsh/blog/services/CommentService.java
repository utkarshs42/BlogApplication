package com.utkarsh.blog.services;

import com.utkarsh.blog.models.Comment;
import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.repositories.CommentRepository;
import com.utkarsh.blog.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    public CommentService(CommentRepository commentRepository, PostRepository postRepository){
        this.commentRepository= commentRepository;
        this.postRepository = postRepository;
    }

    public List<Comment> getCommentsByPostId(Integer postId) {
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        return comments;
    }

    public void addComment(Integer postId, String name, String email, String content){
        Comment comment = new Comment();
        comment.setName(name);
        comment.setContent(content);
        comment.setEmail(email);
        comment.setCreatedAt(new Date());

        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isPresent()){
            Post post = postOptional.get();
            comment.setPost(post);
        }else{
            // TODO:throw new post not found error
        }
        commentRepository.save(comment);
    }

    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }

    public Comment getCommentById(Integer id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if(commentOptional.isPresent()){
            return commentOptional.get();
        }else{
            return null; //TODO: throw comment not found error
        }
    }

    public void updateComment(Comment comment) {
        Comment existingComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setCreatedAt(existingComment.getCreatedAt());

        comment.setUpdatedAt(new Date());
        commentRepository.save(comment);
    }
}
