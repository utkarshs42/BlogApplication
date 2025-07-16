package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.Comment;
import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.User;
import com.utkarsh.blog.services.CommentService;
import com.utkarsh.blog.services.PostService;
import com.utkarsh.blog.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private CommentService commentService;
    private PostService postService;
    private UserService userService;

    public CommentController(CommentService commentService, PostService postService, UserService userService){
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/post/{postId}")
    public String getCommentsByPostId(@PathVariable Integer postId, Model model){
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("postId",postId);
        model.addAttribute("comments", comments);
        return "comments/view";
    }

    @PostMapping("/post/{postId}")
    public String addComment(@PathVariable Integer postId,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("content") String content){
        commentService.addComment(postId,name,email,content);
        return "redirect:/comments/post/" + postId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Integer commentId,
                                @RequestParam("postId") Integer postId,
                                Principal principal){
        Post post = postService.getPostById(postId);
        User currentUser = userService.findByEmail(principal.getName());
        if(post.getAuthor().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals("ROLE_ADMIN")){
            commentService.deleteById(commentId);
            return "redirect:/comments/post/" + postId;
        }else{
            return "error/403";
        }
    }

    @GetMapping("/{commentId}/edit")
    public String editComment(@PathVariable Integer commentId, Model model, Principal principal){
        Comment comment = commentService.getCommentById(commentId);
        Integer postId = comment.getPost().getId();

        Post post = postService.getPostById(postId);
        User currentUser = userService.findByEmail(principal.getName());

        if(post.getAuthor().getEmail().equals(currentUser.getEmail()) || currentUser.getRole().equals("ROLE_ADMIN")){
            model.addAttribute("comment",comment);
            return "comments/edit";
        }else{
            return "error/403";
        }
    }

    @PostMapping("/update")
    public String updateComment(@ModelAttribute Comment comment){
        commentService.updateComment(comment);
        return "redirect:/comments/post/" + comment.getPost().getId();
    }
}
