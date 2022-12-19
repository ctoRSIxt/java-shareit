package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.practicum.shareit.item.model.Item;


@RepositoryRestResource
public interface ItemRepository extends JpaRepository<Item, Long> {
}