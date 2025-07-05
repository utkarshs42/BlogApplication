package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.services.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tags")
public class TagController {
    private TagService tagService;

    public TagController(TagService tagService){
        this.tagService = tagService;
    }

    @GetMapping("/new")
    public String newTag(Model model){
        Tag tag = new Tag();
        model.addAttribute("tag",tag);
        return "new-tag";
    }

    @PostMapping("/add")
    public String addTag(@ModelAttribute("tag") Tag tag){
        tagService.addTag(tag);
        return "redirect:/posts";
    }
}
