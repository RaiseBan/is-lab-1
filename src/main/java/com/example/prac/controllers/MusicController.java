package com.example.prac.controllers;

import com.example.prac.DTO.data.*;
import com.example.prac.DTO.info.ImportHistoryDto;
import com.example.prac.model.dataEntity.MusicBand;
import com.example.prac.model.dataEntity.MusicGenre;
import com.example.prac.model.authEntity.User;
import com.example.prac.service.data.ImportService;
import com.example.prac.service.data.MusicService;
import com.example.prac.utils.DtoUtil;
import com.example.prac.webSocket.MusicWebSocketHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/music")
public class MusicController {
    private final MusicWebSocketHandler musicWebSocketHandler;
    private final MusicService musicService;
    private final ImportService importService;

    @PostMapping
    public ResponseEntity<?> createMusicBand(@Valid @RequestBody MusicDTORequest musicDTORequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        try {
            MusicBand savedMusicBand = musicService.saveMusicBand(musicDTORequest, currentUser);
            return new ResponseEntity<>(DtoUtil.convertToResponse(savedMusicBand), HttpStatus.CREATED);
        }catch (IllegalArgumentException illegalArgumentException){
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating music band", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping
    public ResponseEntity<List<MusicDTOResponse>> getAllMusicBands() {
        List<MusicBand> musicBands = musicService.getAllMusicBands();
        List<MusicDTOResponse> responses = musicBands.stream()
                .map(DtoUtil::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicDTOResponse> getMusicBandById(@PathVariable long id) {
        MusicBand musicBand = musicService.getMusicBandById(id);
        return ResponseEntity.ok(DtoUtil.convertToResponse(musicBand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicDTOResponse> updateMusicBand(@PathVariable long id, @RequestBody MusicDTORequest musicDTORequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        MusicBand existingMusicBand = musicService.getMusicBandById(id);
        if (!existingMusicBand.getOwner().getId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        MusicBand updatedMusicBand = musicService.updateMusicBand(existingMusicBand, musicDTORequest, currentUser);
        return ResponseEntity.ok(DtoUtil.convertToResponse(updatedMusicBand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusicBandById(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        System.out.println("1");
        MusicBand existingMusicBand = musicService.getMusicBandById(id);
        System.out.println("2");
        if (!existingMusicBand.getOwner().getId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            System.out.println("4");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        musicService.deleteMusicBandById(id);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importMusicBands(@RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            ImportHistoryDto result = importService.importMusicBandsFromFile(file, currentUser);
            if (Objects.equals(result.getStatus(), "FAILED")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
            }
            return ResponseEntity.ok(result); // Успешный ответ с результатом импорта
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILED", "message", e.getMessage()));
        }
    }



}
