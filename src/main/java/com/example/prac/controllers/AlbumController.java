package com.example.prac.controllers;

import com.example.prac.DTO.data.AlbumDTOwId;
import com.example.prac.DTO.data.BestAlbumDTOResponse;
import com.example.prac.model.authEntity.User;
import com.example.prac.model.dataEntity.Album;
import com.example.prac.service.data.AlbumService;
import com.example.prac.utils.DtoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumDTOwId>> getUserAlbums() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            
            List<Album> albums = albumService.getAlbumsByUser(currentUser);
            
            List<AlbumDTOwId> response = albums.stream()
                    .map(DtoUtil::convertToAlbumDTOwIdResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
