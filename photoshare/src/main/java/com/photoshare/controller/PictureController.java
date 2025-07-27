package com.photoshare.controller;

import com.photoshare.model.Picture;
import com.photoshare.model.User;
import com.photoshare.service.PictureService;
import com.photoshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<List<Picture>> uploadPictures(@RequestParam("files") MultipartFile[] files, @AuthenticationPrincipal UserDetails userDetails) {
        User uploader = userService.findByEmail(userDetails.getUsername());
        if (files.length > 11) {
            return ResponseEntity.badRequest().build();
        }
        List<Picture> uploadedPictures = Arrays.stream(files)
                .map(file -> pictureService.uploadPicture(file, uploader))
                .collect(Collectors.toList());
        return ResponseEntity.ok(uploadedPictures);
    }

    @GetMapping
    public ResponseEntity<List<Picture>> getPictures(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Picture> uploadedPictures = pictureService.getPicturesByUploader(user);
        List<Picture> taggedPictures = pictureService.getPicturesByUserTag(user);
        List<Picture> allPictures = Stream.concat(uploadedPictures.stream(), taggedPictures.stream())
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(allPictures);
    }
}
