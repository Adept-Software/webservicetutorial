package com.photoshare.repository;

import com.photoshare.model.Picture;
import com.photoshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findByUploader(User uploader);
}
