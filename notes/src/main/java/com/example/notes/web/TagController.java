package com.example.notes.web;

import com.example.notes.tag.Tag;
import com.example.notes.tag.TagService;
import com.example.notes.user.CurrentUserResolver;
import com.example.notes.web.dto.ApiResponse;
import com.example.notes.web.dto.TagCreateRequest;
import com.example.notes.web.dto.TagResponse;
import com.example.notes.web.mapper.TagMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final CurrentUserResolver currentUserResolver;

    public TagController(TagService tagService, TagMapper tagMapper, CurrentUserResolver currentUserResolver) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> create(@AuthenticationPrincipal Object principal,
                              @RequestBody @Valid TagCreateRequest req) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Tag tag = tagService.createTag(ownerId, req.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(tagMapper.toResponse(tag)));
    }

    @GetMapping
    public ApiResponse<List<TagResponse>> list(@AuthenticationPrincipal Object principal) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        return ApiResponse.ok(tagService.listTags(ownerId).stream()
                .map(tagMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<TagResponse> get(@AuthenticationPrincipal Object principal,
                                        @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        Tag tag = tagService.getOwnedTagOrThrow(ownerId, id);
        return ApiResponse.ok(tagMapper.toResponse(tag));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Object principal,
                       @PathVariable Long id) {
        Long ownerId = currentUserResolver.resolveUserId(principal);
        tagService.deleteTag(ownerId, id);
        return ApiResponse.ok(null);
    }
}
