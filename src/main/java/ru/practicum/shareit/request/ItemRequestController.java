package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequestorId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(value = "from", defaultValue = "0")
                                        @Min(value = 0) int from,
                                        @RequestParam(value = "size", defaultValue = "10")
                                        @Min(value = 1) int size) {
        return itemRequestService.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long requestId) {
        return itemRequestService.findById(requestId, userId);
    }

}
