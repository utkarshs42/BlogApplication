package com.utkarsh.blog.services;

import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public Tag getTagByName(String name){
        Optional<Tag> tagOptional = tagRepository.findByName(name);
        if(tagOptional.isPresent()){
            return tagOptional.get();
        }else{
            Tag tag = new Tag(name, new Date());
            Tag newTag = tagRepository.save(tag);
            return newTag;
        }
    }

    public Tag addTag(Tag tag){
        tag.setCreatedAt(new Date());
        Tag newTag = tagRepository.save(tag);
        return newTag;
    }

    public List<Tag> getTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags;
    }
}
