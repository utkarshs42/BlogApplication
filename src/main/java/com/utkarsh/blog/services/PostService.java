package com.utkarsh.blog.services;

import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.repositories.PostRepository;
import com.utkarsh.blog.repositories.TagRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository){
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public List<Post> getPosts(){
       List<Post> posts = postRepository.findAll();
       return posts;
    }

    public void addPost(Post post , List<Integer> tagIds) {
        post.setPublishedAt(new Date());
        post.setPublished(true);
        post.setCreatedAt(new Date());

        List<Tag> tags = new ArrayList<>();
        for(int tagId : tagIds){
            Optional<Tag> tagOptional = tagRepository.findById(tagId);
            if(tagOptional.isPresent()){
                Tag tag = tagOptional.get();
                if(tag.getPosts()==null){
                    tag.setPosts(new ArrayList<>());
                }
                tag.getPosts().add(post);
                tags.add(tag);
            }
        }
        post.setTags(tags);
        postRepository.save(post);
    }

    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    public Post getPostById(Integer id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if(postOptional.isPresent()){
            return postOptional.get();
        }else{
            return null;   //TODO : Throw new post now found exception
        }
    }

    public void updatePost(Post post, List<Integer> selectedTagIds) {
        Post oldPost = postRepository.findById(post.getId())
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

        post.setCreatedAt(oldPost.getCreatedAt());
        post.setUpdatedAt(new Date());
        if(oldPost.getComments() != null){
            post.setComments(oldPost.getComments());
        }

        List<Tag> tags = new ArrayList<>();
        for(int tagId : selectedTagIds){
            Optional<Tag> tagOptional = tagRepository.findById(tagId);
            if(tagOptional.isPresent()){
                Tag tag = tagOptional.get();
                if(tag.getPosts()==null){
                    tag.setPosts(new ArrayList<>());
                }
                tag.getPosts().add(post);
                tags.add(tag);
            }
        }
        post.setTags(tags);
        postRepository.save(post);
    }
}
