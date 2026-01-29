package com.example.notes.web;

import com.example.notes.tag.Tag;
import com.example.notes.tag.TagService;
import com.example.notes.user.AppUserDetails;
import com.example.notes.web.dto.ApiResponse;
import com.example.notes.web.dto.TagCreateRequest;
import com.example.notes.web.dto.TagResponse;
import com.example.notes.web.mapper.TagMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tag endpoints for create/list/delete.
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    public TagController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }

    @PostMapping
    public ApiResponse<TagResponse> create(@AuthenticationPrincipal AppUserDetails principal,
                              @RequestBody TagCreateRequest req) {
        Tag tag = tagService.createTag(principal.getId(), req.name());
        return ApiResponse.ok(tagMapper.toResponse(tag));
    }

    @GetMapping
    public ApiResponse<List<TagResponse>> list(@AuthenticationPrincipal AppUserDetails principal) {
        return ApiResponse.ok(tagService.listTags(principal.getId()).stream()
                .map(tagMapper::toResponse)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal AppUserDetails principal,
                       @PathVariable Long id) {
        tagService.deleteTag(principal.getId(), id);
        return ApiResponse.ok(null);
    }
}
