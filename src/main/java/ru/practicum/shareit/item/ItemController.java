package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@RestController
@RequestMapping(path = "/items")
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
    public List<ItemDto> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text);
    }

}
