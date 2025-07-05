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

    @GetMapping("/{postId}")
    public String getComment(@PathVariable Integer postId, Model model){
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("postId",postId);
        model.addAttribute("comments", comments);
        return "view-comments";
    }

    @PostMapping("/add")
    public String addComment(@RequestParam("postId") Integer postId,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("content") String content){
        commentService.addComment(postId,name,email,content);
        return "redirect:/comments/" + postId;
    }

    @PostMapping("/delete")
    public String deleteComment(@RequestParam("id") Integer id,
                                @RequestParam("postId") Integer postId){
        commentService.deleteById(id);
        return "redirect:/comments/" + postId;
    }
    @GetMapping("/edit")
    public String editComment(@RequestParam("id") Integer id, Model model){
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment",comment);
        return "edit-comment";
    }

    @PostMapping("/update")
    public String updateComment(@ModelAttribute Comment comment){
        commentService.updateComment(comment);
        return "redirect:/comments/" + comment.getPost().getId();
    }
}
