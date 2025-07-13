package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.Comment;
import com.utkarsh.blog.services.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
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
                                @RequestParam("postId") Integer postId){
        commentService.deleteById(commentId);
        return "redirect:/comments/post/" + postId;
    }

    @GetMapping("/{commentId}/edit")
    public String editComment(@PathVariable Integer commentId, Model model){
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment",comment);
        return "comments/edit";
    }

    @PostMapping("/update")
    public String updateComment(@ModelAttribute Comment comment){
        commentService.updateComment(comment);
        return "redirect:/comments/post/" + comment.getPost().getId();
    }
}
