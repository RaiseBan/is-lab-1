package com.example.prac.controllers;

import com.example.prac.DTO.data.MusicDTOResponse;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.MusicBand;
import com.example.prac.service.data.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/special")
@RequiredArgsConstructor
public class SpecialOperationsController {

    private final MusicService musicService;

    @GetMapping("/group-by-creation-date")
    public ResponseEntity<List<Object[]>> getGroupedByCreationDate() {
        return ResponseEntity.ok(musicService.getMusicBandCountByCreationDate());
    }

    @GetMapping("/count-label-greater-than")
    public ResponseEntity<Long> countMusicBandsWithLabelMoreThan(@RequestParam("labelThreshold") String labelThreshold) {
        return ResponseEntity.ok(musicService.countMusicBandsWithLabelMoreThan(labelThreshold));
    }

    @GetMapping("/find-by-description")
    public ResponseEntity<List<MusicDTOResponse>> findMusicBandsByDescription(@RequestParam String substring) {
        List<MusicDTOResponse> bands = musicService.findMusicBandsByDescription(substring);
        return ResponseEntity.ok(bands);
    }

    @GetMapping("/add-single")
    public ResponseEntity<Void> addSingleToMusicBand(@RequestParam Long bandId, @RequestParam int singlesCount) throws IOException {

        MusicBand existingMusicBand = musicService.getMusicBandById(bandId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (!existingMusicBand.getOwner().getId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        musicService.addSingleToMusicBand(bandId, singlesCount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-participant")
    public ResponseEntity<Void> removeParticipantFromMusicBand(@RequestParam("bandId") long bandId) throws IOException {
        MusicBand existingMusicBand = musicService.getMusicBandById(bandId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (!existingMusicBand.getOwner().getId().equals(currentUser.getId()) &&
                currentUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (existingMusicBand.getNumberOfParticipants() == 1) {
            return ResponseEntity.unprocessableEntity().build();
        }
        musicService.removeParticipantFromMusicBand(bandId);
        return ResponseEntity.ok().build();
    }
}
