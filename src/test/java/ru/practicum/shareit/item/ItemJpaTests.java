package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
public class ItemJpaTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    public void searchByTextTest() {

        User user1 = new User(0, "user1", "email1@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(0, "description1",
                user1, LocalDateTime.now());
        Item item1 = new Item(0, "item1", "description item1", true,
                user1, itemRequest1);

        Item item2 = new Item(0, "item2", "description item2", true,
                user1, itemRequest1);

        userRepository.save(user1);
        requestRepository.save(itemRequest1);
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "owner"));

        List<Item> items1 = itemRepository.searchItemsByText("item1", pageRequest)
                .stream()
                .collect(Collectors.toList());


        Assertions.assertEquals(1, items1.size());
        Assertions.assertEquals(item1, items1.get(0));

        List<Item> items2 = itemRepository.searchItemsByText("description", pageRequest)
                .stream()
                .collect(Collectors.toList());

        Assertions.assertEquals(2, items2.size());
        Assertions.assertEquals(item1, items2.get(0));
        Assertions.assertEquals(item2, items2.get(1));

    }

}
