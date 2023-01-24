package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemJsonTests {

    @Autowired
    private JacksonTester<ItemDto> json;


    @Test
    void testItemDto() throws Exception {

        ItemDto itemDto1 = new ItemDto(
                1L,
                "item",
                "item description",
                true,
                1L,
                null,
                null,
                new ArrayList<>()
        );

        User user1 = new User(1, "user1", "email1@gmail.com");
        User user2 = new User(2, "user2", "email2@gmail.com");

        Comment comment1 = new Comment(
                1L,
                "comment1 text",
                1L,
                user1,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        Comment comment2 = new Comment(
                2L,
                "comment2 text",
                1L,
                user2,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        itemDto1.setComments(List.of(ItemDto.toInnerComment(comment1),
                ItemDto.toInnerComment(comment2)));

        JsonContent<ItemDto> result = json.write(itemDto1);


        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto1.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto1.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto1.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto1.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto1.getRequestId().intValue());

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo((int) itemDto1.getComments().get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemDto1.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemDto1.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(itemDto1.getComments().get(0).getCreated().toString());


        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id")
                .isEqualTo((int) itemDto1.getComments().get(1).getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text")
                .isEqualTo(itemDto1.getComments().get(1).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName")
                .isEqualTo(itemDto1.getComments().get(1).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[1].created")
                .isEqualTo(itemDto1.getComments().get(1).getCreated().toString());

    }

}
