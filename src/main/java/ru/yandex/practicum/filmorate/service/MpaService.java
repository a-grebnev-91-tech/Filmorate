package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpaById(int id) {
        if (getAllMpa().stream().anyMatch(x -> x.getId() == id)) {
            return mpaStorage.getMpaById(id);
        } else {
            throw new ModelNotFoundException("MPA not found with id " + id);
        }
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}