package com.example.prac.controllers;

import com.example.prac.DTO.data.*;
import com.example.prac.model.dataEntity.MusicBand;
import com.example.prac.model.dataEntity.MusicGenre;
import com.example.prac.model.authEntity.User;
import com.example.prac.service.data.MusicService;
import com.example.prac.utils.DtoUtil;
import com.example.prac.webSocket.MusicWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/music")
public class MusicController {
    private final MusicWebSocketHandler musicWebSocketHandler;
    private final MusicService musicService;

    // Метод для преобразования MusicBand в MusicDTOResponse


    // Создание новой музыкальной группы
    @PostMapping
    public ResponseEntity<MusicDTOResponse> createMusicBand(@RequestBody MusicDTORequest musicDTORequest) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal(); // Получаем текущего пользователя
        System.out.println("2121");
        MusicBand savedMusicBand = musicService.saveMusicBand(musicDTORequest, currentUser);
        return new ResponseEntity<>(DtoUtil.convertToResponse(savedMusicBand), HttpStatus.CREATED);
    }

    // Получение всех музыкальных групп
    @GetMapping
    public ResponseEntity<List<MusicDTOResponse>> getAllMusicBands() {
        List<MusicBand> musicBands = musicService.getAllMusicBands();
        List<MusicDTOResponse> responses = musicBands.stream()
                .map(DtoUtil::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Получение музыкальной группы по ID
    @GetMapping("/{id}")
    public ResponseEntity<MusicDTOResponse> getMusicBandById(@PathVariable long id) {
        MusicBand musicBand = musicService.getMusicBandById(id);
        return ResponseEntity.ok(DtoUtil.convertToResponse(musicBand));
    }

    // Обновление музыкальной группы
    @PutMapping("/{id}")
    public ResponseEntity<MusicDTOResponse> updateMusicBand(@PathVariable long id, @RequestBody MusicDTORequest musicDTORequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        MusicBand existingMusicBand = musicService.getMusicBandById(id);
        // Проверка прав доступа
        if (!existingMusicBand.getOwner().getId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Нет прав для редактирования
        }



        // Передаем обновленный объект в сервис для сохранения
        MusicBand updatedMusicBand = musicService.updateMusicBand(existingMusicBand, musicDTORequest, currentUser);
        return ResponseEntity.ok(DtoUtil.convertToResponse(updatedMusicBand));
    }

    // Удаление музыкальной группы по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusicBandById(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        MusicBand existingMusicBand = musicService.getMusicBandById(id);
        // Проверка прав доступа
        if (!existingMusicBand.getOwner().getId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Нет прав для удаления
        }

        musicService.deleteMusicBandById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
