package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService service;

    @GetMapping()
    public List<MPA> getList() {
        return service.getList();
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
