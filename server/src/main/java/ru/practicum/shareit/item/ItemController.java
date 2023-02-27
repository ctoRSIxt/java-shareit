package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@RestController
@RequestMapping(path = "/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") long userId,
                         @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(value = "from", defaultValue = "0")
                                             int from,
                                             @RequestParam(value = "size", defaultValue = "10")
                                             int size) {
        return itemService.findAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text,
                                    @RequestParam(value = "from", defaultValue = "0")
                                    int from,
                                    @RequestParam(value = "size", defaultValue = "10")
                                    int size) {
        return itemService.findByText(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }

}
