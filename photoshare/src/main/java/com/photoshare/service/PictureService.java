package com.photoshare.service;

import com.photoshare.model.Picture;
import com.photoshare.model.User;
import com.photoshare.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PictureService {

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    public Picture uploadPicture(MultipartFile file, User uploader) {
        String filename = storageService.store(file);
        Picture picture = new Picture();
        picture.setUrl("/uploads/" + filename);
        picture.setUploader(uploader);
        Picture savedPicture = pictureRepository.save(picture);
        faceRecognitionService.recognizeFaces(savedPicture);
        return savedPicture;
    }

    public List<Picture> getPicturesByUploader(User uploader) {
        return pictureRepository.findByUploader(uploader);
    }

    public List<Picture> getPicturesByUserTag(User user) {
        // This is a placeholder for a more complex query that would join pictures and tags
        return pictureRepository.findAll();
    }
}
