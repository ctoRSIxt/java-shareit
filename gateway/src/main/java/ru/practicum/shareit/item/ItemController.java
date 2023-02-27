package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}", itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        log.info("Patching item {}", itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "from", defaultValue = "0")
                                                      @Min(value = 0) int from,
                                                      @RequestParam(value = "size", defaultValue = "10")
                                                      @Min(value = 1) int size) {
        log.info("Getting all items by owner with id={}, from={}, size={}", userId, from, size);
        return itemClient.findAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Getting item with id={}", itemId);
        return itemClient.findById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam String text,
                                             @RequestParam(value = "from", defaultValue = "0")
                                             @Min(value = 0) int from,
                                             @RequestParam(value = "size", defaultValue = "10")
                                             @Min(value = 1) int size) {
        log.info("Finding item containing \"{}\" from={}, size={}", text, from, size);
        return itemClient.findByText(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable long itemId,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Adding comment to item with id={}", itemId);
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
