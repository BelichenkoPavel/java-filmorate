package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.db.DBMPAStorage;

import java.util.List;

@Service
public class MPAService {
    private MPAStorage mpaStorage;

    public MPAService(DBMPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA getById(Long id) {
        MPA mpa = mpaStorage.getById(id);

        if (mpa == null) {
            throw new NotFoundException("MPA with id " + id + " not found");
        }

        return mpa;
    }

    public List<MPA> getList() {
        return mpaStorage.getList();
    }
}
